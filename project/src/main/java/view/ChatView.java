package view;

import javax.swing.*;
import java.util.*;

import services.MessageService;

import java.awt.*;
import java.awt.event.*;

public class ChatView extends BaseView implements ActionListener {
	private MessageService messageService;
	Container container = getContentPane();

	JList<String> list = new JList<String>();
	JButton logoutButton = new JButton();
	JButton changeNicknameButton = new JButton();
	JTextField writeMessageField = new JTextField();
	JButton sendMessageButton = new JButton();

	public ChatView(MessageService messageService) {
		super();
		this.setTitle(messageService.getNickname());
		this.messageService = messageService;
		setLogoutButton();
		setChangeNicknameButton();
		setSendMessageButton();
		buildPanel();
		setActionListeners();
		setWindowListeners();
	}

	private void setLogoutButton() {
		logoutButton.setText("Logout");
	}

	private void setChangeNicknameButton() {
		changeNicknameButton.setText("Change Nickname");
	}

	private void setSendMessageButton() {
		sendMessageButton.setText("Send Message");
	}

	private void buildPanel() {
		JPanel centerPanel = new JPanel();

		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		centerPanel.setBackground(Color.LIGHT_GRAY);

		// common constraints
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;

		// specific components
		c.gridy = 0;
		centerPanel.add(list, c);

		c.gridy = 1;
		centerPanel.add(logoutButton, c);

		c.gridy = 2;
		centerPanel.add(changeNicknameButton, c);

		c.gridy = 3;
		centerPanel.add(writeMessageField, c);

		c.gridy = 4;
		centerPanel.add(sendMessageButton, c);

		container.add(centerPanel, BorderLayout.CENTER);
	}

	private void setActionListeners() {
		logoutButton.addActionListener(this);
		changeNicknameButton.addActionListener(this);
		sendMessageButton.addActionListener(this);
	}

	private void setWindowListeners() {
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
}
