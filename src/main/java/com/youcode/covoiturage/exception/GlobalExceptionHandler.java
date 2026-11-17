package com.youcode.covoiturage.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "status", 404,
                        "error", "Not Found",
                        "message", exception.getMessage(),
                        "path", request.getRequestURI(),
                        "timestamp", Instant.now().toString()
                )
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity handleBusinessRule(BusinessRuleException exception, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "status", 409,
                        "error", "Conflict",
                        "message", exception.getMessage(),
                        "path", request.getRequestURI(),
                        "timestamp", Instant.now().toString()
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleDuplicate(DataIntegrityViolationException exception, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "status", 409,
                        "error", "Conflict",
                        "message", "Une ressource avec cette valeur unique existe déjà.",
                        "path", request.getRequestURI(),
                        "timestamp", Instant.now().toString()
                )
        );
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity handleForbidden(ForbiddenActionException exception, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "status", 403,
                        "error", "Forbidden",
                        "message", exception.getMessage(),
                        "path", request.getRequestURI(),
                        "timestamp", Instant.now().toString()
                )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequest(BadRequestException exception, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "status", 400,
                        "error", "Bad Request",
                        "message", exception.getMessage(),
                        "path", request.getRequestURI(),
                        "timestamp", Instant.now().toString()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            assert fieldError.getDefaultMessage() != null;
            fieldErrors.merge(fieldError.getField(), fieldError.getDefaultMessage(), (first, next) -> first + ", " + next);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", "Validation failed");
        body.put("path", request.getRequestURI());
        body.put("errors", fieldErrors);
        body.put("timestamp", Instant.now().toString());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleGeneric(Exception exception, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", "Une erreur interne est survenue.",
                        "path", request.getRequestURI(),
                        "timestamp", Instant.now().toString()
                )
        );
    }
}