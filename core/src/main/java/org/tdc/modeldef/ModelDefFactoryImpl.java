package org.tdc.modeldef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.schema.Schema;
import org.tdc.schema.SchemaFactory;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.util.Addr;
import org.tdc.util.Cache;
import org.tdc.util.SimpleCache;

/**
 * A {@link ModelDefFactory} implementation.
 * 
 * <p>Creates parent-level {@link Schema} instances, as necessary.
 * 
 * <p>Caches objects to ensure only one instance per {@linkplain org.tdc.util.Addr address}.
 * 
 * <p>Makes use of a {@link ModelDefBuilder} to do all the heavy lifting of object construction.
 */
public class ModelDefFactoryImpl implements ModelDefFactory {

	private static final Logger log = LoggerFactory.getLogger(ModelDefFactoryImpl.class);

	private final Cache<Addr,ModelDef> cache = new SimpleCache<>();
	private final SchemaFactory schemaFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	
	public ModelDefFactoryImpl(SchemaFactory schemaFactory, SpreadsheetFileFactory spreadsheetFileFactory) {
		this.schemaFactory = schemaFactory;
		this.spreadsheetFileFactory = spreadsheetFileFactory;
	}
	
	@Override
	public synchronized ModelDef getModelDef(ModelConfig config) {
		Addr addr = config.getAddr();
		ModelDef modelDef = cache.get(addr);
		if (modelDef == null) {
			Schema schema = schemaFactory.getSchema(config.getSchemaConfig());
			modelDef = buildNewModelDef(config, schema);
			cache.put(config.getAddr(), modelDef);
		}
		else {
			log.debug("Found cached ModelDef for: {}", addr);
		}
		return modelDef;
	}
	
	@Override
	public synchronized ModelDef getModelDefSkipCustomization(ModelConfig config) {
		Addr addr = config.getAddr();
		Schema schema = schemaFactory.getSchema(config.getSchemaConfig());
		ModelDef modelDef = buildNewModelDefSkipCustomization(config, schema);
		return modelDef;
	}
	
	private ModelDef buildNewModelDef(ModelConfig config, Schema schema) {
		// TODO possibly support building from serialized object;
		//      factory to make determination based on info in config
		ModelDefBuilder modelDefBuilder = new ModelDefBuilderImpl(config, schema, spreadsheetFileFactory);
		return modelDefBuilder.build();
	}

	private ModelDef buildNewModelDefSkipCustomization(ModelConfig config, Schema schema) {
		ModelDefBuilder modelDefBuilder = new ModelDefBuilderImpl(config, schema, null);
		return modelDefBuilder.buildSkipCustomization();
	}
}
