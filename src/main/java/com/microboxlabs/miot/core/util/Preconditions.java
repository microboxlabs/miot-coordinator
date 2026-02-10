package com.microboxlabs.miot.core.util;

/**
 * Argument validation helpers.
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Ensures that a reference is not {@code null}.
     *
     * @param reference    the reference to check
     * @param parameterName the name of the parameter for the error message
     * @param <T>          the reference type
     * @return the non-null reference
     * @throws IllegalArgumentException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, String parameterName) {
        if (reference == null) {
            throw new IllegalArgumentException(parameterName + " must not be null");
        }
        return reference;
    }

    /**
     * Ensures that a string is not {@code null} or blank.
     *
     * @param value         the string to check
     * @param parameterName the name of the parameter for the error message
     * @return the non-blank string
     * @throws IllegalArgumentException if {@code value} is null or blank
     */
    public static String checkNotBlank(String value, String parameterName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(parameterName + " must not be blank");
        }
        return value;
    }

    /**
     * Ensures that a condition is true.
     *
     * @param expression the boolean expression to check
     * @param message    the error message if the check fails
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
}
