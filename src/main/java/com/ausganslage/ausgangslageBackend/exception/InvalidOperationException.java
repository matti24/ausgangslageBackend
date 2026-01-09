package com.ausganslage.ausgangslageBackend.exception;

/**
 * Wird geworfen bei ungültigen oder widersprüchlichen Eingaben.
 */
public class InvalidOperationException extends AusgangslageException {
    
    public InvalidOperationException(String message) {
        super(message, 400);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, 400, cause);
    }
}
