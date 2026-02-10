/**
 * User notification feature.
 *
 * <p>Manages notifications stored as Alfresco content nodes under each user's
 * home folder. Provides a service API for creating, updating, and listing
 * notifications, with an SSE bridge abstraction for real-time delivery.</p>
 *
 * <ul>
 *   <li>{@code api/} — Public contracts: {@code NotificationService},
 *       {@code NotificationModel}, {@code SseNotificationBridge}</li>
 *   <li>{@code internal/} — Implementations: {@code NotificationServiceImpl},
 *       {@code NoOpSseNotificationBridge}</li>
 * </ul>
 */
package com.microboxlabs.miot.feature.notification;
