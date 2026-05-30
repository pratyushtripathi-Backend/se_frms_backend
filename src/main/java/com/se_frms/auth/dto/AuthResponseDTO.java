package com.se_frms.auth.dto;



import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AuthResponseDTO<T> {

    private boolean status;

    private int responseCode;

    private String responseMessage;

    private T responseData;
}


