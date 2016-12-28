package org.tdc.schemaparse;

import java.nio.file.Path;

import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * Builder that creates an Apache Xerses {@link XSModel} in-memory representation of a set of schemas. 
 */
public class ModelDefXSModelBuilder {
	
	private final boolean failOnParserWarning;
	private final boolean failOnParserNonFatalError;
	private final DOMImplementationRegistry domRegistry;
	private final XSLoader xsLoader;
	
	public ModelDefXSModelBuilder(boolean failOnParserWarning, boolean failOnParserNonFatalError) {
		this.failOnParserWarning = failOnParserWarning;
		this.failOnParserNonFatalError = failOnParserNonFatalError;
		this.domRegistry = initDOMRegistry();
		this.xsLoader = initXSLoader();
	}
	
	public XSModel buildXSModelFromSchemas(Path rootSchemaFile) {
		String uri = rootSchemaFile.toUri().toString();
		return xsLoader.loadURI(uri);
	}
	
	private XSLoader initXSLoader() {
		DOMErrorHandler errorHandler = 
				new ModelDefDOMErrorHandler(failOnParserWarning, failOnParserNonFatalError);
		XSImplementation xsImpl = (XSImplementation)domRegistry.getDOMImplementation("XS-Loader");
		XSLoader loader = xsImpl.createXSLoader(null);
		loader.getConfig().setParameter("error-handler", errorHandler);
		return loader;
	}

	private DOMImplementationRegistry initDOMRegistry() {
		System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMXSImplementationSourceImpl");
		try {
			return DOMImplementationRegistry.newInstance();
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
			// intentionally converting mostly checked exceptions to unchecked, as any exception
			// is not something the system can be expected to resolve; indicates a coding or configuration problem
			throw new RuntimeException("Unable to instantiate DOMImplementationRegistry", e);
		}
	}
}
