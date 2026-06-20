package com.se_frms.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginOtpResponseDTO {

    private String email;

    private String maskedPhoneNumber;

    private String otp;

    private Boolean otpRequired;
}