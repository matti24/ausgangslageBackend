package com.ausganslage.ausgangslageBackend.exception;

/**
 * Wird geworfen bei ungültigem/dupliziertem Datensatz (z.B. E-Mail bereits registriert).
 */
public class DuplicateDataException extends AusgangslageException {
    
    public DuplicateDataException(String message) {
        super(message, 409);
    }

    public DuplicateDataException(String fieldName, String value) {
        super(fieldName + " '" + value + "' already exists", 409);
    }
}
