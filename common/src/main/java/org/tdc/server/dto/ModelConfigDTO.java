package org.tdc.server.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class ModelConfigDTO {
	private String addr;
	
	public String getModelAddress() {
		return addr;
	}
	
	public void setModelAddress(String addr) {
		this.addr = addr;
	}
	
	@Override
	public String toString() {
		return addr;
	}
}
