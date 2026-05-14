package com.visiontechcrm.exception;

/** Thrown by services when a queried entity does not exist. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entity, Object id) {
        super(entity + " not found with id: " + id);
    }
}
