package ps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MultiThreadPublisherSubscriberServer{

	// Declaration section:
	// declare a server socket and a client socket for the server
	// declare an array of topics
	// This chat server can accept up to 10 publisher clients' connections
	
	  Socket publisherSocket = null;
	  Socket subscriberSocket = null;
	  ServerSocket serverSocket = null;
      ArrayList<Topic> topics;
      HashMap<Integer, ArrayList<ClientSubscriberThread>> subscriberList;
      ClientPublisherThread pt[] = new ClientPublisherThread[10];           
      ClientSubscriberThread st[] = new ClientSubscriberThread[10]; 
    
	public MultiThreadPublisherSubscriberServer(String args[]) {

		// The default port
		int port_number = 2222;
		
		topics = new ArrayList<Topic>();		
		subscriberList = new HashMap<Integer, ArrayList<ClientSubscriberThread>>();
		
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
		new ClientPublisherThread(this).start();
		
		subscriberSocket = new Socket();
		new ClientSubscriberThread(this).start();
					
				
	}
} 





