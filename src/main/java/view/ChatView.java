package view;

import javax.swing.*;

import services.MessageService;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatView extends JFrame implements ActionListener {
	private MessageService messageService;
	Container container = getContentPane();
	String users[] = { "User1", "User2", "User3",
			"User4", "User5", "User6", "User7", "User8",
			"User9", "User10", "User11", "User12" };
	JList<String> list = new JList<String>(users);
	JButton logoutButton = new JButton("Logout");
	JButton changeNickname = new JButton("Change Nickname");

	public ChatView(MessageService messageService) {
		this.messageService = messageService;

		this.setTitle(this.messageService.getNickname());
		this.setVisible(true);
		this.setBounds(10, 10, 370, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		container.setLayout(null);

		list.setBounds(10, 100, 300, 300);
		logoutButton.setBounds(10, 10, 100, 30);
		changeNickname.setBounds(130, 10, 200, 30);

		container.add(list);
		container.add(logoutButton);
		container.add(changeNickname);

		logoutButton.addActionListener(this);
		changeNickname.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == logoutButton) {
			handleLogoutButton();
		} else if (e.getSource() == changeNickname) {
			handleChangeNicknameButton();
		}
	}

	private void handleLogoutButton() {
		this.messageService.notifyUserStateChanged("disconnected");
		this.messageService.disconnectServer();
		new LoginView(new MessageService());
		dispose();
	}

	private void handleChangeNicknameButton() {
		String text = JOptionPane.showInputDialog(container, "Insert the new nickname.");
		if (text != null) {
			System.out.println(text);
			if (messageService.validateAndAssingUserNickname(text, "nicknameChanged")) {
				this.setTitle(this.messageService.getNickname());
				JOptionPane.showMessageDialog(container, "Nickname updated.");
			} else {
				JOptionPane.showMessageDialog(container, "Nickname not updated.");
			}
		} else {
			JOptionPane.showMessageDialog(container, "Nickname not updated.");
		}
	}
}
