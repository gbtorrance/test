package org.tdc.book;

import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Factory for creating {@link Book} instances.
 */
public interface BookFactory {
	Book getBook(SpreadsheetFile spreadsheetFile);
}
