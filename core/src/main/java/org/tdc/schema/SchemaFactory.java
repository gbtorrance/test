package org.tdc.schema;

import org.tdc.config.schema.SchemaConfig;

/**
 * Factory for creating {@link Schema} instances.
 * 
 * <p>Requires that a {@link SchemaConfig} object be provided.
 */
public interface SchemaFactory {
	Schema getSchema(SchemaConfig config);
}
