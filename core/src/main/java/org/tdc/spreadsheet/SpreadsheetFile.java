package org.tdc.spreadsheet;

import java.nio.file.Path;

/**
 * Defines core functionality for working with a set of {@link Spreadsheet}s 
 * (which are typically stored in a single file on disk).
 */
public interface SpreadsheetFile {
	Spreadsheet getSpreadsheet(String name);
	Spreadsheet createSpreadsheet(String name);
	void deleteSpreadsheet(String name);
	void setDefaultCellStyle(CellStyle style);
	void save(Path path);
	void saveAsNew(Path path);
	void setSpreadsheetHidden(String name, boolean hide);
	void close();
}
