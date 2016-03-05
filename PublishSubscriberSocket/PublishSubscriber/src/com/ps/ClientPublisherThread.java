package ps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ClientPublisherThread extends Thread{

	BufferedReader is = null;
	PrintStream os = null;
	Socket publisherSocket = null;
	ServerSocket serverSocket = null;
	ClientPublisherThread pt[]; 
	ArrayList<Topic> topics;
	MultiThreadPublisherSubscriberServer server = null;

	public ClientPublisherThread(MultiThreadPublisherSubscriberServer server){
		this.publisherSocket = server.publisherSocket;
		this.serverSocket = server.serverSocket;
		this.pt = server.pt;
		this.topics = server.topics;
		this.server = server;
	}

	public void run() {
		String line;
		
		try{
			
			while (true) {
				try {
					publisherSocket = serverSocket.accept();

					is = new BufferedReader(new InputStreamReader(publisherSocket.getInputStream()));
					os = new PrintStream(publisherSocket.getOutputStream());
					
					for(int i=0; i<=9; i++){
						if(pt[i] == null)
						{
							(pt[i] = new ClientPublisherThread(server)).start();
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
			
			Topic aux = topics.get(topicIndex);
			aux.insertMessage(message, topicIndex);
			notifySubscribers(topicIndex);
			
			aux.listAllMessages();
			
        } catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void notifySubscribers(int topicIndex){
		ArrayList<ClientSubscriberThread> subscribers;	
		subscribers = server.subscriberList.get(topicIndex);
		
		for (ClientSubscriberThread clientSubscriberThread : subscribers) {
					
			try {
				Socket clientSocketSubscriber = new Socket(clientSubscriberThread.subscriberSocket.getInetAddress(), 
																 clientSubscriberThread.subscriberSocket.getPort());
								
	            os = new PrintStream(clientSocketSubscriber.getOutputStream());
	            os.print("New message number: " + topicIndex);
	            os.flush();
	            clientSocketSubscriber.close();	            
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
	}	

}
