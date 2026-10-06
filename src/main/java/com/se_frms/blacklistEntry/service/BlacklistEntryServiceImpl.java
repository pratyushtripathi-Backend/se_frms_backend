package com.se_frms.blacklistEntry.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.blacklistEntry.dto.BlacklistEntryRequestDTO;
import com.se_frms.blacklistEntry.dto.BlacklistEntryResponseDTO;
import com.se_frms.blacklistEntry.dto.BlacklistEntryUpdateDTO;
import com.se_frms.blacklistEntry.model.BlacklistEntry;
import com.se_frms.blacklistEntry.repository.BlacklistEntryRepository;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.common.security.XssUtil;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BlacklistEntryServiceImpl implements BlacklistEntryService {

    private static final List<String> VALID_TYPES = List.of("IP", "DEVICE", "LOCATION");

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("type", "type"),
                    Map.entry("value", "value"),
                    Map.entry("status", "status"),
                    Map.entry("riskType", "riskType"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final BlacklistEntryRepository blacklistEntryRepository;
    private final CurrentUserService currentUserService;
    private final CreatedByResolver createdByResolver;

    @Override
    public BlacklistEntryResponseDTO addEntry(BlacklistEntryRequestDTO request) {

        log.info("Add blacklist entry service started, type={}", request.getType());

        String normalizedType = normalizeType(request.getType());
        String cleanedValue = clean(request.getValue());

        if (blacklistEntryRepository.existsActiveDuplicate(normalizedType, cleanedValue, -1)) {
            log.warn("Add blacklist entry failed because it already exists, type={}, value={}",
                    normalizedType, cleanedValue);
            throw new InvalidRequestException("This " + normalizedType + " is already blacklisted");
        }

        User loggedInAdmin = currentUserService.getCurrentUser();

        BlacklistEntry blacklistEntry =
                BlacklistEntry.builder()
                        .type(normalizedType)
                        .value(cleanedValue)
                        .reason(clean(request.getReason()))
                        .riskType(clean(request.getRiskType()))
                        .status(true)
                        .createdBy(loggedInAdmin)
                        .build();

        BlacklistEntry saved = blacklistEntryRepository.save(blacklistEntry);

        log.info("Blacklist entry added successfully, id={}, type={}", saved.getId(), normalizedType);

        return mapToResponse(saved);
    }

    @Override
    public BlacklistEntryResponseDTO updateEntry(Integer id, BlacklistEntryUpdateDTO request) {

        log.info("Update blacklist entry service started, id={}", id);

        BlacklistEntry blacklistEntry =
                blacklistEntryRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Update blacklist entry failed because it was not found, id={}", id);
                            return new InvalidRequestException("Blacklist entry not found");
                        });

        if (request.getType() == null
                && request.getValue() == null
                && request.getReason() == null
                && request.getRiskType() == null) {
            log.warn("Update blacklist entry failed because no field was sent, id={}", id);
            throw new InvalidRequestException(
                    "At least one of type, value, reason or riskType is required to update");
        }

        // null = not sent = keep the existing value
        String newType = request.getType() == null
                ? blacklistEntry.getType()
                : normalizeType(request.getType());

        String newValue = blacklistEntry.getValue();
        if (request.getValue() != null) {
            newValue = clean(request.getValue());
            if (newValue == null || newValue.isBlank()) {
                throw new InvalidRequestException("value must not be blank");
            }
        }

        // Case-only change of its own value (abc -> ABC) is not a new identity.
        boolean identityChanged =
                !newType.equals(blacklistEntry.getType()) || !newValue.equalsIgnoreCase(blacklistEntry.getValue());

        // Same rule as addEntry: only one ACTIVE entry per type + value (ignoring case).
        // Checked when type/value changes, and also whenever this entry is active - so an
        // already-existing duplicate is caught even if the same value is sent again.
        // This very row is excluded from the check (IdNot).
        if ((identityChanged || Boolean.TRUE.equals(blacklistEntry.getStatus()))
                && blacklistEntryRepository.existsActiveDuplicate(newType, newValue, id)) {
            log.warn("Update blacklist entry failed because it already exists, id={}, type={}, value={}",
                    id, newType, newValue);
            throw new InvalidRequestException("This " + newType + " is already blacklisted");
        }

        // Nothing actually changes (same type, value, reason and riskType as already saved):
        // tell the caller instead of silently saving the same data again.
        boolean sameReason = request.getReason() == null
                || java.util.Objects.equals(emptyToNull(clean(request.getReason())), blacklistEntry.getReason());
        boolean sameRiskType = request.getRiskType() == null
                || java.util.Objects.equals(emptyToNull(clean(request.getRiskType())), blacklistEntry.getRiskType());
        if (newType.equals(blacklistEntry.getType())
                && newValue.equals(blacklistEntry.getValue())
                && sameReason && sameRiskType) {
            log.warn("Update blacklist entry skipped because nothing changed, id={}, type={}, value={}",
                    id, newType, newValue);
            throw new InvalidRequestException(
                    "This " + newType + " already exists with the same details, nothing to update");
        }

        blacklistEntry.setType(newType);
        blacklistEntry.setValue(newValue);

        if (request.getReason() != null) {
            String reason = clean(request.getReason());
            blacklistEntry.setReason(reason == null || reason.isBlank() ? null : reason);
        }

        if (request.getRiskType() != null) {
            String riskType = clean(request.getRiskType());
            blacklistEntry.setRiskType(riskType == null || riskType.isBlank() ? null : riskType);
        }

        blacklistEntry.setUpdatedAt(LocalDateTime.now());
        BlacklistEntry saved = blacklistEntryRepository.save(blacklistEntry);

        log.info("Blacklist entry updated successfully, id={}, type={}", id, newType);

        return mapToResponse(saved);
    }

    @Override
    public BlacklistEntryResponseDTO updateStatus(Integer id, Boolean status) {

        log.info("Update blacklist entry status service started, id={}, status={}", id, status);

        if (status == null) {
            log.warn("Update blacklist entry status failed because status was null, id={}", id);
            throw new InvalidRequestException("status is required (true or false)");
        }

        BlacklistEntry blacklistEntry =
                blacklistEntryRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Update blacklist entry status failed because it was not found, id={}", id);
                            return new InvalidRequestException("Blacklist entry not found");
                        });

        // Re-activating must not create a second ACTIVE entry for the same type + value.
        // (Only when it is currently not active, so this very row can never match itself.)
        if (Boolean.TRUE.equals(status)
                && !Boolean.TRUE.equals(blacklistEntry.getStatus())
                && blacklistEntryRepository.existsActiveDuplicate(
                        blacklistEntry.getType(), blacklistEntry.getValue(), id)) {
            log.warn("Update blacklist entry status failed because an active entry already exists, id={}, type={}, value={}",
                    id, blacklistEntry.getType(), blacklistEntry.getValue());
            throw new InvalidRequestException(
                    "Cannot activate: this " + blacklistEntry.getType() + " is already blacklisted by another active entry");
        }

        blacklistEntry.setStatus(status);
        blacklistEntry.setUpdatedAt(LocalDateTime.now());
        BlacklistEntry saved = blacklistEntryRepository.save(blacklistEntry);

        log.info("Blacklist entry status updated successfully, id={}, status={}", id, status);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlacklistEntryResponseDTO> getAllEntries(int page, int size, Map<String, String> filters) {

        log.info("Fetch all blacklist entries service started, page={}, size={}", page, size);

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page, size, filters, FILTER_FIELDS, "createdDate", Sort.Direction.DESC
                );

        Specification<BlacklistEntry> specification =
                DynamicFilterSpecification.build(filters, FILTER_FIELDS);

        Page<BlacklistEntryResponseDTO> response =
                blacklistEntryRepository.findAll(specification, pageable).map(this::mapToResponse);

        log.info("Blacklist entries fetched successfully, count={}", response.getNumberOfElements());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BlacklistEntryResponseDTO getEntryById(Integer id) {

        log.info("Fetch blacklist entry by id service started, id={}", id);

        BlacklistEntry blacklistEntry =
                blacklistEntryRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Fetch blacklist entry failed because it was not found, id={}", id);
                            return new InvalidRequestException("Blacklist entry not found");
                        });

        return mapToResponse(blacklistEntry);
    }

    private String normalizeType(String type) {

        String normalized = type == null ? "" : type.trim().toUpperCase(Locale.ROOT);

        if (!VALID_TYPES.contains(normalized)) {
            throw new InvalidRequestException("type must be one of IP, DEVICE or LOCATION");
        }

        return normalized;
    }

    private String clean(String value) {

        if (value == null) {
            return null;
        }

        return XssUtil.clean(value.trim());
    }

    private BlacklistEntryResponseDTO mapToResponse(BlacklistEntry blacklistEntry) {

        return BlacklistEntryResponseDTO.builder()
                .id(blacklistEntry.getId())
                .type(blacklistEntry.getType())
                .value(blacklistEntry.getValue())
                .reason(blacklistEntry.getReason())
                .riskType(blacklistEntry.getRiskType())
                .status(blacklistEntry.getStatus())
                .createdBy(createdByResolver.resolve(blacklistEntry.getCreatedBy()))
                .createdDate(blacklistEntry.getCreatedDate())
                .updatedAt(blacklistEntry.getUpdatedAt())
                .build();
    }

    private String emptyToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }
}
