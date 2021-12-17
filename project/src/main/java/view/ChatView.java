package view;

import javax.swing.*;
import java.util.*;

import services.MessageService;

import java.awt.*;
import java.awt.event.*;

public class ChatView extends JFrame implements ActionListener {
	private MessageService messageService;
	Container container = getContentPane();

	JList<String> list = new JList<String>();
	JButton logoutButton = new JButton("Logout");
	JButton changeNicknameButton = new JButton("Change Nickname");
	JTextField writeMessageField = new JTextField();
	JButton sendMessageButton = new JButton("Send Message");

	public ChatView(MessageService messageService) {
		this.messageService = messageService;

		this.setTitle(this.messageService.getNickname());
		this.setVisible(true);
		this.setBounds(10, 10, 350, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		container.setLayout(null);

		logoutButton.setBounds(50, 30, 100, 30);
		changeNicknameButton.setBounds(200, 30, 100, 30);
		list.setBounds(10, 100, 300, 100);
		writeMessageField.setBounds(10, 300, 300, 100);
		sendMessageButton.setBounds(10, 450, 300, 100);

		container.add(list);
		container.add(logoutButton);
		container.add(changeNicknameButton);
		container.add(writeMessageField);
		container.add(sendMessageButton);

		logoutButton.addActionListener(this);
		changeNicknameButton.addActionListener(this);
		sendMessageButton.addActionListener(this);

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				logoutProcess();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == logoutButton) {
			handleLogoutButton();
		} else if (e.getSource() == changeNicknameButton) {
			handleChangeNicknameButton();
		} else if (e.getSource() == sendMessageButton) {
			handleSendMessageButton();
		}
	}

	private void logoutProcess() {
		this.messageService.notifyUserStateChanged("disconnected");
		this.messageService.disconnectServer();
	}

	private void handleLogoutButton() {
		this.logoutProcess();
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

	private void handleSendMessageButton() {
		String text = writeMessageField.getText();
		messageService.sendMessageToUser(text);
	}

	public void updateList(Set<String> list) {
		this.list.setListData(list.toArray(new String[list.size()]));
	}

	@Override
	public void dispose() {
		this.messageService.notifyUserStateChanged("disconnected");
		this.messageService.disconnectServer();
		super.dispose();
	}
}
