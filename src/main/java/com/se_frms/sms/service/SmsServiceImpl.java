package com.se_frms.sms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se_frms.sms.dto.Msg24x7SmsRequest;
import com.se_frms.sms.dto.Msg24x7SmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${sms.msg24x7.send-url}")
    private String sendUrl;

    @Value("${sms.msg24x7.api-key}")
    private String apiKey;

    @Value("${sms.msg24x7.client-id}")
    private String clientId;

    @Value("${sms.msg24x7.sender-id}")
    private String senderId;

    @Value("${sms.msg24x7.template-id}")
    private String templateId;

    @Value("${sms.msg24x7.principle-entity-id}")
    private String principleEntityId;

    @Value("${sms.msg24x7.login-otp-message}")
    private String loginOtpMessage;

    @Override
    public void sendLoginOtp(
            String phoneNumber,
            String otp
    ) {

        validateSmsConfig();

        String mobileNumber =
                normalizePhoneNumber(phoneNumber);

        String message =
                loginOtpMessage.replace(
                        "{otp}",
                        otp
                );

        Msg24x7SmsRequest request =
                Msg24x7SmsRequest
                        .builder()
                        .senderId(senderId)
                        .isUnicode(false)
                        .isFlash(false)
                        .isRegisteredForDelivery(true)
                        .validityPeriod("")
                        .dataCoding(0)
                        .schedTime("")
                        .groupId("")
                        .message(message)
                        .mobileNumbers(mobileNumber)
                        .serviceId("")
                        .coRelator("FRMS-OTP-" + UUID.randomUUID())
                        .linkId("")
                        .principleEntityId(principleEntityId)
                        .templateId(templateId)
                        .apiKey(apiKey)
                        .clientId(clientId)
                        .build();

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(
                List.of(
                        MediaType.APPLICATION_JSON,
                        MediaType.TEXT_PLAIN
                )
        );

        HttpEntity<Msg24x7SmsRequest> entity =
                new HttpEntity<>(request, headers);

        try {

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            sendUrl,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );
            log.info(
                    "MSG24x7 SMS API response: {}",
                    response.getBody()
            );

            Msg24x7SmsResponse smsResponse =
                    objectMapper.readValue(
                            response.getBody(),
                            Msg24x7SmsResponse.class
                    );

            if (!smsResponse.isSuccess()) {

                log.warn(
                        "Login OTP SMS failed, mobile={}, errorCode={}, errorDescription={}",
                        maskPhoneNumber(mobileNumber),
                        smsResponse.getErrorCode(),
                        smsResponse.getErrorDescription()
                );

                throw new RuntimeException(
                        "Unable to send OTP SMS"
                );
            }

            log.info(
                    "Login OTP SMS sent successfully, mobile={}",
                    maskPhoneNumber(mobileNumber)
            );

        } catch (Exception ex) {

            log.error(
                    "Login OTP SMS sending failed, mobile={}",
                    maskPhoneNumber(mobileNumber),
                    ex
            );

            throw new RuntimeException(
                    "Unable to send OTP SMS"
            );
        }
    }

    private void validateSmsConfig() {

        if (
                !StringUtils.hasText(apiKey)
                        || !StringUtils.hasText(clientId)
                        || !StringUtils.hasText(senderId)
                        || !StringUtils.hasText(templateId)
                        || !StringUtils.hasText(principleEntityId)
        ) {
            throw new RuntimeException(
                    "SMS configuration is missing"
            );
        }
    }

    private String normalizePhoneNumber(
            String phoneNumber
    ) {

        if (!StringUtils.hasText(phoneNumber)) {
            throw new RuntimeException(
                    "Phone number is required"
            );
        }

        String digits =
                phoneNumber.replaceAll("\\D", "");

        if (digits.length() == 10) {
            return "91" + digits;
        }

        if (
                digits.length() == 12
                        && digits.startsWith("91")
        ) {
            return digits;
        }

        throw new RuntimeException(
                "Invalid phone number"
        );
    }

    private String maskPhoneNumber(
            String phoneNumber
    ) {

        if (
                phoneNumber == null
                        || phoneNumber.length() < 4
        ) {
            return phoneNumber;
        }

        return "******"
                + phoneNumber.substring(
                phoneNumber.length() - 4
        );
    }
}