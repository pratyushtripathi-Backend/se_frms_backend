package com.se_frms.emailNotification.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateRequestDTO;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateResponseDTO;
import com.se_frms.emailNotification.model.EmailNotificationTemplate;
import com.se_frms.emailNotification.repository.EmailNotificationTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
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

        log.info("Create email notification template service started");

        String templateCode =
                request.getTemplateCode()
                        .trim()
                        .toUpperCase();

        if (repository.existsByTemplateCode(templateCode)) {
            log.warn(
                    "Create email template failed because template code already exists, templateCode={}",
                    templateCode
            );

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

        EmailNotificationTemplate savedTemplate =
                repository.save(template);

        log.info(
                "Email notification template created successfully, templateCode={}",
                savedTemplate.getTemplateCode()
        );

        return mapToResponse(
                savedTemplate
        );
    }

    @Override
    public EmailNotificationTemplateResponseDTO updateTemplate(
            String templateCode,
            EmailNotificationTemplateRequestDTO request
    ) {

        String normalizedTemplateCode =
                templateCode.trim().toUpperCase();

        log.info(
                "Update email notification template service started, templateCode={}",
                normalizedTemplateCode
        );

        EmailNotificationTemplate template =
                repository
                        .findByTemplateCode(
                                normalizedTemplateCode
                        )
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Update email template failed because template was not found, templateCode={}",
                                            normalizedTemplateCode
                                    );

                                    return new InvalidRequestException(
                                            "Template not found"
                                    );
                                }
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

        EmailNotificationTemplate savedTemplate =
                repository.save(template);

        log.info(
                "Email notification template updated successfully, templateCode={}",
                savedTemplate.getTemplateCode()
        );

        return mapToResponse(
                savedTemplate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EmailNotificationTemplateResponseDTO getTemplateByCode(
            String templateCode
    ) {

        String normalizedTemplateCode =
                templateCode.trim().toUpperCase();

        log.info(
                "Fetch email notification template service started, templateCode={}",
                normalizedTemplateCode
        );

        EmailNotificationTemplate template =
                repository
                        .findByTemplateCode(
                                normalizedTemplateCode
                        )
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Fetch email template failed because template was not found, templateCode={}",
                                            normalizedTemplateCode
                                    );

                                    return new InvalidRequestException(
                                            "Template not found"
                                    );
                                }
                        );

        log.info(
                "Email notification template fetched successfully, templateCode={}",
                normalizedTemplateCode
        );

        return mapToResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailNotificationTemplateResponseDTO> getAllTemplates() {

        log.info("Fetch all email notification templates service started");

        List<EmailNotificationTemplateResponseDTO> response =
                repository.findAll()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        log.info(
                "Email notification templates fetched successfully, count={}",
                response.size()
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public String getSubject(
            String templateCode
    ) {

        log.debug(
                "Fetch email template subject started, templateCode={}",
                templateCode
        );

        return getActiveTemplate(templateCode)
                .getSubject();
    }

    @Override
    @Transactional(readOnly = true)
    public String renderTemplate(
            String templateCode,
            Map<String, String> values
    ) {

        log.debug(
                "Render email template started, templateCode={}",
                templateCode
        );

        String body =
                getActiveTemplate(templateCode).getBody();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            body = body.replace(
                    "{{" + entry.getKey() + "}}",
                    entry.getValue()
            );
        }

        log.debug(
                "Email template rendered successfully, templateCode={}",
                templateCode
        );

        return body;
    }

    private EmailNotificationTemplate getActiveTemplate(
            String templateCode
    ) {

        String normalizedTemplateCode =
                templateCode.trim().toUpperCase();

        return repository
                .findByTemplateCodeAndStatus(
                        normalizedTemplateCode,
                        true
                )
                .orElseThrow(
                        () -> {
                            log.warn(
                                    "Active email template lookup failed, templateCode={}",
                                    normalizedTemplateCode
                            );

                            return new InvalidRequestException(
                                    "Active email notification template not found"
                            );
                        }
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