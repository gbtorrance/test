package org.tdc.config.model;

import java.util.List;
import java.util.Map;

import org.tdc.util.Addr;

/**
 * Factory for creating {@link ModelConfig} instances.
 */
public interface ModelConfigFactory {
	ModelConfig getModelConfig(Addr addr);
	boolean isModelConfig(Addr addr);
	List<Addr> getAllModelConfigAddrs();
	List<ModelConfig> getAllModelConfigs(Map<Addr, Exception> errors);
}
