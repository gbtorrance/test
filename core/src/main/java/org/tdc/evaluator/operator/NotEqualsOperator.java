package org.tdc.evaluator.operator;

/**
 * An {@link Operator} implementation for 'not-equals'.
 */
public class NotEqualsOperator extends EqualsOperator {
	@Override
	public boolean testTrue(String operand1, String operand2) {
		return !super.testTrue(operand1, operand2);
	}
}
