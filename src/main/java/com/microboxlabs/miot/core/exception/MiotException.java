package com.microboxlabs.miot.core.exception;

/**
 * Base unchecked exception for all MIOT module errors.
 *
 * <p>All custom exceptions in the MIOT module should extend this class
 * to allow callers to catch the full hierarchy with a single type.</p>
 */
public class MiotException extends RuntimeException {

    public MiotException(String message) {
        super(message);
    }

    public MiotException(String message, Throwable cause) {
        super(message, cause);
    }
}
