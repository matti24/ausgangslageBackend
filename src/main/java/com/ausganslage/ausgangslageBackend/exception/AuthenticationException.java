package com.ausganslage.ausgangslageBackend.exception;

/**
 * Wird geworfen bei Authentifizierung/Autorisierungsfehlern.
 */
public class AuthenticationException extends AusgangslageException {
    
    public AuthenticationException(String message) {
        super(message, 401);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, 401, cause);
    }
}
