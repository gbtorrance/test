package org.tdc.schemavalidate;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.book.TestDoc;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A {@link SchemaValidator} implementation for validating {@link TestDoc} DOM {@link Document}s.
 */
public class SchemaValidatorImpl implements SchemaValidator {
	
	private static final Logger log = LoggerFactory.getLogger(SchemaValidatorImpl.class);
	
	private final Schema schema;
	private final int maxMessages;
	
	private SchemaValidatorImpl(Builder builder) {
		// Schema objects are safe to use in a multi-threaded environment
		this.schema = builder.schema;
		this.maxMessages = builder.maxMessages;
	}
	
	@Override
	public void validate(TestDoc testDoc) {
		Document domDocument = testDoc.getDOMDocument();
		
		Validator validator = schema.newValidator();
		SchemaValidatorErrorHandler errorHandler = 
				new SchemaValidatorErrorHandler(validator, testDoc, maxMessages);
		validator.setErrorHandler(errorHandler);
		try {
			validator.validate(new DOMSource(domDocument));
		}
		catch (SchemaValidatorErrorHandler.MaxMessagesExceededException e) {
			// maximum messages exceeded; do nothing
		}
		catch (SAXException | IOException e) {
			throw new RuntimeException("Unable to validate document", e);
		}
	}
	
	public static class Builder {
		
		private final Path schemaRootFile;
		private final int maxMessages;
		
		private Schema schema;
		
		public Builder(Path schemaRootFile, int maxMessages) {
			this.schemaRootFile = schemaRootFile;
			this.maxMessages = maxMessages;
		}
		
		public SchemaValidator build() {
			StreamSource streamSource = new StreamSource(schemaRootFile.toFile());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				schema = schemaFactory.newSchema(streamSource);
			}
			catch (SAXException e) {
				throw new RuntimeException(
						"Unable to create Schema for SchemaValidator from root schema file: " + schemaRootFile);
			}
			 return new SchemaValidatorImpl(this);
		}
	}
}
