package org.tdc.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for tracking the "result" of validating or processing a
 * particular Test object during a particular processing stage.
 */
public class Result {
	private final List<Message> messages;

	public Result() {
		this.messages = new ArrayList<>();
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public void addMessage(Message message) {
		messages.add(message);
	}
}
