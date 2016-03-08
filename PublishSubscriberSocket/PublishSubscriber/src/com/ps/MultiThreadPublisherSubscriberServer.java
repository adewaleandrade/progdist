package com.ps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.ps.Topic;

public class MultiThreadPublisherSubscriberServer{

	// Declaration section:
	// declare a server socket and a client socket for the server
	// declare an array of topics
	// This chat server can accept up to 10 publisher clients' connections
	
	  ServerSocket publisherServerSocket = null;
	  ServerSocket subscriberServerSocket = null;
      ArrayList<Topic> topics;
      HashMap<Integer, ArrayList<ClientSubscriberThread>> subscriberList;
    
	public MultiThreadPublisherSubscriberServer(String args[]) {

		// The default port
		int publsher_port_number=2225;
		int subscriber_port_number=2226;
		
		this.topics = new ArrayList<Topic>();		
		this.subscriberList = new HashMap<Integer, ArrayList<ClientSubscriberThread>>();
		
		if (args.length < 2)
		{
			System.out.println("Usage: java MultiThreadPublisherSubscriberServer \n"+
							   "Now using port number="+publsher_port_number+ " for publishers and port number=" + subscriber_port_number + "for subscribers");
		} else {
			publsher_port_number=Integer.valueOf(args[0]).intValue();
			subscriber_port_number=Integer.valueOf(args[1]).intValue();
		}

		try {
			this.subscriberServerSocket = new ServerSocket(subscriber_port_number);
			this.publisherServerSocket = new ServerSocket(publsher_port_number);
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
		new Thread(new SubscriberListnerThread(this)).start();
		new Thread(new PublisherListnerThread(this)).start();
	}
	
	public int getTopicIndexByKey (int key) {
		for (Topic topic : topics) {
			if (topic.key == key) {
				return topics.indexOf(topic);
			}
		}
		
		return -1;
	}
} 

/**
 * Classe responsável pela captação de novas conexções com subscribers
 * Após uma nova conexão, uma nova thread de tratamento de subscribers é gerada. 
 * 
 * @author adewale
 */
class SubscriberListnerThread extends Thread{
	MultiThreadPublisherSubscriberServer server = null;
	public SubscriberListnerThread(MultiThreadPublisherSubscriberServer server){
		this.server = server;
	}
	
