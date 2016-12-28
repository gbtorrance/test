package org.tdc.task;

import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Abstract implementation of {@link Task} to prevent subclasses
 * from having to implement rarely-used methods.
 */
public abstract class AbstractTask implements Task {

	@Override
	public void preProcessRead(SpreadsheetFile spreadsheetFile) {
		// do nothing by default; typically not needed;
		// will only be used if a task needs to read some additional
		// information from a spreadsheet file (beyond the 
		// TestSets, TestCases, and TestDocs)
	}

	@Override
	public void postProcessWrite(SpreadsheetFile spreadsheetFile) {
		// do nothing by default; typically not needed
		// will only be used if a task needs to write some additional
		// information to spreadsheet file (beyond the 
		// basic Log information)
	}
}
