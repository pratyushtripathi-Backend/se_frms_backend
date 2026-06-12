package com.se_frms.common.exception;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.auth.exception.InvalidRoleException;
import com.se_frms.auth.exception.UserAlreadyExistsException;
import com.se_frms.user.exception.InvalidCredentialsException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleDuplicateEmailException(
            DuplicateEmailException ex
    ) {

        log.warn("Duplicate email exception handled: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleDuplicatePhoneException(
            DuplicatePhoneException ex
    ) {

        log.warn("Duplicate phone exception handled: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleInvalidRoleException(
            InvalidRoleException ex
    ) {

        log.warn("Invalid role exception handled: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleInvalidRequestException(
            InvalidRequestException ex
    ) {

        log.warn("Invalid request exception handled: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleUserAlreadyExistsException(
            UserAlreadyExistsException ex
    ) {

        log.warn("User already exists exception handled: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String errorMessage =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(error -> error.getDefaultMessage())
                        .orElse("Validation failed");

        log.warn("Validation exception handled: {}", errorMessage);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleInvalidCredentialsException(
            InvalidCredentialsException ex
    ) {

        log.warn("Invalid credentials exception handled: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponseDTO<Object>>
    handleGenericException(
            Exception ex
    ) {

        log.error(
                "Unhandled exception occurred",
                ex
        );

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong"
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