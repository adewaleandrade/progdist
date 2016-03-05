package ps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

class ClientSubscriberThread extends Thread{
	
	BufferedReader is_stream = null;
	PrintStream os_stream = null;
	Socket subscriberSocket = null;
	ServerSocket serverSocket = null;
	ClientSubscriberThread st[]; 
	ArrayList<Topic> topics;
	HashMap<Integer, ArrayList<ClientSubscriberThread>> subscriberList;
	MultiThreadPublisherSubscriberServer server = null;
	
	public ClientSubscriberThread(MultiThreadPublisherSubscriberServer server){
		this.subscriberSocket = server.subscriberSocket;
		this.serverSocket = server.serverSocket;
		this.st = server.st;
		this.topics = server.topics;
		this.subscriberList = server.subscriberList;
		this.server = server;
	}
	
	public void run() {
		String line;
		
		try{
			

			while (true) {
				try {
					subscriberSocket = serverSocket.accept();
					
					is_stream = new BufferedReader(new InputStreamReader(subscriberSocket.getInputStream()));
					os_stream = new PrintStream(subscriberSocket.getOutputStream());
					
					for(int i=0; i<=9; i++){
						if(st[i] == null)
						{
							(st[i] = new ClientSubscriberThread(server)).start();
							System.out.println(st[i].subscriberSocket.getInetAddress());
							showTopics();
							break;
						}
					}
					
				}
				catch (IOException e) {
					System.out.println(e);
				}
				os_stream.println("\nTo leave enter /quit in a new line");
				line = is_stream.readLine();
				if(line.startsWith("/quit")) break;   
			}

			for(int i=0; i<=9; i++)
				if (st[i] == this) st[i] = null;

			is_stream.close();
			os_stream.close();
			subscriberSocket.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	public void showTopics(){
		        
        try {
        	String index;
        	os_stream.println("Topicos existentes:");
			for (Topic topic : topics) {
				os_stream.println( topics.indexOf(topic) + ": " + topic.getTopic());
			}
			
			os_stream.println("Selecione o topico que deseja se inscrever:");
			index = is_stream.readLine();
			Integer topicIndex = Integer.parseInt(index);
			
			ArrayList<ClientSubscriberThread> subscribers;		
			
			if (subscriberList != null){
				if (subscriberList.isEmpty()){					
					subscribers = new ArrayList<ClientSubscriberThread>();
				} else {
					subscribers = subscriberList.get(topicIndex);
				}
				
				subscribers.add(this);
				subscriberList.put(topicIndex, subscribers);
			}else{ 
				//messagem qualquer... bla bla bla
			}
        } catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}