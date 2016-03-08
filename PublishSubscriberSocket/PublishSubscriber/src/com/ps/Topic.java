package com.ps;

import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Topic {
	int key = 0;
	private String name = null;
	public ArrayList<Subscriber> subscribers;
	public ArrayList<Message> messages;
	
	public Topic(String name, int key) {
		this.name = name;
		this.key = key;
		messages = new ArrayList<Message>();
		subscribers = new ArrayList<Subscriber>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String topic) {
		this.name = topic;
	}
	
	public ArrayList<Message> getMessages() {
		return messages;
	}
	
	public void insertMessage(String content, Socket publisherSocket) {
		Publisher publisher = new Publisher(publisherSocket.getInetAddress().toString(), publisherSocket.getLocalPort());
		Message message = new Message(content, publisher);
		this.messages.add(message);
		
		notifySubscribers(content);
//		System.out.println(message.content.toString());
		
	}
	
	public void addSubscriber(String host, int port) {
		host = host.replace("/", "");
		host = host.replace("\\", "");
		this.subscribers.add(new Subscriber(host, port));
	}
	
	public void notifySubscribers(String message){
		if (subscribers.isEmpty()) {
			return;
		}
		
		System.out.println("Sending message to subscribers....");
		
		for (Subscriber subscriber : subscribers) {
			System.out.println("Sending message to " + subscriber.host + ":" + subscriber.port);
			new Thread(new MessengerThread(subscriber.host, subscriber.port, message)).start();
		}				
	}
	
	public void listAllMessages(){

		for (Message message : messages)
		{
		    System.out.println(message.publisher.getAddress() + "/" + message.content);
		}
	}
	
	class Subscriber {
		public String host = null;
		public int port = 0;
		
		public Subscriber(String host, int port) {
			this.host = host;
			this.port = port;
		}
	}
	
	class Publisher {
		private String host;
		private int port;
		
		public Publisher(String host, int port) {
			this.host = host;
			this.port = port;
		}
		
		public String getAddress(){
			return this.host + ":" + this.port;
		}
	}
	
	class Message {
		public String content = null;
		public Publisher publisher = null;
		
		public Message (String content, Publisher publisher) {
			this.content = content;
			this.publisher = publisher;
		}
	}
}

class MessengerThread extends Thread {
    static Socket socket = null;
    ServerSocket serverSocket = null;
    PrintStream subscriberOs = null;
    public String message = null;
    int server_port_number;
    
    public MessengerThread(String host, int port, String message) {
    	int minPort = 2500;
		Random ran = new Random();
		server_port_number = minPort + ran.nextInt(10);
		boolean created = false;
		
		while(!created){
			try {
				this.serverSocket = new ServerSocket(server_port_number);
				created = true;
			}
			catch (IOException e)
			{
				server_port_number = minPort + ran.nextInt(10);
			}
		}
		
    	try {
    		System.out.println("Connecting to " + host + ":" + port);
    		socket = new Socket(host, port);
    		subscriberOs = new PrintStream(socket.getOutputStream());
            this.message = message;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host "+host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "+host+":"+port);
        }
    	
    	if (socket != null && subscriberOs != null && message != null) {
	    	// 	Create a thread to send message
	    	new Thread(new MultiThreadPublisherClient()).start();
        }
    }
	@Override
	public void run() {
		System.out.println("Printing the message.");
		try {
			subscriberOs.println("***srv_port:" + server_port_number);
			Socket subscriberConnection = serverSocket.accept();
			BufferedReader subscriber_is = new BufferedReader(new InputStreamReader(subscriberConnection.getInputStream()));
			
			while (true) {
				String line = subscriber_is.readLine();
				if(line.equalsIgnoreCase("**akg conn**")) break; 
			}
			//Send msg
			subscriberOs.println(message);
			
			while (true) {
				String line = subscriber_is.readLine();
				if(line.equalsIgnoreCase("**msg rcvd**")) break; 
			}
			System.out.println("New message: " + message);
			// Clean up:
	    	subscriber_is.close();
	    	subscriberConnection.close();
	    	subscriberOs.close();
	    	socket.close();
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	
}