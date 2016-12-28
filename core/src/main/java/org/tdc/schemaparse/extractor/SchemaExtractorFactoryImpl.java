package org.tdc.schemaparse.extractor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.tdc.config.util.Config;

/**
 * A {@link SchemaExtractorFactory} implementation.
 */
public class SchemaExtractorFactoryImpl implements SchemaExtractorFactory {

	@Override
	public SchemaExtractor createSchemaExtractor(Config config, String extractorKey) {
		String className = getClassName(config, extractorKey);
		Class<?> classy = getClass(className);
		Method buildMethod = getBuildMethod(classy);
		SchemaExtractor extractor = buildExtractor(buildMethod, config, extractorKey);
		return extractor;
	}

	@Override
	public List<SchemaExtractor> createSchemaExtractors(Config config, String extractorsKey) {
		String extractorKey = extractorsKey + ".SchemaExtractor"; 
		int count = config.getCount(extractorKey);
		List<SchemaExtractor> extractors = new ArrayList<>();
		for (int i=0; i < count; i++) {
			extractors.add(createSchemaExtractor(config, extractorKey + "(" + i + ")"));
		}
		return extractors;
	}
	
	private String getClassName(Config config, String extractorKey) {
		// both are optional; but at least one of type or class is required
		String type = config.getString(extractorKey + "[@type]", null, false); 
		String className = config.getString(extractorKey + "[@class]", null, false);
		
		if (type != null && className != null) {
			throw new RuntimeException("SchemaExtractor '" + extractorKey + 
					"' cannot have both a 'type' and 'class' defined");
		}
		if (className == null) {
			className = getDefaultClassName(type);
		}
		if (className == null) {
			throw new RuntimeException("Unable to locate class for SchemaExtractor '" + extractorKey + "'");
		}
		return className;
	}

	protected String getDefaultClassName(String type) {
		switch (type) {
		case "annotation": 
			return "org.tdc.schemaparse.extractor.DefaultSchemaAnnotationExtractor";
		case "dataType": 
			return "org.tdc.schemaparse.extractor.DefaultSchemaDataTypeExtractor";
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
			buildMethod = classy.getMethod("build", Config.class, String.class);
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
				"' must have a static build() method with Config and String parameters";
		throw new RuntimeException(message, ex);
	}

	private SchemaExtractor buildExtractor(Method buildMethod, Config config, String extractorKey) {
		Object extractor = null;
		try {
			extractor = buildMethod.invoke(null, config, extractorKey);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException("Unable to execute static build() method for '" + extractorKey + "'", ex);
		}
		if (!(extractor instanceof SchemaExtractor)) {
			throw new RuntimeException("Class '" + extractor.getClass().getName() + 
					"' must implement SchemaExtractor interface");
		}
		return (SchemaExtractor)extractor;
	}
}
