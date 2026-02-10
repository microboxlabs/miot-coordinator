package com.microboxlabs.miot.feature.notification.api;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Service for managing user notifications stored as Alfresco content nodes.
 */
public interface NotificationService {

    /**
     * Creates a new notification for the given user.
     *
     * @param userId  the user identifier (e.g. email)
     * @param message the notification message
     * @param type    the notification type (e.g. INFO, WARNING, ERROR)
     * @param url     an optional URL associated with the notification
     * @return the node reference of the created notification
     */
    NodeRef createNotification(String userId, String message, String type, String url);

    /**
     * Updates an existing notification.
     *
     * @param notificationId the notification's unique ID
     * @param userId         the owner's user identifier
     * @param message        new message (may be {@code null} to keep the current one)
     * @param isRead         whether the notification should be marked as read
     */
    void updateNotification(String notificationId, String userId,
                            String message, boolean isRead);

    /**
     * Returns all notifications for the given user.
     *
     * @param userId the user identifier
     * @return list of notification node references (may be empty, never {@code null})
     */
    List<NodeRef> getNotificationsForUser(String userId);
}
