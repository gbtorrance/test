package org.tdc.schemavalidate;

import org.tdc.book.TestDoc;

/**
 * Defines functionality for performing schema validation on {@link TestDoc}s. 
 */
public interface SchemaValidator {
	void validate(TestDoc testDoc);
}
