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

    static public ArrayList<Message> getHistory() {
		if (historyService == null) {
			historyService = new HistoryService();
		}
		try {
			String query = "SELECT * FROM test";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			ArrayList<Message> messages = new ArrayList<Message>();
			while (rs.next()) {
				messages.add(
					new Message(
						new MessagePDU("Mocked User 1").withMessageContent(rs.getString("id"))
					)
				);
			}
			st.close();
			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error executing query");
			return new ArrayList<Message>();
		}
    }

}
