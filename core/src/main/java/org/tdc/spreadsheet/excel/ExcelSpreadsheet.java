package org.tdc.spreadsheet.excel;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;

/**
 * A {@link Spreadsheet} implementation for Excel worksheets.
 * 
 * <p>Multiple ExcelSpreadsheets are typically part of an {@link ExcelSpreadsheetFile}.
 * 
 * <p>Note that row and column index values are 1-based. 
 * (Internally, though, the Apache POI libraries use 0-based indexes.)
 */
public class ExcelSpreadsheet implements Spreadsheet {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelSpreadsheet.class);

	private final XSSFSheet xssfSheet;
	private XSSFCreationHelper xssfCreationHelper;
	private final ExcelStyleManager styleManager;
	
	private ExcelSpreadsheet(Builder builder) {
		this.xssfSheet = builder.xssfSheet;
		this.xssfCreationHelper = builder.xssfCreationHelper;
		this.styleManager = builder.styleManager;
	}
	
	@Override
	public String getName() {
		return xssfSheet.getSheetName();
	}
	
	@Override 
	public String getCellValue(int rowNum, int colNum) {
		XSSFCell cell = getCell(rowNum, colNum);
		return cell == null ? "" : getCellValue(cell);
	}

	@Override
	public void setCellValue(String value, int rowNum, int colNum) {
		setCellValue(value, rowNum, colNum, null);
	}
	
	@Override
	public void setCellValue(String value, int rowNum, int colNum, CellStyle style) {
		XSSFCell cell = getCellCreateIfNotExist(rowNum, colNum);
		cell.setCellValue(value.length() == 0 ? null : value);
		styleManager.setCellStyle(cell, style);
	}
	
	@Override
	public CellStyle getCellStyle(int rowNum, int colNum) {
		XSSFCell cell = getCell(rowNum, colNum);
		return styleManager.getCellStyle(cell);
	}

	@Override
	public void setCellStyle(int rowNum, int colNum, CellStyle style) {
		XSSFCell cell = getCellCreateIfNotExist(rowNum, colNum);
		styleManager.setCellStyle(cell, style);
	}
	
	@Override
	public void setDefaultColumnCellStyle(int colNum, CellStyle style) {
		if (style != null) {
			styleManager.setDefaultColumnStyle(xssfSheet, colNum, style);
		}
	}
	
	@Override
	public int getColumnWidth(int colNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		return xssfSheet.getColumnWidth(colNum-1);
	}

	@Override
	public void setColumnWidth(int colNum, int colWidth) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		xssfSheet.setColumnWidth(colNum-1, colWidth);
	}

	@Override
	public void freeze(int colNum, int rowNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		xssfSheet.createFreezePane(colNum-1, rowNum-1, colNum-1, rowNum-1);
	}
	
	@Override
	public int getLastRowNum() {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		return xssfSheet.getLastRowNum() + 1;
	}

	@Override
	public int getLastColNum(int rowNum) {
		// Strangely getLastCellNum() returns a 1-based index, 
		// so no conversion is necessary when returning
		// a 1-based index to the caller
		
		return xssfSheet.getRow(rowNum-1).getLastCellNum();
	}
	
	@Override
	public String getColLetter(int colNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		return CellReference.convertNumToColString(colNum-1);
	}

	@Override
	public void setHyperlink(String targetPage, String targetCellRef, 
			String value, int rowNum, int colNum, CellStyle style) {
		
		String linkAddr = "'" + targetPage + "'!" + targetCellRef;
		XSSFCell cell = getCellCreateIfNotExist(rowNum, colNum);
		Hyperlink hl = xssfCreationHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
		hl.setAddress(linkAddr);
		cell.setHyperlink(hl);
		cell.setCellValue(value);
	}
	
	private String getCellValue(XSSFCell cell) {
		return getCellValueByType(cell, cell.getCellType());
	}
	
	private XSSFCell getCell(int rowNum, int colNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)

		XSSFRow row = xssfSheet.getRow(rowNum-1);
		return row == null ? null : row.getCell(colNum-1);
	}
	
	private XSSFCell getCellCreateIfNotExist(int rowNum, int colNum) {
		XSSFRow row = getRowCreateIfNotExist(rowNum);
		XSSFCell cell = getCellCreateIfNotExist(row, colNum);
		return cell;
	}
	
	private XSSFCell getCellCreateIfNotExist(XSSFRow row, int colNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)

		XSSFCell cell = row.getCell(colNum-1);
		if (cell == null) {
			cell = row.createCell(colNum-1);
		}
		return cell;
	}
	
	private XSSFRow getRowCreateIfNotExist(int rowNum) {
		// Whereas the public methods in this class use 1-based index values, 
		// Apache POI libraries use 0-based indexes;
		// this method accepts 1-based indexes and converts to 0-based indexes for POI (as necessary)
		
		XSSFRow row = xssfSheet.getRow(rowNum-1);
		if (row == null) {
			row = xssfSheet.createRow(rowNum-1);
		}
		return row;
	}
	
	private String getCellValueByType(XSSFCell cell, int cellType) {
		String value = null;
		switch (cellType) {
			case XSSFCell.CELL_TYPE_BLANK:
				value = "";
				break;
			case XSSFCell.CELL_TYPE_BOOLEAN:
				value = "" + cell.getBooleanCellValue();
				break;
			case XSSFCell.CELL_TYPE_ERROR:
				// TODO better way to handle this?
				throw new UnsupportedOperationException("Currently do not support errors [sheet: " + cell.getSheet().getSheetName() + ", row: " + (cell.getRowIndex()+1) + ", col: " + (cell.getColumnIndex()+1) + "]");
			case XSSFCell.CELL_TYPE_FORMULA:
				value = getCellValueByType(cell, cell.getCachedFormulaResultType());
				break;
			case XSSFCell.CELL_TYPE_NUMERIC:
				value = cell.getRawValue();
				break;
			case XSSFCell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			default:
				throw new IllegalStateException("Cell type " + cell.getCellType() + " is unknown");
		}
		return value;
	}
	
	public static class Builder {
		private final XSSFWorkbook workbook;
		private final ExcelStyleManager styleManager;
		
		private XSSFSheet xssfSheet;
		private XSSFCreationHelper xssfCreationHelper;
		
		public Builder(XSSFWorkbook workbook, ExcelStyleManager styleManager) {
			this.workbook = workbook;
			this.styleManager = styleManager;
		}
		
		public Spreadsheet build(String name) {
			if (workbook.getSheet(name) != null) {
				throw new IllegalStateException("A worksheet with the name '" + name + "' already exists");
			}
			return build(workbook.createSheet(name));
		}
		
		public Map<String, Spreadsheet> buildAll() {
			Map<String, Spreadsheet> spreadsheets = new LinkedHashMap<>();
			int sheetCount = workbook.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Spreadsheet spreadsheet = build(workbook.getSheetAt(i));
				spreadsheets.put(spreadsheet.getName(), spreadsheet);
			}
			return spreadsheets;
		}
		
		private Spreadsheet build(XSSFSheet xssfSheet) {
			this.xssfSheet = xssfSheet;
			xssfCreationHelper = workbook.getCreationHelper();
			return new ExcelSpreadsheet(this);
		}
	}
}
