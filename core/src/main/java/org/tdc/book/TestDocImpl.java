package org.tdc.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tdc.config.book.DocIDRowConfig;
import org.tdc.config.book.DocIDType;
import org.tdc.config.book.PageConfig;
import org.tdc.result.Results;
import org.tdc.spreadsheet.Spreadsheet;
import org.tdc.spreadsheet.SpreadsheetFile;
import org.w3c.dom.Document;

/**
 * A {@link TestDoc} implementation.
 */
public class TestDocImpl implements TestDoc {
	private final PageConfig pageConfig;
	private final int colNum;
	private final String colLetter;
	private final int caseNum;
	private final String setName;
	private final Map<String, String> docVariables;
	private final Map<String, String> caseVariables;
	private final Results results;
	
	private Document domDocument;
	
	private TestDocImpl(Builder builder) {
		this.pageConfig = builder.pageConfig;
		this.colNum = builder.colNum;
		this.colLetter = builder.colLetter;
		this.caseNum = builder.caseNum;
		this.setName = builder.setName;
		this.docVariables = Collections.unmodifiableMap(builder.docVariables); // unmodifiable
		this.caseVariables = Collections.unmodifiableMap(builder.caseVariables); // unmodifiable
		this.results = builder.results;
	}
	
	@Override
	public PageConfig getPageConfig() {
		return pageConfig;
	}

	@Override
	public int getColNum() {
		return colNum;
	}

	@Override
	public String getColLetter() {
		return colLetter;
	}

	@Override
	public int getCaseNum() {
		return caseNum;
	}

	@Override
	public String getSetName() {
		return setName;
	}

	@Override
	public Map<String, String> getDocVariables() {
		return docVariables;
	}

	@Override
	public Map<String, String> getCaseVariables() {
		return caseVariables;
	}

	@Override
	public Document getDOMDocument() {
		return domDocument;
	}

	@Override
	public void setDOMDocument(Document domDocument) {
		this.domDocument = domDocument;
	}
	
	@Override
	public Results getResults() {
		return results;
	}

	@Override
	public String toString() {
		return pageConfig.getPageName() + "!" + colLetter + " [" + caseNum + 
				(setName == null || setName.length() == 0 ? "" : ", " + setName) + "]";
	}

	public static class Builder {
		private final PageConfig pageConfig;
		private final SpreadsheetFile spreadsheetFile;
		
		private int colNum;
		private String colLetter;
		private int caseNum;
		private String setName;
		private Map<String, String> docVariables;
		private Map<String, String> caseVariables;
		private Results results;
		
		public Builder(PageConfig pageConfig, SpreadsheetFile spreadsheetFile) {
			this.pageConfig = pageConfig;
			this.spreadsheetFile = spreadsheetFile;
		}
		
		public List<TestDoc> buildAll() {
			List<TestDoc> testDocs = new ArrayList<>();
			Spreadsheet sheet = 
					spreadsheetFile.getSpreadsheet(pageConfig.getPageName());
			if (sheet != null) {
				int caseNumRowNum = pageConfig.getPageStructConfig().getCaseNumDocIDRowConfig().getRowNum();
				int startCol = pageConfig.getPageStructConfig().getTestDocColStart();
				int endCol = sheet.getLastColNum(caseNumRowNum);
				for (int colNum = startCol; colNum <= endCol; colNum++) {
					String caseNum = sheet.getCellValue(caseNumRowNum, colNum);
					// if case num specified, consider this a valid test doc; otherwise ignore
					if (caseNum.trim().length() > 0) {
						TestDoc testDoc = build(sheet, colNum);
						testDocs.add(testDoc);
					}
				}
			}
			return testDocs;
		}
		
		private TestDoc build(Spreadsheet sheet, int colNum) {
			this.colNum = colNum;
			this.colLetter = sheet.getColLetter(colNum);
			String caseNumStr = sheet.getCellValue(
					pageConfig.getPageStructConfig().getCaseNumDocIDRowConfig().getRowNum(), colNum);
			try {
				caseNum = Integer.parseUnsignedInt(caseNumStr);
			}
			catch (NumberFormatException ex) {
				throw new RuntimeException("Case Num must be a positive number for page '" + 
						sheet.getName() + "' column " + colNum, ex);
			}
			DocIDRowConfig setNameConfig = 
					pageConfig.getPageStructConfig().getSetNameDocIDRowConfig();
			setName = setNameConfig == null ? 
					"" : sheet.getCellValue(setNameConfig.getRowNum(), colNum);
			buildVariables(sheet);
			results = new Results();
			return new TestDocImpl(this);
		}

		private void buildVariables(Spreadsheet sheet) {
			docVariables = new HashMap<>();
			caseVariables = new HashMap<>();
			List<DocIDRowConfig> varConfigs = 
					pageConfig.getPageStructConfig().getVarDocIDRowConfigs();
			for (DocIDRowConfig var : varConfigs) {
				String varName = var.getVariableName();
				String value = sheet.getCellValue(var.getRowNum(), colNum).trim();
				if (var.getType() == DocIDType.DOC_VARIABLE) {
					docVariables.put(varName, value);
				}
				else { // DocIDType.CASE_VARIABLE
					caseVariables.put(varName, value);
				}
			}
		}
	}
}
