package org.tdc.config.system;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.tdc.config.util.Config;

/**
 * A {@link InitializerConfigFactory} implementation.
 */
public class InitializerConfigFactoryImpl implements InitializerConfigFactory {

	@Override
	public InitializerConfig createInitializerConfig(
			Config config, String initConfigKey, Path systemConfigRoot) {
		
		String className = getClassName(config, initConfigKey);
		Class<?> classy = getClass(className);
		Method buildMethod = getBuildMethod(classy);
		InitializerConfig initConfig = buildInitializerConfig(
				buildMethod, config, initConfigKey, systemConfigRoot);
		return initConfig;
	}

	@Override
	public List<InitializerConfig> createInitializerConfigs(
			Config config, String initConfigsKey, Path systemConfigRoot) {
		
		String initConfigKey = initConfigsKey + ".Initializer"; 
		int count = config.getCount(initConfigKey);
		List<InitializerConfig> initConfigs = new ArrayList<>();
		for (int i=0; i < count; i++) {
			InitializerConfig initConfig = createInitializerConfig(
					config, initConfigKey + "(" + i + ")", systemConfigRoot); 
			initConfigs.add(initConfig);
		}
		return initConfigs;
	}
	
	private String getClassName(Config config, String initConfigKey) {
		// both are optional; but at least one of type or class is required
		String type = config.getString(initConfigKey + "[@type]", null, false); 
		String className = config.getString(initConfigKey + "[@class]", null, false);
		
		if (type != null && className != null) {
			throw new RuntimeException("InitializerConfig '" + initConfigKey + 
					"' cannot have both a 'type' and 'class' defined");
		}
		if (className == null) {
			className = getDefaultClassName(type);
		}
		if (className == null) {
			throw new RuntimeException("Unable to locate class for InitializerConfig '" + initConfigKey + "'");
		}
		return className;
	}

	protected String getDefaultClassName(String type) {
		// currently no predefined types
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
					Config.class, String.class, Path.class);
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
				"build(Config config, String key, Path systemConfigRoot) method";
		throw new RuntimeException(message, ex);
	}

	private InitializerConfig buildInitializerConfig(
			Method buildMethod, Config config, 
			String initConfigKey, Path systemConfigRoot) {
		
		Object initConfig = null;
		try {
			initConfig = buildMethod.invoke(null, config, initConfigKey, systemConfigRoot);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException("Unable to execute static build() method for '" + initConfigKey + "'", ex);
		}
		if (!(initConfig instanceof InitializerConfig)) {
			throw new RuntimeException("Class '" + initConfig.getClass().getName() + 
					"' must implement InitializerConfig interface");
		}
		return (InitializerConfig)initConfig;
	}
}
