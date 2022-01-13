package models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Base64;

import utils.NetworkUtils;

public class MessagePDU implements Serializable {
	public enum Status {
		CONNECTION,
		DISCONNECTION,
		NICKNAME_CHANGED,
		DISCOVER,
		MESSAGE,
	}

	private Status status;
	private String messageContent;
	private Timestamp timestamp;
	private String sourceNickname;
	private String sourceMAC;
	private String sourceAddress;

	private MessagePDU() {
	};

	public MessagePDU(String nickname) {
		this.sourceNickname = nickname;
		this.sourceMAC = NetworkUtils.getLocalMACAdress();
		this.sourceAddress = NetworkUtils.getIPAddress();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		this.timestamp = now;
	}

	public MessagePDU withMessageContent(String messageContent) {
		this.messageContent = messageContent;
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

	public Timestamp getTimestamp() {
		return this.timestamp;
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

	public String getSourceMAC() {
		return this.sourceMAC;
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
}
