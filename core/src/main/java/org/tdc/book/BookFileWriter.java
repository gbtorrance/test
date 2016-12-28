package org.tdc.book;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import org.tdc.config.book.BookConfig;
import org.tdc.config.book.DocIDRowConfig;
import org.tdc.config.book.PageConfig;
import org.tdc.config.book.NodeDetailColumnConfig;
import org.tdc.model.AttribNode;
import org.tdc.model.CompositorNode;
import org.tdc.model.ElementNode;
import org.tdc.model.MPathIndex;
import org.tdc.model.ModelVisitor;
import org.tdc.model.NonAttribNode;
import org.tdc.model.TDCNode;
import org.tdc.modelinst.ElementNodeInst;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;
import org.tdc.modelinst.NodeInst;
import org.tdc.modelinst.NonAttribNodeInst;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.util.Util;

/**
 * Implements functionality for writing a new {@link Book} spreadsheet file confirming to a {@link BookConfig}. 
 */
public class BookFileWriter {
	
	private final BookConfig config;
	private final SpreadsheetFile spreadsheetFile;
	private final ModelInstFactory modelInstFactory;
	
	private int maxColumns;
	private PageConfig currentPageConfig;
	private ModelInst currentModelInst;
	private Spreadsheet currentSheet;
	private PageConfig basedOnPageConfig;
	private MPathIndex<NodeInst> basedOnIndex;
	private Spreadsheet basedOnSheet;
	private int basedOnColStart;
	private int basedOnColEnd;
	
	public BookFileWriter(BookConfig config, SpreadsheetFile spreadsheetFile, ModelInstFactory modelInstFactory) {
		this.config = config;
		this.spreadsheetFile = spreadsheetFile;
		this.modelInstFactory = modelInstFactory;
	}

	public void write() {
		initSpreadsheetDefaultStyle();
		writePages(null, null);
		writeConfigSheet();
	}
	
	public void writeWithTestDataFromExistingBook(Book basedOnBook, SpreadsheetFile basedOnBookSF) {
		initSpreadsheetDefaultStyle();
		writePages(basedOnBook, basedOnBookSF);
		writeConfigSheet();
	}
	
	public void deleteDefaultSheetIfExists() {
		// when using a template file, a default sheet will always exist 
		// (because Excel won't save a file without at least one sheet);
		String defaultSheetName = "Sheet1";
		if (spreadsheetFile.getSpreadsheet(defaultSheetName) != null) {
			spreadsheetFile.deleteSpreadsheet(defaultSheetName);
		}
	}
	
	private void initSpreadsheetDefaultStyle() {
		spreadsheetFile.setDefaultCellStyle(config.getDefaultStyle());
	}

	private void writePages(Book basedOnBook, SpreadsheetFile basedOnBookSF) {
		Collection<PageConfig> pageConfigs = config.getPageConfigs().values();
		for (PageConfig pageConfig : pageConfigs) {
			writePage(pageConfig, basedOnBook, basedOnBookSF);
		}
	}
	
	private void writePage(PageConfig pageConfig, Book basedOnBook, SpreadsheetFile basedOnBookSF) {
		initPageVars(pageConfig, basedOnBook, basedOnBookSF);
		formatColumns();
		writeModelStructure();
		writeDocIDRowLabels();
		writeDocIDRowValuesFromBasedOnBook();
		writeHeaderLabels();
	}

	private void initPageVars(PageConfig pageConfig, Book basedOnBook, SpreadsheetFile basedOnBookSF) {
		maxColumns = 0;
		currentPageConfig = pageConfig;
		currentModelInst = modelInstFactory.getModelInst(currentPageConfig.getModelConfig());
		currentSheet = spreadsheetFile.createSpreadsheet(currentPageConfig.getPageName());
		basedOnPageConfig = null;
		basedOnIndex = null;
		basedOnSheet = null;
		basedOnColStart = Util.UNDEFINED;
		basedOnColEnd = Util.UNDEFINED;
		if (basedOnBook != null) {
			Page basedOnPage = basedOnBook.getPages().get(currentPageConfig.getPageName());
			if (basedOnPage != null) {
				basedOnPageConfig = basedOnPage.getConfig();
				basedOnIndex = basedOnPage.getModelInst().getMPathIndex();
				basedOnSheet = basedOnBookSF.getSpreadsheet(basedOnPage.getName());
				basedOnColStart = basedOnPageConfig.getPageStructConfig().getTestDocColStart();
				basedOnColEnd = basedOnSheet.getLastColNum(
						basedOnPageConfig.getPageStructConfig().getCaseNumDocIDRowConfig().getRowNum());
			}
		}
	}

