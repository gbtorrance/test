package org.tdc.config.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.util.Config;
import org.tdc.config.util.ConfigImpl;
import org.tdc.evaluator.factory.GeneralEvaluatorFactory;
import org.tdc.schemaparse.extractor.SchemaExtractor;
import org.tdc.schemaparse.extractor.SchemaExtractorFactory;
import org.tdc.schemaparse.filter.SchemaFilter;
import org.tdc.schemaparse.filter.SchemaFilterImpl;
import org.tdc.util.Addr;
import org.tdc.util.Util;

/**
 * A {@link ModelConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class ModelConfigImpl implements ModelConfig {
	
	public static final String CONFIG_FILE = "TDCModelConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(ModelConfigImpl.class);
	private static final String MODEL_CONFIG_ROOT_PROP_KEY = "modelConfigRoot";

	private final SchemaConfig schemaConfig;
	private final Addr addr;
	private final Path modelConfigRoot;
	private final String modelName;
	private final String modelDescription;
	private final String schemaRootFile;
	private final String schemaRootElementName;
	private final String schemaRootElementNamespace;
	private final boolean schemaFailOnParserWarning;
	private final boolean schemaFailOnParserNonFatalError;
	private final int defaultOccursCount;
	private final SchemaFilter schemaFilter;
	private final List<SchemaExtractor> schemaExtractors;
	private final ModelCustomizerConfig modelCustomizerConfig;
	private final int testLoadMaxMessages;
	private final int schemaValidateMaxMessages;
	private final boolean schemaValidateEnable;
	
	private ModelConfigImpl(Builder builder) {
		this.schemaConfig = builder.schemaConfig;
		this.addr = builder.addr;
		this.modelConfigRoot = builder.modelConfigRoot;
		this.modelName = builder.modelName;
		this.modelDescription = builder.modelDescription;
		this.schemaRootFile = builder.schemaRootFile;
		this.schemaRootElementName = builder.schemaRootElementName;
		this.schemaRootElementNamespace = builder.schemaRootElementNamespace;
		this.schemaFailOnParserWarning = builder.schemaFailOnParserWarning;
		this.schemaFailOnParserNonFatalError = builder.schemaFailOnParserNonFatalError;
		this.defaultOccursCount = builder.defaultOccursCount;
		this.schemaFilter = builder.schemaFilter;
		this.schemaExtractors = Collections.unmodifiableList(builder.schemaExtractors); // unmodifiable
		this.modelCustomizerConfig = builder.modelCustomizerConfig;
		this.testLoadMaxMessages = builder.testLoadMaxMessages;
		this.schemaValidateMaxMessages = builder.schemaValidateMaxMessages;
		this.schemaValidateEnable = builder.schemaValidateEnable;
	}
	
	@Override
	public SchemaConfig getSchemaConfig() {
		return schemaConfig;
	}
	
	@Override
	public Addr getAddr() {
		return addr; 
	}
	
	@Override
	public Path getModelConfigRoot() {
		return modelConfigRoot;
	}
	
	@Override
	public String getModelConfigRootPropKey() {
		return MODEL_CONFIG_ROOT_PROP_KEY;
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public String getModelDescription() {
		return modelDescription;
	}

	@Override
	public String getSchemaRootFile() {
		return schemaRootFile;
	}
	
	@Override
	public Path getSchemaRootFileFullPath() {
		return schemaConfig.getSchemaFilesRoot().resolve(schemaRootFile);
	}

	@Override
	public String getSchemaRootElementName() {
		return schemaRootElementName;
	}

	@Override
	public String getSchemaRootElementNamespace() {
		return schemaRootElementNamespace;
	}

	@Override
	public boolean getFailOnParserWarning() {
		return schemaFailOnParserWarning;
	}

	@Override
	public boolean getFailOnParserNonFatalError() {
		return schemaFailOnParserNonFatalError;
	}
	
	@Override
	public int getDefaultOccursCount() {
		return defaultOccursCount;
	}

	@Override
	public SchemaFilter getSchemaFilter() {
		return schemaFilter;
	}
	
	@Override
	public List<SchemaExtractor> getSchemaExtractors() {
		return schemaExtractors;
	}
	
	@Override
	public ModelCustomizerConfig getModelCustomizerConfig() {
		return modelCustomizerConfig;
	}
	
	@Override
	public boolean hasModelCustomizerConfig() {
		return modelCustomizerConfig != null;
	}
	
	@Override
	public int getTestLoadMaxMessages() {
		return testLoadMaxMessages;
	}
	
	@Override
	public int getSchemaValidateMaxMessages() {
		return schemaValidateMaxMessages;
	}
	
	@Override
	public boolean getSchemaValidateEnable() {
		return schemaValidateEnable;
	}
	
	public static class Builder {
		private final Config config;
		private final SchemaConfig schemaConfig;
		private final Addr addr;
		private final Path modelConfigRoot;
		private final SchemaExtractorFactory schemaExtractorFactory;
		private final GeneralEvaluatorFactory evaluatorFactory;

		private String modelName;
		private String modelDescription;
		private String schemaRootFile;
		private String schemaRootElementName;
		private String schemaRootElementNamespace;
		private boolean schemaFailOnParserWarning;
		private boolean schemaFailOnParserNonFatalError;
		private int defaultOccursCount;
		private SchemaFilter schemaFilter;
		private List<SchemaExtractor> schemaExtractors;
		private ModelCustomizerConfig modelCustomizerConfig;
		private int testLoadMaxMessages;
		private int schemaValidateMaxMessages;
		private boolean schemaValidateEnable;
		
		public Builder(SchemaConfig schemaConfig, String name, 
				SchemaExtractorFactory schemaExtractorFactory, GeneralEvaluatorFactory evaluatorFactory) {
			this.schemaConfig = schemaConfig;
			this.addr = schemaConfig.getAddr().resolve(name);
			log.info("Creating ModelConfigImpl: {}", addr);
			this.modelConfigRoot = schemaConfig.getSchemaConfigRoot().resolve(name);
			if (!Files.isDirectory(modelConfigRoot)) {
				throw new IllegalStateException("ModelConfig root dir does not exist: " + modelConfigRoot.toString());
			}
			this.schemaExtractorFactory = schemaExtractorFactory;
			this.evaluatorFactory = evaluatorFactory;
			Path modelConfigFile = modelConfigRoot.resolve(CONFIG_FILE);
			this.config = new ConfigImpl
					.Builder(modelConfigFile)
					.addLookup(
							schemaConfig.getSchemasConfigRootPropKey(), 
							schemaConfig.getSchemasConfigRoot().toString())
					.addLookup(
							schemaConfig.getSchemaConfigRootPropKey(), 
							schemaConfig.getSchemaConfigRoot().toString())
					.addLookup(MODEL_CONFIG_ROOT_PROP_KEY, modelConfigRoot.toString())
					.build();
		}

		public ModelConfig build() {
			modelName = config.getString("ModelName", null, true);
			modelDescription = config.getString("ModelDescription", "", false);
			schemaRootFile = config.getString("SchemaRootFile", null, true);
			schemaRootElementName = config.getString("SchemaRootElementName", null, true);
			schemaRootElementNamespace = config.getString("SchemaRootElementNamespace", null, true);
			schemaFailOnParserWarning = config.getBoolean("SchemaFailOnParserWarning", false , false);
			schemaFailOnParserNonFatalError = config.getBoolean("SchemaFailOnParserNonFatalError", true, false);
			defaultOccursCount = config.getInt("DefaultOccursCount", 5, false);
			schemaFilter = new SchemaFilterImpl.Builder().buildFromConfig(config, "SchemaFilter");
			schemaExtractors = schemaExtractorFactory.createSchemaExtractors(config, "SchemaExtractors");
			modelCustomizerConfig = new ModelCustomizerConfigImpl.Builder(
					config, modelConfigRoot, defaultOccursCount, modelName, evaluatorFactory).build();
			testLoadMaxMessages = config.getInt("TestLoadMaxMessages", Util.NO_LIMIT, false);
			schemaValidateMaxMessages = config.getInt("SchemaValidateMaxMessages", Util.NO_LIMIT, false);
			schemaValidateEnable = config.getBoolean("SchemaValidateEnable", true, false);
			config.ensureNoUnprocessedKeys();
			return new ModelConfigImpl(this);
		}
	}
}
