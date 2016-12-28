package org.tdc.config.schema;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.ConfigFinder;
import org.tdc.util.SimpleCache;

/**
 * A {@link SchemaConfigFactory} implementation.
 * 
 * <p>Caches configuration objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class SchemaConfigFactoryImpl implements SchemaConfigFactory {

	private static final Logger log = LoggerFactory.getLogger(SchemaConfigFactoryImpl.class);

	private final Cache<Addr,SchemaConfig> cache = new SimpleCache<>();
	private final Path schemasConfigRoot;
	
	public SchemaConfigFactoryImpl(Path schemasConfigRoot) {
		this.schemasConfigRoot = schemasConfigRoot;
	}
	
	public Path getSchemasConfigRoot() {
		return schemasConfigRoot;
	}

	@Override
	public synchronized SchemaConfig getSchemaConfig(Addr addr) {
		SchemaConfig schemaConfig = cache.get(addr);
		if (schemaConfig == null) {
			schemaConfig = new SchemaConfigImpl.Builder(schemasConfigRoot, addr).build();
			cache.put(addr, schemaConfig);
		}
		else {
			log.debug("Found cached SchemaConfig for: {}", addr);
		}
		return schemaConfig;
	}

	@Override
	public boolean isSchemaConfig(Addr addr) {
		return getAllSchemaConfigAddrs()
				.stream()
				.anyMatch(a -> a.equals(addr));
	}

	@Override
	public List<Addr> getAllSchemaConfigAddrs() {
		return ConfigFinder.findAllConfigsContainingConfigFile(
				schemasConfigRoot, 
				SchemaConfigImpl.CONFIG_FILE);
	}

	@Override
	public List<SchemaConfig> getAllSchemaConfigs(Map<Addr, Exception> errors) {
		List<Addr> allConfigAddrs = getAllSchemaConfigAddrs();
		List<SchemaConfig> schemaConfigs = new ArrayList<>();
		for (Addr addr : allConfigAddrs) {
			processConfigWithErrorTracking(addr, schemaConfigs, errors);
		}
		return schemaConfigs;
	}
	
	private void processConfigWithErrorTracking(Addr addr, List<SchemaConfig> schemaConfigs, Map<Addr, Exception> errors) {
		try {
			SchemaConfig schemaConfig = getSchemaConfig(addr);
			schemaConfigs.add(schemaConfig);
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
