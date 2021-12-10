package models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class MessagePDU implements Serializable {
	public enum Status {
		ACTIVE,
		CONNECTION,
		DECONNECTION,
		NICKNAME_CHANGED,
		DISCOVER,
	}

	public enum MessageType {
		NOTIFICATION,
		TEXT,
	}

	private Status status;
	private MessageType type;
	private String messageContent;
	private String timestamp;
	private String sourceNickname;
	private String sourceID;
	private byte[] sourceAddress;
	private String destinationNickname = "";
	private String destinationID = "";

	public MessagePDU withMessageContent(String messageContent) {
		this.messageContent = messageContent;
		return this;
	}

	public MessagePDU withMessageType(MessageType type) {
		this.type = type;
		return this;
	}

	public MessagePDU withStatus(Status status) {
		this.status = status;
		return this;
	}

	public MessagePDU withSourceNickname(String sourceNickname) {
		this.sourceNickname = sourceNickname;
		return this;
	}

	public MessagePDU withSourceID(String sourceID) {
		this.sourceID = sourceID;
		return this;
	}

	public MessagePDU withSourceAddress(byte[] sourceAddress) {
		this.sourceAddress = sourceAddress;
		return this;
	}

	public byte[] getSourceAddress() {
		return this.sourceAddress;
	}

	public MessagePDU withDestinationNickname(String destinationNickname) {
		this.destinationNickname = destinationNickname;
		return this;
	}

	public MessagePDU withDestinationID(String destinationID) {
		this.destinationID = destinationID;
		return this;
	}

	public String getMessageContent() {
		return this.messageContent;
	}

	public String getSourceNickname() {
		return this.sourceNickname;
	}

	public Status getStatus() {
		return this.status;
	}

	public String serialize() {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			String serializedObject = "";
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(this);
			so.flush();
			serializedObject = new String(Base64.getEncoder().encode(bo.toByteArray()));
			return serializedObject;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static MessagePDU deserialize(String serializedObject) {
		try {
			byte b[] = Base64.getDecoder().decode(serializedObject.getBytes());
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			return (MessagePDU) si.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return new MessagePDU();
		}
	}

	@Override
	public String toString() {
		return "status: " + status + "\n" +
				"type: " + type + "\n" +
				"messageContent: " + messageContent + "\n" +
				"timestamp: " + timestamp + "\n" +
				"sourceNickname: " + sourceNickname + "\n" +
				"sourceID: " + sourceID + "\n" +
				"sourceAddress: " + sourceAddress + "\n" +
				"destinationNickname: " + destinationNickname + "\n" +
				"destinationID: " + destinationID + "\n";
	}

}
