package org.tdc.evaluator.factory;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.LiteralEvaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link LiteralEvaluator} objects.
 */
public class LiteralEvaluatorFactory implements TypeEvaluatorFactory {
	
	private static final String TYPE = "literal"; 
	
	@Override
	public Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String literal = config.getString(configKey, "", false);
		return new LiteralEvaluator(literal);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
