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

	HashMap<String, String> MACbyNickname = new HashMap<String, String>();
	DefaultListModel<String> connectedUsers = new DefaultListModel<String>();
	JList<String> connectedUsersJList = new JList<String>(connectedUsers);
	JScrollPane messageListScroll = new JScrollPane();
	JPanel messagesList = new JPanel();
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
		c.weighty = 0;
		c.ipady = 5;
		rightPanel.add(currentSelectedUserLabel, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1;
		c.weightx = 1;
		messagesList.setLayout(new GridBagLayout());
		messagesList.setBackground(Color.GREEN);
		messageListScroll.setViewportView(messagesList);
		rightPanel.add(messageListScroll, c);

		c.weighty = 0;
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
		c.weighty = 0;
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
					updateSelectedUserMessages();
				}
			}
		};
		connectedUsersJList.addMouseListener(selectUserListener);
	}

	public void updateSelectedUserMessages() {
		currentSelectedUserLabel.setText(currentSelectedUser);
		ArrayList<Message> messages = this.messageService.getUserMessages(currentSelectedUser);
		messagesList.removeAll();

		for (int i = 0; i < messages.size(); i++) {
			messagesList.add(createMessagePanel(messages.get(i)),
					new GridBagConstraints(0, i, 1, 1, 1, 0,
							GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
							new Insets(0, 5, 5, 5), 0, 0));
		}

		messagesList.revalidate();
		messagesList.repaint();
	}

	private JPanel createMessagePanel(Message message) {
		JPanel pane = new JPanel();
		pane.setOpaque(false);
		pane.add(new JLabel(message.getFormattedMessage()));
		return pane;
	}

	private void setActionListeners() {
		logoutButton.addActionListener(this);
		changeNicknameButton.addActionListener(this);
		sendMessageButton.addActionListener(this);
		SwingUtilities.getRootPane(sendMessageButton).setDefaultButton(sendMessageButton);
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
		if (currentSelectedUser != "") {
			messageService.sendMessageToUser(text, MACbyNickname.get(currentSelectedUser));
		}
		writeMessageField.setText("");
	}

	public void updateConnectedUsersList() {
		MACbyNickname = messageService.getAllActiveUsers();
		connectedUsers.clear();
		for (String user : MACbyNickname.keySet()) {
			connectedUsers.addElement(user);
		}
	}
}
