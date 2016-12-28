package org.tdc.config.schema;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link SchemaConfig} instances.
 */
public interface SchemaConfigFactory {
	Path getSchemasConfigRoot();
	SchemaConfig getSchemaConfig(Addr addr);
	boolean isSchemaConfig(Addr addr);
	List<Addr> getAllSchemaConfigAddrs();
	List<SchemaConfig> getAllSchemaConfigs(Map<Addr, Exception> errors);
}
