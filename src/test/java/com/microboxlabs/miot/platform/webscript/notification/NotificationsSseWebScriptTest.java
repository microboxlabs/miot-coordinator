package com.microboxlabs.miot.platform.webscript.notification;

import com.microboxlabs.miot.feature.notification.api.NotificationService;
import org.alfresco.service.cmr.repository.NodeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.StringWriter;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NotificationsSseWebScriptTest {

    private NotificationsSseWebScript webScript;
    private NotificationService notificationService;
    private NodeService nodeService;
    private WebScriptResponse response;
    private StringWriter writer;

    @Before
    public void setUp() throws Exception {
        notificationService = mock(NotificationService.class);
        nodeService = mock(NodeService.class);

        webScript = new NotificationsSseWebScript() {
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
    public void testReturnsSseFormat() throws Exception {
        WebScriptRequest request = mock(WebScriptRequest.class);
        when(notificationService.getNotificationsForUser("testuser"))
                .thenReturn(Collections.emptyList());

        webScript.execute(request, response);

        verify(response).setContentType("text/event-stream");
        verify(response).setHeader("Cache-Control", "no-cache");
        verify(response).setHeader("Connection", "keep-alive");

        String output = writer.toString();
        assertTrue("Should start with 'data: '", output.startsWith("data: "));
        assertTrue("Should end with double newline", output.endsWith("\n\n"));
    }

    @Test
    public void testReturnsUnauthorizedWhenNoUser() throws Exception {
        NotificationsSseWebScript noUserWebScript = new NotificationsSseWebScript() {
            @Override
            protected String getAuthenticatedUser(WebScriptRequest req) {
                return null;
            }
        };

        setField(noUserWebScript, "notificationService", notificationService);
        setField(noUserWebScript, "nodeService", nodeService);

        WebScriptRequest request = mock(WebScriptRequest.class);
        WebScriptResponse noUserResponse = mock(WebScriptResponse.class);
        StringWriter noUserWriter = new StringWriter();
        when(noUserResponse.getWriter()).thenReturn(noUserWriter);

        noUserWebScript.execute(request, noUserResponse);

        verify(noUserResponse).setStatus(401);
        assertEquals("Unauthorized", noUserWriter.toString());
    }

    private static void setField(Object target, String fieldName, Object value)
            throws Exception {
        java.lang.reflect.Field field =
                target.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
