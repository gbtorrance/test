package org.tdc.schemavalidate;

import org.tdc.config.model.ModelConfig;

/**
 * Factory for creating {@link SchemaValidator} instances.
 */
public interface SchemaValidatorFactory {
	SchemaValidator getSchemaValidator(ModelConfig config);
}
