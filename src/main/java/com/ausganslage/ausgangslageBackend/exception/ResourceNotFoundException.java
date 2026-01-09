package com.ausganslage.ausgangslageBackend.exception;

/**
 * Wird geworfen, wenn eine angeforderte Ressource nicht gefunden wird.
 */
public class ResourceNotFoundException extends AusgangslageException {
    
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with ID " + id + " not found", 404);
    }
}
