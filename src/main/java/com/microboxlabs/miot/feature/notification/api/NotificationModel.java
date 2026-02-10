package com.microboxlabs.miot.feature.notification.api;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * QName constants and JSON mapping for the {@code notifications:} content model.
 */
public final class NotificationModel {

    public static final String NAMESPACE =
            "http://www.microboxlabs.com/model/notifications/1.0";

    public static final QName TYPE_NOTIFICATION = QName.createQName(NAMESPACE, "notification");

    public static final QName PROP_ID        = QName.createQName(NAMESPACE, "id");
    public static final QName PROP_USER_ID   = QName.createQName(NAMESPACE, "userId");
    public static final QName PROP_MESSAGE   = QName.createQName(NAMESPACE, "message");
    public static final QName PROP_TIMESTAMP = QName.createQName(NAMESPACE, "timestamp");
    public static final QName PROP_IS_READ   = QName.createQName(NAMESPACE, "isRead");
    public static final QName PROP_TYPE      = QName.createQName(NAMESPACE, "type");
    public static final QName PROP_URL       = QName.createQName(NAMESPACE, "url");

    private NotificationModel() {
    }

    /**
     * Maps a list of notification node references to a JSON array.
     *
     * @param notifications the notification node refs
     * @param nodeService   the node service to read properties from
     * @return a JSON array with one object per notification
     */
    public static JSONArray mapNotifications(List<NodeRef> notifications,
                                             NodeService nodeService) {
        JSONArray array = new JSONArray();
        for (NodeRef nodeRef : notifications) {
            if (nodeRef != null) {
                JSONObject obj = new JSONObject();
                obj.put("id", nodeService.getProperty(nodeRef, PROP_ID));
                obj.put("user_id", nodeService.getProperty(nodeRef, PROP_USER_ID));
                obj.put("message", nodeService.getProperty(nodeRef, PROP_MESSAGE));
                obj.put("timestamp", nodeService.getProperty(nodeRef, PROP_TIMESTAMP));
                obj.put("is_read", nodeService.getProperty(nodeRef, PROP_IS_READ));
                obj.put("type", nodeService.getProperty(nodeRef, PROP_TYPE));
                obj.put("url", nodeService.getProperty(nodeRef, PROP_URL));
                array.put(obj);
            }
        }
        return array;
    }
}
