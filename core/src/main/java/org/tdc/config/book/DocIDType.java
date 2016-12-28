package org.tdc.config.book;

/**
 * Enum defining the possible types for a {@link DocIDRowConfig} entry in a {@link BookConfig} file.
 */
public enum DocIDType {
	CASE_NUM("case-num", false),
	SET_NAME("set-name", false),
	DOC_VARIABLE("doc-variable", true),
	CASE_VARIABLE("case-variable", true);

	private final String configType;
	private final boolean isVariableType;
	
	private DocIDType(String configType, boolean isVariableType) {
		this.configType = configType;
		this.isVariableType = isVariableType;
	}
	
	public String getConfigType() {
		return configType;
	}
	
	public boolean isVariableType() {
		return isVariableType;
	}
	
	public static DocIDType getDocIDTypeByConfigType(String configType) {
		for (DocIDType docIDType : DocIDType.values()) {
			if (docIDType.getConfigType().equalsIgnoreCase(configType)) {
				return docIDType;
			}
		}
		throw new IllegalStateException("DocIDRow type '" + configType + "' is not valid");
	}
}
