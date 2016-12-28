package org.tdc.schemaparse.extractor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.util.Config;
import org.tdc.modeldef.AttribNodeDef;
import org.tdc.modeldef.ElementNodeDef;
import org.tdc.modeldef.NodeDef;
import org.w3c.dom.Node;

/**
 * Implementation of {@link SchemaAnnotationExtractor}.
 * 
 * <p>This implementation supports the reading of multiple annotations (using XPath Expressions)
 * and the subsequent setting of these annotation values to variables associated with the 
 * corresponding {@link NodeDef}s.
 */
public class DefaultSchemaAnnotationExtractor implements SchemaAnnotationExtractor {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultSchemaAnnotationExtractor.class);
	
	private final List<AnnotationInfo> annotations;
	
	public DefaultSchemaAnnotationExtractor(List<AnnotationInfo> annotations) {
		this.annotations = annotations;
	}
	
	@Override
	public void extractAnnotation(XSAnnotation xsAnnotation, NodeDef nodeDef) {
		if (xsAnnotation != null) {
			for (AnnotationInfo annotation : annotations) {
					boolean match = 
						annotation.isElement && nodeDef instanceof ElementNodeDef ||
						annotation.isAttrib && nodeDef instanceof AttribNodeDef;
				if (match) {
					extract(xsAnnotation, nodeDef, annotation);
				}
			}
		}
	}
	
	private void extract(XSAnnotation xsAnnotation, NodeDef nodeDef, AnnotationInfo annotation) {
		// TODO this is a decent solution, but it doesn't properly deal with namespaces; 
		//      possibly implement something using a NamespaceContext
		//      http://stackoverflow.com/questions/6390339/how-to-query-xml-using-namespaces-in-java-with-xpath
		Node dom = new DocumentImpl();
		xsAnnotation.writeAnnotation(dom,  XSAnnotation.W3C_DOM_DOCUMENT);
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			String result = xpath.evaluate(annotation.xpathExpression, dom);
			nodeDef.setVariable(annotation.variableName, result);
		} 
		catch (XPathExpressionException ex) {
			throw new RuntimeException("Invalid XPathExpression: " + annotation.xpathExpression, ex);
		}
	}
	
	/**
	 * Static builder method to create an instance of this class based on XML configuration settings.
	 * 
	 * @param config Config class.
	 * @param key Configuration key pointing to location of information to read.
	 * @return Instance of this class.
	 */
	public static DefaultSchemaAnnotationExtractor build(Config config, String key) {
		String annotationKey = key + ".Annotations.Annotation";
		List<AnnotationInfo> annotations = getAnnotations(config, annotationKey);
		return new DefaultSchemaAnnotationExtractor(annotations);
	}
	
	private static List<AnnotationInfo> getAnnotations(Config config, String annotationsKey) {
		int count = config.getCount(annotationsKey);
		if (count == 0) {
			throw new IllegalStateException("SchemaAnnotationExtractor must have at least 1 '" + annotationsKey + "'");
		}
		
		List<AnnotationInfo> annotations = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			String annotationKey = annotationsKey + "(" + i + ")";
			String xpathExpression = config.getString(annotationKey + ".XPathExpression", null, true);
			String variableName = config.getString(annotationKey + ".VariableName", null, true);
			String type = config.getString(annotationKey + "[@type]", "both", false);
			boolean isElement = type.equals("element") || type.equals("both");
			boolean isAttrib = type.equals("attribute") || type.equals("both");
			if (!isElement && !isAttrib) {
				throw new IllegalStateException("Annotation type, if provided, must be 'element', 'attribute', or 'both'");
			}
			AnnotationInfo annotationInfo = new AnnotationInfo(xpathExpression, variableName, isElement, isAttrib);
			annotations.add(annotationInfo);
		}
		return annotations;
	}
	
	/**
	 * Data value class used for internally storing information about annotations to read. 
	 */
	private static class AnnotationInfo {
		public final String xpathExpression;
		public final String variableName;
		public final boolean isElement;
		public final boolean isAttrib;
		
		public AnnotationInfo(String xpathExpression, String variableName, 
				boolean isElement, boolean isAttrib) {
			this.xpathExpression = xpathExpression;
			this.variableName = variableName;
			this.isElement = isElement;
			this.isAttrib = isAttrib;
		}
	}
}
