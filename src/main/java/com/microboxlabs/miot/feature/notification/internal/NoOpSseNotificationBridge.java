package com.microboxlabs.miot.feature.notification.internal;

import com.microboxlabs.miot.core.annotation.Internal;
import com.microboxlabs.miot.feature.notification.api.SseNotificationBridge;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * No-op SSE bridge used when SSE infrastructure is not yet available.
 */
@Internal
@Component
public class NoOpSseNotificationBridge implements SseNotificationBridge {

    private static final Logger logger =
            LoggerFactory.getLogger(NoOpSseNotificationBridge.class);

    @Override
    public void pushNotificationEvent(String userId, Map<String, Object> payload) {
        logger.debug("SSE bridge not available; discarding event for user {}", userId);
    }
}
