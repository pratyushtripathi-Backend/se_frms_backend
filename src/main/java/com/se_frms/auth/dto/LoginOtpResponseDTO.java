package com.se_frms.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOtpResponseDTO {

    private String email;
    private String maskedPhoneNumber;
    private String otp;
    private Boolean otpRequired;
}