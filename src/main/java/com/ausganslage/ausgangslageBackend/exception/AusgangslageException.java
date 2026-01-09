package com.ausganslage.ausgangslageBackend.exception;

/**
 * Basis-Exception für alle Anwendungs-spezifischen Fehler.
 * Alle anderen Custom Exceptions erben von dieser Klasse.
 */
public class AusgangslageException extends Exception {
    
    private int httpStatusCode;

    public AusgangslageException(String message) {
        super(message);
        this.httpStatusCode = 500;
    }

    public AusgangslageException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = 500;
    }

    public AusgangslageException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public AusgangslageException(String message, int httpStatusCode, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
