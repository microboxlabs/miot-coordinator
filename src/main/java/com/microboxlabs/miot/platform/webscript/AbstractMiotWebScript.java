package com.microboxlabs.miot.platform.webscript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.Map;

/**
 * Base class for all MIOT web scripts.
 *
 * <p>Provides common error handling and logging infrastructure.
 * Subclasses implement {@link #executeMiot(WebScriptRequest, Status, Cache)}
 * instead of overriding {@code executeImpl} directly.</p>
 */
public abstract class AbstractMiotWebScript extends DeclarativeWebScript {

    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        try {
            return executeMiot(req, status, cache);
        } catch (Exception e) {
            logger.error("Web script error: " + getDescription().getId(), e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR);
            status.setMessage(e.getMessage());
            status.setRedirect(true);
            return null;
        }
    }

    /**
     * Execute the web script logic.
     *
     * @param req    the web script request
     * @param status the response status
     * @param cache  the response cache control
     * @return the model map to pass to the template
     */
    protected abstract Map<String, Object> executeMiot(
            WebScriptRequest req, Status status, Cache cache);
}
