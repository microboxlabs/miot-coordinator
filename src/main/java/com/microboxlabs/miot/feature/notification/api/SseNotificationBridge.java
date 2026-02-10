package com.microboxlabs.miot.feature.notification.api;

import java.util.Map;

/**
 * Abstraction for pushing real-time notification events via SSE.
 *
 * <p>Implementations may forward events to an SSE infrastructure service.
 * A no-op implementation is provided when SSE infrastructure is not available.</p>
 */
public interface SseNotificationBridge {

    /**
     * Pushes a notification event to the given user.
     *
     * @param userId  the target user identifier
     * @param payload the event payload
     */
    void pushNotificationEvent(String userId, Map<String, Object> payload);
}
