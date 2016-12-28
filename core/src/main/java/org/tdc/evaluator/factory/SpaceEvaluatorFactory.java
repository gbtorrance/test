package org.tdc.evaluator.factory;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.SpaceEvaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link SpaceEvaluator} objects.
 */
public class SpaceEvaluatorFactory implements TypeEvaluatorFactory {
	
	private static final String TYPE = "space"; 
	
	@Override
	public Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		int spaceSize = config.getInt(configKey + "[@size]", 1, false);
		return new SpaceEvaluator(spaceSize);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
