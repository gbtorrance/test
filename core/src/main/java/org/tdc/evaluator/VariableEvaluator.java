package org.tdc.evaluator;

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;

/**
 * An {@link Evaluator} implementation for variable values.
 * 
 * <p>Supports providing a variable value as an Evaluator result. 
 * 
 * <pre>
 * {@code
 * <Evaluator type="variable" source="current">MY_VARIABLE_NAME</Evaluator>
 * 
 * <Evaluator type="variable" source="previous">MY_VARIABLE_NAME</Evaluator>
 * }
 * </pre>
 */
public class VariableEvaluator implements Evaluator {
	private final Source source;
	private final String variableName;

	public VariableEvaluator(Source source, String variableName) {
		this.source = source;
		this.variableName = variableName;
	}
	
	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		TDCNode node = source == Source.CURRENT ? currentNode : prevNode;
		String value = node == null ? null : node.getVariable(variableName);
		value = value == null ? "" : value;
		return new ValueResult(value);
	}
}

