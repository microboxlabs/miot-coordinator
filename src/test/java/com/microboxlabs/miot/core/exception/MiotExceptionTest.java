package com.microboxlabs.miot.core.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MiotExceptionTest {

    @Test
    public void testMessageOnly() {
        MiotException ex = new MiotException("something failed");
        assertEquals("something failed", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void testMessageAndCause() {
        Throwable cause = new IllegalStateException("root cause");
        MiotException ex = new MiotException("wrapper", cause);
        assertEquals("wrapper", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void testIsRuntimeException() {
        assertTrue(new MiotException("test") instanceof RuntimeException);
    }

    @Test
    public void testConfigurationExceptionHierarchy() {
        MiotConfigurationException ex = new MiotConfigurationException("bad config");
        assertTrue(ex instanceof MiotException);
        assertEquals("bad config", ex.getMessage());
    }

    @Test
    public void testConfigurationExceptionWithCause() {
        Throwable cause = new RuntimeException("root");
        MiotConfigurationException ex = new MiotConfigurationException("bad config", cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    public void testOperationExceptionHierarchy() {
        MiotOperationException ex = new MiotOperationException("op failed");
        assertTrue(ex instanceof MiotException);
        assertEquals("op failed", ex.getMessage());
    }

    @Test
    public void testOperationExceptionWithCause() {
        Throwable cause = new RuntimeException("root");
        MiotOperationException ex = new MiotOperationException("op failed", cause);
        assertSame(cause, ex.getCause());
    }
}
