package org.tdc.task;

import java.util.Map;

import org.tdc.book.Book;
import org.tdc.config.book.TaskConfig;
import org.tdc.filter.Filter;

/**
 * Interface defining a factory for creating {@link Task} instances 
 * based on {@link TaskConfig} parameters.
 */
public interface TaskFactory {
	Task createTask(TaskConfig config, Book book, 
			Map<String, String> taskParams, Filter filter);
}
