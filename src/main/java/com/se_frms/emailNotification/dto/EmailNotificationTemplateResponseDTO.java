package com.se_frms.emailNotification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmailNotificationTemplateResponseDTO {

    private Integer id;

    private String templateCode;

    private String channel;

    private String subject;

    private String body;

    private Boolean status;

    private Integer createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}