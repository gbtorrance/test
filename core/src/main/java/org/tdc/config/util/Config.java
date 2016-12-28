package org.tdc.config.util;

import java.util.Set;

/**
 * Defines core configuration functionality.
 */
public interface Config {
	String getString(String key, String defaultValue, boolean required);
	int getInt(String key, int defaultValue, boolean required);
	Integer getInt(String key, Integer defaultValue, boolean required);
	double getDouble(String key, double defaultValue, boolean required);
	Double getDouble(String key, Double defaultValue, boolean required);
	boolean getBoolean(String key, boolean defaultValue, boolean required);
	Boolean getBoolean(String key, Boolean defaultValue, boolean required);
	boolean hasNode(String key);
	int getMaxIndex(String key);
	int getCount(String key);
	String getEffectiveConfig();
	Set<String> getUnprocessedKeys();
	void ensureNoUnprocessedKeys();
	void throwConfigItemNotFoundException(String key);
	void logKeyValue(String key, String value, boolean isDefault);
}
