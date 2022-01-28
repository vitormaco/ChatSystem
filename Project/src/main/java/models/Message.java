package models;

import java.sql.Timestamp;

public class Message {
	private Timestamp timestamp;
	private String content;
	private String senderMac;

	public Message(String message, String mac) {
		timestamp = new Timestamp(System.currentTimeMillis());
		content = message;
		senderMac = mac;
	}

	public Message(String message, String mac, Timestamp t) {
		timestamp = t;
		content = message;
		senderMac = mac;
	}

	public String getContent() {
		return content;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getSenderId() {
		return senderMac;
	}
}
