package org.tdc.server.dto;

import java.util.List;

/**
 * Data Transfer Object for use with REST services. 
 */
public class ResultDTO {
	private List<MessageDTO> messages;
	
	public List<MessageDTO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageDTO> messages) {
		this.messages = messages;
	}
}
