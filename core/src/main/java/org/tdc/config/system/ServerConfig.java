package org.tdc.config.system;

import java.nio.file.Path;

/**
 * Defines server-specific configuration items.
 */
public interface ServerConfig {
	Path getWorkingRoot();
	Path getBooksWorkingRoot();
	int getServerPort();
	int getBookCacheMaxSize();
}
