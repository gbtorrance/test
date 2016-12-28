package org.tdc.evaluator.factory;

import java.util.HashMap;
import java.util.Map;

import org.tdc.config.util.Config;
import org.tdc.evaluator.Evaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * A {@link GeneralEvaluatorFactory} implementation that can create any type of {@link Evaluator}
 * through the use of type-specific {@link TypeEvaluatorFactory} objects.
 */
public class GeneralEvaluatorFactoryImpl implements GeneralEvaluatorFactory {
	private final Map<String, TypeEvaluatorFactory> typeFactoryMap = new HashMap<>();
	
	@Override
	public synchronized Evaluator createEvaluator(Config config, String configKey, CellStyle defaultStyle) {
		String type = EvaluatorFactoryUtil.getEvaluatorType(config, configKey);
		TypeEvaluatorFactory factory = typeFactoryMap.get(type);
		if (factory == null) {
			throw new RuntimeException("Unable to locate TypeEvaluatorFactory for '" + configKey + "' with type '" + type + "'");
		}
		return factory.createEvaluator(config, configKey, defaultStyle);
	}

	public synchronized void setTypeSpecificFactory(TypeEvaluatorFactory evaluatorFactory) {
		typeFactoryMap.put(evaluatorFactory.getType(), evaluatorFactory);
	}
	
	public static GeneralEvaluatorFactory createWithDefaultTypeEvaluators() {
		GeneralEvaluatorFactoryImpl factory = new GeneralEvaluatorFactoryImpl();
		factory.setTypeSpecificFactory(new EmptyEvaluatorFactory());
		factory.setTypeSpecificFactory(new SpaceEvaluatorFactory());
		factory.setTypeSpecificFactory(new LiteralEvaluatorFactory());
		factory.setTypeSpecificFactory(new CompoundEvaluatorFactory(factory));
		factory.setTypeSpecificFactory(new CoalesceEvaluatorFactory(factory));
		factory.setTypeSpecificFactory(new ValuePlusStyleEvaluatorFactory(factory));
		factory.setTypeSpecificFactory(new VariableEvaluatorFactory());
		factory.setTypeSpecificFactory(new PropertyEvaluatorFactory());
		factory.setTypeSpecificFactory(IfEvaluatorFactory.createWithDefaultOperators(factory));
		return factory;
	}
}
