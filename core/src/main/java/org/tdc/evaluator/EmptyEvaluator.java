package org.tdc.evaluator;

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;

/**
 * An {@link Evaluator} implementation for empty/blank values.
 * 
 * <p>Supports providing an empty/blank value as an Evaluator result.
 * 
 * <p>Essentially equivalent to using a 'literal' Evaluator with no value.
 * 
 * <pre>
 * {@code
 * <Evaluator type="empty"/>
 * 
 * <Evaluator type="empty"></Evaluator>
 * }
 * </pre>
 */
public class EmptyEvaluator implements Evaluator {
	private static final ValueResult EMPTY_RESULT = new ValueResult("");
	
	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		return EMPTY_RESULT;
	}
}
