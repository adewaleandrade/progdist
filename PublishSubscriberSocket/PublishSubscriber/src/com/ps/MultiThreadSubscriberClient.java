
import java.io.*;
import java.net.*;
import java.util.Random;

public class MultiThreadSubscriberClient implements Runnable{

	// Declaration section
	// clientClient: the client socket
	// os: the output stream
	// is: the input stream

	static Socket clientSocket = null;
	static ServerSocket serverSocket = null;
	static PrintStream os = null;
	static BufferedReader is = null;
	static BufferedReader inputLine = null;
	static boolean closed = false;
	static int server_port_number;

	public static void main(String[] args) {

		// The default port	
		int port_number=2226;
//		String host="localhost";
		String host="127.0.0.1";

		int minPort = 2232;
		Random ran = new Random();

		server_port_number = minPort + ran.nextInt(10);

		if (args.length < 2)
		{
			System.out.println("Usage: java MultiThreadSubscriberClient  \n"+
					"Now connecting to "+host+":"+port_number);

			System.out.println("Usage: java MultiThreadSubscriberClient  \n"+
					"Creating server on port="+server_port_number+ "\n\n");
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
				new Thread(new SubscriberServerListner(server_port_number)).start();
				new Thread(new MultiThreadSubscriberClient()).start();
				//comunicação com o cliente Board
				while (!closed) {
					os.println(inputLine.readLine()); 
				}

				// Clean up:
				// close the output stream
				// close the input stream
				// close the socket

				os.close();
				is.close();
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}           

	public void run() {		
		String clientLine = null;

		// Keep on reading from the socket till we receive the "Bye" from the server,
		// once we received that then we want to break.
		try{ 
			while ( ((clientLine = is.readLine()) != null)) {	    		    	    	
				if (clientLine != null) {
					System.out.println(clientLine);
					clientLine.compareToIgnoreCase("Informe a porta para a conexão: ");
					if (clientLine.compareToIgnoreCase("Informe a porta para a conexão: ") == 0){
						os.println(server_port_number);
					}
					if (clientLine.indexOf("*** Bye") != -1) break;	
				}
			}
			closed = true;
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}
}


/**
 * Classe responsável pela captação de novas conexções com subscribers
 * Após uma nova conexão, uma nova thread de tratamento de subscribers é gerada. 
 * 
 * @author adewale
 */
class SubscriberServerListner extends Thread{
	BufferedReader is_server = null;
	boolean closedServer = false;
	ServerSocket server = null;
	Socket backChannel = null;
	BufferedReader bc_is = null;
	PrintStream bc_os = null;
	
	public SubscriberServerListner (int server_port_number) throws IOException {
		server = new ServerSocket(server_port_number);
	}
	
	public void connectToBoard(Socket connection, String port) {
		String line;
		try {
			port = port.substring(12);
			String host = connection.getInetAddress().toString();
			host = host.replace("/", "");
			host = host.replace("\\", "");
			backChannel = new Socket(host, Integer.parseInt(port));
			bc_is = new BufferedReader(new InputStreamReader(backChannel.getInputStream()));
			bc_os = new PrintStream(backChannel.getOutputStream());
			bc_os.println("**akg conn**");
			
			while ( ((line = is_server.readLine()) != null)) {
				if (line != null) {
					if (line.startsWith("***msg_end")) break;
					System.out.println("***Nova mensagem recebida: ");
					System.out.println(line);
					System.out.println("***Fim da mensagem.");
				}
			}
			bc_os.println("**msg rcvd**");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String serverLine = null;
		Socket serverClientSocket = null;
		// Keep on reading from the socket till we receive the "Bye" from the server,
		// once we received that then we want to break.
		try{ 
			serverClientSocket = server.accept();
			is_server = new BufferedReader(new InputStreamReader(serverClientSocket.getInputStream()));
			while ( ((serverLine = is_server.readLine()) != null)) {
				if (serverLine.startsWith("***srv_port:")) {
					connectToBoard(serverClientSocket, serverLine);
				} 
			}
			closedServer = true;
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}
}
