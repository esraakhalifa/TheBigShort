//package com.example.BigShort.url_service.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import com.example.BigShort.url_service.domain.dto.ApiError;
//
//import java.util.stream.Collectors;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    // -------------------------------------------------------
//    // Handle ShortCodeAlreadyExistsException
//    // -------------------------------------------------------
//    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
//    public ResponseEntity<ApiError> handleShortCodeConflict(ShortCodeAlreadyExistsException ex, WebRequest request) {
//        ApiError error = ApiError.builder()
//                .type("https://example.com/probs/shortcode-exists")
//                .title("Short Code Conflict")
//                .status(HttpStatus.CONFLICT.value())
//                .detail(ex.getMessage()) // Use actual exception message
//                .instance(request.getDescription(false).replace("uri=", ""))
//                .build();
//
//        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
//    }
//
//    // -------------------------------------------------------
//    // Handle InvalidUrlException
//    // -------------------------------------------------------
//    @ExceptionHandler(InvalidUrlException.class)
//    public ResponseEntity<ApiError> handleInvalidUrl(InvalidUrlException ex, WebRequest request) {
//        ApiError error = ApiError.builder()
//                .type("https://example.com/probs/invalid-url")
//                .title("Invalid URL")
//                .status(HttpStatus.BAD_REQUEST.value())
//                .detail(ex.getMessage()) // Use actual exception message
//                .instance(request.getDescription(false).replace("uri=", ""))
//                .build();
//
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
//    // -------------------------------------------------------
//    // Handle ShortCodeTooLongException
//    // -------------------------------------------------------
//    @ExceptionHandler(ShortCodeTooLongException.class)
//    public ResponseEntity<ApiError> handleShortCodeTooLong(ShortCodeTooLongException ex, WebRequest request) {
//        ApiError error = ApiError.builder()
//                .type("https://example.com/probs/shortcode-too-long")
//                .title("Custom Short Code Too Long")
//                .status(HttpStatus.BAD_REQUEST.value())
//                .detail(ex.getMessage()) // Use actual exception message
//                .instance(request.getDescription(false).replace("uri=", ""))
//                .build();
//
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
//    // -------------------------------------------------------
//    // Handle Validation Errors (from @Valid annotations)
//    // -------------------------------------------------------
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
//        String errorDetails = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                .collect(Collectors.joining(", "));
//
//        ApiError error = ApiError.builder()
//                .type("https://example.com/probs/validation-error")
//                .title("Validation Failed")
//                .status(HttpStatus.BAD_REQUEST.value())
//                .detail(errorDetails)
//                .instance(request.getDescription(false).replace("uri=", ""))
//                .build();
//
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
//    // -------------------------------------------------------
//    // Handle General RuntimeException
//    // -------------------------------------------------------
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex, WebRequest request) {
//        ApiError error = ApiError.builder()
//                .type("https://example.com/probs/runtime-error")
//                .title("Runtime Error")
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .detail(ex.getMessage()) // Use actual exception message
//                .instance(request.getDescription(false).replace("uri=", ""))
//                .build();
//
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    // -------------------------------------------------------
//    // Handle All Other Exceptions
//    // -------------------------------------------------------
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiError> handleGeneralError(Exception ex, WebRequest request) {
//        ApiError error = ApiError.builder()
//                .type("https://example.com/probs/internal-error")
//                .title("Internal Server Error")
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .detail("An unexpected error occurred: " + ex.getMessage())
//                .instance(request.getDescription(false).replace("uri=", ""))
//                .build();
//
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}


package com.example.BigShort.url_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for logging and formatting error responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUrlException(
            InvalidUrlException ex, WebRequest request) {

        log.warn("Invalid URL exception - message={}, path={}",
                ex.getMessage(),
                request.getDescription(false));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid URL");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleShortCodeAlreadyExistsException(
            ShortCodeAlreadyExistsException ex, WebRequest request) {

        log.warn("Short code already exists - message={}, path={}",
                ex.getMessage(),
                request.getDescription(false));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Short Code Already Exists");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("Runtime exception - message={}, path={}, exception={}",
                ex.getMessage(),
                request.getDescription(false),
                ex.getClass().getSimpleName(),
                ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Validation exception - fieldCount={}, path={}",
                ex.getBindingResult().getFieldErrorCount(),
                request.getDescription(false));

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
            log.debug("Validation error - field={}, message={}",
                    error.getField(),
                    error.getDefaultMessage());
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("errors", errors);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected exception - message={}, path={}, exception={}",
                ex.getMessage(),
                request.getDescription(false),
                ex.getClass().getSimpleName(),
                ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}