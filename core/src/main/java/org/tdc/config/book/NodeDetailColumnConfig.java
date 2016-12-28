package org.tdc.config.book;

import org.tdc.book.Book;
import org.tdc.book.Page;
import org.tdc.model.TDCNode;
import org.tdc.spreadsheet.CellStyle;

/**
 * Defines configuration for a Node Detail column 
 * (which provides additional descriptive information about a {@link TDCNode} on a {@link Book} {@link Page}).
 * 
 * <p>Such columns can be used for providing the user with information like node description, 
 * node data type, form line number, notes, etc.
 */
public interface NodeDetailColumnConfig {
	String getHeaderLabel(int headerRowNum);
	int getWidth();
	CellStyle getStyle(); 
	String getReadFromVariable();
	String getReadFromProperty();
	int getIndex();
	int getColNum();
}
