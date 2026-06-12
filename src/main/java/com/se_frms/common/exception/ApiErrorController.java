package com.se_frms.common.exception;

import com.se_frms.auth.dto.AuthResponseDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiErrorController implements ErrorController {

  @RequestMapping("/error")
  public ResponseEntity<AuthResponseDTO<Object>> handleError(HttpServletRequest request) {

    HttpStatus status = resolveStatus(request);

    AuthResponseDTO<Object> response =
        AuthResponseDTO.builder()
            .status(false)
            .responseCode(status.value())
            .responseMessage(resolveMessage(status))
            .responseData(null)
            .build();

    return ResponseEntity.status(status).body(response);
  }

  private HttpStatus resolveStatus(HttpServletRequest request) {

    Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    if (statusCode instanceof Integer code) {
      HttpStatus status = HttpStatus.resolve(code);

      if (status != null) {
        return status;
      }
    }

    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  private String resolveMessage(HttpStatus status) {

    return switch (status) {
      case NOT_FOUND -> "API endpoint not found";
      case METHOD_NOT_ALLOWED -> "Request method not supported";
      case UNAUTHORIZED -> "Authentication required. Please login.";
      case FORBIDDEN -> "Access denied";
      case BAD_REQUEST -> "Invalid request";
      default -> "Something went wrong";
    };
  }
}
