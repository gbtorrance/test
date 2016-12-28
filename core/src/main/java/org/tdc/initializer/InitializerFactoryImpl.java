package org.tdc.initializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.tdc.config.system.InitializerConfig;

/**
 * A {@link InitializerFactory} implementation.
 */
public class InitializerFactoryImpl implements InitializerFactory {

	@Override
	public Initializer createInitializer(InitializerConfig config) {
		String className = config.getInitializerClassName();
		Class<?> classy = getClass(className);
		Method buildMethod = getBuildMethod(classy);
		return buildInitializer(buildMethod, config);
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
			buildMethod = classy.getMethod("build", InitializerConfig.class);
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
				"build(InitializerConfig config) method";
		throw new RuntimeException(message, ex);
	}

	private Initializer buildInitializer(Method buildMethod, InitializerConfig config) {
		Object initializer = null;
		try {
			initializer = buildMethod.invoke(null, config);
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to execute static " + 
					"build(InitializerConfig config) " + 
					"method for Initializer '" + config.getInitializerClassName() + "'", e);
		} 
		catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException().getMessage(), e);
		}
		if (!(initializer instanceof Initializer)) {
			throw new RuntimeException("Class '" + initializer.getClass().getName() + 
					"' must implement Initializer interface");
		}
		return (Initializer)initializer;
	}
}
