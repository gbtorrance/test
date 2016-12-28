package org.tdc.evaluator;

import org.tdc.evaluator.operator.Operator;
import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;

/**
 * An {@link Evaluator} implementation for if-then-else statements.
 * 
 * <p>The 'If' block  must always contain exactly two Evaluators.
 * They will be evaluated and the results compared using the defined 'operator' attribute.
 * 
 * <p>If the 'If' block evaluates to true, the Evaluator in the 'Then' block will be
 * evaluated and the result returned.
 * 
 * <p>If the 'If' block evaluates to false, the Evaluator in the 'Else' block will be
 * evaluated and the result returned. 
 * If no 'Else' block is provided an empty result will be returned.
 * 
 * <p>Currently two operators are supported: 'equals' and 'not-equals'.
 * 
 * <pre>
 * {@code
 * <Evaluator type="if" operator="equals">
 *     <If>
 *         <Evaluator.../>
 *         <Evaluator.../>
 *     </If>
 *     <Then>
 *         <Evaluator.../>
 *     </Then>
 *     <Else>
 *         <Evaluator.../>
 *     </Else>
 * </Evaluator>
 * }
 * </pre>
 */
public class IfEvaluator implements Evaluator {
	private final Operator operator;
	private final Evaluator operand1;
	private final Evaluator operand2;
	private final Evaluator thenEval;
	private final Evaluator elseEval; // optional
	
	public IfEvaluator(
			Operator operator, Evaluator operand1, Evaluator operand2, 
			Evaluator thenEval, Evaluator elseEval) {
		
		this.operator = operator;
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.thenEval = thenEval;
		this.elseEval = elseEval;
	}
	
	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		ValueResult result1 = operand1.evaluate(currentNode, prevNode);
		ValueResult result2 = operand2.evaluate(currentNode, prevNode);
		ValueResult result;
		if (operator.testTrue(result1.getValue(), result2.getValue())) {
			result = thenEval.evaluate(currentNode, prevNode);
		}
		else if (elseEval != null) {
			result = elseEval.evaluate(currentNode, prevNode);
		}
		else {
			result = new ValueResult("");
		}
		return result;
	}
}
