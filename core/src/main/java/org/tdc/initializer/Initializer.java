package org.tdc.initializer;

import org.tdc.config.system.InitializerConfig;

/**
 * Interface defining Initializer functionality. 
 * Initializers can be used for performing custom system-level tasks during system startup.
 */
public interface Initializer {
	InitializerConfig getConfig();
	void process();
}
