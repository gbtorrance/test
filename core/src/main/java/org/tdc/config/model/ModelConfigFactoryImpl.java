package org.tdc.config.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.evaluator.factory.GeneralEvaluatorFactoryImpl;
import org.tdc.schemaparse.extractor.SchemaExtractorFactory;
import org.tdc.schemaparse.extractor.SchemaExtractorFactoryImpl;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.ConfigFinder;
import org.tdc.util.SimpleCache;

/**
 * A {@link ModelConfigFactory} implementation.
 * 
 * <p>Creates parent-level {@link SchemaConfig} instances, as necessary.
 * 
 * <p>Caches configuration objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class ModelConfigFactoryImpl implements ModelConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelConfigFactoryImpl.class);

	private final Cache<Addr,ModelConfig> cache = new SimpleCache<>();
	private final SchemaConfigFactory schemaConfigFactory;

	public ModelConfigFactoryImpl(SchemaConfigFactory schemaConfigFactory) {
		this.schemaConfigFactory = schemaConfigFactory;
	}

	@Override
	public synchronized ModelConfig getModelConfig(Addr addr) {
		ModelConfig modelConfig = cache.get(addr);
		if (modelConfig == null) {
			SchemaConfig schemaConfig = schemaConfigFactory.getSchemaConfig(addr.getParentAddr());
			SchemaExtractorFactory schemaExtractorFactory = new SchemaExtractorFactoryImpl();
			GeneralEvaluatorFactory evaluatorFactory = GeneralEvaluatorFactoryImpl.createWithDefaultTypeEvaluators();
			modelConfig = new ModelConfigImpl.Builder(
					schemaConfig, addr.getName(), schemaExtractorFactory, evaluatorFactory).build();
			cache.put(addr, modelConfig);
		}
		else {
			log.debug("Found cached ModelConfig for: {}", addr);
		}
		return modelConfig;
	}

	@Override
	public boolean isModelConfig(Addr addr) {
		return getAllModelConfigAddrs()
				.stream()
				.anyMatch(a -> a.equals(addr));
	}

	@Override
	public List<Addr> getAllModelConfigAddrs() {
		return ConfigFinder.findAllConfigsContainingConfigFile(
				schemaConfigFactory.getSchemasConfigRoot(), 
				ModelConfigImpl.CONFIG_FILE);
	}

	@Override
	public List<ModelConfig> getAllModelConfigs(Map<Addr, Exception> errors) {
		List<Addr> allConfigAddrs = getAllModelConfigAddrs();
		List<ModelConfig> modelConfigs = new ArrayList<>();
		for (Addr addr : allConfigAddrs) {
			processConfigWithErrorTracking(addr, modelConfigs, errors);
		}
		return modelConfigs;
	}
	
	private void processConfigWithErrorTracking(Addr addr, List<ModelConfig> modelConfigs, Map<Addr, Exception> errors) {
		try {
			ModelConfig modelConfig = getModelConfig(addr);
			modelConfigs.add(modelConfig);
		}
		catch (RuntimeException e) {
			if (errors == null) {
				throw e;
			}
			else {
				errors.put(addr, e);
				log.debug("Exception encountered while getting config for " + addr, e);
			}
		}
	}
}
