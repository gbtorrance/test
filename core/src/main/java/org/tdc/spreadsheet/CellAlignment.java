package org.tdc.spreadsheet;

/**
 * Enum defining the types of cell alignments used in spreadsheet processing.
 */
public enum CellAlignment {
	LEFT("left"),
	CENTER("center"),
	RIGHT("right"),
	GENERAL("general");
	
	private final String configType;
	
	private CellAlignment(String configType) {
		this.configType = configType;
	}
	
	public String getConfigType() {
		return configType;
	}
	
	public static CellAlignment getCellAlignmentByConfigType(String configType) {
		for (CellAlignment align : CellAlignment.values()) {
			if (align.getConfigType().equalsIgnoreCase(configType)) {
				return align;
			}
		}
		throw new IllegalStateException("CellAlignment type '" + configType + "' is not valid");
	}
}
