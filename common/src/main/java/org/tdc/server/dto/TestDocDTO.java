package org.tdc.server.dto;

import java.util.Map;

/**
 * Data Transfer Object for use with REST services. 
 */
public class TestDocDTO {
	private String pageName;
	private String docTypeName;
	private int colNum;
	private String colLetter;
	private Map<String, String> docVariables;
	private ResultsDTO results;

	public String getPageName() {
		return pageName;
	}
	
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	public String getDocTypeName() {
		return docTypeName;
	}
	
	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}
	
	public int getColNum() {
		return colNum;
	}
	
	public void setColNum(int columnNum) {
		this.colNum = columnNum;
	}
	
	public String getColLetter() {
		return colLetter;
	}
	
	public void setColLetter(String columnLetter) {
		this.colLetter = columnLetter;
	}
	
	public Map<String, String> getDocVariables() {
		return docVariables;
	}
	
	public void setDocVariables(Map<String, String> docVariables) {
		this.docVariables = docVariables;
	}

	public ResultsDTO getResults() {
		return results;
	}
	
	public void setResults(ResultsDTO results) {
		this.results = results;
	}
}
