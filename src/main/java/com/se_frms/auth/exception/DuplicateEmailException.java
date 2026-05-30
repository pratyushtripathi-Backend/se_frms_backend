package com.se_frms.auth.exception;


public class DuplicateEmailException extends RuntimeException
{ public DuplicateEmailException( String message ) {
    super(message);
}
}
