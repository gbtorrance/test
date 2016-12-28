package org.tdc.modeldef;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.MPathIndex;
import org.tdc.modelcustomizer.ModelCustomizerReader;
import org.tdc.schema.Schema;
import org.tdc.schemaparse.ModelDefSchemaParser;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;

/**
 * A {@link ModelDefBuilder} implementation.
 * 
 * <p>Builds a {@link ModelDefSchemaParser}, initializing it with the root schema file. 
 * It then uses the parser to construct a {@link ModelDef} tree, getting back the root {@link ElementNodeDef}.
 * This is then used in constructing the final ModelDefBuilderImpl.
 */
public class ModelDefBuilderImpl implements ModelDefBuilder {

	private static final Logger log = LoggerFactory.getLogger(ModelDefBuilderImpl.class);
	
	private final ModelConfig config;
	private final Schema schema;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	
	private MPathIndex<NodeDef> mpathIndex;
	private ModelDefSharedState sharedState;
	
	public ModelDefBuilderImpl(
			ModelConfig config, Schema schema, 
			SpreadsheetFileFactory spreadsheetFileFactory) {
		
		this.config = config;
		this.schema = schema;
		this.spreadsheetFileFactory = spreadsheetFileFactory;
	}
	
	@Override
	public ModelDef build() {
		// TODO possibly add ability to build from zipped schemas?
		ElementNodeDef rootElement = buildNodeTree();
		if (config.hasModelCustomizerConfig()) {
			customizeModelDefTree(rootElement);
		}
		return new ModelDefImpl(config, schema, rootElement, mpathIndex, sharedState);
	}
	
	@Override
	public ModelDef buildSkipCustomization() {
		ElementNodeDef rootElement = buildNodeTree();
		return new ModelDefImpl(config, schema, rootElement, mpathIndex, sharedState);
	}
	
	public ElementNodeDef buildNodeTree() {
		log.debug("Start building ModelDef tree");
		mpathIndex = new MPathIndex<>();
		sharedState = new ModelDefSharedState();
		
		Path rootSchemaFile = config.getSchemaRootFileFullPath();
		if (!Files.isReadable(rootSchemaFile)) {
			throw new IllegalStateException("Unable to read root schema file: " + rootSchemaFile.toString());
		}
		
		ModelDefSchemaParser modelDefSchemaParser =
				new ModelDefSchemaParser
						.Builder(rootSchemaFile, mpathIndex, sharedState, 
								config.getSchemaFilter(), 
								config.getSchemaExtractors())
						.setFailOnParserWarning(config.getFailOnParserWarning())
						.setFailOnParserNonFatalError(config.getFailOnParserNonFatalError())
						.build();
		
		ElementNodeDef rootElement = modelDefSchemaParser.buildModelDefTreeFromSchema(
				config.getSchemaRootElementName(), config.getSchemaRootElementNamespace());
		
		log.debug("Finish building ModelDef tree: rootElementDef: {}", rootElement.getName());
		return rootElement;
	}
	
	private void customizeModelDefTree(ElementNodeDef rootElement) {
		log.debug("Start customizing ModelDef tree");
		ModelCustomizerConfig customizerConfig = config.getModelCustomizerConfig();
		Path path = customizerConfig.getFilePath();
		if (!Files.isRegularFile(path)) {
			throw new IllegalStateException("Unable to locate or read customizer spreadsheet file: " + path.toString());
		}
		SpreadsheetFile spreadsheetFile = spreadsheetFileFactory.createReadOnlySpreadsheetFileFromPath(path);
		ModelCustomizerReader reader = new ModelCustomizerReader(rootElement, 
				config.getModelCustomizerConfig(), spreadsheetFile);
		reader.readCustomizer();
		log.debug("Finish customizing ModelDef tree: rootElementDef: {}", rootElement.getName());
	}
}
