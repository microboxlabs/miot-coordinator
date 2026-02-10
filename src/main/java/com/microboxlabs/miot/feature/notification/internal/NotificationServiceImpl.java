package com.microboxlabs.miot.feature.notification.internal;

import com.microboxlabs.miot.core.annotation.Internal;
import com.microboxlabs.miot.feature.notification.api.NotificationModel;
import com.microboxlabs.miot.feature.notification.api.NotificationService;
import com.microboxlabs.miot.feature.notification.api.SseNotificationBridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Stores notification nodes under {@code <user-home>/notifications/}.
 */
@Internal
@Service("miotNotificationService")
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    @Qualifier("ServiceRegistry")
    private ServiceRegistry serviceRegistry;

    @Autowired
    private SseNotificationBridge sseNotificationBridge;

    @Override
    public NodeRef createNotification(String userId, String message,
                                      String type, String url) {
        NodeRef notificationsFolder = ensureNotificationsFolder(userId);
        if (notificationsFolder == null) {
            throw new RuntimeException(
                    "Could not find or create notifications folder for user: " + userId);
        }

        String notificationId = UUID.randomUUID().toString();
        NodeRef newNotification = createNotificationNode(
                notificationsFolder, notificationId, userId, message, type, url);
        logger.info("Notification created for user {}: {}", userId, newNotification);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", notificationId);
        payload.put("message", message);
        payload.put("type", type);
        sseNotificationBridge.pushNotificationEvent(userId, payload);

        return newNotification;
    }

    @Override
    public void updateNotification(String notificationId, String userId,
                                   String message, boolean isRead) {
        NodeService nodeService = serviceRegistry.getNodeService();
        NodeRef userHome = findUserHome(userId);

        NodeRef notificationsFolder = nodeService.getChildByName(
                userHome, ContentModel.ASSOC_CONTAINS, "notifications");
        if (notificationsFolder == null) {
            throw new RuntimeException(
                    "User " + userId + " does not have a notifications folder.");
        }

        List<ChildAssociationRef> childAssocs =
                nodeService.getChildAssocs(notificationsFolder);
        NodeRef found = null;
        for (ChildAssociationRef assoc : childAssocs) {
            NodeRef child = assoc.getChildRef();
            if (NotificationModel.TYPE_NOTIFICATION.equals(nodeService.getType(child))) {
                Object idProp = nodeService.getProperty(child, NotificationModel.PROP_ID);
                if (idProp != null && idProp.equals(notificationId)) {
                    found = child;
                    break;
                }
            }
        }

        if (found == null) {
            throw new RuntimeException(
                    "Notification with ID " + notificationId
                            + " not found for user " + userId);
        }

        if (message != null && !message.isEmpty()) {
            nodeService.setProperty(found, NotificationModel.PROP_MESSAGE, message);
        }
        nodeService.setProperty(found, NotificationModel.PROP_IS_READ, isRead);
        logger.info("Notification updated: {}", found);
    }

    @Override
    public List<NodeRef> getNotificationsForUser(String userId) {
        NodeService nodeService = serviceRegistry.getNodeService();
        NodeRef userHome = findUserHome(userId);

        NodeRef notificationsFolder = nodeService.getChildByName(
                userHome, ContentModel.ASSOC_CONTAINS, "notifications");
        if (notificationsFolder == null) {
            return Collections.emptyList();
        }

        List<ChildAssociationRef> childAssocs =
                nodeService.getChildAssocs(notificationsFolder);
        List<NodeRef> result = new ArrayList<>();
        for (ChildAssociationRef assoc : childAssocs) {
            NodeRef child = assoc.getChildRef();
            if (NotificationModel.TYPE_NOTIFICATION.equals(
                    nodeService.getType(child))) {
                result.add(child);
            }
        }
        return result;
    }

    private NodeRef ensureNotificationsFolder(String userName) {
        NodeService nodeService = serviceRegistry.getNodeService();
        NodeRef userHome = findUserHome(userName);

        NodeRef notificationsFolder = nodeService.getChildByName(
                userHome, ContentModel.ASSOC_CONTAINS, "notifications");
        if (notificationsFolder != null) {
            return notificationsFolder;
        }

        Map<QName, Serializable> props = new HashMap<>();
        props.put(ContentModel.PROP_NAME, "notifications");

        ChildAssociationRef assoc = nodeService.createNode(
                userHome,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(
                        NamespaceService.CONTENT_MODEL_1_0_URI, "notifications"),
                ContentModel.TYPE_FOLDER,
                props);

        logger.info("Notifications folder created for user: {}", userName);
        return assoc.getChildRef();
    }

    private NodeRef createNotificationNode(NodeRef folder, String id,
                                           String userId, String message,
                                           String type, String url) {
        NodeService nodeService = serviceRegistry.getNodeService();

        Map<QName, Serializable> properties = new HashMap<>();
        properties.put(ContentModel.PROP_NAME, "notification-" + id + ".txt");
        properties.put(NotificationModel.PROP_ID, id);
        properties.put(NotificationModel.PROP_USER_ID, userId);
        properties.put(NotificationModel.PROP_MESSAGE, message);
        properties.put(NotificationModel.PROP_TIMESTAMP, new Date());
        properties.put(NotificationModel.PROP_IS_READ, false);
        properties.put(NotificationModel.PROP_TYPE, type);
        properties.put(NotificationModel.PROP_URL, url);

        ChildAssociationRef assoc = nodeService.createNode(
                folder,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(
                        NamespaceService.CONTENT_MODEL_1_0_URI,
                        "notification-" + id),
                NotificationModel.TYPE_NOTIFICATION,
                properties);

        return assoc.getChildRef();
    }

    private NodeRef findUserHome(String userName) {
        SearchService searchService = serviceRegistry.getSearchService();
        String encodedName = userName.replace("@", "_x0040_");
        String xpath = "/app:company_home/app:user_homes/cm:" + encodedName;

        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_XPATH);
        sp.setQuery(xpath);

        ResultSet rs = searchService.query(sp);
        try {
            if (rs.length() == 0) {
                throw new RuntimeException(
                        "User home not found for user: " + userName);
            }
            return rs.getNodeRef(0);
        } finally {
            rs.close();
        }
    }
}
