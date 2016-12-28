package org.tdc.evaluator.result;

/**
 * Used for returning results from {@link org.tdc.evaluator.Evaluator Evaluator} evaluations.
 * 
 * <p>Result contains a single text string.
 */
public class ValueResult {
	private final String value;

	public ValueResult(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
