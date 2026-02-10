package com.microboxlabs.miot.core.model;

import java.util.Optional;

/**
 * Generic success/failure wrapper for operation outcomes.
 *
 * @param <T> the type of the success value
 */
public final class OperationResult<T> {

    private final boolean success;
    private final T value;
    private final String errorMessage;

    private OperationResult(boolean success, T value, String errorMessage) {
        this.success = success;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static <T> OperationResult<T> success(T value) {
        return new OperationResult<>(true, value, null);
    }

    public static <T> OperationResult<T> failure(String errorMessage) {
        return new OperationResult<>(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
