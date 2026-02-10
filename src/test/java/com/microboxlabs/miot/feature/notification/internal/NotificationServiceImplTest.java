package com.microboxlabs.miot.feature.notification.internal;

import com.microboxlabs.miot.feature.notification.api.NotificationModel;
import com.microboxlabs.miot.feature.notification.api.SseNotificationBridge;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NotificationServiceImplTest {

    private NotificationServiceImpl service;
    private ServiceRegistry serviceRegistry;
    private NodeService nodeService;
    private SearchService searchService;
    private SseNotificationBridge sseBridge;
    private ResultSet resultSet;

    private static final NodeRef USER_HOME =
            new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "user-home");
    private static final NodeRef NOTIFICATIONS_FOLDER =
            new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "notif-folder");
    private static final NodeRef NOTIFICATION_NODE =
            new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "notif-1");

    @Before
    public void setUp() throws Exception {
        serviceRegistry = mock(ServiceRegistry.class);
        nodeService = mock(NodeService.class);
        searchService = mock(SearchService.class);
        sseBridge = mock(SseNotificationBridge.class);
        resultSet = mock(ResultSet.class);

        when(serviceRegistry.getNodeService()).thenReturn(nodeService);
        when(serviceRegistry.getSearchService()).thenReturn(searchService);
        when(searchService.query(any(SearchParameters.class))).thenReturn(resultSet);
        when(resultSet.length()).thenReturn(1);
        when(resultSet.getNodeRef(0)).thenReturn(USER_HOME);

        service = new NotificationServiceImpl();

        var srField = NotificationServiceImpl.class.getDeclaredField("serviceRegistry");
        srField.setAccessible(true);
        srField.set(service, serviceRegistry);

        var sseField = NotificationServiceImpl.class
                .getDeclaredField("sseNotificationBridge");
        sseField.setAccessible(true);
        sseField.set(service, sseBridge);
    }

    @Test
    public void testCreateNotificationCreatesNode() {
        when(nodeService.getChildByName(
                USER_HOME, ContentModel.ASSOC_CONTAINS, "notifications"))
                .thenReturn(NOTIFICATIONS_FOLDER);

        ChildAssociationRef childAssoc = mock(ChildAssociationRef.class);
        when(childAssoc.getChildRef()).thenReturn(NOTIFICATION_NODE);
        when(nodeService.createNode(
                eq(NOTIFICATIONS_FOLDER), eq(ContentModel.ASSOC_CONTAINS),
                any(QName.class), eq(NotificationModel.TYPE_NOTIFICATION),
                any(Map.class)))
                .thenReturn(childAssoc);

        NodeRef result = service.createNotification(
                "user1", "test message", "INFO", "http://example.com");

        assertEquals(NOTIFICATION_NODE, result);
        verify(nodeService).createNode(
                eq(NOTIFICATIONS_FOLDER), eq(ContentModel.ASSOC_CONTAINS),
                any(QName.class), eq(NotificationModel.TYPE_NOTIFICATION),
                any(Map.class));
        verify(sseBridge).pushNotificationEvent(eq("user1"), any(Map.class));
    }

    @Test
    public void testCreateNotificationCreatesFolder() {
        when(nodeService.getChildByName(
                USER_HOME, ContentModel.ASSOC_CONTAINS, "notifications"))
                .thenReturn(null);

        ChildAssociationRef folderAssoc = mock(ChildAssociationRef.class);
        when(folderAssoc.getChildRef()).thenReturn(NOTIFICATIONS_FOLDER);
        when(nodeService.createNode(
                eq(USER_HOME), eq(ContentModel.ASSOC_CONTAINS),
                any(QName.class), eq(ContentModel.TYPE_FOLDER), any(Map.class)))
                .thenReturn(folderAssoc);

        ChildAssociationRef notifAssoc = mock(ChildAssociationRef.class);
        when(notifAssoc.getChildRef()).thenReturn(NOTIFICATION_NODE);
        when(nodeService.createNode(
                eq(NOTIFICATIONS_FOLDER), eq(ContentModel.ASSOC_CONTAINS),
                any(QName.class), eq(NotificationModel.TYPE_NOTIFICATION),
                any(Map.class)))
                .thenReturn(notifAssoc);

        NodeRef result = service.createNotification(
                "user1", "test", "INFO", "http://example.com");

        assertEquals(NOTIFICATION_NODE, result);
        verify(nodeService).createNode(
                eq(USER_HOME), eq(ContentModel.ASSOC_CONTAINS),
                any(QName.class), eq(ContentModel.TYPE_FOLDER), any(Map.class));
    }

    @Test
    public void testGetNotificationsForUserReturnsEmpty() {
        when(nodeService.getChildByName(
                USER_HOME, ContentModel.ASSOC_CONTAINS, "notifications"))
                .thenReturn(null);

        List<NodeRef> result = service.getNotificationsForUser("user1");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetNotificationsForUserReturnsList() {
        when(nodeService.getChildByName(
                USER_HOME, ContentModel.ASSOC_CONTAINS, "notifications"))
                .thenReturn(NOTIFICATIONS_FOLDER);

        ChildAssociationRef assoc = mock(ChildAssociationRef.class);
        when(assoc.getChildRef()).thenReturn(NOTIFICATION_NODE);
        when(nodeService.getChildAssocs(NOTIFICATIONS_FOLDER))
                .thenReturn(Collections.singletonList(assoc));
        when(nodeService.getType(NOTIFICATION_NODE))
                .thenReturn(NotificationModel.TYPE_NOTIFICATION);

        List<NodeRef> result = service.getNotificationsForUser("user1");

        assertEquals(1, result.size());
        assertEquals(NOTIFICATION_NODE, result.get(0));
    }

    @Test
    public void testUpdateNotificationMarksAsRead() {
        when(nodeService.getChildByName(
                USER_HOME, ContentModel.ASSOC_CONTAINS, "notifications"))
                .thenReturn(NOTIFICATIONS_FOLDER);

        ChildAssociationRef assoc = mock(ChildAssociationRef.class);
        when(assoc.getChildRef()).thenReturn(NOTIFICATION_NODE);
        when(nodeService.getChildAssocs(NOTIFICATIONS_FOLDER))
                .thenReturn(Collections.singletonList(assoc));
        when(nodeService.getType(NOTIFICATION_NODE))
                .thenReturn(NotificationModel.TYPE_NOTIFICATION);
        when(nodeService.getProperty(NOTIFICATION_NODE, NotificationModel.PROP_ID))
                .thenReturn("n1");

        service.updateNotification("n1", "user1", null, true);

        verify(nodeService).setProperty(
                NOTIFICATION_NODE, NotificationModel.PROP_IS_READ, true);
        verify(nodeService, never()).setProperty(
                eq(NOTIFICATION_NODE), eq(NotificationModel.PROP_MESSAGE), any());
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateNotificationThrowsWhenNotFound() {
        when(nodeService.getChildByName(
                USER_HOME, ContentModel.ASSOC_CONTAINS, "notifications"))
                .thenReturn(NOTIFICATIONS_FOLDER);
        when(nodeService.getChildAssocs(NOTIFICATIONS_FOLDER))
                .thenReturn(Collections.emptyList());

        service.updateNotification("nonexistent", "user1", null, true);
    }
}
