package org.tdc.config.book;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.tdc.spreadsheet.CellStyle;
import org.tdc.util.Addr;

/**
 * Defines getters for configuration items applicable to {@link org.tdc.book.Book Books}.
 */
public interface BookConfig {
	Path getBooksConfigRoot();
	String getBooksConfigRootPropKey();
	Addr getAddr();
	Path getBookConfigRoot();
	String getBookConfigRootPropKey();
	String getBookName();
	String getBookDescription();
	Path getBookTemplateFile();
	String getBookTemplateFileExtension();
	Map<String, DocTypeConfig> getDocTypeConfigs();
	Map<String, PageStructConfig> getPageStructConfigs();
	Map<String, PageConfig> getPageConfigs();
	List<FilterConfig> getFilterConfigs();
	List<TaskConfig> getTaskConfigs();
	CellStyle getDefaultStyle();
	CellStyle getNodeHeaderStyle();
	CellStyle getDefaultNodeStyle();
	CellStyle getParentNodeStyle();
	CellStyle getAttribNodeStyle();
	CellStyle getCompositorNodeStyle();
	CellStyle getChoiceMarkerNodeStyle();
	CellStyle getOccurMarkerNodeStyle();
	CellStyle getNodeDetailHeaderStyle();
	CellStyle getDefaultNodeDetailStyle();
	CellStyle getDocIDRowLabelStyle();
	CellStyle getConversionNewRowStyle();
	CellStyle getConversionPrevNewRowStyle();
	CellStyle getDefaultLogStyle();
	CellStyle getHeaderLogStyle();
	CellStyle getErrorLogStyle();
}
