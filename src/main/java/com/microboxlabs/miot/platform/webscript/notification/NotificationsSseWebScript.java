package com.microboxlabs.miot.platform.webscript.notification;

import com.microboxlabs.miot.feature.notification.api.NotificationModel;
import com.microboxlabs.miot.feature.notification.api.NotificationService;
import com.microboxlabs.miot.platform.webscript.AbstractJsonWebScript;

import java.io.IOException;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

/**
 * Returns current notifications as a Server-Sent Events snapshot.
 *
 * <p>URL: {@code GET /miot/v1/notifications/sse}</p>
 */
@Component("webscript.miot.v1.notifications.sse.get")
public class NotificationsSseWebScript extends AbstractJsonWebScript {

    @Autowired
    @Qualifier("notificationService")
    private NotificationService notificationService;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res)
            throws IOException {
        String currentUser = getAuthenticatedUser(req);

        if (currentUser == null) {
            res.setStatus(401);
            res.getWriter().write("Unauthorized");
            return;
        }

        res.setContentType("text/event-stream");
        res.setHeader("Cache-Control", "no-cache");
        res.setHeader("Connection", "keep-alive");

        try {
            List<NodeRef> notifications =
                    notificationService.getNotificationsForUser(currentUser);
            JSONArray data =
                    NotificationModel.mapNotifications(notifications, nodeService);

            res.getWriter().write("data: " + data.toString() + "\n\n");
            res.getWriter().flush();
        } catch (Exception e) {
            logger.error("Error streaming notifications", e);
            res.getWriter().write("event: error\ndata: " + e.getMessage() + "\n\n");
            res.getWriter().flush();
        }
    }
}
