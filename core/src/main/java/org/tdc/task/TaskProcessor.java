package org.tdc.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.config.book.TaskConfig;
import org.tdc.filter.Filter;
import org.tdc.spreadsheet.SpreadsheetFile;

/**
 * Responsible for processing, in sequence, a series of {@link Task}s
 * against a particular {@link Book}.
 */
public class TaskProcessor {
	private final List<Task> tasks;
	
	private TaskProcessor(Builder builder) {
		this.tasks = builder.tasks;
	}
	
	public void preProcessRead(SpreadsheetFile spreadsheetFile) {
		for (Task task : tasks) {
			task.preProcessRead(spreadsheetFile);
		}
	}
	
	public void processTasks() {
		for (Task task : tasks) {
			task.process();
		}
	}
	
	public void postProcessWrite(SpreadsheetFile spreadsheetFile) {
		for (Task task : tasks) {
			task.postProcessWrite(spreadsheetFile);
		}
	}
	
	public static class Builder {
		private final TaskFactory taskFactory;
		private final Book book;
		
		private List<String> taskIDsToProcess;
		private Map<String, String> taskParams;
		private Filter filter;
		private List<Task> tasks;
		
		public Builder(TaskFactory taskFactory, Book book) {
			this.taskFactory = taskFactory;
			this.book = book;
		}
		
		public Builder setTaskIDList(List<String> taskIDsToProcess) {
			this.taskIDsToProcess = taskIDsToProcess;
			return this;
		}
		
		public Builder setTaskParams(Map<String, String> taskParams) {
			this.taskParams = taskParams;
			return this;
		}
		
		public Builder setFilter(Filter filter) {
			this.filter = filter;
			return this;
		}
		
		public TaskProcessor build() {
			this.tasks = createTasks();
			return new TaskProcessor(this);
		}
		
		private List<Task> createTasks() {
			List<Task> list = new ArrayList<>();
			List<TaskConfig> taskConfigs = book.getConfig().getTaskConfigs();
			for (TaskConfig taskConfig : taskConfigs) {
				if (processThisTask(taskConfig)) {
					Task task = taskFactory.createTask(taskConfig, book, taskParams, filter);
					list.add(task);
				}
			}
			return list;
		}

		private boolean processThisTask(TaskConfig taskConfig) {
			boolean processTask = true;
			if (taskIDsToProcess != null) {
				processTask = taskIDsToProcess.stream().anyMatch(
						taskID -> taskID.equals(taskConfig.getTaskID()));
			}
			return processTask;
		}
	}
}
