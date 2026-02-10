package com.microboxlabs.miot.platform.webscript.health;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HealthCheckWebScriptTest {

    @Test
    public void testReturnsUpStatus() {
        WebScriptRequest req = Mockito.mock(WebScriptRequest.class);
        Status status = Mockito.mock(Status.class);
        Cache cache = Mockito.mock(Cache.class);

        HealthCheckWebScript webScript = new HealthCheckWebScript();
        Map<String, Object> model = webScript.executeMiot(req, status, cache);

        assertNotNull("Model should not be null", model);
        assertEquals("UP", model.get("status"));
        assertEquals("miot-coordinator", model.get("module"));
    }
}
