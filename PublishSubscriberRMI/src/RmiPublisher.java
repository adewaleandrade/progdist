import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class RmiPublisher
{
    static ReceiveMessageInterface rmiServer;
    static Registry registry;
    static String serverAddress = "localhost"; 
    static String serverPort = "3232";
    
    private static Runnable threadPublisher = new Runnable() {
        public void run() {        	
    		String line;
 			 		
    		try{
    			while (true) {
    		    	   
    		           String option;    		          
    		    	   BufferedReader in = new BufferedReader(new InputStreamReader(System.in));           
    		           
    		    	   System.out.print("0: Para criar novo tópico:\n1: Publicar mensagem");
    		    	   option = in.readLine();    	   
    		    	   
    		           switch (option) {
    						case "0":					  
    							createNewTopic(rmiServer);
    							break;
    						case "1":
    							publishMessage(rmiServer, serverAddress, serverPort);
    					        break;		
    				}
    				
    		        System.out.println("\nTo leave enter /quit in a new line");
    				line = in.readLine();
    				if(line.startsWith("/quit")) break;   
    			}
    		} catch(IOException e){
    			System.out.println(e.getMessage());
    		}
       }
    };
    
	static public void main(String args[])
    {
       try{
    	   
    	   if(args.length >= 2){
    		     serverAddress = args[0]; 
    		     serverPort = args[1];    		   
    	   }else{
    		   System.out.println("É necessário informar o ip e porta do servidor!");
	  		   System.exit(0);
    	   }
    	   
           // get the registry 
           registry = LocateRegistry.getRegistry(
								               serverAddress,
								               (new Integer(serverPort)).intValue());   	             
    	   
           // look up the remote object
           rmiServer = (ReceiveMessageInterface)(registry.lookup("rmiServer"));
           
           new Thread(threadPublisher).start();

       }
       catch(RemoteException e){
           e.printStackTrace();
       }
       catch(NotBoundException e){
           e.printStackTrace();
       } 
    }
	
	/*
	 * Método para criação de tópicos
	 */
    public static void createNewTopic(ReceiveMessageInterface rmiServer) {
       String topicName;
       String topicMessage;
       try{ 
		   //lendo mensagem do cliente
		   BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
		   
	 	   System.out.println("Informe o nome do tópico: ");
	 	   topicName = in.readLine();	
	 	   System.out.println("Digite a mensagem para este tópico: ");
	 	   topicMessage = in.readLine();
	 	   
	 	   //instanciando um tópico novo 
	 	   Topic newTopic = new Topic();
	 	   newTopic.setTopic(topicName);
	 	   //inserindo mensagem para o tópico
	 	   newTopic.insertMessage(topicMessage, 1);		   
	 	   rmiServer.addTopic(newTopic);  	 	   	 	  
	 	   
       }catch(IOException e){
    	   e.printStackTrace();
       }
	}
    
    /*
     * Publicar nova mensagem para o tópico
     */
    public static void publishMessage(ReceiveMessageInterface rmiServer, String serverAddress, String serverPort) {
    	String message;
    	String idTopic;
    	
    	try{
 		   //lendo mensagem do cliente
 		   BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
 		   
	 	   //listando todos os topicos
	 	   listAllTopics(rmiServer);
	 	   System.out.println("Selecione o tópico: ");
	 	   idTopic = in.readLine();
	 	  
	 	   System.out.println("Digite a mensagem que deseja publicar: ");
	       message = in.readLine();	
	       
	       rmiServer.addMessageTopic(message, idTopic);
	       System.out.println("Mensagem adicionada com sucesso: ");
	       
	       //notificando subscribers inscritos neste tópico
	       notifySubscribers(message, idTopic, rmiServer);
  	
    	}catch(IOException e){
    		e.printStackTrace();
    	}
	}
    
    /*
     * Método para enviar notificação a todos os subscribers inscritos no tópico
     */
    public static void notifySubscribers(String message, String idTopic, ReceiveMessageInterface rmiServer) {
		// TODO Auto-generated method stub
		try {		
			rmiServer.notifyMessageToSubscribers(message, Integer.parseInt(idTopic));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /*
     * Método para listar todos os tópicos cadastrados
     */
	public static void listAllTopics(ReceiveMessageInterface rmiServer) {
        try{ 
     	   ArrayList<Topic> topicList = new ArrayList<Topic>();
 		   System.out.println("tópicos cadastrados: \n");
 		   
 		   topicList = rmiServer.getTopics();  
 		   for (Topic topic : topicList) { 			  
 			  System.out.println("ID: " + topic.getTopicId()+ " | " + topic.getTopic());
 		   }
        }catch(IOException e){
     	   e.printStackTrace();
        }
 	}  
}
