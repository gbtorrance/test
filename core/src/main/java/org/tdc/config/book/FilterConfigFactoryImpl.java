package org.tdc.config.book;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.tdc.config.util.Config;
import org.tdc.util.Addr;

/**
 * A {@link FilterConfigFactory} implementation.
 */
public class FilterConfigFactoryImpl implements FilterConfigFactory {

	@Override
	public FilterConfig createFilterConfig(
			Config config, String filterConfigKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
		
		String className = getClassName(config, filterConfigKey);
		Class<?> classy = getClass(className);
		Method buildMethod = getBuildMethod(classy);
		FilterConfig filterConfig = buildFilterConfig(
				buildMethod, config, filterConfigKey, 
				bookConfigRoot, bookAddr, bookName);
		return filterConfig;
	}

	@Override
	public List<FilterConfig> createFilterConfigs(
			Config config, String filterConfigsKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
		
		String filterConfigKey = filterConfigsKey + ".Filter"; 
		int count = config.getCount(filterConfigKey);
		List<FilterConfig> filterConfigs = new ArrayList<>();
		for (int i=0; i < count; i++) {
			FilterConfig filterConfig = createFilterConfig(
					config, filterConfigKey + "(" + i + ")",
					bookConfigRoot, bookAddr, bookName); 
			filterConfigs.add(filterConfig);
		}
		return filterConfigs;
	}
	
	private String getClassName(Config config, String filterConfigKey) {
		// both are optional; but at least one of type or class is required
		String type = config.getString(filterConfigKey + "[@type]", null, false); 
		String className = config.getString(filterConfigKey + "[@class]", null, false);
		
		if (type != null && className != null) {
			throw new RuntimeException("FilterConfig '" + filterConfigKey + 
					"' cannot have both a 'type' and 'class' defined");
		}
		if (className == null) {
			className = getDefaultClassName(type);
		}
		if (className == null) {
			throw new RuntimeException("Unable to locate class for FilterConfig '" + filterConfigKey + "'");
		}
		return className;
	}

	protected String getDefaultClassName(String type) {
		switch (type) {
		case "default-case-var-filter": 
			return "org.tdc.filter.DefaultCaseVarFilterConfig";
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

	private FilterConfig buildFilterConfig(
			Method buildMethod, Config config, String filterConfigKey, 
			Path bookConfigRoot, Addr bookAddr, String bookName) {
		
		Object filterConfig = null;
		try {
			filterConfig = buildMethod.invoke(
					null, config, filterConfigKey,
					bookConfigRoot, bookAddr, bookName);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException("Unable to execute static build() method for '" + filterConfigKey + "'", ex);
		}
		if (!(filterConfig instanceof FilterConfig)) {
			throw new RuntimeException("Class '" + filterConfig.getClass().getName() + 
					"' must implement FilterConfig interface");
		}
		return (FilterConfig)filterConfig;
	}
}
