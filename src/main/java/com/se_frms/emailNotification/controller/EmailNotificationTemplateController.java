package com.se_frms.emailNotification.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateRequestDTO;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateResponseDTO;
import com.se_frms.emailNotification.service.EmailNotificationTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        EmailNotificationTemplateResponseDTO responseData =
                emailNotificationTemplateService.createTemplate(request);

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
    public ResponseEntity<AuthResponseDTO<List<EmailNotificationTemplateResponseDTO>>>
    getAllEmailNotificationTemplates() {

        List<EmailNotificationTemplateResponseDTO> responseData =
                emailNotificationTemplateService.getAllTemplates();

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<EmailNotificationTemplateResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Email notification templates fetched successfully"
                        )
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

        EmailNotificationTemplateResponseDTO responseData =
                emailNotificationTemplateService.getTemplateByCode(templateCode);

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

        EmailNotificationTemplateResponseDTO responseData =
                emailNotificationTemplateService.updateTemplate(
                        templateCode,
                        request
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