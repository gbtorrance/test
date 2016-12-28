package org.tdc.evaluator;

import org.tdc.evaluator.result.ValuePlusStyleResult;
import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.TDCNode;
import org.tdc.spreadsheet.CellStyle;

/**
 * An {@link Evaluator} implementation for combining value results with {@link CellStyle} results.
 * 
 * <p>Supports the combining of values results (produced by an Evaluator) and
 * style information.
 * 
 * <p>Exactly one 'Evaluator' and one 'Style' element must be included in a 
 * 'value-plus-style' Evaluator block.
 * 
 * <p>This Evaluator is particularly helpful when combined with an {@link IfEvaluator}.
 * When the 'Then' or 'Else' block contains a ValuePlusStyleEvaluator, different styles
 * can be applied to a spreadsheet cell (based on the results of the 'If' condition).
 * This is ideal for highlighting changes resulting from schema updates.
 * 
 * <pre>
 * {@code
 * <Evaluator type="value-plus-style">
 *     <Evaluator.../>
 *     <Style>
 *         <FontName>Calibri</FontName>
 *         <FontHeight>11</FontHeight>
 *         ...
 *     </Style>
 * </Evaluator>
 * }
 * </pre>
 */
public class ValuePlusStyleEvaluator implements Evaluator {
	private final Evaluator evaluator;
	private final CellStyle style;
	
	public ValuePlusStyleEvaluator(Evaluator evaluator, CellStyle style) {
		this.evaluator = evaluator;
		this.style = style;
	}

	@Override
	public ValueResult evaluate(TDCNode currentNode, TDCNode prevNode) {
		String value = evaluator.evaluate(currentNode, prevNode).getValue();
		return new ValuePlusStyleResult(value, style);
	}
}
