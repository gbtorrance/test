package org.tdc.evaluator.operator;

/**
 * An {@link Operator} implementation for 'equals'.
 */
public class EqualsOperator implements Operator {
	@Override
	public boolean testTrue(String operand1, String operand2) {
		boolean result = false;
		if (operand1 != null && operand2 != null) {
			result = operand1.equals(operand2);
		}
		return result;
	}
}
