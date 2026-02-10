package com.microboxlabs.miot.platform.webscript;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractJsonWebScriptTest {

    private TestJsonWebScript webScript;
    private WebScriptResponse response;
    private StringWriter writer;

    @Before
    public void setUp() throws IOException {
        webScript = new TestJsonWebScript();
        response = mock(WebScriptResponse.class);
        writer = new StringWriter();
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testWriteJsonResponse() throws IOException {
        JSONObject json = new JSONObject();
        json.put("key", "value");

        webScript.writeJsonResponse(response, 200, json);

        verify(response).setStatus(200);
        verify(response).setContentType("application/json");
        JSONObject result = new JSONObject(writer.toString());
        assertEquals("value", result.getString("key"));
    }

    @Test
    public void testWriteErrorResponse() throws IOException {
        webScript.writeErrorResponse(response, 404, "not found");

        verify(response).setStatus(404);
        verify(response).setContentType("application/json");
        JSONObject result = new JSONObject(writer.toString());
        assertEquals("not found", result.getString("error"));
    }

    @Test
    public void testParseRequestBody() throws IOException {
        WebScriptRequest request = mock(WebScriptRequest.class, RETURNS_DEEP_STUBS);
        when(request.getContent().getContent()).thenReturn("{\"foo\":\"bar\"}");

        JSONObject result = webScript.parseRequestBody(request);

        assertEquals("bar", result.getString("foo"));
    }

    /** Concrete subclass for testing the abstract base. */
    private static class TestJsonWebScript extends AbstractJsonWebScript {
        @Override
        public void execute(WebScriptRequest req, WebScriptResponse res)
                throws IOException {
            // no-op for testing
        }
    }
}
