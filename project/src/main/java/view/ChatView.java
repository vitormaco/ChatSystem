package view;

import javax.swing.*;

import models.Message;

import java.util.*;

import services.MessageService;

import java.awt.*;
import java.awt.event.*;

public class ChatView extends BaseView implements ActionListener {
	private MessageService messageService;
	Container container = getContentPane();

	DefaultListModel<String> connectedUsers = new DefaultListModel<String>();
	JList<String> connectedUsersJList = new JList<String>(connectedUsers);
	DefaultListModel<String> usersMessagesModel = new DefaultListModel<String>();
	JList<String> messagesList = new JList<String>(usersMessagesModel);
	String currentSelectedUser = "";
	JButton logoutButton = new JButton();
	JButton changeNicknameButton = new JButton();
	JLabel currentSelectedUserLabel = new JLabel(currentSelectedUser);
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
		setMouseListeners();
		setActionListeners();
		setWindowListeners();
		updateConnectedUsersList();
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

	private GridBagConstraints getBaseConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		return c;
	}

	private void buildPanel() {
		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = getBaseConstraints();
		mainPanel.setBackground(Color.LIGHT_GRAY);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());

		// TOP PANEL

		c.gridx = 0;
		c.gridy = 0;
		topPanel.add(logoutButton, c);

		c.gridx = 1;
		topPanel.add(changeNicknameButton, c);

		// LEFT PANEL

		c = getBaseConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		leftPanel.add(connectedUsersJList, c);

		// RIGHT PANEL

		c = getBaseConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		rightPanel.add(currentSelectedUserLabel, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 19;
		rightPanel.add(messagesList, c);

		c.weighty = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 9;
		rightPanel.add(writeMessageField, c);

		c.gridx = 1;
		c.weightx = 1;
		rightPanel.add(sendMessageButton, c);

		// MAIN PANEL
		c = getBaseConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weighty = 1;
		mainPanel.add(topPanel, c);
		c.weighty = 9;
		c.gridwidth = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 3;
		mainPanel.add(leftPanel, c);
		c.gridx = 1;
		c.weightx = 7;
		mainPanel.add(rightPanel, c);

		container.add(mainPanel, BorderLayout.CENTER);
	}

	private void setMouseListeners() {
		MouseListener selectUserListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 0) {
					currentSelectedUser = connectedUsersJList.getSelectedValue();
					updateSelectedUser(currentSelectedUser);
				}
			}
		};
		connectedUsersJList.addMouseListener(selectUserListener);
	}

	private void updateSelectedUser(String user) {
		currentSelectedUserLabel.setText(user);
		ArrayList<Message> messages = this.messageService.getUserMessages(user);
		usersMessagesModel.clear();
		for (int i = 0; i < messages.size(); i++) {
			usersMessagesModel.addElement(messages.get(i).getFormattedMessage());
		}
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
		messageService.sendMessageToUser(text, currentSelectedUser);
		writeMessageField.setText("");
	}

	public void updateConnectedUsersList() {
		Set<String> usersList = messageService.getAllActiveUsers();
		connectedUsers.clear();
		for (String user : usersList) {
			connectedUsers.addElement(user);
		}
	}
}
