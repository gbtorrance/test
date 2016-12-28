package org.tdc.schemavalidate;

import javax.xml.validation.Validator;

import org.tdc.book.TestDoc;
import org.tdc.dom.DOMUtil;
import org.tdc.modelinst.NodeInst;
import org.tdc.result.Message;
import org.tdc.result.Result;
import org.tdc.util.Util;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * {@link ErrorHandler} implementation that collects {@link Message}s 
 * during the validation of a {@link TestDoc}.  
 */
public class SchemaValidatorErrorHandler implements ErrorHandler {
	
	public static final String MESSAGE_TYPE_SCHEMA_ERROR = "schema-error";
	public static final String MESSAGE_TYPE_SCHEMA_FATAL_ERROR = "schema-fatal-error";
	public static final String MESSAGE_TYPE_SCHEMA_WARNING = "schema-warning";

	private static final String CURRENT_ELEMENT_PROPERTY = 
			"http://apache.org/xml/properties/dom/current-element-node";
	
	private final Validator validator;
	private final TestDoc testDoc;
	private final int maxMessages;
	private final Result result;
	
	public SchemaValidatorErrorHandler(Validator validator, TestDoc testDoc, int maxMessages) {
		this.validator = validator;
		this.testDoc = testDoc;
		this.maxMessages = maxMessages;
		result = testDoc.getResults().getSchemaValidateResult()
				.orElseThrow(() -> new IllegalStateException("SchemaValidateResult not set"));
	}
	
	@Override
	public void error(SAXParseException ex) throws SAXException {
		logParseException(ex, MESSAGE_TYPE_SCHEMA_ERROR);
	}

	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		logParseException(ex, MESSAGE_TYPE_SCHEMA_FATAL_ERROR);
	}

	@Override
	public void warning(SAXParseException ex) throws SAXException {
		logParseException(ex, MESSAGE_TYPE_SCHEMA_WARNING);
	}
	
	private void logParseException(SAXParseException ex, String type) {
		try {
			Element currentElement = (Element)validator.getProperty(CURRENT_ELEMENT_PROPERTY);
			NodeInst currentNodeInst = 
					(NodeInst)currentElement.getUserData(DOMUtil.DOM_USER_DATA_RELATED_TDC_NODE);
			int rowNum = testDoc.getPageConfig().getPageStructConfig().getNodeRowStart() + 
					currentNodeInst.getRowOffset();
			int colNum = testDoc.getColNum();
			String pageName = testDoc.getPageConfig().getPageName();
			String cellRef = testDoc.getColLetter() + rowNum;
			Message message = 
					new Message.Builder(type, ex.getMessage())
					.setRowNumColNum(rowNum, colNum)
					.setPageName(pageName)
					.setCellRef(cellRef)
					.setValue(currentElement.getNodeValue())
					.build();
			result.addMessage(message);
		}
		catch (SAXNotRecognizedException | SAXNotSupportedException e) {
			throw new UnsupportedOperationException(e);
		}
		if (maxMessages != Util.NO_LIMIT && result.getMessages().size() >= maxMessages) {
			throw new MaxMessagesExceededException();
		}
	}
	
	@SuppressWarnings("serial")
	class MaxMessagesExceededException extends RuntimeException {
		public MaxMessagesExceededException() {
			super();
		}
	}
}
