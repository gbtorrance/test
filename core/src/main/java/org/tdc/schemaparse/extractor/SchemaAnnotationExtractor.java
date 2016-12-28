package org.tdc.schemaparse.extractor;

import org.apache.xerces.xs.XSAnnotation;
import org.tdc.modeldef.NodeDef;

/**
 * Interface defining functionality for extracting 'annotation'
 * information during schema processing.
 * 
 * @see DefaultSchemaAnnotationExtractor
 */
public interface SchemaAnnotationExtractor extends SchemaExtractor {
	void extractAnnotation(XSAnnotation xsAnnotation, NodeDef nodeDef);
}
