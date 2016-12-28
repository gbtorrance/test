package org.tdc.config.book;

import java.util.List;

/**
 * Defines getters for configuration items for page structures.
 */
public interface PageStructConfig {
	String getPageStructName();
	List<NodeDetailColumnConfig> getNodeDetailColumnConfigs();
	List<DocIDRowConfig> getDocIDRowConfigs();
	DocIDRowConfig getCaseNumDocIDRowConfig();
	DocIDRowConfig getSetNameDocIDRowConfig();
	List<DocIDRowConfig> getVarDocIDRowConfigs();
	int getNodeColumnCount();
	int getNodeColumnWidth();
	int getHeaderRowCount();
	String getNodeHeaderLabel(int headerRowNum);
	int getDocIDRowStart();
	int getDocIDRowLabelCol();
	int getHeaderRowStart();
	int getNodeRowStart();
	int getNodeColStart();
	int getNodeDetailColStart();
	int getTestDocColStart();
}
