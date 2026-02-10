package com.microboxlabs.miot.platform.webscript.notification;

import com.microboxlabs.miot.feature.notification.api.NotificationService;
import org.alfresco.service.cmr.repository.NodeService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.StringWriter;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GetNotificationsWebScriptTest {

    private GetNotificationsWebScript webScript;
    private NotificationService notificationService;
    private NodeService nodeService;
    private WebScriptResponse response;
    private StringWriter writer;

    @Before
    public void setUp() throws Exception {
        notificationService = mock(NotificationService.class);
        nodeService = mock(NodeService.class);

        webScript = new GetNotificationsWebScript() {
            @Override
            protected String getAuthenticatedUser(WebScriptRequest req) {
                return "testuser";
            }
        };

        setField(webScript, "notificationService", notificationService);
        setField(webScript, "nodeService", nodeService);

        response = mock(WebScriptResponse.class);
        writer = new StringWriter();
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testReturnsEmptyNotifications() throws Exception {
        WebScriptRequest request = mock(WebScriptRequest.class);
        when(notificationService.getNotificationsForUser("testuser"))
                .thenReturn(Collections.emptyList());

        webScript.execute(request, response);

        verify(response).setStatus(200);
        verify(response).setContentType("application/json");
        JSONObject result = new JSONObject(writer.toString());
        assertEquals(0, result.getJSONArray("notifications").length());
    }

    private static void setField(Object target, String fieldName, Object value)
            throws Exception {
        java.lang.reflect.Field field =
                target.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
