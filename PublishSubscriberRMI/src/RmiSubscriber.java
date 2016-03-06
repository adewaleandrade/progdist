import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class RmiSubscriber extends java.rmi.server.UnicastRemoteObject implements ReceiveNotifyMessageInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6994008455499297467L;
	int      thisPort;
    String   thisAddress;
    Registry registry_;    // rmi registry for lookup the remote objects.		
	
    static ReceiveMessageInterface rmiServer;
    static ReceiveNotifyMessageInterface rmiSubscriber;
    static Registry registry;
    static String serverAddress = "localhost"; 
    static String serverPort = "3232";
    
    private static Runnable threadSubscriber = new Runnable() {
        public void run() {
    		String line;
    		String idTopic;
    		
    		try{
    			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
    			while (true) {    		    	       				      				   	     	   
    		    	   listAllTopics(rmiServer); 
    		    	   System.out.println("Selecione, digitando o ID do tópico correspondente: ");
    			 	   idTopic = in.readLine();
    			 	   //inscrevendo cliente
    			 	   subscribeTopic(idTopic, rmiServer);  
    				
	    		       System.out.println("\n Para continuar [Enter] / Para sair digite [quit]");
	    			   line = in.readLine();
	    			   if(line.startsWith("/quit")) break;   
    			}
    			
    			while (true) {    	
    				System.out.println("Aguardando mensagens...");
    		        System.out.println("\n Para continuar [Enter] / Para sair digite [quit]");
    				line = in.readLine();
    				if(line.startsWith("/quit")) break;  				   				
 
    			}    			
    			
    			
    		} catch(IOException e){
    			System.out.println(e.getMessage());
    		}
       }
    };    
    
    public RmiSubscriber() throws RemoteException
    {
        try{
            // get the address of this host.
            thisAddress = (InetAddress.getLocalHost()).toString();
        }
        catch(Exception e){
            throw new RemoteException("can't get inet address.");
        }
       
        thisPort = 3232;  // this port(registry’s port)
        System.out.println("this address="+thisAddress+",port="+thisPort);
        
        try{
	        // create the registry and bind the name and object.
	        registry_ = LocateRegistry.createRegistry( thisPort );
            registry_.rebind("rmiSubscriber", this);
        }
        catch(RemoteException e){
        	throw e;
        }
    }       
    
	static public void main(String args[])
    {
       try{
           // get the “registry” 
           registry = LocateRegistry.getRegistry(
               serverAddress,
               (new Integer(serverPort)).intValue()
           );   	             
    	   
           // look up the remote object
           rmiServer = (ReceiveMessageInterface)(registry.lookup("rmiServer"));   
           
           new Thread(threadSubscriber).start();
       }
       catch(RemoteException e){
           e.printStackTrace();
       }
       catch(NotBoundException e){
           e.printStackTrace();
       } 
    }
	
	public static void unSubscribeTopic(int idTopic) {
	 
	   System.out.println(" : ");
	}
	
	public static void subscribeTopic(String idTopic, ReceiveMessageInterface rmiServer) {
		try {
			rmiServer.subscribeTopicSubscriber(rmiSubscriber);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void listAllTopics(ReceiveMessageInterface rmiServer) {
        try{ 
     	   ArrayList<Topic> topicList = new ArrayList<Topic>();
 		   System.out.println("Tópicos cadastrados: \n");
 		   
 		   topicList = rmiServer.getTopics();  
 		   for (Topic topic : topicList) {
 			  System.out.println("ID   |   TOPICO ");
 			  System.out.println(topic.getTopicId()+ "   |   " + topic.getTopic());
 		   }
        }catch(IOException e){
     	   e.printStackTrace();
        }
 	}

	@Override
	public void receiveMessage(String message, String idTopic) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Nova mensagem do tópico "+idTopic+": "+ message);
		
	}

	@Override
	public void sendMessage(String message, int topicIndex) throws RemoteException {
		// TODO Auto-generated method stub
		
	}   
}
