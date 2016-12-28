package org.tdc.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.tdc.book.TestCase;
import org.tdc.book.TestDoc;
import org.tdc.book.TestSet;
import org.tdc.task.Task;

/**
 * Used for storing all {@link Result} objects associated with a particular
 * Test object ({@link TestDoc}, {@link TestCase}, {@link TestSet}).
 * 
 * {@link Result} objects may be associated with multiple stages of Test 
 * processing, including data loading, schema validation, and custom {@link Task}s.
 */
public class Results {
	private final Map<String, TaskResult> taskResultMap = new HashMap<>();

	private Result testLoadResult;
	private Result schemaValidateResult;
	
	public Optional<Result> getTestLoadResult() {
		return Optional.ofNullable(testLoadResult);
	}
	
	public void setTestLoadResult(Result testLoadResult) {
		this.testLoadResult = testLoadResult;
	}
	
	public Optional<Result> getSchemaValidateResult() {
		return Optional.ofNullable(schemaValidateResult);
	}
	
	public void setSchemaValidateResult(Result schemaValidateResult) {
		this.schemaValidateResult = schemaValidateResult;
	}
	
	public Optional<TaskResult> getTaskResult(String taskID) {
		return Optional.ofNullable(taskResultMap.get(taskID));
	}
	
	public void setTaskResult(String taskID, TaskResult taskResult) {
		taskResultMap.put(taskID, taskResult);
	}
}
