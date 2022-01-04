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
		GridBagConstraints c = getBaseConstraints();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(Color.LIGHT_GRAY);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());

		// TOP PANEL

		topPanel.add(logoutButton,
				new GridBagConstraints(0, 0, 1, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

		topPanel.add(changeNicknameButton,
				new GridBagConstraints(1, 0, 1, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

		// LEFT PANEL

		c = getBaseConstraints();

		leftPanel.add(connectedUsersJList,
				new GridBagConstraints(0, 0, 1, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

		// RIGHT PANEL

		c = getBaseConstraints();
		rightPanel.add(currentSelectedUserLabel,
				new GridBagConstraints(0, 0, 2, 1, 1, 0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 5));

		messagesList.setLayout(new GridBagLayout());
		messagesList.setBackground(Color.GREEN);
		messageListScroll.setViewportView(messagesList);
		rightPanel.add(messageListScroll,
				new GridBagConstraints(0, 1, 2, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 5));

		rightPanel.add(writeMessageField,
				new GridBagConstraints(0, 2, 1, 1, 9, 0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 5));

		rightPanel.add(sendMessageButton,
				new GridBagConstraints(1, 2, 1, 1, 1, 0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 5));

		// MAIN PANEL
		mainPanel.add(topPanel,
				new GridBagConstraints(0, 0, 2, 1, 1, 0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(10, 10, 10, 10), 0, 0));

		mainPanel.add(leftPanel,
				new GridBagConstraints(0, 1, 1, 1, 3, 9,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(10, 10, 10, 10), 0, 0));

		mainPanel.add(rightPanel,
				new GridBagConstraints(1, 1, 1, 1, 7, 9,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(10, 10, 10, 10), 0, 0));

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
