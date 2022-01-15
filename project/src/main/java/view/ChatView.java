package view;

import javax.swing.*;

import models.Message;
import models.MessagePDU;

import java.util.*;

import services.MessageService;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

public class ChatView extends BaseView implements ActionListener {
	private MessageService messageService;
	Container container = getContentPane();

	DefaultListModel<String> connectedUsers = new DefaultListModel<String>();
	JList<String> connectedUsersJList = new JList<String>(connectedUsers);

	JScrollPane messageListScroll = new JScrollPane();
	JPanel messagesList = new JPanel();

	String currentSelectedUser = "";

	JLabel currentSelectedUserLabel = new JLabel(currentSelectedUser);
	JButton logoutButton = new JButton();
	JButton changeNicknameButton = new JButton();
	JTextField writeMessageField = new JTextField();
	JButton sendMessageButton = new JButton();

	public ChatView(MessageService messageService) {
		super();
		this.setTitle(messageService.getMyNickname());
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

	public String getSelectedUserMAC() {
		return messageService.getMACByNickname(this.currentSelectedUser);
	}

	private void buildPanel() {
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

		leftPanel.add(connectedUsersJList,
				new GridBagConstraints(0, 0, 1, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

		// RIGHT PANEL

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
					String userMAC = getSelectedUserMAC();
					messageService.createTCPConnection(userMAC);
				}
			}
		};
		connectedUsersJList.addMouseListener(selectUserListener);
	}

	public void updateSelectedUserMessages() {
		currentSelectedUserLabel.setText(currentSelectedUser);
		String userMAC = getSelectedUserMAC();
		ArrayList<Message> messages = this.messageService.getUserMessages(userMAC);

		messagesList.removeAll();

		int i;
		for (i = 0; i < messages.size(); i++) {
			GridBagConstraints c;

			if (messages.get(i).isClient()) {
				// Right padding, left align
				c = new GridBagConstraints(0, i, 1, 1, 0, 0,
						GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
						new Insets(5, 5, 5, 100), 0, 0);
			} else {
				// Left padding, right align
				c = new GridBagConstraints(0, i, 1, 1, 0, 0,
						GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
						new Insets(5, 100, 5, 5), 0, 0);
			}

			messagesList.add(createMessagePanel(messages.get(i)), c);
		}

		JPanel verticalSpacing = new JPanel();
		verticalSpacing.setOpaque(false);
		messagesList.add(verticalSpacing,
				new GridBagConstraints(0, i, 0, 0, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

		messagesList.revalidate();
		messagesList.repaint();
	}

	private JPanel createMessagePanel(Message message) {
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		String formattedDate = new SimpleDateFormat("hh:mm").format(message.getTimestamp());
		JLabel timeLabel = new JLabel(formattedDate);
		Font font = new Font("SansSerif", Font.BOLD, 8);
		timeLabel.setFont(font);
		pane.add(timeLabel);

		String messageContent = message.getContent();
		pane.add(new JLabel(messageContent));

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
		this.messageService.notifyUserStateChanged(MessagePDU.Status.DISCONNECTION);
		this.messageService.disconnectServer();
	}

	private void handleLogoutButton() {
		this.logoutProcess();
		new LoginView(new MessageService());
		dispose();
	}

	private void handleChangeNicknameButton() {
		String text = JOptionPane.showInputDialog(container, "Insert the new nickname.");
		if (text != null && messageService.validateAndAssingUserNickname(text, MessagePDU.Status.NICKNAME_CHANGED)) {
			this.setTitle(this.messageService.getMyNickname());
			JOptionPane.showMessageDialog(container, "Nickname updated.");
		} else {
			JOptionPane.showMessageDialog(container, "Nickname not updated.");
		}
	}

	private void handleSendMessageButton() {
		String text = writeMessageField.getText();
		if (currentSelectedUser != "" && !text.isBlank()) {
			String userMAC = getSelectedUserMAC();
			messageService.sendMessageToUserTCP(text, userMAC);
		}
		writeMessageField.setText("");
	}

	public void updateConnectedUsersList() {
		System.out.println("UPDATE UI");
		System.out.println(connectedUsers);
		connectedUsers.clear();
		for (String user : messageService.getAllActiveUsers().keySet()) {
			connectedUsers.addElement(user);
		}
		connectedUsersJList.updateUI();
	}
}
