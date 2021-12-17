package models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import services.MessageService;
import utils.NetworkUtils;

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
	private String sourceMAC;
	private String sourceAddress;
	private String destinationNickname;
	private String destinationMAC;
	private String destinationAddress;

	private MessagePDU() {};

	public MessagePDU(String nickname) {
		this.sourceNickname = nickname;
		this.sourceMAC = NetworkUtils.getLocalMACAdress();
		this.sourceAddress = NetworkUtils.getIPAddress();
	}

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

	public MessagePDU withSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
		return this;
	}

	public String getSourceAddress() {
		return this.sourceAddress;
	}

	public MessagePDU withDestination(String nickname, String id, String address) {
		this.destinationNickname = nickname;
		this.destinationMAC = id;
		this.destinationAddress = address;
		return this;
	}

	public MessagePDU withDestinationBroadcast() {
		this.destinationNickname = "*";
		this.destinationMAC = "*";
		this.destinationAddress = "*";
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
				"sourceMAC: " + sourceMAC + "\n" +
				"sourceAddress: " + sourceAddress + "\n" +
				"destinationNickname: " + destinationNickname + "\n" +
				"destinationMAC: " + destinationMAC + "\n" +
				"destinationAddress: " + destinationAddress + "\n";
	}
}
