package com.microboxlabs.miot.core.exception;

/**
 * Thrown when the MIOT module encounters invalid or missing configuration.
 */
public class MiotConfigurationException extends MiotException {

    public MiotConfigurationException(String message) {
        super(message);
    }

    public MiotConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
