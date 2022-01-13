package services;

import java.sql.*;
import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;
import models.Message;
import models.MessagePDU;

public class HistoryService {
	static private Connection conn = null;
	static private Dotenv dotenv = Dotenv.load();
	static private HistoryService historyService = null;

	private HistoryService() {
		try {
			conn = DriverManager.getConnection(
					"jdbc:" + dotenv.get("DATABASE_HOST"),
					dotenv.get("DATABASE_USER"),
					dotenv.get("DATABASE_PASSWORD"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error connecting to database");
		}
	}

	static public HistoryService singletonCheck() {
		if (historyService == null) {
			historyService = new HistoryService();
		}
		return historyService;
	}

	static public ArrayList<Message> getHistory() {
		singletonCheck();
		try {
			String query = "SELECT * FROM test";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			ArrayList<Message> messages = new ArrayList<Message>();
			while (rs.next()) {
				messages.add(
						new Message(
								new MessagePDU("Mocked User 1").withMessageContent(rs.getString("id"))));
			}
			st.close();
			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error executing query");
			return new ArrayList<Message>();
		}
	}

	static public void saveMessage(String source, String dest, Message message) {
		singletonCheck();
		try {
			PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO messages (time_sent, content, source_id, destination_id) VALUES(?,?,?,?)");
			statement.setTimestamp(1, message.getTimestamp());
			statement.setString(2, message.getContent());
			statement.setString(3, source);
			statement.setString(4, dest);
			statement.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error executing query");
		}
	}
}
