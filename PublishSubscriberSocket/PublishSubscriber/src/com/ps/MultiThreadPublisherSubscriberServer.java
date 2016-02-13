package com.ps;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MultiThreadPublisherSubscriberServer{

	// Declaration section:
	// declare a server socket and a client socket for the server
	// declare an input and an output stream

	static  Socket publisherSocket = null;
	static  ServerSocket serverSocket = null;

	// This chat server can accept up to 10 clients' connections

	static  clientPublisherThread pt[] = new clientPublisherThread[10];           
	Calculator calculator = new Calculator();
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
		new clientPublisherThread(publisherSocket, serverSocket, pt).start();
	}
} 



class clientPublisherThread extends Thread{

	DataInputStream is = null;
	PrintStream os = null;
	Socket publisherSocket = null;
	ServerSocket serverSocket = null;
	clientPublisherThread pt[]; 

	public clientPublisherThread(Socket publisherSocket,ServerSocket serverSocket, clientPublisherThread[] pt){
		this.publisherSocket = publisherSocket;
		this.serverSocket = serverSocket;
		this.pt = pt;
	}

	public void run() {
		try{
			//is = new DataInputStream(publisherSocket.getInputStream());
			//os = new PrintStream(publisherSocket.getOutputStream());

			while (true) {
				try {
					publisherSocket = serverSocket.accept();
					for(int i=0; i<=9; i++){
						if(pt[i] == null)
						{
							(pt[i] = new clientPublisherThread(publisherSocket,serverSocket,pt)).start();
							break;
						}
					}
				}
				catch (IOException e) {
					System.out.println(e);
				}
				if(1 == 1) break;  
			}

			for(int i=0; i<=9; i++)
				if (pt[i] == this) pt[i] = null;

			//is.close();
			//os.close();
			publisherSocket.close();
		} catch(IOException e){

		}
	}
}



// This client thread opens the input and the output streams for a particular client,
// ask the client's name, informs all the clients currently connected to the 
// server about the fact that a new client has joined the chat room, 
// and as long as it receive data, echos that data back to all other clients.
// When the client leaves the chat room this thread informs also all the
// clients about that and terminates. 

class clientCalculatorThread extends Thread{

	DataInputStream is = null;
	PrintStream os = null;
	Socket clientSocket = null;       
	clientCalculatorThread t[]; 
	Calculator calculator;
	ArrayList<Character> operatorList;

	public clientCalculatorThread(Socket clientSocket, clientCalculatorThread[] t){
		this.clientSocket=clientSocket;
		this.t=t;
		calculator = new Calculator();
		createOperatorList();
	}

	public void run() 
	{
		String line;
		String operation;
		String a;
		String b;
		String result;
		try{
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			os.println("Hello!\nTo leave enter /quit in a new line");


			while (true) {
				/*line = is.readLine();
	        if(line.startsWith("/quit")){

	         	break; 
	     	}*/
				os.println("Enter the operation you wanna do:");
				os.println("(options: + - * /)"); 
				operation = is.readLine();
				if (validateOperator(operation.charAt(0))) {
					try {
						os.println("Enter the value of A:"); 
						a = is.readLine(); 
						os.println("Enter the value of B:"); 
						b = is.readLine();
						result = calculator.calculate(operation.charAt(0), Integer.valueOf(a), Integer.valueOf(b));
						os.println("Result: "+ a + operation + b + " = " + result);
					} catch (NumberFormatException e) {
						os.println("invalid parameter value."); 
					}
				} else {
					os.println("Invalid operator");
				}

				os.println("\nTo leave enter /quit in a new line");
				line = is.readLine();
				if(line.startsWith("/quit")) break;  
			}

			// be accepted by the server

			for(int i=0; i<=9; i++)
				if (t[i]==this) t[i]=null;  

			// close the output stream
			// close the input stream
			// close the socket

			is.close();
			os.close();
			clientSocket.close();
		}
		catch(IOException e){};
	}

	private boolean validateOperator(char operator){
		return operatorList.contains(operator);
	}
	private void createOperatorList(){
		operatorList = new ArrayList<Character>();
		operatorList.add('+');
		operatorList.add('-');
		operatorList.add('*');
		operatorList.add('/'); 
	}
}

class Calculator {

	public Integer sum(int a, int b){
		return a + b;
	}

	public Integer subtraction(int a, int b){
		return a - b;
	}

	public Integer multiply(int a, int b){
		return a * b;
	}

	public Float division(int a, int b){
		return ((float)a / b);
	}

	public String calculate(char op, int a, int b){

		switch(op){
		case '+' :
			return sum(a, b).toString();
			//break;

		case '-' :
			return subtraction(a, b).toString();
			//break;

		case '*' :
			return multiply(a, b).toString();
			//break;

		case '/' :
			return division(a, b).toString();
			//break;
		default :
			return "Invalid operator";
		}

	}
}
