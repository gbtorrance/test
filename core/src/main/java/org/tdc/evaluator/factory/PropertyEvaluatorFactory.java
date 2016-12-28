package org.tdc.evaluator.factory;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.PropertyEvaluator;
import org.tdc.evaluator.Source;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link PropertyEvaluator} objects.
 */
public class PropertyEvaluatorFactory implements TypeEvaluatorFactory {
	
	private static final String TYPE = "property"; 
	
	@Override
	public Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String sourceStr = config.getString(configKey + "[@source]", null, true);
		Source source = null;
		try {
			source = Source.valueOf(sourceStr.toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			throw new RuntimeException("PropertyEvaluator must have source attribute of 'current' or 'previous'");
		}
		String propertyName = config.getString(configKey, null, true);
		return new PropertyEvaluator(source, propertyName);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
