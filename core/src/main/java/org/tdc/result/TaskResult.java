package org.tdc.result;

import org.tdc.task.Task;

/**
 * An extension of {@link Result} specific to storing information
 * resulting from processing {@link Task}s.
 */
public class TaskResult extends Result {
	private final String taskID;
	
	public TaskResult(String taskID) {
		this.taskID = taskID;
	}

	public String getTaskID() {
		return taskID;
	}
}
