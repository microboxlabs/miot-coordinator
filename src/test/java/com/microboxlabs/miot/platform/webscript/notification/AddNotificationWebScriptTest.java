package com.microboxlabs.miot.platform.webscript.notification;

import com.microboxlabs.miot.feature.notification.api.NotificationService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AddNotificationWebScriptTest {

    private AddNotificationWebScript webScript;
    private NotificationService notificationService;
    private WebScriptResponse response;
    private StringWriter writer;

    @Before
    public void setUp() throws Exception {
        notificationService = mock(NotificationService.class);

        webScript = new AddNotificationWebScript() {
            @Override
            protected String getAuthenticatedUser(WebScriptRequest req) {
                return "testuser";
            }
        };

        setField(webScript, "notificationService", notificationService);

        response = mock(WebScriptResponse.class);
        writer = new StringWriter();
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testCreatesNotification() throws Exception {
        WebScriptRequest request = mock(WebScriptRequest.class, RETURNS_DEEP_STUBS);
        when(request.getContent().getContent())
                .thenReturn("{\"message\":\"hello\"}");

        NodeRef created = new NodeRef(
                StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "new-notif");
        when(notificationService.createNotification(
                eq("testuser"), eq("hello"), eq("INFO"), anyString()))
                .thenReturn(created);

        webScript.execute(request, response);

        verify(response).setStatus(201);
        verify(response).setContentType("application/json");
        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.getBoolean("success"));
        assertEquals("hello", result.getString("message"));
    }

    private static void setField(Object target, String fieldName, Object value)
            throws Exception {
        java.lang.reflect.Field field =
                target.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
