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
 * Marks a notification as read.
 *
 * <p>URL: {@code PUT /miot/v1/notifications/mark-as-read?id=\{id\}}</p>
 */
@Component("webscript.miot.v1.notifications.mark-as-read.put")
public class MarkAsReadNotificationWebScript extends AbstractJsonWebScript {

    @Autowired
    @Qualifier("notificationService")
    private NotificationService notificationService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res)
            throws IOException {
        try {
            String currentUser = getAuthenticatedUser(req);
            String id = req.getParameter("id");

            notificationService.updateNotification(id, currentUser, null, true);

            logger.debug("Marked notification {} as read for user: {}",
                    id, currentUser);

            JSONObject response = new JSONObject();
            response.put("success", true);
            writeJsonResponse(res, 200, response);
        } catch (Exception e) {
            logger.error("Error marking notification as read", e);
            writeErrorResponse(res, 500, e.getMessage());
        }
    }
}
