package com.se_frms.common.exception;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.auth.exception.InvalidRoleException;
import com.se_frms.auth.exception.InvalidTokenException;
import com.se_frms.auth.exception.TokenExpiredException;
import com.se_frms.auth.exception.UserAlreadyExistsException;
import com.se_frms.user.exception.InvalidCredentialsException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleDuplicateEmailException(
            DuplicateEmailException ex
    ) {

        log.warn("Duplicate email exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleDuplicatePhoneException(
            DuplicatePhoneException ex
    ) {

        log.warn("Duplicate phone exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleInvalidRoleException(
            InvalidRoleException ex
    ) {

        log.warn("Invalid role exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleInvalidRequestException(
            InvalidRequestException ex
    ) {

        log.warn("Invalid request exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex
    ) {

        log.warn("User already exists exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleValidationException(
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

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleInvalidCredentialsException(
            InvalidCredentialsException ex
    ) {

        log.warn("Invalid credentials exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleInvalidTokenException(
            InvalidTokenException ex
    ) {

        log.warn("Invalid token exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleTokenExpiredException(
            TokenExpiredException ex
    ) {

        log.warn("Token expired exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<AuthResponseDTO<Object>> handleNotFoundException(Exception ex) {

        log.warn("Endpoint not found exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, "API endpoint not found");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex
    ) {

        log.warn("Method not supported exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Request method not supported");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex
    ) {

        log.warn("Media type not supported exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleMessageNotReadableException(
            HttpMessageNotReadableException ex
    ) {

        log.warn("Message not readable exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request body");
    }

    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MissingRequestHeaderException.class,
        MethodArgumentTypeMismatchException.class,
        ConstraintViolationException.class
    })
    public ResponseEntity<AuthResponseDTO<Object>> handleBadRequestException(Exception ex) {

        log.warn("Bad request exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, resolveMessage(ex, "Invalid request"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {

        log.warn("Data integrity exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT, "Data integrity violation");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleAuthenticationException(
            AuthenticationException ex
    ) {

        log.warn("Authentication exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication required. Please login.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleAccessDeniedException(
            AccessDeniedException ex
    ) {

        log.warn("Access denied exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {

        log.warn("Runtime exception handled: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, resolveMessage(ex, "Invalid request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponseDTO<Object>> handleGenericException(Exception ex) {

        log.error("Unexpected exception handled", ex);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }

    private ResponseEntity<AuthResponseDTO<Object>> buildErrorResponse(
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

        return ResponseEntity.status(status).body(response);
    }

    private String resolveMessage(Exception ex, String fallbackMessage) {

        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return fallbackMessage;
        }

        return ex.getMessage();
    }
}
