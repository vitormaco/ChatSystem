package models;

public class Message {
	private String timestamp;
	private String contentData;
	private String contentType;
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getContentData() {
		return contentData;
	}
	public void setContentData(String contentData) {
		this.contentData = contentData;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
