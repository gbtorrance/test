package org.tdc.evaluator;

import java.util.List;

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;

/**
 * An {@link Evaluator} implementation for compound statements.
 * 
 * <p>Evaluator that supports multiple sub-evaluators. 
 * Each one is evaluated in turn and the individual results are 
 * combined into a single result and returned.
 * 
 * <pre>
 * {@code
 * <Evaluator type="compound">
 *     <Evaluator.../>
 *     <Evaluator.../>
 *     <Evaluator.../>
 *     <Evaluator.../>
 * </Evaluator>
 * }
 * </pre>
 */
public class CompoundEvaluator implements Evaluator {
	private final List<Evaluator> evaluators;
	
	public CompoundEvaluator(List<Evaluator> evaluators) {
		this.evaluators = evaluators;
	}

	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		StringBuilder sb = new StringBuilder();
		for (Evaluator eval : evaluators) {
			sb.append(eval.evaluate(currentNode, prevNode).getValue());
		}
		return new ValueResult(sb.toString());
	}
}
