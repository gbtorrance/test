package org.tdc.process;

import java.nio.file.Path;

import org.tdc.util.Addr;

/**
 * Interface defining core functionality for processing Models.
 */
public interface ModelProcessor {
	void createCustomizer(
			Addr modelAddr, Path targetPath, 
			Addr basedOnModelAddr, boolean overwriteExisting);
}
