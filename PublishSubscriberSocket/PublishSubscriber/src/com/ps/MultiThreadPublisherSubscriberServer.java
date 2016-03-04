package com.ps;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MultiThreadPublisherSubscriberServer{

	// Declaration section:
	// declare a server socket and a client socket for the server
	// declare an array of topics
	// This chat server can accept up to 10 publisher clients' connections
	
	static  Socket publisherSocket = null;
	static  ServerSocket serverSocket = null;
    static ArrayList<Topic> topics;
    static  clientPublisherThread pt[] = new clientPublisherThread[10];           

	public static void main(String args[]) {

		// The default port
		int port_number=2222;
		topics = new ArrayList<Topic>();
		
		if (args.length < 1)
		{
			System.out.println("Usage: java MultiThreadCalculatorServer \n"+
							   "Now using port number="+port_number);
		} else {
			port_number=Integer.valueOf(args[0]).intValue();
		}

		try {
			serverSocket = new ServerSocket(port_number);
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
		publisherSocket = new Socket();
		new clientPublisherThread(publisherSocket, serverSocket, pt, topics).start();
				
	}
} 



class clientPublisherThread extends Thread{

	//DataInputStream is = null;
	BufferedReader is = null;
	PrintStream os = null;
	Socket publisherSocket = null;
	ServerSocket serverSocket = null;
	clientPublisherThread pt[]; 
	ArrayList<Topic> topics;

	public clientPublisherThread(Socket publisherSocket,ServerSocket serverSocket, clientPublisherThread[] pt, ArrayList<Topic> topics){
		this.publisherSocket = publisherSocket;
		this.serverSocket = serverSocket;
		this.pt = pt;
		this.topics = topics;
	}

	public void run() {
		String line;
		
		try{
			

			while (true) {
				try {
					publisherSocket = serverSocket.accept();
					//is = new DataInputStream(publisherSocket.getInputStream());
					is = new BufferedReader(new InputStreamReader(publisherSocket.getInputStream()));
					os = new PrintStream(publisherSocket.getOutputStream());
					
					for(int i=0; i<=9; i++){
						if(pt[i] == null)
						{
							(pt[i] = new clientPublisherThread(publisherSocket,serverSocket,pt,topics)).start();
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
        	System.out.println(e1);
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




