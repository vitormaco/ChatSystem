package services;

import java.sql.*;
import java.util.ArrayList;

import utils.ConfigManager;
import models.Message;

public class HistoryService {
	static private Connection connectionSingleton;
	static private ConfigManager properties = new ConfigManager();

	static public Connection getConnection() throws Exception {
		if (connectionSingleton == null) {
			connectionSingleton = DriverManager.getConnection(
				"jdbc:" + properties.get("DATABASE_HOST"),
				properties.get("DATABASE_USER"),
				properties.get("DATABASE_PASSWORD"));
		}

		return connectionSingleton;
	}

	static public ArrayList<Message> getHistory(String myMac, String otherUserMac) {
		try {
			Connection conn = getConnection();
			PreparedStatement statement = conn.prepareStatement(
				"select * from messages where" +
				"(source_id=? and destination_id=?) or" +
				"(source_id=? and destination_id=?)"
			);
			statement.setString(1, myMac);
			statement.setString(2, otherUserMac);
			statement.setString(3, otherUserMac);
			statement.setString(4, myMac);
			ResultSet rs = statement.executeQuery();

			ArrayList<Message> messages = new ArrayList<Message>();
			while (rs.next()) {
				messages.add(new Message(rs.getString("content"), rs.getString("source_id"), rs.getTimestamp("time_sent")));
			}

			statement.close();
			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error executing query to retrieve messages from database");
			return new ArrayList<Message>();
		}
	}

	static public void saveMessage(String source, String dest, Message message) {
		try {
			Connection conn = getConnection();
			PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO messages (time_sent, content, source_id, destination_id) VALUES(?,?,?,?)");
			statement.setTimestamp(1, message.getTimestamp());
			statement.setString(2, message.getContent());
			statement.setString(3, source);
			statement.setString(4, dest);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error executing query to save message from database");
		}
	}
}
