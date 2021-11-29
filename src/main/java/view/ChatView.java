package view;
import javax.swing.*;

import services.MessageService;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatView extends JFrame implements ActionListener {
	private MessageService messageService;
	Container container = getContentPane();
	String users[]= { "User1", "User2", "User3",
	        "User4", "User5", "User6", "User7", "User8",
	        "User9", "User10", "User11", "User12"};
	JList list = new JList(users);
	
	public ChatView(MessageService messageService) {
		this.messageService = messageService;
		
		this.setTitle(this.messageService.getId());
        this.setVisible(true);
        this.setBounds(10, 10, 370, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);


        container.setLayout(null);
        list.setBounds(10, 10, 300, 500);
        
        container.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
