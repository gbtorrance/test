package org.tdc.server.dto;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for use with REST services. 
 */
public class TestCaseDTO {
	private int caseNum;
	private List<TestDocDTO> testDocs;
	private Map<String, String> caseVariables;
	private ResultsDTO results;

	public int getCaseNum() {
		return caseNum;
	}

	public void setCaseNum(int caseNum) {
		this.caseNum = caseNum;
	}

	public List<TestDocDTO> getTestDocs() {
		return testDocs;
	}
	
	public void setTestDocs(List<TestDocDTO> testDocs) {
		this.testDocs = testDocs;
	}

	public Map<String, String> getCaseVariables() {
		return caseVariables;
	}

	public void setCaseVariables(Map<String, String> caseVariables) {
		this.caseVariables = caseVariables;
	}

	public ResultsDTO getResults() {
		return results;
	}
	
	public void setResults(ResultsDTO results) {
		this.results = results;
	}
}
