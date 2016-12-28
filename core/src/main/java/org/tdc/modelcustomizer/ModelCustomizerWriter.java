package org.tdc.modelcustomizer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.model.ModelCustomizerColumnConfig;
import org.tdc.config.model.ModelCustomizerConfig;
import org.tdc.evaluator.result.ValuePlusStyleResult;
import org.tdc.evaluator.result.ValueResult;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.MPathIndex;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.NodeDef;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Implementation of class with functionality for creating an initial customizer spreadsheet.
 */
public class ModelCustomizerWriter extends AbstractModelCustomizer {

	private static final Logger log = LoggerFactory.getLogger(ModelCustomizerWriter.class);
	
	private final MPathIndex<NodeDef> prevModelMPathIndex;

	private int maxColumns;
	
	public ModelCustomizerWriter(
			ElementNodeDef rootElement, ModelCustomizerConfig config, 
			SpreadsheetFile spreadsheetFile, MPathIndex<NodeDef> prevModelMPathIndex) {
		
		super(rootElement, config, spreadsheetFile);
		this.prevModelMPathIndex = prevModelMPathIndex;
	}
	
	public void writeCustomizer() {
		initSpreadsheetDefaultStyle();
		getSpreadsheetFile().createSpreadsheet(CUSTOMIZER_SHEET_NAME);
		maxColumns = 0;
		processTree();
		formatColumns();
		writeHeaderLabels();
	}
	
	private void initSpreadsheetDefaultStyle() {
		getSpreadsheetFile().setDefaultCellStyle(getConfig().getDefaultStyle());
	}

	@Override
	protected void processAttribNode(AttribNode node) {
		trackMaxColumns(node);

		outputNodeName(node, getConfig().getAttribNodeStyle());
		outputCustomColumns(node);
	}

	@Override
	protected void processCompositorNode(CompositorNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, getConfig().getChoiceMarkerNodeStyle());
		}
		
		outputNodeName(node, getConfig().getCompositorNodeStyle());
		outputCustomColumns(node);
	}
	
	@Override
	protected void processElementNode(ElementNode node) {
		trackMaxColumns(node);

		if (node.isChildOfChoice()) {
			outputChoiceMarker(node, getConfig().getChoiceMarkerNodeStyle());
		}
		
		CellStyle cellStyle = getConfig().getDefaultNodeStyle();
		if (node.hasChild()) {
			cellStyle = getConfig().getParentNodeStyle();
		}

		outputNodeName(node, cellStyle);
		outputCustomColumns(node);
	}
	
	private void trackMaxColumns(TDCNode node) {
		maxColumns = Integer.max(maxColumns, node.getColOffset()+1);
	}
	
	private void outputChoiceMarker(NonAttribNode node, CellStyle cellStyle) {
		getCustomizerSheet().setCellValue(">", getNodeRow(node), getNodeCol(node) - 1, cellStyle);
	}

	private void outputNodeName(TDCNode node, CellStyle cellStyle) {
		getCustomizerSheet().setCellValue(node.getDispName(), getNodeRow(node), getNodeCol(node), cellStyle);
	}
	
	private void outputCustomColumns(TDCNode node) {
		List<ModelCustomizerColumnConfig> columns = getConfig().getNodeDetailColumns(); 
		for (int i = 0; i < columns.size(); i++) {
			ModelCustomizerColumnConfig column = columns.get(i);
			CellStyle style = column.getStyle();
			ValueResult result = null;
			if (prevModelMPathIndex == null) {
				result = column.getInitAsNewEvaluator().evaluate(node, null);
			}
			else {
				TDCNode prevNode = lookupPreviousNode(node);
				result = column.getInitFromPrevEvaluator().evaluate(node, prevNode);
			}
			if (result instanceof ValuePlusStyleResult) {
				style = ((ValuePlusStyleResult)result).getCellStyle();
			}
			getCustomizerSheet().setCellValue(result.getValue(), getNodeRow(node), column.getColNum(), style);
		}
	}

	private TDCNode lookupPreviousNode(TDCNode node) {
		String mpathOfCurrentNode = node.getMPath();
		return prevModelMPathIndex.getNode(mpathOfCurrentNode);
	}

	private void formatColumns() {
		int allowedColumns = getConfig().getNodeColumnCount(); 
		if (allowedColumns < maxColumns) {
			throw new RuntimeException("TreeStructureColumnCount (" + allowedColumns + 
					") must be at least " + maxColumns + " to support this particular model");
		}
		for (int i = 1; i <= allowedColumns; i++) {
			getCustomizerSheet().setColumnWidth(i, getConfig().getNodeColumnWidth());
		}
		List<ModelCustomizerColumnConfig> columns = getConfig().getNodeDetailColumns(); 
		for (int i = 0; i < columns.size(); i++) {
			ModelCustomizerColumnConfig column = columns.get(i);
			getCustomizerSheet().setColumnWidth(column.getColNum(), column.getWidth());
		}
		getCustomizerSheet().freeze(getConfig().getNodeDetailColStart(), getConfig().getNodeRowStart());
	}

	private void writeHeaderLabels() {
		int rowCount = getConfig().getHeaderRowCount();
		List<ModelCustomizerColumnConfig> columns = getConfig().getNodeDetailColumns(); 
		for (int rowNum = 1; rowNum <= rowCount; rowNum++) {
			writeNodeHeaderLabels(rowNum);
			for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
				writeNodeDetailHeaderLabels(rowNum, columns.get(colIndex));
			}
		}
	}

	private void writeNodeHeaderLabels(int rowNum) {
		CellStyle style = getConfig().getNodeHeaderStyle();
		getCustomizerSheet().setCellValue(
				getConfig().getNodeHeaderLabel(rowNum), rowNum, 1, style);
	}

	private void writeNodeDetailHeaderLabels(
			int rowNum, ModelCustomizerColumnConfig columnConfig) {
		
		CellStyle style = getConfig().getNodeDetailHeaderStyle();
		getCustomizerSheet().setCellValue(
				columnConfig.getHeaderLabel(rowNum), rowNum, columnConfig.getColNum(), style);
	}
}
