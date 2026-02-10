package com.microboxlabs.miot.platform.webscript.health;

import com.microboxlabs.miot.platform.webscript.AbstractMiotWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check web script that reports module status.
 *
 * <p>URL: {@code GET /com/microboxlabs/miot/health}</p>
 */
public class HealthCheckWebScript extends AbstractMiotWebScript {

    @Override
    protected Map<String, Object> executeMiot(
            WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();
        model.put("status", "UP");
        model.put("module", "miot-coordinator");
        return model;
    }
}
