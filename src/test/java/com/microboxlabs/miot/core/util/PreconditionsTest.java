package com.microboxlabs.miot.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class PreconditionsTest {

    @Test
    public void testCheckNotNullReturnsValue() {
        String value = "hello";
        assertSame(value, Preconditions.checkNotNull(value, "param"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckNotNullThrowsOnNull() {
        Preconditions.checkNotNull(null, "param");
    }

    @Test
    public void testCheckNotNullErrorMessage() {
        try {
            Preconditions.checkNotNull(null, "myParam");
        } catch (IllegalArgumentException e) {
            assertEquals("myParam must not be null", e.getMessage());
        }
    }

    @Test
    public void testCheckNotBlankReturnsValue() {
        assertEquals("hello", Preconditions.checkNotBlank("hello", "param"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckNotBlankThrowsOnNull() {
        Preconditions.checkNotBlank(null, "param");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckNotBlankThrowsOnEmpty() {
        Preconditions.checkNotBlank("", "param");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckNotBlankThrowsOnWhitespace() {
        Preconditions.checkNotBlank("   ", "param");
    }

    @Test
    public void testCheckNotBlankErrorMessage() {
        try {
            Preconditions.checkNotBlank("", "myParam");
        } catch (IllegalArgumentException e) {
            assertEquals("myParam must not be blank", e.getMessage());
        }
    }

    @Test
    public void testCheckArgumentPasses() {
        Preconditions.checkArgument(true, "should not fail");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckArgumentThrowsOnFalse() {
        Preconditions.checkArgument(false, "condition failed");
    }

    @Test
    public void testCheckArgumentErrorMessage() {
        try {
            Preconditions.checkArgument(false, "custom message");
        } catch (IllegalArgumentException e) {
            assertEquals("custom message", e.getMessage());
        }
    }
}
