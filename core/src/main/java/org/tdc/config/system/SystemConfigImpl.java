package org.tdc.config.system;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.util.Config;
import org.tdc.config.util.ConfigImpl;

/**
 * A {@link SystemConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class SystemConfigImpl implements SystemConfig {
	public static final String CONFIG_FILE = "TDCSystemConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(SystemConfigImpl.class);
	private static final String SYSTEM_CONFIG_ROOT_PROP_KEY = "systemConfigRoot";

	private final Path systemConfigRoot;
	private final Path schemasConfigRoot;
	private final Path booksConfigRoot;
	private final List<InitializerConfig> initializerConfigs;
	private final ServerConfig serverConfig;
	
	private SystemConfigImpl(Builder builder) {
		this.systemConfigRoot = builder.systemConfigRoot;
		this.schemasConfigRoot = builder.schemasConfigRoot;
		this.booksConfigRoot = builder.booksConfigRoot;
		this.initializerConfigs = builder.initializerConfigs;
		this.serverConfig = builder.serverConfig;
	}
	
	@Override
	public Path getSystemConfigRoot() {
		return systemConfigRoot;
	}

	@Override
	public String getSystemConfigRootPropKey() {
		return SYSTEM_CONFIG_ROOT_PROP_KEY;
	}

	@Override
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}

	@Override
	public Path getBooksConfigRoot() {
		return booksConfigRoot;
	}

	@Override
	public List<InitializerConfig> getInitializerConfigs() {
		return initializerConfigs;
	}
	
	@Override
	public ServerConfig getServerConfig() {
		return serverConfig;
	}
	
	public static class Builder {
		private final Path systemConfigRoot;
		private final InitializerConfigFactory initializerConfigFactory;
		private final Config config;
		
		private Path schemasConfigRoot;
		private Path booksConfigRoot;
		private List<InitializerConfig> initializerConfigs;
		private ServerConfig serverConfig;
		
		public Builder(Path systemConfigRoot, InitializerConfigFactory initializerConfigFactory) {
			log.info("Creating SystemConfig: {}", systemConfigRoot);
			this.systemConfigRoot = systemConfigRoot;
			this.initializerConfigFactory = initializerConfigFactory;
			if (!Files.isDirectory(systemConfigRoot)) {
				throw new IllegalStateException("SystemConfigRoot dir does not exist: " + 
						systemConfigRoot.toString());
			}
			Path systemConfigFile = systemConfigRoot.resolve(CONFIG_FILE);
			this.config = new ConfigImpl
					.Builder(systemConfigFile)
					.addLookup(SYSTEM_CONFIG_ROOT_PROP_KEY, systemConfigRoot.toString())
					.build();
		}

		public SystemConfig build() {
			schemasConfigRoot = systemConfigRoot.resolve(
					config.getString("SchemasConfigRoot", null, true));
			if (!Files.isDirectory(schemasConfigRoot)) {
				throw new IllegalStateException(
						"SchemasConfigRoot dir does not exist: " + schemasConfigRoot.toString());
			}
			booksConfigRoot = systemConfigRoot.resolve(
					config.getString("BooksConfigRoot", null, true));
			if (!Files.isDirectory(booksConfigRoot)) {
				throw new IllegalStateException(
						"BooksConfigRoot dir does not exist: " + booksConfigRoot.toString());
			}
			initializerConfigs = initializerConfigFactory
					.createInitializerConfigs(config, "Initializers", systemConfigRoot);
			serverConfig = new ServerConfigImpl
					.Builder(config, systemConfigRoot)
					.build();
			config.ensureNoUnprocessedKeys();
			return new SystemConfigImpl(this);
		}
	}
}
