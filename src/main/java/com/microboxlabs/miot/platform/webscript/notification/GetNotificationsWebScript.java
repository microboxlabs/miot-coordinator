package com.microboxlabs.miot.platform.webscript.notification;

import com.microboxlabs.miot.feature.notification.api.NotificationModel;
import com.microboxlabs.miot.feature.notification.api.NotificationService;
import com.microboxlabs.miot.platform.webscript.AbstractJsonWebScript;

import java.io.IOException;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

/**
 * Returns notifications for the authenticated user.
 *
 * <p>URL: {@code GET /miot/v1/notifications}</p>
 */
@Component("webscript.miot.v1.notifications.notifications.get")
public class GetNotificationsWebScript extends AbstractJsonWebScript {

    @Autowired
    @Qualifier("miotNotificationService")
    private NotificationService notificationService;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res)
            throws IOException {
        try {
            String currentUser = getAuthenticatedUser(req);
            logger.debug("Getting notifications for user: {}", currentUser);

            List<NodeRef> notifications =
                    notificationService.getNotificationsForUser(currentUser);
            JSONObject response = new JSONObject();
            response.put("notifications",
                    NotificationModel.mapNotifications(notifications, nodeService));

            writeJsonResponse(res, 200, response);
        } catch (Exception e) {
            logger.error("Error getting notifications", e);
            writeErrorResponse(res, 500, e.getMessage());
        }
    }
}
