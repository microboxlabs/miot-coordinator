package com.microboxlabs.miot.platform.webscript;

import java.io.IOException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Base class for annotation-registered web scripts that write JSON directly.
 *
 * <p>Unlike {@link AbstractMiotWebScript} (which extends {@code DeclarativeWebScript}
 * and requires FreeMarker templates), this class extends {@link AbstractWebScript}
 * and writes JSON to the response output stream.</p>
 */
public abstract class AbstractJsonWebScript extends AbstractWebScript {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Writes a JSON object to the response with the given HTTP status.
     */
    protected void writeJsonResponse(WebScriptResponse res, int status,
                                     JSONObject json) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.getWriter().write(json.toString());
    }

    /**
     * Writes a JSON error response.
     */
    protected void writeErrorResponse(WebScriptResponse res, int status,
                                      String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", message);
        writeJsonResponse(res, status, error);
    }

    /**
     * Returns the fully authenticated user for the current request.
     */
    protected String getAuthenticatedUser(WebScriptRequest req) {
        return AuthenticationUtil.getFullyAuthenticatedUser();
    }

    /**
     * Parses the request body as a JSON object.
     */
    protected JSONObject parseRequestBody(WebScriptRequest req) throws IOException {
        String content = req.getContent().getContent();
        return new JSONObject(content);
    }
}
