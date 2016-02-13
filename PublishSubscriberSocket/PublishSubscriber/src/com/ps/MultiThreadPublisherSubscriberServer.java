package com.ps;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;

public class MultiThreadPublisherSubscriberServer{

	// Declaration section:
	// declare a server socket and a client socket for the server
	// declare an input and an output stream

	static  Socket publisherSocket = null;
	static  ServerSocket serverSocket = null;

	// This chat server can accept up to 10 clients' connections

	static  clientPublisherThread pt[] = new clientPublisherThread[10];           
	//Calculator calculator = new Calculator();
	public static void main(String args[]) {

		// The default port

		int port_number=2222;

		if (args.length < 1)
		{
			System.out.println("Usage: java MultiThreadCalculatorServer \n"+
					"Now using port number="+port_number);
		} else {
			port_number=Integer.valueOf(args[0]).intValue();
		}

		// Initialization section:
		// Try to open a server socket on port port_number (default 2222)
		// Note that we can't choose a port less than 1023 if we are not
		// privileged users (root)

		try {
			serverSocket = new ServerSocket(port_number);
		}
		catch (IOException e)
		{System.out.println(e);}

		// Create a socket object from the ServerSocket to listen and accept 
		// connections.
		// Open input and output streams for this socket will be created in 
		// client's thread since every client is served by the server in
		// an individual thread

		/*while(true){
			try {
				publisherSocket = serverSocket.accept();
				for(int i=0; i<=9; i++){
					if(t[i]==null)
					{
						(t[i] = new clientCalculatorThread(publisherSocket,t)).start();
						break;
					}
				}
			}
			catch (IOException e) {
				System.out.println(e);
			}
		}*/
		publisherSocket = new Socket();
		new clientPublisherThread(publisherSocket, serverSocket, pt).start();
		
		
	}
} 



class clientPublisherThread extends Thread{

	DataInputStream is = null;
	PrintStream os = null;
	Socket publisherSocket = null;
	ServerSocket serverSocket = null;
	clientPublisherThread pt[]; 
	ArrayList<Topic> topics;

	public clientPublisherThread(Socket publisherSocket,ServerSocket serverSocket, clientPublisherThread[] pt){
		this.publisherSocket = publisherSocket;
		this.serverSocket = serverSocket;
		this.pt = pt;
		topics = new ArrayList<Topic>();
	}

	public void run() {
		String line;
		
		try{
			

			while (true) {
				try {
					publisherSocket = serverSocket.accept();
					is = new DataInputStream(publisherSocket.getInputStream());
					os = new PrintStream(publisherSocket.getOutputStream());
					
					for(int i=0; i<=9; i++){
						if(pt[i] == null)
						{
							(pt[i] = new clientPublisherThread(publisherSocket,serverSocket,pt)).start();
							System.out.println(pt[i].publisherSocket.getInetAddress());
							publishTopicMessage(i);
							break;
						}
					}
					
				}
				catch (IOException e) {
					System.out.println(e);
				}
				os.println("\nTo leave enter /quit in a new line");
				line = is.readLine();
				if(line.startsWith("/quit")) break;   
			}

			for(int i=0; i<=9; i++)
				if (pt[i] == this) pt[i] = null;

			is.close();
			os.close();
			publisherSocket.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	public void publishTopicMessage(int index){
		String line;
        String operation;
        
        try {
		    while (true) {
				
				os.println("Opcoes:");
				os.println("1: Criar Topico"); 
				os.println("2: Publicar mensagem"); 
			    
					operation = is.readLine();
				
	
			    try {
			    	switch(operation) {
			    		case "1": createTopic(index); break;
			    		case "2": publishMessage(); break;
			    	}
				    
				} catch (Exception e) {
					os.println("invalid parameter value."); 
				} finally{
					os.println("\nTo leave enter /quit in a new line");
				    line = is.readLine();
		            if(line.startsWith("/quit")) break;  
				}
				 
			    
		    }
        } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}
	public void createTopic(int index){
        String operation;
        
        try {
				
			os.println("Informe o nome do topico:");
			operation = is.readLine();
			Topic topic = new Topic();
			topic.setTopic(operation);

			os.println("Digite a mensagem para este topico:");
			operation = is.readLine();
			topic.insertMessage(operation, Integer.valueOf(index));
			topics.add(topic);
			
				
        } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public void publishMessage(){
		String operation;
        String message;
        try {
				
        	os.println("Topicos existentes:");
			for (Topic topic : topics) {
				os.println( topics.indexOf(topic) + ": " + topic.getTopic());
			}
			operation = is.readLine();
			os.println("Digite a mensagem para este topico:");
			message = is.readLine();
			Integer topicIndex = Integer.parseInt(operation);
			//int indexOf = topics.indexOf(topicIndex);
			Topic aux = topics.get(topicIndex);
			aux.insertMessage(message, topicIndex);
			
			aux.listAllMessages();
			
        } catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
// This client thread opens the input and the output streams for a particular client,
// ask the client's name, informs all the clients currently connected to the 
// server about the fact that a new client has joined the chat room, 
// and as long as it receive data, echos that data back to all other clients.
// When the client leaves the chat room this thread informs also all the
// clients about that and terminates. 



