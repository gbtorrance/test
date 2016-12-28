package org.tdc.book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.TaskConfig;
import org.tdc.filter.Filter;
import org.tdc.result.Message;
import org.tdc.result.Result;
import org.tdc.result.Results;
import org.tdc.schemavalidate.SchemaValidatorErrorHandler;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Writes all {@link Result} / {@link Message} information to a Log 
 * {@link Spreadsheet} in the provided {@link SpreadsheetFile}. 
 */
public class BookSpreadsheetLogWriter {
	private static final Logger log = LoggerFactory.getLogger(BookSpreadsheetLogWriter.class);
	private static final String LOG_SPREADSHEET_NAME = "Log";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = 
			DateTimeFormatter.ofPattern("E MM/dd/yyyy 'at' hh:mm:ssa");
	
	private final Book book;
	private final SpreadsheetFile spreadsheetFile;
	private final Filter filter;
	
	private Spreadsheet logSheet;
	private int rowNum;
	private int colNum;
	
	public BookSpreadsheetLogWriter(Book book, SpreadsheetFile spreadsheetFile, Filter filter) {
		this.book = book;
		this.spreadsheetFile = spreadsheetFile;
		this.filter = filter;
	}
	
	public void writeLog() {
		rowNum = 1;
		colNum = 1;
		initLogSpreadsheet();
		writeProcessDateTime();
		writeBook();
	}

	private void initLogSpreadsheet() {
		logSheet = spreadsheetFile.getSpreadsheet(LOG_SPREADSHEET_NAME);
		if (logSheet != null) {
			spreadsheetFile.deleteSpreadsheet(LOG_SPREADSHEET_NAME);
		}
		logSheet = spreadsheetFile.createSpreadsheet(LOG_SPREADSHEET_NAME);
	}

	private void writeProcessDateTime() {
		String msg = "File processed: " + LocalDateTime.now().format(DATE_TIME_FORMATTER);
		logSheet.setCellValue(msg, rowNum, colNum, book.getConfig().getHeaderLogStyle());
		rowNum += 2;
	}
	
	private void writeBook() {
		List<TestSet> testSets = book.getTestSets();
		for (TestSet testSet : testSets) {
			if (filter == null || !filter.ignoreTestSet(testSet)) {
				writeTestSet(testSet);
			}
		}
	}

	private void writeTestSet(TestSet testSet) {
		String setName = testSet.getSetName().equals("") ? "[none]" : testSet.getSetName(); 
		String setLabel = "Test Set: " + setName;
		logSheet.setCellValue(setLabel, rowNum, colNum, book.getConfig().getHeaderLogStyle());
		rowNum++;
		colNum++;
		List<TestCase> testCases = testSet.getTestCases();
		for (TestCase testCase : testCases) {
			if (filter == null || !filter.ignoreTestCase(testSet, testCase)) {
				writeTestCase(testSet, testCase);
			}
		}
		writeResults(testSet.getResults());
		colNum--;
	}

	private void writeTestCase(TestSet testSet, TestCase testCase) {
		String setLabel = testCase.getSetName().equals("") ? 
				"" : " [Set Name: " + testCase.getSetName() + "]";
		String caseLabel = "Test Case: " + testCase.getCaseNum() + setLabel;
		logSheet.setCellValue(caseLabel, rowNum, colNum, book.getConfig().getHeaderLogStyle());
		rowNum++;
		colNum++;
		List<TestDoc> testDocs = testCase.getTestDocs();
		for (TestDoc testDoc : testDocs) {
			if (filter == null || !filter.ignoreTestDoc(testSet, testCase, testDoc)) {
				writeTestDoc(testDoc);
			}
		}
		writeResults(testCase.getResults());
		colNum--;
	}

	private void writeTestDoc(TestDoc testDoc) {
		String docLabel = "Document: " + testDoc.getPageConfig().getPageName();
		logSheet.setCellValue(docLabel, rowNum, colNum, book.getConfig().getHeaderLogStyle());
		rowNum++;
		colNum++;
		writeResults(testDoc.getResults());
		colNum--;
	}

	private void writeResults(Results results) {
		results.getTestLoadResult().ifPresent(r -> writePhase(r, "Load"));
		results.getSchemaValidateResult().ifPresent(r -> writePhase(r, "Schema"));
		for (TaskConfig task : book.getConfig().getTaskConfigs()) {
			results.getTaskResult(task.getTaskID()).ifPresent(r -> writePhase(r, "Task: " + task.getTaskID()));
		}
	}

	private void writePhase(Result result, String phaseName) {
		for (Message message : result.getMessages()) {
			logSheet.setCellValue(phaseName, rowNum, colNum, book.getConfig().getDefaultLogStyle());
			colNum++;
			writeMessage(message);
			colNum--;
		}
	}

	private void writeMessage(Message message) {
		CellStyle style = getMessageStyle(message);
		StringBuffer buf = new StringBuffer();
		concat(buf, "Page", message.getPageName());
		concat(buf, "Cell", message.getCellRef());
		concat(buf, "Value", message.getValue());
		concat(buf, "XPath", message.getXpath());
		if (buf.length() > 0) {
			buf.insert(0, " [");
			buf.append("]");
		}
		buf.insert(0, message.getMessage());
		logSheet.setCellValue(buf.toString(), rowNum, colNum + 1, style);
		createHyperlink(message);
		rowNum++;
	}

	private void concat(StringBuffer buf, String label, String value) {
		if (value != null) {
			if (buf.length() > 0) {
				buf.append(", ");
			}
			buf.append(label).append(": ").append(value);
		}
	}

	private CellStyle getMessageStyle(Message message) {
		CellStyle style = book.getConfig().getDefaultLogStyle();
		String type = message.getType();
		if (type.equals(SchemaValidatorErrorHandler.MESSAGE_TYPE_SCHEMA_ERROR) ||
				type.equals(SchemaValidatorErrorHandler.MESSAGE_TYPE_SCHEMA_FATAL_ERROR) ||
				type.equals(Message.MESSAGE_TYPE_ERROR)) {
			style = book.getConfig().getErrorLogStyle();
		}
		return style;
	}

	private void createHyperlink(Message message) {
		String pageName = message.getPageName() == null ? "" : message.getPageName();
		String cellRef = message.getCellRef() == null ? "" : message.getCellRef();
		if (pageName.length() > 0 && cellRef.length() > 0) {
			logSheet.setHyperlink(pageName, cellRef, "<------", rowNum, colNum, 
					book.getConfig().getDefaultLogStyle());
		}
	}
}
