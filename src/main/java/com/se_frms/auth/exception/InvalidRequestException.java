package com.se_frms.auth.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException( String message )
    { super(message); } }