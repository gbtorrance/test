package org.tdc.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;

/**
 * A {@link Schema} implementation.
 */
public class SchemaImpl implements Schema {
	
	private static final Logger log = LoggerFactory.getLogger(SchemaImpl.class);
	
	private final SchemaConfig config;
	
	public SchemaImpl(SchemaConfig config) {
		log.info("Creating SchemaImpl: {}", config.getAddr());
		this.config = config;
	}
}
