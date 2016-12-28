package org.tdc.evaluator.result;

import org.tdc.spreadsheet.CellStyle;

/**
 * Used for returning results from {@link org.tdc.evaluator.ValuePlusStyleEvaluator ValuePlusStyleEvaluator} evaluations.
 * 
 * <p>Result contains a text string along with a {@link CellStyle} object 
 * indicating the style to apply to the spreadsheet cell.
 */
public class ValuePlusStyleResult extends ValueResult {
	private final CellStyle style;

	public ValuePlusStyleResult(String value, CellStyle style) {
		super(value);
		if (style == null) {
			throw new IllegalArgumentException("Style cannot be null");
		}
		this.style = style;
	}
	
	public CellStyle getCellStyle() {
		return style;
	}
}
