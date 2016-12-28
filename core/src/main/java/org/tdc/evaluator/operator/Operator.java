package org.tdc.evaluator.operator;

/**
 * Defines functionality for IfEvaluator Operators
 */
public interface Operator {

	/**
	 * Returns true if applying the operator to the two operands produces a true result.
	 *   
	 * @param operand1 The first operand.
	 * @param operand2 The second operand.
	 * @return Result of applying Operator to operand 1 and operand 2.
	 */
	public boolean testTrue(String operand1, String operand2);
}