	private void writeConfigSheet() {
		Spreadsheet configSheet = spreadsheetFile.createSpreadsheet(
				BookUtil.CONFIG_SHEET_NAME);
		configSheet.setCellValue(
				config.getAddr().toString(), 
				BookUtil.CONFIG_SHEET_BOOK_ADDR_ROW, BookUtil.CONFIG_SHEET_BOOK_ADDR_COL);
		spreadsheetFile.setSpreadsheetHidden(BookUtil.CONFIG_SHEET_NAME, true);
	}
	
	private void writeModelStructure() {
		ElementNodeInst rootElement = currentModelInst.getRootElement();
		ModelWriterVisitor writerVisitor = new ModelWriterVisitor();
		rootElement.accept(writerVisitor);
	}
	
	private void formatColumns() {
		int allowedColumns = currentPageConfig.getPageStructConfig().getNodeColumnCount(); 
		if (allowedColumns < maxColumns) {
			throw new RuntimeException("TreeStructureColumnCount (" + allowedColumns + 
					") must be at least " + maxColumns + " to support this particular model");
		}
		for (int i = 1; i <= allowedColumns; i++) {
			currentSheet.setColumnWidth(i, 
					currentPageConfig.getPageStructConfig().getNodeColumnWidth());
		}
		List<NodeDetailColumnConfig> columns = 
				currentPageConfig.getPageStructConfig().getNodeDetailColumnConfigs(); 
		for (int i = 0; i < columns.size(); i++) {
			NodeDetailColumnConfig columnConfig = columns.get(i);
			currentSheet.setColumnWidth(columnConfig.getColNum(), columnConfig.getWidth());
		}
		currentSheet.freeze(
				currentPageConfig.getPageStructConfig().getTestDocColStart(), 
				currentPageConfig.getPageStructConfig().getNodeRowStart());
	}
	
	private void writeDocIDRowLabels() {
		List<DocIDRowConfig> docIDRows = 
				currentPageConfig.getPageStructConfig().getDocIDRowConfigs();
		CellStyle style = config.getDocIDRowLabelStyle();
		int rowCount = docIDRows.size();
		for (int row = 0; row < rowCount; row++) {
			DocIDRowConfig docIDRow = docIDRows.get(row);
			currentSheet.setCellValue(docIDRow.getLabel(), docIDRow.getRowNum(), 
					currentPageConfig.getPageStructConfig().getDocIDRowLabelCol(), style);
		}
	}
	
	private void writeDocIDRowValuesFromBasedOnBook() {
		if (basedOnPageConfig == null) {
			return;
		}
		for (int basedOnColNum = basedOnColStart; basedOnColNum <= basedOnColEnd; basedOnColNum++) {
			int colNum = basedOnToCurrentColNum(basedOnColNum);
			List<DocIDRowConfig> docIDRows = 
					currentPageConfig.getPageStructConfig().getDocIDRowConfigs();
			int rowCount = docIDRows.size();
			for (int row = 0; row < rowCount; row++) {
				DocIDRowConfig docIDRow = docIDRows.get(row);
				DocIDRowConfig basedOnDocIDRow = currentToBasedOnDocIDRow(docIDRow);
				if (basedOnDocIDRow != null) {
					copyBasedOnCellValueAndStyleToCurrent(
							basedOnDocIDRow.getRowNum(), basedOnColNum, 
							docIDRow.getRowNum(), colNum);
				}
			}
			currentSheet.setColumnWidth(colNum, basedOnSheet.getColumnWidth(basedOnColNum));
		}
	}

