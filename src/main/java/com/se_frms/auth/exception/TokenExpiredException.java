package com.se_frms.auth.exception;

public class TokenExpiredException
        extends RuntimeException {

    public TokenExpiredException(
            String message
    ) {
        super(message);
    }
}