package com.example.BigShort.url_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.example.BigShort.url_service.domain.dto.ApiError;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------
    // Handle ShortCodeAlreadyExistsException
    // -------------------------------------------------------
    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleShortCodeConflict(ShortCodeAlreadyExistsException ex, WebRequest request) {
        ApiError error = ApiError.builder()
                .type("https://example.com/probs/shortcode-exists")
                .title("Short Code Conflict")
                .status(HttpStatus.CONFLICT.value())
                .detail(ex.getMessage()) // Use actual exception message
                .instance(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // -------------------------------------------------------
    // Handle InvalidUrlException
    // -------------------------------------------------------
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ApiError> handleInvalidUrl(InvalidUrlException ex, WebRequest request) {
        ApiError error = ApiError.builder()
                .type("https://example.com/probs/invalid-url")
                .title("Invalid URL")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(ex.getMessage()) // Use actual exception message
                .instance(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------
    // Handle ShortCodeTooLongException
    // -------------------------------------------------------
    @ExceptionHandler(ShortCodeTooLongException.class)
    public ResponseEntity<ApiError> handleShortCodeTooLong(ShortCodeTooLongException ex, WebRequest request) {
        ApiError error = ApiError.builder()
                .type("https://example.com/probs/shortcode-too-long")
                .title("Custom Short Code Too Long")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(ex.getMessage()) // Use actual exception message
                .instance(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------
    // Handle Validation Errors (from @Valid annotations)
    // -------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        String errorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiError error = ApiError.builder()
                .type("https://example.com/probs/validation-error")
                .title("Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(errorDetails)
                .instance(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------
    // Handle General RuntimeException
    // -------------------------------------------------------
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ApiError error = ApiError.builder()
                .type("https://example.com/probs/runtime-error")
                .title("Runtime Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(ex.getMessage()) // Use actual exception message
                .instance(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------
    // Handle All Other Exceptions
    // -------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralError(Exception ex, WebRequest request) {
        ApiError error = ApiError.builder()
                .type("https://example.com/probs/internal-error")
                .title("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("An unexpected error occurred: " + ex.getMessage())
                .instance(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}