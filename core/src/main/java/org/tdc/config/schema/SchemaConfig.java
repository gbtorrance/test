package org.tdc.config.schema;

import java.nio.file.Path;

import org.tdc.util.Addr;

/**
 * Defines getters for configuration items applicable to {@link org.tdc.schema.Schema Schemas}.
 */
public interface SchemaConfig {
	Path getSchemasConfigRoot();
	String getSchemasConfigRootPropKey();
	Addr getAddr();
	Path getSchemaConfigRoot();
	String getSchemaConfigRootPropKey();
	Path getSchemaFilesRoot();
}
