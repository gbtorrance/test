package org.tdc.spreadsheet;

import java.nio.file.Path;

/**
 * Factory for creating {@link SpreadsheetFileFactory} instances.
 */
public interface SpreadsheetFileFactory {
	SpreadsheetFile createNewSpreadsheetFile();
	SpreadsheetFile createReadOnlySpreadsheetFileFromPath(Path path);
	SpreadsheetFile createEditableSpreadsheetFileFromPath(Path path);
}
