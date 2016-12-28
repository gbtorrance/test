package org.tdc.config.book;

/**
 * Defines getters for configuration items applicable to DocTypes.
 */
public interface DocTypeConfig {
	String getDocTypeName();
	int getMinPerTestCase();
	int getMaxPerTestCase();
}
