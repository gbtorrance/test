package org.tdc.config.system;

import java.nio.file.Path;
import java.util.List;

/**
 * Defines getters for configuration items applicable to the system as a whole (i.e. global).
 */
public interface SystemConfig {
	Path getSystemConfigRoot();
	String getSystemConfigRootPropKey();
	Path getSchemasConfigRoot();
	Path getBooksConfigRoot();
	List<InitializerConfig> getInitializerConfigs();
	ServerConfig getServerConfig();
}
