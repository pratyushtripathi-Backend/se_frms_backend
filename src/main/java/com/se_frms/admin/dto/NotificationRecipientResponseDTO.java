package com.se_frms.admin.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Minimal recipient data exposed only to internal services for fraud alerts.
 */
@Getter
@Builder
public class NotificationRecipientResponseDTO {

    private Integer userId;
    private String name;
    private String email;
    private String phoneNumber;
}
