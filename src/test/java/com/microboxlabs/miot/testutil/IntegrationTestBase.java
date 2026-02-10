package com.microboxlabs.miot.testutil;

import org.alfresco.rad.test.AbstractAlfrescoIT;

/**
 * Base class for MIOT integration tests.
 *
 * <p>Extends the Alfresco RAD test infrastructure and provides common
 * utilities for integration tests across the module.</p>
 */
public abstract class IntegrationTestBase extends AbstractAlfrescoIT {

    /**
     * Returns the base URL for MIOT web scripts.
     */
    protected String getMiotWebScriptBaseUrl() {
        return "/service/com/microboxlabs/miot";
    }
}
