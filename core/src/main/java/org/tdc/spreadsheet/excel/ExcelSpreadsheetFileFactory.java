package org.tdc.spreadsheet.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.tdc.spreadsheet.SpreadsheetFileFactory;

/**
 * A {@link SpreadsheetFileFactory} implementation.
 */
public class ExcelSpreadsheetFileFactory implements SpreadsheetFileFactory {

	@Override
	public SpreadsheetFile createNewSpreadsheetFile() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		return new ExcelSpreadsheetFile.Builder(workbook).build();
	}

	@Override
	public SpreadsheetFile createReadOnlySpreadsheetFileFromPath(Path path) {
		XSSFWorkbook workbook = createReadOnlyWorkbookFromPath(path);
		return new ExcelSpreadsheetFile.Builder(workbook).build();
	}
	
	@Override
	public SpreadsheetFile createEditableSpreadsheetFileFromPath(Path path) {
		XSSFWorkbook workbook = createEditableWorkbookFromPath(path);
		return new ExcelSpreadsheetFile.Builder(workbook).build();
	}
	
	private XSSFWorkbook createReadOnlyWorkbookFromPath(Path path) {
		try {
			// load from a file rather than a stream; 
			// uses less memory and is more efficient, but cannot be edited without
			// having to keep the Workbook open (along with associate file references/locks);
			// don't want to do this, therefore create Workbook as "read only"
			Workbook workbook = WorkbookFactory.create(path.toFile(), "", true);
			// closing underlying package references; 
			// workbook is still usable and contains all needed data from underlying file 
			workbook.close();
			return confirmXSSFWorkbookFormat(workbook, path);
		}
		catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			throw new RuntimeException("Unable to read Excel file: " + path.toString(), e);
		}
	}
	
	private XSSFWorkbook createEditableWorkbookFromPath(Path path) {
		// load from a stream; uses more memory, but allows Workbook
		// to be subsequently edited and saved *without* having to 
		// keep open references to the file on disk
		try (FileInputStream fis = new FileInputStream(path.toFile())) {
			Workbook workbook = WorkbookFactory.create(fis);
			return confirmXSSFWorkbookFormat(workbook, path);
		}
		catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			throw new RuntimeException("Unable to read Excel file: " + path.toString(), e);
		}
	}
	
	private XSSFWorkbook confirmXSSFWorkbookFormat(Workbook workbook, Path path) {
		if (!(workbook instanceof XSSFWorkbook)) {
			throw new RuntimeException(
					"Only XML-based Excel workbooks are supported (.XLSX, .XLSM): " + path.toString());
		}
		return (XSSFWorkbook)workbook;
	}
}
