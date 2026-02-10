package com.microboxlabs.miot.feature.notification.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.json.JSONArray;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NotificationModelTest {

    @Test
    public void testNamespaceConstant() {
        assertEquals("http://www.microboxlabs.com/model/notifications/1.0",
                NotificationModel.NAMESPACE);
    }

    @Test
    public void testTypeNotificationQName() {
        assertEquals("notification",
                NotificationModel.TYPE_NOTIFICATION.getLocalName());
        assertEquals(NotificationModel.NAMESPACE,
                NotificationModel.TYPE_NOTIFICATION.getNamespaceURI());
    }

    @Test
    public void testPropertyQNames() {
        assertEquals("id", NotificationModel.PROP_ID.getLocalName());
        assertEquals("userId", NotificationModel.PROP_USER_ID.getLocalName());
        assertEquals("message", NotificationModel.PROP_MESSAGE.getLocalName());
        assertEquals("timestamp", NotificationModel.PROP_TIMESTAMP.getLocalName());
        assertEquals("isRead", NotificationModel.PROP_IS_READ.getLocalName());
        assertEquals("type", NotificationModel.PROP_TYPE.getLocalName());
        assertEquals("url", NotificationModel.PROP_URL.getLocalName());
    }

    @Test
    public void testMapNotificationsEmptyList() {
        NodeService nodeService = mock(NodeService.class);
        JSONArray result = NotificationModel.mapNotifications(
                Collections.emptyList(), nodeService);
        assertEquals(0, result.length());
    }

    @Test
    public void testMapNotificationsSkipsNull() {
        NodeService nodeService = mock(NodeService.class);
        JSONArray result = NotificationModel.mapNotifications(
                Arrays.asList((NodeRef) null, null), nodeService);
        assertEquals(0, result.length());
    }

    @Test
    public void testMapNotificationsMapsProperties() {
        NodeService nodeService = mock(NodeService.class);
        NodeRef nodeRef = new NodeRef(
                StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "test-id");
        Date now = new Date();

        when(nodeService.getProperty(nodeRef, NotificationModel.PROP_ID))
                .thenReturn("n1");
        when(nodeService.getProperty(nodeRef, NotificationModel.PROP_USER_ID))
                .thenReturn("user1");
        when(nodeService.getProperty(nodeRef, NotificationModel.PROP_MESSAGE))
                .thenReturn("hello");
        when(nodeService.getProperty(nodeRef, NotificationModel.PROP_TIMESTAMP))
                .thenReturn(now);
        when(nodeService.getProperty(nodeRef, NotificationModel.PROP_IS_READ))
                .thenReturn(false);
        when(nodeService.getProperty(nodeRef, NotificationModel.PROP_TYPE))
                .thenReturn("INFO");
        when(nodeService.getProperty(nodeRef, NotificationModel.PROP_URL))
                .thenReturn("http://example.com");

        JSONArray result = NotificationModel.mapNotifications(
                Collections.singletonList(nodeRef), nodeService);

        assertEquals(1, result.length());
        assertEquals("n1", result.getJSONObject(0).get("id"));
        assertEquals("user1", result.getJSONObject(0).get("user_id"));
        assertEquals("hello", result.getJSONObject(0).get("message"));
        assertEquals(false, result.getJSONObject(0).get("is_read"));
        assertEquals("INFO", result.getJSONObject(0).get("type"));
        assertEquals("http://example.com", result.getJSONObject(0).get("url"));
    }
}
