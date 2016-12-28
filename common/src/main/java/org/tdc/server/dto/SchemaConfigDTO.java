package org.tdc.server.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class SchemaConfigDTO {
	private String addr;
	
	public String getSchemaAddress() {
		return addr;
	}
	
	public void setSchemaAddress(String addr) {
		this.addr = addr;
	}

	@Override
	public String toString() {
		return addr;
	}
}
