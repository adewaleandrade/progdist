package com.ps;

import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintStream;
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
		this.subscribers.add(new Subscriber(host, port));
	}
	
	public void notifySubscribers(String message){
		if (subscribers.isEmpty()) {
			return;
		}
		
		for (Subscriber subscriber : subscribers) {
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

class MessengerThread implements Runnable {
    static Socket socket = null;
    static PrintStream os = null;
    public String message = null;
    
    public MessengerThread(String host, int port, String message) {
    	try {
    		socket = new Socket(host, port);
            os = new PrintStream(socket.getOutputStream());
            this.message = message;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host "+host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "+host);
        }
    	
    	if (socket != null && os != null && message != null) {
            try 
            {
            	// 	Create a thread to send message
            	new Thread(new MultiThreadPublisherClient()).start();
	
				// Clean up:
				// close the output stream
				// close the input stream
				// close the socket
				os.close();
				socket.close();   
		     } catch (IOException e) {
		       System.err.println("IOException:  " + e);
		     }
        }
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		os.println("Nova mensagem: ");
		os.println(message);
	}
	
}