	private void writeHeaderLabels() {
		int rowCount = 
				currentPageConfig.getPageStructConfig().getHeaderRowCount();
		List<NodeDetailColumnConfig> columns = 
				currentPageConfig.getPageStructConfig().getNodeDetailColumnConfigs(); 
		for (int rowNum = 1; rowNum <= rowCount; rowNum++) {
			writeNodeHeaderLabels(rowNum);
			for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
				writeNodeDetailHeaderLabels(rowNum, columns.get(colIndex));
			}
		}
	}
	
	private void writeNodeHeaderLabels(int rowNum) {
		CellStyle style = config.getNodeHeaderStyle();
		currentSheet.setCellValue(
				currentPageConfig.getPageStructConfig().getNodeHeaderLabel(rowNum), 
				currentPageConfig.getPageStructConfig().getHeaderRowStart() + rowNum -1, 1, style);
	}

	private void writeNodeDetailHeaderLabels(
			int rowNum, NodeDetailColumnConfig columnConfig) {
		
		CellStyle style = config.getNodeDetailHeaderStyle();
		currentSheet.setCellValue(
				columnConfig.getHeaderLabel(rowNum), 
				currentPageConfig.getPageStructConfig().getHeaderRowStart() + rowNum -1, 
				columnConfig.getColNum(), style);
	}

	private void trackMaxColumns(TDCNode node) {
		maxColumns = Integer.max(maxColumns, node.getColOffset()+1);
	}
	
	private int getNodeRow(TDCNode node) {
		return currentPageConfig.getPageStructConfig().getNodeRowStart() + 
				node.getRowOffset();
	}
	
	private int getNodeCol(TDCNode node) {
		return currentPageConfig.getPageStructConfig().getNodeColStart() + 
				node.getColOffset();
	}
	
	private int getBasedOnNodeRow(NodeInst basedOnNode) {
		return basedOnPageConfig.getPageStructConfig().getNodeRowStart() + 
				basedOnNode.getRowOffset();
	}
	
	private int basedOnToCurrentColNum(int basedOnColNum) {
		return basedOnColNum -
				basedOnPageConfig.getPageStructConfig().getTestDocColStart() + 
				currentPageConfig.getPageStructConfig().getTestDocColStart();
	}

	private DocIDRowConfig currentToBasedOnDocIDRow(DocIDRowConfig docIDRow) {
		List<DocIDRowConfig> basedOnDocIDRows = 
				basedOnPageConfig.getPageStructConfig().getDocIDRowConfigs();
		for (DocIDRowConfig basedOnDocIDRow : basedOnDocIDRows) {
			if (basedOnDocIDRow.isIdentityEqual(docIDRow)) {
				return basedOnDocIDRow;
			}
		}
		return null;
	}

	private void writeChoiceMarker(NonAttribNode node, CellStyle cellStyle) {
		currentSheet.setCellValue(">", getNodeRow(node), getNodeCol(node) - 1, cellStyle);
	}

	private void writeNodeName(TDCNode node, CellStyle cellStyle) {
		currentSheet.setCellValue(node.getDispName(), getNodeRow(node), getNodeCol(node), cellStyle);
	}
	
	private void writeOccurrenceMarkers(NonAttribNode nonAttribNode) {
		// current node is an element or compositor; might repeat
		writeOccurrenceMarkersMayRepeat(
				(NonAttribNodeInst)nonAttribNode, 
				getNodeRow(nonAttribNode));
	}
	
	private void writeOccurrenceMarkers(AttribNode attribNode) {
		// node is an attribute; won't repeat; 
		// but parent may; call parent
		writeOccurrenceMarkersMayRepeat(
				(NonAttribNodeInst)attribNode.getParent(), 
				getNodeRow(attribNode));
	}
	
	private void writeOccurrenceMarkersMayRepeat(NonAttribNodeInst nonAttribNodeInst, int rowNum) {
		NonAttribNodeInst possibleRepeatingNode = nonAttribNodeInst;
		do {
			if (possibleRepeatingNode.getOccurCount() > 1) {
				currentSheet.setCellValue("" + possibleRepeatingNode.getOccurNum(), 
						rowNum, getNodeCol(possibleRepeatingNode) - 1, config.getOccurMarkerNodeStyle());
			}
			possibleRepeatingNode = (NonAttribNodeInst)possibleRepeatingNode.getParent();
		} 
		while (possibleRepeatingNode != null);
	}

	private void writeCustomColumns(TDCNode node) {
		List<NodeDetailColumnConfig> columns = 
				currentPageConfig.getPageStructConfig().getNodeDetailColumnConfigs();
		for (int i = 0; i < columns.size(); i++) {
			NodeDetailColumnConfig column = columns.get(i);
			CellStyle style = column.getStyle();
			String value = "";
			String variableName = column.getReadFromVariable();
			String propertyName = column.getReadFromProperty();
			if (variableName != null) {
				value = node.getVariable(variableName);
			}
			if (propertyName != null) {
				value = Util.getStringValueFromProperty(node, propertyName, "");
			}
			currentSheet.setCellValue(value, getNodeRow(node), column.getColNum(), style);
		}
	}
	
	private void writeBasedOnBookTests(TDCNode node) {
		if (basedOnIndex != null) {
			NodeInst basedOnNode = basedOnIndex.getNode(node.getMPath());
			if (basedOnNode == null) {
				markNodeAsNew(node);
			}
			else if (basedOnSheet != null) {
				copyBasedOnNodeInfo(node, basedOnNode);
			}
		}
	}

	private void markNodeAsNew(TDCNode node) {
		for (int basedOnColNum = basedOnColStart; 
				basedOnColNum <= basedOnColEnd; basedOnColNum++) {
			
			int colNum = basedOnToCurrentColNum(basedOnColNum);
			currentSheet.setCellStyle(getNodeRow(node), 
					colNum, config.getConversionNewRowStyle());
		}
	}

	private void copyBasedOnNodeInfo(TDCNode node, NodeInst basedOnNode) {
		for (int basedOnColNum = basedOnColStart; basedOnColNum <= basedOnColEnd; basedOnColNum++) {
			int basedOnRowNum = getBasedOnNodeRow(basedOnNode);
			int colNum = basedOnToCurrentColNum(basedOnColNum);
			int rowNum = getNodeRow(node);
			copyBasedOnCellValueAndStyleToCurrent(basedOnRowNum, basedOnColNum, rowNum, colNum);
		}
	}
	
	private void copyBasedOnCellValueAndStyleToCurrent(
			int basedOnRowNum, int basedOnColNum, 
			int rowNum, int colNum) {
		
		String value = basedOnSheet.getCellValue(basedOnRowNum, basedOnColNum);
		CellStyle basedOnStyle = basedOnSheet.getCellStyle(basedOnRowNum, basedOnColNum);
		CellStyle style = getColorOnlyStyle(basedOnStyle);
		if (fillColorEqualsConversionNewRowFillColor(style)) {
			style = config.getConversionPrevNewRowStyle();
		}
		currentSheet.setCellValue(value, rowNum, colNum, style);
	}

	private CellStyle getColorOnlyStyle(CellStyle style) {
		return style == null ? 
				null : new CellStyleImpl.Builder()
				.setFrom(config.getDefaultStyle())
				.setColor(style.getColor())
				.setFillColor(style.getFillColor())
				.build();
	}

	private boolean fillColorEqualsConversionNewRowFillColor(CellStyle style) {
		Color conversionNewRowFillColor = 
				config.getConversionNewRowStyle().getFillColor();
		return style != null && 
				style.getFillColor() != null && 
				conversionNewRowFillColor != null &&
				style.getFillColor().equals(conversionNewRowFillColor);
	}

	class ModelWriterVisitor implements ModelVisitor {
		@Override
		public void visit(AttribNode node) {
			trackMaxColumns(node);
			writeNodeName(node, config.getAttribNodeStyle());
			writeOccurrenceMarkers(node); 
			writeCustomColumns(node);
			writeBasedOnBookTests(node);
		}

		@Override
		public void visit(CompositorNode node) {
			trackMaxColumns(node);
			if (node.isChildOfChoice()) {
				writeChoiceMarker(node, config.getChoiceMarkerNodeStyle());
			}
			writeNodeName(node, config.getCompositorNodeStyle());
			writeOccurrenceMarkers(node);
			writeCustomColumns(node);
			writeBasedOnBookTests(node);
		}

		@Override
		public void visit(ElementNode node) {
			trackMaxColumns(node);
			if (node.isChildOfChoice()) {
				writeChoiceMarker(node, config.getChoiceMarkerNodeStyle());
			}
			CellStyle cellStyle = config.getDefaultNodeStyle();
			if (node.hasChild()) {
				cellStyle = config.getParentNodeStyle();
			}
			writeNodeName(node, cellStyle);
			writeOccurrenceMarkers(node);
			writeCustomColumns(node);
			writeBasedOnBookTests(node);
		}
	}
}
