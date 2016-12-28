package org.tdc.server.dto;

/**
 * Data Transfer Object for use with REST services. 
 */
public class BookConfigDTO {
	private String addr;
	
	public String getBookAddress() {
		return addr;
	}
	
	public void setBookAddress(String addr) {
		this.addr = addr;
	}
	
	@Override
	public String toString() {
		return addr;
	}
}
