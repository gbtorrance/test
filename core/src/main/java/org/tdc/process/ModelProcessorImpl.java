package org.tdc.process;

import java.nio.file.Path;

import org.tdc.config.model.ModelConfig;
import org.tdc.config.model.ModelConfigFactory;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.model.MPathIndex;
import org.tdc.modelcustomizer.ModelCustomizerWriter;
import org.tdc.modeldef.ModelDef;
import org.tdc.modeldef.ModelDefFactory;
import org.tdc.modeldef.NodeDef;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;
import org.tdc.util.Addr;

/**
 * A {@link ModelProcessor} implementation.
 */
public class ModelProcessorImpl implements ModelProcessor {
	private final ModelConfigFactory modelConfigFactory;
	private final ModelDefFactory modelDefFactory;
	private final SpreadsheetFileFactory spreadsheetFileFactory;
	
	public ModelProcessorImpl(
			ModelConfigFactory modelConfigFactory, 
			ModelDefFactory modelDefFactory,
			SpreadsheetFileFactory spreadsheetFileFactory) {
		
		this.modelConfigFactory = modelConfigFactory;
		this.modelDefFactory = modelDefFactory;
		this.spreadsheetFileFactory = spreadsheetFileFactory;
	}
	
	@Override
	public void createCustomizer(Addr modelAddr, Path targetPath, 
			Addr basedOnModelAddr, boolean overwriteExisting) {

		ModelConfig config = modelConfigFactory.getModelConfig(modelAddr);
		confirmCustomizerIsDefinedForModel(config);
		ModelDef model = modelDefFactory.getModelDefSkipCustomization(config);
		ModelDef basedOnModel = loadBasedOnModelIfExists(basedOnModelAddr);
		verifyTargetPathExtension(targetPath);
		SpreadsheetFile spreadsheetFile = 
				spreadsheetFileFactory.createNewSpreadsheetFile();
		writeToSpreadsheetFile(config, model, spreadsheetFile, basedOnModel);
		if (overwriteExisting) {
			spreadsheetFile.save(targetPath);
		}
		else {
			spreadsheetFile.saveAsNew(targetPath);
		}
	}

	private void confirmCustomizerIsDefinedForModel(ModelConfig modelConfig) {
		ModelCustomizerConfig customizerConfig = modelConfig.getModelCustomizerConfig();
		if (customizerConfig == null) {
			throw new RuntimeException("In order to create a Customizer for model " + 
					modelConfig.getAddr() + " a Customizer must be configured");
		}
	}

	private ModelDef loadBasedOnModelIfExists(Addr basedOnModelAddr) {
		if (basedOnModelAddr == null) {
			return null;
		}
		ModelConfig basedOnModelConfig = 
				modelConfigFactory.getModelConfig(basedOnModelAddr);
		return modelDefFactory.getModelDef(basedOnModelConfig);
	}

	private void verifyTargetPathExtension(Path targetPath) {
		String expectedExtension = "xlsx";
		String targetFilename = targetPath.toString();
		String targetExtension = targetFilename.substring(targetFilename.lastIndexOf(".") + 1);
		if (!expectedExtension.equals(targetExtension)) {
			throw new IllegalStateException("Extension of target Customizer file " + 
					targetFilename + " does not match expected extension '." + 
					expectedExtension + "'");
		}
	}
	
	private void writeToSpreadsheetFile(
			ModelConfig config, ModelDef modelDef, 
			SpreadsheetFile spreadsheetFile, ModelDef prevModelDef) {
		
		MPathIndex<NodeDef> prevModelMPathIndex =
				prevModelDef == null ? null : prevModelDef.getMPathIndex();
		ModelCustomizerWriter writer = new ModelCustomizerWriter(
				modelDef.getRootElement(), 
				config.getModelCustomizerConfig(), 
				spreadsheetFile, 
				prevModelMPathIndex);
		writer.writeCustomizer();
	}
}
