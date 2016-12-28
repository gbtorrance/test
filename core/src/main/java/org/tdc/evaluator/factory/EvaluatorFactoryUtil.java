package org.tdc.evaluator.factory;

import org.tdc.config.util.Config;

/**
 * Contains a few simple helper methods used by Evaluator factories.
 */
class EvaluatorFactoryUtil {

	public static String getEvaluatorType(Config config, String configKey) {
		return config.getString(configKey + "[@type]", null, true);
	}
	
	public static void ensureCorrectEvalatorType(Config config, String configKey, String expectedType) {
		String type = getEvaluatorType(config, configKey);
		if (!type.equals(expectedType)) {
			throw new RuntimeException("Invalid evaluator type '" + type + "' for '" + configKey + "'; expected '" + expectedType + "'");
		}
	}
}
