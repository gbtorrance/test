package org.tdc.server.dto;

import java.util.List;

/**
 * Data Transfer Object for use with REST services. 
 */
public class BookDTO {
	private String addr;
	private List<TestSetDTO> testSets;
	
	public String getBookAddress() {
		return addr;
	}
	
	public void setBookAddress(String addr) {
		this.addr = addr;
	}

	public List<TestSetDTO> getTestSets() {
		return testSets;
	}

	public void setTestSets(List<TestSetDTO> testSets) {
		this.testSets = testSets;
	}
	
	@Override
	public String toString() {
		return addr;
	}
}
