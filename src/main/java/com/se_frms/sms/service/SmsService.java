package com.se_frms.sms.service;

public interface SmsService {

    void sendLoginOtp(
            String phoneNumber,
            String otp
    );
}