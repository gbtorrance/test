package org.tdc.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.SimpleCache;

/**
 * A {@link SchemaFactory} implementation.
 *
 * <p>Caches objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 */
public class SchemaFactoryImpl implements SchemaFactory {

	private static final Logger log = LoggerFactory.getLogger(SchemaFactoryImpl.class);
	
	private final Cache<Addr,Schema> cache = new SimpleCache<>();
	
	@Override
	public synchronized Schema getSchema(SchemaConfig config) {
		Addr addr = config.getAddr();
		Schema schema = cache.get(addr);
		if (schema == null) {
			schema = new SchemaImpl(config);
			cache.put(addr, schema);
		}
		else {
			log.debug("Found cached Schema for: {}", addr);
		}
		return schema;
	}
}
