package org.tdc.evaluator.factory;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.ValuePlusStyleEvaluator;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link ValuePlusStyleEvaluator} objects.
 */
public class ValuePlusStyleEvaluatorFactory implements TypeEvaluatorFactory {

	private static final String TYPE = "value-plus-style";
	
	private final GeneralEvaluatorFactory generalEvaluatorFactory;
	
	public ValuePlusStyleEvaluatorFactory(
			GeneralEvaluatorFactory generalEvaluatorFactory) {
		
		this.generalEvaluatorFactory = generalEvaluatorFactory;
	}

	@Override
	public Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String evaluatorKey = configKey + ".Evaluator";
		String styleKey = configKey + ".Style";

		if (config.getCount(evaluatorKey) != 1) {
			throw new RuntimeException("ValuePlusStyle element '" + 
					configKey + "' expected to contain exactly 1 Evaluator");
		}
		if (config.getCount(styleKey) != 1) {
			throw new RuntimeException("ValuePlusStyle element '" + 
					configKey + "' expected to contain exactly 1 Style");
		}
		
		Evaluator evaluator = generalEvaluatorFactory.createEvaluator(
				config, evaluatorKey, defaultStyle);
		CellStyle style = new CellStyleImpl.Builder().setFromConfig(
				config, styleKey, defaultStyle, true).build();

		return new ValuePlusStyleEvaluator(evaluator, style);
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
