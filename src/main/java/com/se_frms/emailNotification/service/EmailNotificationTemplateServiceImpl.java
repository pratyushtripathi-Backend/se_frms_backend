package com.se_frms.emailNotification.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateRequestDTO;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateResponseDTO;
import com.se_frms.emailNotification.model.EmailNotificationTemplate;
import com.se_frms.emailNotification.repository.EmailNotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailNotificationTemplateServiceImpl
        implements EmailNotificationTemplateService {

    private final EmailNotificationTemplateRepository repository;

    @Override
    public EmailNotificationTemplateResponseDTO createTemplate(
            EmailNotificationTemplateRequestDTO request
    ) {

        String templateCode =
                request.getTemplateCode()
                        .trim()
                        .toUpperCase();

        if (repository.existsByTemplateCode(templateCode)) {
            throw new InvalidRequestException(
                    "Template code already exists"
            );
        }

        EmailNotificationTemplate template =
                EmailNotificationTemplate.builder()
                        .templateCode(templateCode)
                        .channel(
                                request.getChannel()
                                        .trim()
                                        .toUpperCase()
                        )
                        .subject(request.getSubject())
                        .body(request.getBody())
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : true
                        )
                        .createdBy(request.getCreatedBy())
                        .build();

        return mapToResponse(
                repository.save(template)
        );
    }

    @Override
    public EmailNotificationTemplateResponseDTO updateTemplate(
            String templateCode,
            EmailNotificationTemplateRequestDTO request
    ) {

        EmailNotificationTemplate template =
                repository
                        .findByTemplateCode(
                                templateCode.trim().toUpperCase()
                        )
                        .orElseThrow(
                                () -> new InvalidRequestException(
                                        "Template not found"
                                )
                        );

        template.setChannel(
                request.getChannel()
                        .trim()
                        .toUpperCase()
        );
        template.setSubject(request.getSubject());
        template.setBody(request.getBody());

        if (request.getStatus() != null) {
            template.setStatus(request.getStatus());
        }

        return mapToResponse(
                repository.save(template)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EmailNotificationTemplateResponseDTO getTemplateByCode(
            String templateCode
    ) {

        EmailNotificationTemplate template =
                repository
                        .findByTemplateCode(
                                templateCode.trim().toUpperCase()
                        )
                        .orElseThrow(
                                () -> new InvalidRequestException(
                                        "Template not found"
                                )
                        );

        return mapToResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailNotificationTemplateResponseDTO> getAllTemplates() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String getSubject(String templateCode) {

        return getActiveTemplate(templateCode).getSubject();
    }

    @Override
    @Transactional(readOnly = true)
    public String renderTemplate(
            String templateCode,
            Map<String, String> values
    ) {

        String body =
                getActiveTemplate(templateCode).getBody();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            body = body.replace(
                    "{{" + entry.getKey() + "}}",
                    entry.getValue()
            );
        }

        return body;
    }

    private EmailNotificationTemplate getActiveTemplate(
            String templateCode
    ) {

        return repository
                .findByTemplateCodeAndStatus(
                        templateCode.trim().toUpperCase(),
                        true
                )
                .orElseThrow(
                        () -> new InvalidRequestException(
                                "Active email notification template not found"
                        )
                );
    }

    private EmailNotificationTemplateResponseDTO mapToResponse(
            EmailNotificationTemplate template
    ) {

        return EmailNotificationTemplateResponseDTO
                .builder()
                .id(template.getId())
                .templateCode(template.getTemplateCode())
                .channel(template.getChannel())
                .subject(template.getSubject())
                .body(template.getBody())
                .status(template.getStatus())
                .createdBy(template.getCreatedBy())
                .createdDate(template.getCreatedDate())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}