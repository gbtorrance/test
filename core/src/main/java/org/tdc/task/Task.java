package org.tdc.task;

import org.tdc.book.Book;
import org.tdc.config.book.TaskConfig;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Defines core functionality for custom Tasks that can be performed
 * on {@link Book}s. These Tasks may include such things as rule validation
 * and file generation, exporting, etc..
 */
public interface Task {
	TaskConfig getConfig();
	void preProcessRead(SpreadsheetFile spreadsheetFile);
	void process();
	void postProcessWrite(SpreadsheetFile spreadsheetFile);
}
