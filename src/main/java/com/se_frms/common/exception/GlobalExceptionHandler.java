package com.se_frms.common.exception;


import com.se_frms.auth.dto.AuthResponseDTO;

import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRoleException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.auth.exception.UserAlreadyExistsException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            DuplicateEmailException.class
    )
    public ResponseEntity<AuthResponseDTO<Object>>
    handleDuplicateEmailException(
            DuplicateEmailException ex
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(
            DuplicatePhoneException.class
    )
    public ResponseEntity<AuthResponseDTO<Object>>
    handleDuplicatePhoneException(
            DuplicatePhoneException ex
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(
            InvalidRoleException.class
    )
    public ResponseEntity<AuthResponseDTO<Object>>
    handleInvalidRoleException(
            InvalidRoleException ex
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(
            InvalidRequestException.class
    )
    public ResponseEntity<AuthResponseDTO<Object>>
    handleInvalidRequestException(
            InvalidRequestException ex
    ) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(
            UserAlreadyExistsException.class
    )
    public ResponseEntity<AuthResponseDTO<Object>>
    handleUserAlreadyExistsException(
            UserAlreadyExistsException ex
    ) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<AuthResponseDTO<Object>>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String errorMessage =
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage();

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleGenericException(
            Exception ex
    ) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    private ResponseEntity<AuthResponseDTO<Object>>
    buildErrorResponse(
            HttpStatus status,
            String message
    ) {

        AuthResponseDTO<Object> response =
                AuthResponseDTO.builder()
                        .status(false)
                        .responseCode(status.value())
                        .responseMessage(message)
                        .responseData(null)
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}

