package org.tdc.evaluator;

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;
import org.tdc.util.Util;

/**
 * An {@link Evaluator} implementation for spaces.
 * 
 * <p>Supports providing any number of spaces as an Evaluator result.
 * This may, in some cases, be necessary, as normal Apache Commons Configuration
 * processing strips out leading and trailing spaces from element values.
 * This Evaluator, used within a 'compound' Evaluator, allows for the 
 * adding of spaces at any position in an Evaluator result. 
 * 
 * <pre>
 * {@code
 * <Evaluator type="space"/>             <!-- defaults to 1 space -->
 * 
 * <Evaluator type="space" size="0"/>    <!-- equivalent to 'empty' Evaluator -->
 * 
 * <Evaluator type="space" size="1"/>
 * 
 * <Evaluator type="space" size="5"/>
 * }
 * </pre>
 */
public class SpaceEvaluator implements Evaluator {
	private static final ValueResult SPACE_1 = new ValueResult(" ");
	
	private final ValueResult spaceResult;
	
	public SpaceEvaluator(int size) {
		this.spaceResult = size == 1 ? SPACE_1 : new ValueResult(Util.spaces(size)); 
	}
	
	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		return spaceResult;
	}
}
