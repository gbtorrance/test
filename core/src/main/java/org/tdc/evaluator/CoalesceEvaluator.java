package org.tdc.evaluator;

import java.util.List;

import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;

/**
 * An {@link Evaluator} implementation for coalesce statements.
 * 
 * <p>Evaluator that supports multiple sub-evaluators. 
 * Each one is evaluated in turn and the first non-empty result is returned.
 * 
 * <p>Similar to an SQL coalesce function.
 * 
 * <pre>
 * {@code
 * <Evaluator type="coalesce">
 *     <Evaluator.../>
 *     <Evaluator.../>
 *     <Evaluator.../>
 *     <Evaluator.../>
 * </Evaluator>
 * }
 * </pre>
 */
public class CoalesceEvaluator implements Evaluator {
	private final List<Evaluator> evaluators;
	
	public CoalesceEvaluator(List<Evaluator> evaluators) {
		this.evaluators = evaluators;
	}

	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		// intentionally return original ValueResult object, if there is a match; 
		// might contain CellStyle information we want to pass on up
		ValueResult result = null;
		for (Evaluator eval : evaluators) {
			result = eval.evaluate(currentNode, prevNode);
			if (!result.getValue().equals("")) {
				break;
			}
		}
		if (result == null || result.getValue().equals("")) {
			result = new ValueResult("");
		}
		return result;
	}
}
