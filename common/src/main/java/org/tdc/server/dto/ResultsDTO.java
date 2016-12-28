package org.tdc.server.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object for use with REST services. 
 */
public class ResultsDTO {
	private ResultDTO testLoadResult;
	private ResultDTO schemaValidateResult;
	private Map<String, TaskResultDTO> taskResultMap = new HashMap<>();

	public ResultDTO getTestLoadResult() {
		return testLoadResult;
	}
	
	public void setTestLoadResult(ResultDTO testLoadResult) {
		this.testLoadResult = testLoadResult;
	}
	
	public ResultDTO getSchemaValidateResult() {
		return schemaValidateResult;
	}
	
	public void setSchemaValidateResult(ResultDTO schemaValidateResult) {
		this.schemaValidateResult = schemaValidateResult;
	}
	
//	TODO map tasks
//	public Map<String, TaskResultDTO> getTaskResults() {
//		return taskResultMap;
//	}
//	
//	public void setTaskResults(Map<String, TaskResultDTO> taskResultMap) {
//		this.taskResultMap = taskResultMap;
//	}
}
