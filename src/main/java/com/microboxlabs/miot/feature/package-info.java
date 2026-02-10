/**
 * Self-contained feature modules for the MIOT Coordinator.
 *
 * <p>Each sub-package is an independent feature that follows the convention:</p>
 * <ul>
 *   <li>{@code api/} — Public interfaces, value objects, exceptions (the contract)</li>
 *   <li>{@code internal/} — {@code @Service}/{@code @Component} implementations (the details)</li>
 *   <li>{@code config/} — Optional {@code @Configuration} classes</li>
 * </ul>
 *
 * <p>Dependency rule: features depend only on {@code core}. They must not depend
 * on {@code integration} or {@code platform}.</p>
 */
package com.microboxlabs.miot.feature;
