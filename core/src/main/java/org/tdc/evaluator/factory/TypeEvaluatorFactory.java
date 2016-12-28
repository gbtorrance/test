package org.tdc.evaluator.factory;

/**
 * Defines type-specific functionality for {@link EvaluatorFactory} objects. 
 */
public interface TypeEvaluatorFactory extends EvaluatorFactory {
	
	/**
	 * Returns the Evaluator 'type' attribute string as represented in XML configuration files.
	 * 
	 * <pre>
	 * {@code
	 * <Evaluator type="literal"/>  <!-- 'type' example for LiteralEvaluator -->
	 * }
	 * </pre>
	 * 
	 * @return 'type' string.
	 */
	String getType();
}
