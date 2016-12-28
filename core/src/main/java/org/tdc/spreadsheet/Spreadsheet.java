package org.tdc.spreadsheet;

/**
 * Defines core functionality for working with an individual spreadsheet.
 * 
 * <p>Multiple ExcelSpreadsheets are typically part of a {@link SpreadsheetFile}.
 * 
 * <p>Does not support multiple spreadsheet pages/tabs -- just a single sheet.
 * Supporting multiple pages/tabs is outside the scope of this interface.
 * 
 * <p>Note that indexes for this interface are 1-based.
 */
public interface Spreadsheet {
	
	/**
	 * Return Spreadsheet name.
	 * 
	 * @return Spreadsheet name
	 */
	String getName();
	
	/**
	 * Returns the string value for a particular cell.
	 * 
	 * @param rowNum Row number for cell value to retrieve (1-based index)
	 * @param colNum Column number for cell value to retrieve (1-based index)
	 * @return String value
	 */
	String getCellValue(int rowNum, int colNum);

	/**
	 * Sets the string value for a particular cell.
	 *
	 * @param value String value for cell
	 * @param rowNum Row number for cell value to retrieve (1-based index)
	 * @param colNum Column number for cell value to retrieve (1-based index)
	 */
	void setCellValue(String value, int rowNum, int colNum);

	/**
	 * Sets the string value for a particular cell (with optional formatting).
	 *
	 * @param value String value for cell
	 * @param rowNum Row number for cell value to retrieve (1-based index)
	 * @param colNum Column number for cell value to retrieve (1-based index)
	 * @param style Desired formatting for cell; null = use default (if specified)
	 */
	void setCellValue(String value, int rowNum, int colNum, CellStyle style);
	
	/**
	 * Returns a CellStyle containing the various style attributes for the cell.
	 * 
	 * @param rowNum Row number for CellStyle to retrieve (1-based index)
	 * @param colNum Column number for CellStyle to retrieve (1-based index); 
	 * @return CellStyle value
	 */
	CellStyle getCellStyle(int rowNum, int colNum);
	
	/**
	 * Sets the CellStyle for a particular cell.
	 * 
	 * @param rowNum Row number for cell to set (1-based index)
	 * @param colNum Column number for cell to set (1-based index)
	 * @param style CellStyle to use
	 */
	void setCellStyle(int rowNum, int colNum, CellStyle style);
	
	/**
	 * Set default cell style to use for a particular column.
	 * 
	 * @param colNum Column number for which to set style (1-based index)
	 * @param style Default CellStyle or null
	 */
	void setDefaultColumnCellStyle(int colNum, CellStyle style);

	/**
	 * Get column width in units of 1/256 the width of a character (see Apache POI docs for further details).
	 * 
	 * @see <a href="https://poi.apache.org/apidocs/org/apache/poi/xssf/usermodel/XSSFSheet.html#setColumnWidth%28int,%20int%29">Apache POI XSSFSheet.setColumnWidth()</a>
	 *
	 * @param colNum Column number for which to set width (1-based index)
	 * @return Column width in units of 1/256 the width of a character
	 */
	int getColumnWidth(int colNum);

	/**
	 * Set column width in units of 1/256 the width of a character (see Apache POI docs for further details).
	 * 
	 * @see <a href="https://poi.apache.org/apidocs/org/apache/poi/xssf/usermodel/XSSFSheet.html#setColumnWidth%28int,%20int%29">Apache POI XSSFSheet.setColumnWidth()</a>
	 *
	 * @param colNum Column number for which to set width (1-based index)
	 * @param colWidth Column width in units of 1/256 the width of a character
	 */
	void setColumnWidth(int colNum, int colWidth);
	
	/**
	 * Freeze a particular column/row.
	 * 
	 * @param colNum Number of column just to the right of freeze (1-based index).
	 * @param rowNum Number of row just to the right of freeze (1-based index).
	 */
	void freeze(int colNum, int rowNum);
	
	/**
	 * Get row num (1-based) of last row in Spreadsheet.
	 * 
	 * @return Number of last row in Spreadsheet (1-based)
	 */
	int getLastRowNum();
	
	/**
	 * Get column num (1-based) of last column in specified row in Spreadsheet.
	 * 
	 * @param rowNum Row number to inspect (1-based).
	 * @return Number of last column in row containing a value (1-based) 
	 */
	int getLastColNum(int rowNum);
	
	/**
	 * Gets the letter corresponding to a column number (e.g. col number 4 = "D"). 
	 *
	 * @param colNum Column number (1-based index).
	 * @return Column letter.
	 */
	String getColLetter(int colNum);

	/**
	 * Create a hyperlink in the sheet to the specified location.
	 * 
	 * @param targetPage Name of target page for hyperlink.
	 * @param targetCellRef Cell reference target for hyperlink.
	 * @param value Value to display in hyperlink cell.
	 * @param rowNum Row number where hyperlink should be stored (1-based).
	 * @param colNum Column number where hyperlink should be sored (1-based.
	 * @param style Style to use for hyperlink cell.
	 */
	void setHyperlink(String targetPage, String targetCellRef, String value, 
			int rowNum, int colNum, CellStyle style);
}
