package services;
import models.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;


class ServerThread extends Thread {
	int sendPort = 8080;
	int receivePort = 8081;
	
	public void run() {
		ServerSocket ss;
		try {
			ss = new ServerSocket(receivePort);
			Socket cl = ss.accept();
			new ClientThread(cl).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ClientThread extends Thread {

	
    Socket serverClient;

    ClientThread(Socket s){
        serverClient = s;
    }

    public void run(){
        try {
            DataInputStream in = 
            		new DataInputStream(serverClient.getInputStream());
            
            DataOutputStream out = 
            		new DataOutputStream(serverClient.getOutputStream());
            
            /*
            while(!clientMessage.equals("bye")){
                clientMessage = in.readUTF();
                System.out.println("Client: " + clientNo +  " Message: " + clientMessage);
                serverMessage = "From server to client " + clientNo + " " + clientMessage + " I'm your server";
                out.writeUTF(serverMessage);
            }
            in.close();
            out.close();
            */
            
            serverClient.close();
        } catch (Exception e) {
            System.out.println("Client error " + e.getMessage());
        }

    }
}


public class MessageService {
	// Change to UserMessages Type
	private ArrayList <UserMessages> usersList;
	private String id;
	private String nickname;
	
	public MessageService() {
		try {
			this.id = this.getMACAdress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.usersList = new ArrayList<UserMessages>();
		
		/*
		try {
            int port = 8000;
            ServerSocket ss = new ServerSocket(port);
            int counter = 0;
            System.out.println("Server start...");
            while(true){
                counter++;
                Socket serverClient = ss.accept();
                System.out.println("* Client no: " +  counter + " started");
                ClientThread ct = new ClientThread(serverClient, counter);
                ct.start();
                if(counter == 5)
                    ss.close();   
            }            
        } catch (Exception e) {
            System.out.print("Error: ");
            System.out.println(e.toString());
        }
        */
	}
	
	private String getMACAdress() throws Exception {
		byte[] mac;
		StringBuilder sb = new StringBuilder();
		
		try {

	        Enumeration<NetworkInterface> networkInterfaces = 
	        		NetworkInterface.getNetworkInterfaces();
	        
	        while(networkInterfaces.hasMoreElements())
	        {
	            NetworkInterface network = networkInterfaces.nextElement();
	            mac = network.getHardwareAddress();
	            if(mac != null)
	            {
	                sb = new StringBuilder();
	                for (int i = 0; i < mac.length; i++)
	                {
	                    sb.append(String.format("%02X%s", 
	                    		mac[i], (i < mac.length - 1) ? "-" : ""));        
	                }  
	            }
	        }
	    } catch (SocketException e){
	        e.printStackTrace();
	    }

		return sb.toString();
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public String getId() {
		return this.id;
	}
	
	/* PUBLIC METHODS */
	
	public HashSet<String> discoverUsers() {
		HashSet<String> nicknames = new HashSet<String>();
		for(UserMessages user : this.usersList) {
			nicknames.add(user.getNickname());
		}
		return nicknames;
	}
	
	public boolean validateAndAssingUserNickname(String nickname) {
		Set<String> nicknames = this.discoverUsers();
		if(!nicknames.contains(nickname)) {
			this.nickname = nickname;
			this.notifyUserStateChanged("connected");
			return true;
		}else {
			return false;
		}
	}
	
	public void notifyUserStateChanged(String state) {
		
	}
}
