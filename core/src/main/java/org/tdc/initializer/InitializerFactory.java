package org.tdc.initializer;

import org.tdc.config.system.InitializerConfig;

/**
 * Interface defining a factory for creating {@link Initializer} instances 
 * based on {@link InitializerConfig} parameters.
 */
public interface InitializerFactory {
	Initializer createInitializer(InitializerConfig config);
}
