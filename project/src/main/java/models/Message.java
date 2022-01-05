package models;

import java.sql.Timestamp;

import models.MessagePDU.MessageType;

public class Message {
	private Timestamp timestamp;
	private String content;
	private MessageType type;

	public Message(MessagePDU message) {
		timestamp = message.getTimestamp();
		content = message.getMessageContent();
		type = message.getMessageType();
	}
	
	public Message(String message) {
		timestamp = new Timestamp(System.currentTimeMillis());
		type = MessageType.TEXT;
		content = message;
	}

	public String getFormattedMessage() {
		return content;
	}
}
