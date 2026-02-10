package com.microboxlabs.miot.core.exception;

/**
 * Thrown when a runtime operation within the MIOT module fails.
 */
public class MiotOperationException extends MiotException {

    public MiotOperationException(String message) {
        super(message);
    }

    public MiotOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
