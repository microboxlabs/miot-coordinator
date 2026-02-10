package com.microboxlabs.miot.platform.webscript.notification;

import com.microboxlabs.miot.feature.notification.api.NotificationService;
import com.microboxlabs.miot.platform.webscript.AbstractJsonWebScript;

import java.io.IOException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

/**
 * Creates a notification for the authenticated user.
 *
 * <p>URL: {@code POST /miot/v1/notifications}</p>
 */
@Component("webscript.miot.v1.notifications.notifications.post")
public class AddNotificationWebScript extends AbstractJsonWebScript {

    @Autowired
    @Qualifier("miotNotificationService")
    private NotificationService notificationService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res)
            throws IOException {
        try {
            String currentUser = getAuthenticatedUser(req);
            JSONObject body = parseRequestBody(req);
            String message = body.optString("message", "No message provided");

            notificationService.createNotification(
                    currentUser, message, "INFO",
                    "http://example.com/notification-icon.png");

            logger.debug("Notification added for user: {}", currentUser);

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("message", message);
            writeJsonResponse(res, 201, response);
        } catch (Exception e) {
            logger.error("Error adding notification", e);
            writeErrorResponse(res, 500, e.getMessage());
        }
    }
}
