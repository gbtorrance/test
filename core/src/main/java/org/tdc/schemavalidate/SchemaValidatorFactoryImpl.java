package org.tdc.schemavalidate;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.SimpleCache;

/**
 * A {@link SchemaValidatorFactory} implementation.
 * 
 * <p>Caches objects to ensure only one instance per {@link ModelConfig}.
 */
public class SchemaValidatorFactoryImpl implements SchemaValidatorFactory {

	private static final Logger log = LoggerFactory.getLogger(SchemaValidatorFactoryImpl.class);

	private final Cache<Addr,SchemaValidator> cache = new SimpleCache<>();

	@Override
	public synchronized SchemaValidator getSchemaValidator(ModelConfig config) {
		Addr addr = config.getAddr();
		SchemaValidator schemaValidator = cache.get(addr);
		if (schemaValidator == null) {
			Path rootFile = config.getSchemaRootFileFullPath();
			int maxMessages = config.getSchemaValidateMaxMessages();
			schemaValidator = new SchemaValidatorImpl.Builder(rootFile, maxMessages).build();
			cache.put(addr, schemaValidator);
		}
		else {
			log.debug("Found cached SchemaValidator for: {}", addr);
		}
		return schemaValidator;
	}
}
