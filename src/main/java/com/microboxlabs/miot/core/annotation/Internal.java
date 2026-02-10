package com.microboxlabs.miot.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class or method as internal implementation detail.
 *
 * <p>Types annotated with {@code @Internal} are {@code public} only because Spring
 * requires visibility for proxying and component scanning. They are <b>not</b> part
 * of the module's public API and may change without notice.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Internal {
}
