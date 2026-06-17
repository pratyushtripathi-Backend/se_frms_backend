package com.se_frms.emailNotification.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateRequestDTO;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateResponseDTO;
import com.se_frms.emailNotification.service.EmailNotificationTemplateService;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import com.se_frms.common.dto.PagedResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/admin/email-notification-templates")
@RequiredArgsConstructor
public class EmailNotificationTemplateController {

    private final EmailNotificationTemplateService emailNotificationTemplateService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO<EmailNotificationTemplateResponseDTO>>
    createEmailNotificationTemplate(
            @Valid
            @RequestBody
            EmailNotificationTemplateRequestDTO request
    ) {

        log.info("Create email notification template request received");

        EmailNotificationTemplateResponseDTO responseData =
                emailNotificationTemplateService.createTemplate(request);

        log.info("Email notification template created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<EmailNotificationTemplateResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage(
                                        "Email notification template created successfully"
                                )
                                .responseData(responseData)
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<EmailNotificationTemplateResponseDTO>>>
    getAllEmailNotificationTemplates(
            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size
    ) {

        Page<EmailNotificationTemplateResponseDTO> pageData =
                emailNotificationTemplateService.getAllTemplates(page, size);

        PagedResponseDTO<EmailNotificationTemplateResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<EmailNotificationTemplateResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Email notification templates fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{templateCode}")
    public ResponseEntity<AuthResponseDTO<EmailNotificationTemplateResponseDTO>>
    getEmailNotificationTemplateByCode(
            @PathVariable
            String templateCode
    ) {

        log.info(
                "Fetch email notification template request received, templateCode={}",
                templateCode
        );

        EmailNotificationTemplateResponseDTO responseData =
                emailNotificationTemplateService.getTemplateByCode(templateCode);

        log.info(
                "Email notification template fetched successfully, templateCode={}",
                templateCode
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<EmailNotificationTemplateResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Email notification template fetched successfully"
                        )
                        .responseData(responseData)
                        .build()
        );
    }

    @PutMapping("/{templateCode}")
    public ResponseEntity<AuthResponseDTO<EmailNotificationTemplateResponseDTO>>
    updateEmailNotificationTemplate(
            @PathVariable
            String templateCode,

            @Valid
            @RequestBody
            EmailNotificationTemplateRequestDTO request
    ) {

        log.info(
                "Update email notification template request received, templateCode={}",
                templateCode
        );

        EmailNotificationTemplateResponseDTO responseData =
                emailNotificationTemplateService.updateTemplate(
                        templateCode,
                        request
                );

        log.info(
                "Email notification template updated successfully, templateCode={}",
                templateCode
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<EmailNotificationTemplateResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Email notification template updated successfully"
                        )
                        .responseData(responseData)
                        .build()
        );
    }
}