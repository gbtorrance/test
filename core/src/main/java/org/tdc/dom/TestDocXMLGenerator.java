package org.tdc.dom;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Generates XML output file(s) from DOM {@link Document}s.
 * 
 * <p>Note: This class uses objects ({@link Document}, {@link Transformer}, {@link TransformerFactory})
 * that are not thread safe. As such, instances of this class should not be shared in multiple threads.
 */
public class TestDocXMLGenerator {
	
	private static final int DEFAULT_INDENT = 3;
	
	private final Transformer transformer;
	
	private Document document;

	public TestDocXMLGenerator() {
		transformer = createTransformer();
		initTransformerIndent(DEFAULT_INDENT);
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}
	
	public void setIndent(int indent) {
		initTransformerIndent(indent);
	}
	
	public void generateXML(Path fileToCreate) {
		try (OutputStream out = new FileOutputStream(fileToCreate.toFile());
				Writer writer = new OutputStreamWriter(out, "UTF-8");
				BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

			generateXMLToWriter(bufferedWriter);
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to create output file: " + fileToCreate, e);
		}
	}
	
	private void generateXMLToWriter(Writer writer) {
		Result result = new StreamResult(writer);
		Source source = new DOMSource(document);
		try {
			transformer.transform(source, result);
		}
		catch (TransformerException e) {
			throw new RuntimeException("Unable to generate XML from DOM Document", e);
		}
	}

	private Transformer createTransformer() {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		try {
			Transformer t = tFactory.newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.STANDALONE, "yes");
			return t;
		}
		catch (TransformerConfigurationException e) {
			throw new RuntimeException("Unable to create XML Transformer", e);
		}
	}

	private void initTransformerIndent(int indent) {
		if (indent > 0) {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", 
					Integer.toString(indent));
		}
		else {
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
		}
	}
}
