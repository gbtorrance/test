package org.tdc.server.dto;

import java.util.List;

/**
 * Data Transfer Object for use with REST services. 
 */
public class TestSetDTO {
	private String setName;
	private List<TestCaseDTO> testCases;
	private ResultsDTO results;
	
	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public List<TestCaseDTO> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<TestCaseDTO> testCases) {
		this.testCases = testCases;
	}
	
	public ResultsDTO getResults() {
		return results;
	}
	
	public void setResults(ResultsDTO results) {
		this.results = results;
	}
}
