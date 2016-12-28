package org.tdc.modeldef;

import org.tdc.config.model.ModelConfig;

/**
 * Factory for creating {@link ModelDef} instances.
 */
public interface ModelDefFactory {
	ModelDef getModelDef(ModelConfig config);
	ModelDef getModelDefSkipCustomization(ModelConfig config);
}
