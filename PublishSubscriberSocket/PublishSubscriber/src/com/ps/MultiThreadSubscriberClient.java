package com.ps;

import java.io.*;
import java.net.*;

public class MultiThreadSubscriberClient implements Runnable{
    
    // Declaration section
    // clientClient: the client socket
    // os: the output stream
    // is: the input stream
    
    static Socket clientSocket = null;
    static ServerSocket serverSocket = null;
    static PrintStream os = null;
    //static DataInputStream is = null;
    static BufferedReader is_server = null;    
    static BufferedReader is = null;
    static BufferedReader inputLine = null;
    static boolean closed = false;
    
    public static void main(String[] args) {
	
	// The default port	
	int port_number=2226;
    String host="localhost";

    int server_port_number = 2221;
	
	if (args.length < 2)
    {
		System.out.println("Usage: java MultiThreadSubscriberClient  \n"+
			   "Now using host="+host+", port_number="+port_number);
    } else {
		host=args[0];
		port_number=Integer.valueOf(args[1]).intValue();
    }
	// Initialization section:
	// Try to open a socket on a given host and port
	// Try to open input and output streams
	try {
            clientSocket = new Socket(host, port_number);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            //is = new DataInputStream(clientSocket.getInputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host "+host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "+host);
        }
	
	// If everything has been initialized then we want to write some data
	// to the socket we have opened a connection to on port port_number 
	
        if (clientSocket != null && os != null && is != null) {
            try {
		
            	// 	Create a thread to read from the server
		
                new Thread(new MultiThreadSubscriberClient()).start();
                //comunicação com o cliente Board
        		serverSocket = new ServerSocket(server_port_number);			
        		Socket serverClientSocket = serverSocket.accept();
    			is_server = new BufferedReader(new InputStreamReader(serverClientSocket.getInputStream()));			    								   
    			// System.out.println("Mensagem: " + is_server.readLine());
    			while (!closed) {
                    os.println(inputLine.readLine()); 
                }
		
				// Clean up:
				// close the output stream
				// close the input stream
				// close the socket
				
				os.close();
				is.close();
				is_server.close();
				clientSocket.close();
				serverSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }           
    
    public void run() {		
	String clientLine = null;
	String serverLine = null;
	
	// Keep on reading from the socket till we receive the "Bye" from the server,
	// once we received that then we want to break.
	try{ 
	    while ( ((clientLine = is.readLine()) != null) || ((serverLine = is_server.readLine()) != null)) {	    		    	    	
			if (clientLine != null) {
				System.out.println(clientLine);
				if (clientLine.indexOf("*** Bye") != -1) break;	
			}

			if (serverLine != null) {
				System.out.println("Nova mensagem recebida: ");
				System.out.println(serverLine); 
			}
	    }
            closed = true;
	} catch (IOException e) {
	    System.err.println("IOException:  " + e);
	}
    }
}
