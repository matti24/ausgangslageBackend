package com.ausganslage.ausgangslageBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Zentrale Exception-Behandlung für alle Custom Exceptions.
 * Demonstriert:
 * - Differenzierte Behandlung verschiedener Exception-Typen
 * - Einheitliches Error-Response Format
 * - HTTP-Statuskodes basierend auf Exception-Typ
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Behandelt ResourceNotFoundException (404).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    /**
     * Behandelt DuplicateDataException (409).
     */
    @ExceptionHandler(DuplicateDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleDuplicateData(DuplicateDataException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    /**
     * Behandelt InvalidOperationException (400).
     */
    @ExceptionHandler(InvalidOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleInvalidOperation(InvalidOperationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    /**
     * Behandelt AuthenticationException (401).
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, Object>> handleAuthenticationError(AuthenticationException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
    }

    /**
     * Fallback für alle anderen AusgangslageException-Subklassen (500).
     */
    @ExceptionHandler(AusgangslageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleAusgangslageException(AusgangslageException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
    }

    /**
     * Fallback für unerwartete Exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Ein unerwarteter Fehler ist aufgetreten.";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, ex);
    }

    /**
     * Hilfsmethode zum Bauen einheitlicher Error-Responses.
     * Demonstriert strukturierte Fehlerausgabe mit Timestamp, Status und Message.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, Exception ex) {
        
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("timestamp", LocalDateTime.now());
        errorMap.put("status", status.value());
        errorMap.put("error", status.getReasonPhrase());
        errorMap.put("message", message);
        
        if (ex.getCause() != null) {
            errorMap.put("cause", ex.getCause().getMessage());
        }

        return new ResponseEntity<>(errorMap, status);
    }
}
