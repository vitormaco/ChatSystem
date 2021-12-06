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
		NICKNAME_CHANGED
	}
	
	enum MessageType {
		TEXT,
	}
	
	private Status status = Status.ACTIVE; 
	private MessageType type = MessageType.TEXT;
	private String messageContent = "";
	private String sourceNickname = "";
	private String sourceID = "";
	private String destinationNickname = "";
	private String destinationID = "";

	public MessagePDU withMessageContent(String messageContent) {
		this.messageContent = messageContent;
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
}
