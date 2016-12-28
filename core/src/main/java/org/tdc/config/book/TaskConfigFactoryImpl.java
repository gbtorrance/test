package org.tdc.config.book;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tdc.config.util.Config;
import org.tdc.util.Addr;

/**
 * A {@link TaskConfigFactory} implementation.
 */
public class TaskConfigFactoryImpl implements TaskConfigFactory {

	@Override
	public TaskConfig createTaskConfig(
			Config config, String taskConfigKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
		
		String className = getClassName(config, taskConfigKey);
		Class<?> classy = getClass(className);
		Method buildMethod = getBuildMethod(classy);
		TaskConfig taskConfig = buildTaskConfig(
				buildMethod, config, taskConfigKey, 
				bookConfigRoot, bookAddr, bookName);
		return taskConfig;
	}

	@Override
	public List<TaskConfig> createTaskConfigs(
			Config config, String taskConfigsKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
		
		String taskConfigKey = taskConfigsKey + ".Task"; 
		int count = config.getCount(taskConfigKey);
		List<TaskConfig> taskConfigs = new ArrayList<>();
		for (int i=0; i < count; i++) {
			TaskConfig taskConfig = createTaskConfig(
					config, taskConfigKey + "(" + i + ")",
					bookConfigRoot, bookAddr, bookName); 
			taskConfigs.add(taskConfig);
		}
		ensureNoDuplicateTaskIDs(taskConfigs);
		return taskConfigs;
	}
	
	private void ensureNoDuplicateTaskIDs(List<TaskConfig> taskConfigs) {
		Set<String> taskIDSet = new HashSet<>();
		for (TaskConfig taskConfig : taskConfigs) {
			if (!taskIDSet.add(taskConfig.getTaskID())) {
				throw new IllegalStateException(
						"Task configuration already contains Task with Task ID '" + 
						taskConfig.getTaskID() + "'");
			}
		}
	}

	private String getClassName(Config config, String taskConfigKey) {
		// both are optional; but at least one of type or class is required
		String type = config.getString(taskConfigKey + "[@type]", null, false); 
		String className = config.getString(taskConfigKey + "[@class]", null, false);
		
		if (type != null && className != null) {
			throw new RuntimeException("TaskConfig '" + taskConfigKey + 
					"' cannot have both a 'type' and 'class' defined");
		}
		if (className == null && type != null) {
			className = getDefaultClassName(type);
		}
		if (className == null) {
			throw new RuntimeException("Unable to locate class for TaskConfig '" + taskConfigKey + "'");
		}
		return className;
	}

	protected String getDefaultClassName(String type) {
		switch (type) {
		case "default-export": 
			return "org.tdc.export.DefaultExportTaskConfig";
		}
		return null;
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
			buildMethod = classy.getMethod("build", 
					Config.class, String.class,
					Path.class, Addr.class, String.class);
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
				"Class '" + classy.getName() + 
				"' must have a static build(Config config, String key, " + 
				"Path bookConfigRoot, Addr bookAddr, String bookName) method";
		throw new RuntimeException(message, ex);
	}

	private TaskConfig buildTaskConfig(
			Method buildMethod, Config config, String taskConfigKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
		
		Object taskConfig = null;
		try {
			taskConfig = buildMethod.invoke(
					null, config, taskConfigKey,
					bookConfigRoot, bookAddr, bookName);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException("Unable to execute static build() method for '" + taskConfigKey + "'", ex);
		}
		if (!(taskConfig instanceof TaskConfig)) {
			throw new RuntimeException("Class '" + taskConfig.getClass().getName() + 
					"' must implement TaskConfig interface");
		}
		return (TaskConfig)taskConfig;
	}
}