	public void run() {
		while (true) {
			try {
				Socket subscriberConnection = this.server.subscriberServerSocket.accept();
				new Thread(new ClientSubscriberThread(this.server, subscriberConnection)).start();
			}
			catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

/**
 * This class handles the subscriber requests implementing a Socket server
 * @author adewale
 */
class ClientSubscriberThread extends Thread{
	MultiThreadPublisherSubscriberServer board = null;
	Socket subscriber = null;
	BufferedReader is = null;
	PrintStream os = null;
	
	public ClientSubscriberThread(MultiThreadPublisherSubscriberServer board, Socket subscriber){
		this.board = board;
		this.subscriber = subscriber;
	}
	
	public void run() {
		String line;
		try{
			while (true) {
				is = new BufferedReader(new InputStreamReader(this.subscriber.getInputStream()));;
				os = new PrintStream(this.subscriber.getOutputStream());
				showTopics();
				os.println("\nTo leave enter /quit in a new line");
				line = is.readLine();
				if(line.startsWith("/quit")) break; 
			}

			is.close();
			os.close();
			this.subscriber.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	
	// refatorar
	public void showTopics(){
		String line;
        String operator;
   
		try {
		    while (true) {
		    	os.println("Topicos cadastrados:");
				for (Topic topic : board.topics) {
					os.println( topic.key + " -> " + topic.getName());
				}
				os.println("Selecione o topico que deseja se inscrever: ");
				
				operator = is.readLine();
				if (operator != null && !operator.equals("")){
					int topicIndex = board.getTopicIndexByKey(Integer.parseInt(operator));
					if (topicIndex != -1) {
						os.println("Informe a porta para a conexão: ");
						operator = is.readLine();
						
						Topic topic = board.topics.get(topicIndex);
		
						topic.addSubscriber(subscriber.getInetAddress().toString(), Integer.parseInt(operator));
						os.println("Inscrito na lista!");
						os.println("Esperando publicacoes.");
					}
				}
								    
				os.println("\nTo leave enter /quit in a new line");
			    line = is.readLine();
			    if (line == null){
			    	os.println("Null line");
			    } else {
			    	if(line.startsWith("/quit")) break;
			    }
		    }
        } catch (IOException e1) {
        	System.out.println(e1);
		}	
	}
}


/**
 * Classe responsável pela captação de novas conexções com subscribers
 * Após uma nova conexão, uma nova thread de tratamento de subscribers é gerada. 
 * 
 * @author adewale
 */
class PublisherListnerThread extends Thread{
	MultiThreadPublisherSubscriberServer server = null;
	public PublisherListnerThread(MultiThreadPublisherSubscriberServer server){
		this.server = server;
	}
	
	public void run() {
		while (true) {
			try {
				Socket publisherConnection = this.server.publisherServerSocket.accept();
				new Thread(new PublisherClientThread(this.server, publisherConnection)).start();
			}
			catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
class PublisherClientThread extends Thread{
	
	MultiThreadPublisherSubscriberServer board = null;
	Socket publisherConnection = null;
	BufferedReader is = null;
	PrintStream os = null;

	public PublisherClientThread(MultiThreadPublisherSubscriberServer server, Socket connection){
		this.publisherConnection = connection;
		this.board = server;
	}

	public void run() {
		String line;
		
		try{
			while (true) {
				is = new BufferedReader(new InputStreamReader(publisherConnection.getInputStream()));
				os = new PrintStream(publisherConnection.getOutputStream());
				
				System.out.println(publisherConnection.getInetAddress());
//				publishTopicMessage(i);
				publisherMenu(publisherConnection);
				os.println("\nTo leave enter /quit in a new line");
				line = is.readLine();
				if(line.startsWith("/quit")) break;   
			}

			is.close();
			os.close();
			publisherConnection.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	public void publisherMenu(Socket publisherConnection){
		String line;
        String operation;
        
        try {
		    while (true) {
				
				os.println("Opcoes:");
				os.println("1: Criar Topico"); 
				os.println("2: Publicar mensagem"); 

				operation = is.readLine();
				switch(operation) {
		    		case "1": createTopic(); break;
		    		case "2": publishMessage(publisherConnection); break;
		    	}
			    
				os.println("\nTo leave enter /quit in a new line");
			    line = is.readLine();
	            if(line.startsWith("/quit")) break;  
		    }
        } catch (IOException e1) {
        	System.out.println(e1);
		}
	
	}
	
	/**
	 * Cira um novo tópico registrável.
	 * @param publisherConnection
	 */
	public void createTopic(){
        String topicName = null;
        
        os.println("Informe o nome do topico:");
        
        try {
			topicName = is.readLine();
        } catch (IOException e1) {
			e1.printStackTrace();
		}
        
        if (topicName == null) {
        	os.println("Topico invalido!");
        	return;
        }
        
        Topic topic = new Topic(topicName, board.topics.size());
		this.board.topics.add(topic);
		os.println("Topico adicionado com sucesso!");
	}
	
	
	public void publishMessage(Socket publisherConnection){
		String operation;
        String message;
        try {
        	os.println("Topicos existentes:");
			for (Topic topic : this.board.topics) {
				os.println( topic.key + "-> " + topic.getName());
			}
			operation = is.readLine();
			
			int topicIndex = board.getTopicIndexByKey(Integer.parseInt(operation));
			
			if (topicIndex == -1) {
				os.println("Topico invalido!");
				return;
			}
			
			os.println("Digite a mensagem para este topico:");
			message = is.readLine();
			
			Topic topic = board.topics.get(topicIndex);
			
			topic.insertMessage(message, publisherConnection);
			os.println("Mensagem criada com sucesso!");
			
        } catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}




