package com.microboxlabs.miot.platform.bootstrap;

import org.alfresco.repo.module.AbstractModuleComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Module bootstrap component executed once when the MIOT module is installed.
 *
 * <p>Registered in {@code bootstrap-context.xml} as a child of
 * {@code module.baseComponent}.</p>
 */
public class MiotModuleComponent extends AbstractModuleComponent {

    private static final Log logger = LogFactory.getLog(MiotModuleComponent.class);

    @Override
    protected void executeInternal() throws Throwable {
        logger.info("MIOT Coordinator module initialized (v" + getModuleId() + ")");
    }
}
