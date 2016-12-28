package org.tdc.evaluator;	

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;

/**
 * An {@link Evaluator} implementation for literal values.
 * 
 * <p>Supports providing a literal value as an Evaluator result. 
 * 
 * <pre>
 * {@code
 * <Evaluator type="literal">My literal value</Evaluator>
 * 
 * <Evaluator type="literal"/>             <!-- empty literal -->
 * 
 * <Evaluator type="literal"></Evaluator>  <!-- empty literal -->
 * }
 * </pre>
 */
public class LiteralEvaluator implements Evaluator {
	private final ValueResult literalResult;
	
	public LiteralEvaluator(String literal) {
		this.literalResult = new ValueResult(literal); 
	}
	
	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		return literalResult;
	}
}
