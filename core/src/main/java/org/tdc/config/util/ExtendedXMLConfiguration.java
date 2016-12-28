package org.tdc.config.util;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * Overridden version of {@link XMLConfiguration} that ensures
 * that any written out XML will be properly indented.
 */
public class ExtendedXMLConfiguration extends XMLConfiguration {

	public ExtendedXMLConfiguration(CombinedConfiguration cc) {
		super(cc);
	}
	
	@Override
	protected Transformer createTransformer() throws ConfigurationException {
		Transformer transformer = super.createTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
		return transformer;
	}
}
