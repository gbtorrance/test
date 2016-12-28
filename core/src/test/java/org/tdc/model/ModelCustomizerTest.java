package org.tdc.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelConfigFactoryImpl;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.config.schema.SchemaConfig;
import org.tdc.config.schema.SchemaConfigFactory;
import org.tdc.config.schema.SchemaConfigFactoryImpl;
import org.tdc.modelcustomizer.ModelCustomizerReader;
import org.tdc.modelcustomizer.ModelCustomizerWriter;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.ModelDefBuilderImpl;
import org.tdc.schema.Schema;
import org.tdc.schema.SchemaFactory;
import org.tdc.schema.SchemaFactoryImpl;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.excel.ExcelSpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * Unit tests for {@link ModelCustomizerWriter} and {@link ModelCustomizerReader}.
 */
public class ModelCustomizerTest {
	
	// TODO improve test 
	// not true unit testing at this point, since there is no verification of results;
	// however this does verify that a created model customizer spreadsheet can subsequently be read
	
	private static SchemaConfigFactory schemaConfigFactory;
	private static ModelConfigFactory modelConfigFactory;
	private static SchemaFactory schemaFactory;
	
	@BeforeClass
	public static void setup() {
		Path schemaRoot = Paths.get("testfiles/TDCFiles/Schemas");
		schemaConfigFactory = new SchemaConfigFactoryImpl(schemaRoot);
		modelConfigFactory = new ModelConfigFactoryImpl(schemaConfigFactory);
		schemaFactory = new SchemaFactoryImpl();
	}
	
	@Test
	public void testCustomizerWriteThenRead() throws IOException {
		// build ModelDef tree;
		// normally a factory would be used for this 
		// (i.e. just build the ModelDef using the factory and then get the tree from it);
		// however, due to the immutability of ModelDefs, it is too late once
		// the ModelDef is created to use a customizer on it;
		// need to get around that by just creating the tree without the ModelDef;
		// in normal circumstances the factory will take care of 'customizing' the model
		Addr modelAddr = new Addr("Test/TestSchemaV1.0/Model_OldTest_Customized");
		ModelConfig modelConfig = modelConfigFactory.getModelConfig(modelAddr);
		SchemaConfig schemaConfig = modelConfig.getSchemaConfig();
		Schema schema = schemaFactory.getSchema(schemaConfig);
		ModelDefBuilderImpl builder = new ModelDefBuilderImpl(modelConfig, schema, null);
		ElementNodeDef rootElement = builder.buildNodeTree();		
		
		// write customizer based on tree; then read it back again
		writeCustomizer(rootElement, modelConfig.getModelCustomizerConfig());
		readCustomizer(rootElement, modelConfig.getModelCustomizerConfig());
	}
	
	private void writeCustomizer(ElementNodeDef rootElement, ModelCustomizerConfig customizerConfig) {
		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = factory.createNewSpreadsheetFile();
		
		ModelCustomizerWriter writer = new ModelCustomizerWriter(
				rootElement, customizerConfig, spreadsheetFile, null);
		writer.writeCustomizer();
		
		Path path = Paths.get("testfiles/Temp/TestModelCustomizer.xlsx");
		spreadsheetFile.save(path);
	}
	
	private void readCustomizer(ElementNodeDef rootElement, ModelCustomizerConfig customizerConfig) {
		Path path = Paths.get("testfiles/Temp/TestModelCustomizer.xlsx");
		ExcelSpreadsheetFileFactory factory = new ExcelSpreadsheetFileFactory();
		SpreadsheetFile spreadsheetFile = factory.createReadOnlySpreadsheetFileFromPath(path);
		
		ModelCustomizerReader reader = new ModelCustomizerReader(
				rootElement, customizerConfig, spreadsheetFile);
		reader.readCustomizer();
	}
}
