package org.tdc.evaluator.factory;

import java.util.HashMap;
import java.util.Map;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.evaluator.IfEvaluator;
import org.tdc.evaluator.operator.EqualsOperator;
import org.tdc.evaluator.operator.NotEqualsOperator;
import org.tdc.evaluator.operator.Operator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link TypeEvaluatorFactory} implementation to create {@link IfEvaluator} objects.
 */
public class IfEvaluatorFactory implements TypeEvaluatorFactory {

	private static final String TYPE = "if";
	
	private final GeneralEvaluatorFactory generalEvaluatorFactory;
	private final Map<String, Operator> operatorMap = new HashMap<>();
	
	public IfEvaluatorFactory(GeneralEvaluatorFactory generalEvaluatorFactory) {
		this.generalEvaluatorFactory = generalEvaluatorFactory;
	}

	@Override
	public Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle) {
		EvaluatorFactoryUtil.ensureCorrectEvalatorType(config, configKey, TYPE);
		String operatorKey = configKey + "[@operator]";
		String ifKey = configKey + ".If.Evaluator";
		String thenKey = configKey + ".Then.Evaluator";
		String elseKey = configKey + ".Else.Evaluator";

		if (config.getCount(ifKey) != 2) {
			throw new RuntimeException("If block for '" + configKey + "' expected to contain exactly 2 Evaluators");
		}
		if (config.getCount(thenKey) != 1) {
			throw new RuntimeException("Then block for '" + configKey + "' expected to contain exactly 1 Evaluator");
		}
		int elseCount = config.getCount(elseKey);
		if (elseCount > 1) {
			throw new RuntimeException("Else block for '" + configKey + "' can have at most one Evaluator");
		}
		
		String operatorType = config.getString(operatorKey, null, true);
		Operator operator = getOperator(operatorType);
		Evaluator operand1 = generalEvaluatorFactory.createEvaluator(config, ifKey + "(" + 0 + ")", defaultStyle);
		Evaluator operand2 = generalEvaluatorFactory.createEvaluator(config, ifKey + "(" + 1 + ")", defaultStyle);
		Evaluator thenEval = generalEvaluatorFactory.createEvaluator(config, thenKey, defaultStyle);
		Evaluator elseEval = null; 
		if (elseCount > 0) {
			elseEval =  generalEvaluatorFactory.createEvaluator(config, elseKey, defaultStyle);
		}

		return new IfEvaluator(operator, operand1, operand2, thenEval, elseEval);
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	public void setOperator(String operatorType, Operator operator) {
		operatorMap.put(operatorType, operator);
	}
	
	private Operator getOperator(String operatorType) {
		// operators are immutable; return the same instance every time
		Operator operator = operatorMap.get(operatorType);
		if (operator == null) {
			throw new RuntimeException("Operator of type '" + operatorType + "' not found");
		}
		return operator;
	}

	public static IfEvaluatorFactory createWithDefaultOperators(GeneralEvaluatorFactory generalEvaluatorFactory) {
		IfEvaluatorFactory factory = new IfEvaluatorFactory(generalEvaluatorFactory);
		factory.setOperator("equals", new EqualsOperator());
		factory.setOperator("not-equals", new NotEqualsOperator());
		return factory;
	}
}
