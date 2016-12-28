package org.tdc.config.schema;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.util.Config;
import org.tdc.config.util.ConfigImpl;
import org.tdc.util.Addr;

/**
 * A {@link SchemaConfig} implementation.
 * 
 * <p>Reads from XML configuration files. 
 */
public class SchemaConfigImpl implements SchemaConfig {
	
	public static final String CONFIG_FILE = "TDCSchemaConfig.xml";
	
	private static final Logger log = LoggerFactory.getLogger(SchemaConfigImpl.class);
	private static final String SCHEMAS_CONFIG_ROOT_PROP_KEY = "schemasConfigRoot";
	private static final String SCHEMA_CONFIG_ROOT_PROP_KEY = "schemaConfigRoot";

	private final Path schemasConfigRoot;
	private final Addr addr;
	private final Path schemaConfigRoot;
	private final Path schemaFilesRoot;
	
	private SchemaConfigImpl(Builder builder) {
		this.schemasConfigRoot = builder.schemasConfigRoot;
		this.addr = builder.addr;
		this.schemaConfigRoot = builder.schemaConfigRoot;
		this.schemaFilesRoot = builder.schemaFilesRoot;
	}
	
	@Override
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}
	
	@Override
	public String getSchemasConfigRootPropKey() {
		return SCHEMAS_CONFIG_ROOT_PROP_KEY;
	}
	
	@Override
	public Addr getAddr() {
		return addr; 
	}

	@Override
	public Path getSchemaConfigRoot() {
		return schemaConfigRoot;
	}

	@Override
	public String getSchemaConfigRootPropKey() {
		return SCHEMA_CONFIG_ROOT_PROP_KEY;
	}
	
	@Override
	public Path getSchemaFilesRoot() {
		return schemaFilesRoot;
	}

	public static class Builder {
		private final Config config;
		private final Path schemasConfigRoot;
		private final Addr addr;
		private final Path schemaConfigRoot;
		
		private Path schemaFilesRoot;
		
		public Builder(Path schemasConfigRoot, Addr addr) {
			log.info("Creating SchemaConfig: {}", addr);
			this.schemasConfigRoot = schemasConfigRoot;
			this.addr = addr;
			this.schemaConfigRoot = schemasConfigRoot.resolve(addr.getPath());
			if (!Files.isDirectory(schemaConfigRoot)) {
				throw new IllegalStateException("SchemaConfig root dir does not exist: " + schemaConfigRoot.toString());
			}
			Path schemaConfigFile = schemaConfigRoot.resolve(CONFIG_FILE);
			this.config = new ConfigImpl
					.Builder(schemaConfigFile)
					.addLookup(SCHEMAS_CONFIG_ROOT_PROP_KEY, schemasConfigRoot.toString())
					.addLookup(SCHEMA_CONFIG_ROOT_PROP_KEY, schemaConfigRoot.toString())
					.build();
		}

		public SchemaConfig build() {
			schemaFilesRoot = schemaConfigRoot.resolve(config.getString("SchemaFilesRoot", null, true));
			if (!Files.isDirectory(schemaFilesRoot)) {
				throw new IllegalStateException("Schema files root dir does not exist: " + schemaFilesRoot.toString());
			}
			config.ensureNoUnprocessedKeys();
			return new SchemaConfigImpl(this);
		}
	}
}
