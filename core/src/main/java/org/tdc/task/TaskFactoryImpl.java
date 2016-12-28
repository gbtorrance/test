package org.tdc.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.tdc.book.Book;
import org.tdc.config.book.TaskConfig;
import org.tdc.filter.Filter;

/**
 * A {@link TaskFactory} implementation.
 */
public class TaskFactoryImpl implements TaskFactory {

	@Override
	public Task createTask(
			TaskConfig config, Book book, 
			Map<String, String> taskParams, Filter filter) {
		
		String className = config.getTaskClassName();
		Class<?> classy = getClass(className);
		Method buildMethod = getBuildMethod(classy);
		Task task = buildTask(buildMethod, config, book, taskParams, filter);
		return task;
	}

	private Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		}
		catch (Exception ex) {
			throw new RuntimeException("Unable to locate class: " + className, ex);
		}
	}

	private Method getBuildMethod(Class<?> classy) {
		Method buildMethod = null;
		try {
			buildMethod = classy.getMethod(
					"build", TaskConfig.class, Book.class, Map.class, Filter.class);
		} 
		catch (NoSuchMethodException | SecurityException ex) {
			throwBuildMethodNotFoundException(classy, ex);
		}
		if (!Modifier.isStatic(buildMethod.getModifiers())) {
			throwBuildMethodNotFoundException(classy, null);
		}
		return buildMethod;
	}

	private void throwBuildMethodNotFoundException(Class<?> classy, Exception ex) {
		String message =
				"Class '" + classy.getName() + "' must have a static " + 
				"build(TaskConfig config, Book book, " + 
				"Map<String, String> taskParams, Filter filter) method";
		throw new RuntimeException(message, ex);
	}

	private Task buildTask(
			Method buildMethod, TaskConfig config, 
			Book book, Map<String, String> taskParams, Filter filter) {
		
		Object task = null;
		try {
			task = buildMethod.invoke(null, config, book, taskParams, filter);
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to execute static " + 
					"build(TaskConfig config, Book book, " + 
					"Map<String, String> taskParams, Filter filter) " + 
					"method for Task '" + config.getTaskClassName() + "'", e);
		} 
		catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException().getMessage(), e);
		}
		if (!(task instanceof Task)) {
			throw new RuntimeException("Class '" + task.getClass().getName() + 
					"' must implement Task interface");
		}
		return (Task)task;
	}
}
