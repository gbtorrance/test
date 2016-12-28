package org.tdc.server.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class TaskResultDTO extends ResultDTO {
	public String taskID;

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
}
