package com.microboxlabs.miot.platform.webscript.notification;

import com.microboxlabs.miot.feature.notification.api.NotificationService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MarkAsReadNotificationWebScriptTest {

    private MarkAsReadNotificationWebScript webScript;
    private NotificationService notificationService;
    private WebScriptResponse response;
    private StringWriter writer;

    @Before
    public void setUp() throws Exception {
        notificationService = mock(NotificationService.class);

        webScript = new MarkAsReadNotificationWebScript() {
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
    public void testMarksAsRead() throws Exception {
        WebScriptRequest request = mock(WebScriptRequest.class);
        when(request.getParameter("id")).thenReturn("notif-123");

        webScript.execute(request, response);

        verify(notificationService)
                .updateNotification("notif-123", "testuser", null, true);
        verify(response).setStatus(200);
        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.getBoolean("success"));
    }

    private static void setField(Object target, String fieldName, Object value)
            throws Exception {
        java.lang.reflect.Field field =
                target.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
