package org.tdc.server.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class MessageDTO {
	private String type;
	private String message;
	private String pageName;
	private Integer rowNum;
	private Integer colNum;
	private String cellRef;
	private String xpath;
	private String value;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getPageName() {
		return pageName;
	}
	
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	public Integer getRowNum() {
		return rowNum;
	}
	
	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}
	
	public Integer getColNum() {
		return colNum;
	}
	
	public void setColNum(Integer colNum) {
		this.colNum = colNum;
	}
	
	public String getCellRef() {
		return cellRef;
	}
	
	public void setCellRef(String cellRef) {
		this.cellRef = cellRef;
	}
	
	public String getXpath() {
		return xpath;
	}
	
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
