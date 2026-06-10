package com.se_frms.emailNotification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailNotificationTemplateRequestDTO {

    @NotBlank(message = "Template code is required")
    private String templateCode;

    @NotBlank(message = "Channel is required")
    private String channel;

    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    private Boolean status;

    private Integer createdBy;
}