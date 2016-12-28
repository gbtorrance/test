package org.tdc.config.model;

import org.tdc.evaluator.Evaluator;
import org.tdc.spreadsheet.CellStyle;

/**
 * Defines the information needed to configure a Model Customizer Column.
 */
public interface ModelCustomizerColumnConfig {
	String getHeaderLabel(int headerRowNum);
	int getWidth();
	CellStyle getStyle(); 
	Evaluator getInitAsNewEvaluator();
	Evaluator getInitFromPrevEvaluator();
	String getStoreValueWithVariableName();
	int getIndex();
	int getColNum();
}
