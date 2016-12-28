package org.tdc.config.book;

/**
 * Defines the information needed to configure a Doc ID row.
 */
public interface DocIDRowConfig {
	DocIDType getType();
	String getVariableName();
	String getLabel();
	int getIndex();
	int getRowNum();
	boolean isIdentityEqual(DocIDRowConfig compareTo);
}
