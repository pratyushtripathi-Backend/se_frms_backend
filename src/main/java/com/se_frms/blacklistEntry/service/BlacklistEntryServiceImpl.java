package com.se_frms.blacklistEntry.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.blacklistEntry.dto.BlacklistEntryRequestDTO;
import com.se_frms.blacklistEntry.dto.BlacklistEntryResponseDTO;
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

        if (blacklistEntryRepository.existsByTypeAndValueAndStatus(normalizedType, cleanedValue, true)) {
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
    public BlacklistEntryResponseDTO removeEntry(Integer id) {

        log.info("Remove blacklist entry service started, id={}", id);

        BlacklistEntry blacklistEntry =
                blacklistEntryRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Remove blacklist entry failed because it was not found, id={}", id);
                            return new InvalidRequestException("Blacklist entry not found");
                        });

        blacklistEntry.setStatus(false);
        blacklistEntry.setUpdatedAt(LocalDateTime.now());
        BlacklistEntry saved = blacklistEntryRepository.save(blacklistEntry);

        log.info("Blacklist entry removed successfully, id={}", id);

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
}
