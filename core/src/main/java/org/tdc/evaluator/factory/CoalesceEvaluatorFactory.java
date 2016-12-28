package org.tdc.evaluator.factory;

import java.util.ArrayList;
import java.util.List;

import org.tdc.config.util.Config;
import org.tdc.evaluator.CoalesceEvaluator;
import org.tdc.evaluator.Evaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link CoalesceEvaluator} objects.
 */
public class CoalesceEvaluatorFactory implements TypeEvaluatorFactory {

	private static final String TYPE = "coalesce";
	
	private final GeneralEvaluatorFactory generalEvaluatorFactory;
	
	public CoalesceEvaluatorFactory(GeneralEvaluatorFactory generalEvaluatorFactory) {
		this.generalEvaluatorFactory = generalEvaluatorFactory;
	}

	@Override
	public Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String subKey = configKey + ".Evaluator";
		List<Evaluator> evaluators = new ArrayList<>();
		for (int i = 0; i < config.getCount(subKey); i++) {
			Evaluator evaluator = generalEvaluatorFactory.createEvaluator(config, subKey + "(" + i + ")", defaultStyle);
			evaluators.add(evaluator);
		}
		return new CoalesceEvaluator(evaluators);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
