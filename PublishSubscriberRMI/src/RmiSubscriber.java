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
	protected RmiSubscriber() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6994008455499297467L;
	int      thisPort;
    String   thisAddress;
    Registry registry;    // rmi registry for lookup the remote objects.		
	
    static ReceiveMessageInterface rmiServer;
    static Registry registryServer;
    static String serverAddress = "localhost"; 
    static String serverPort = "3232";
    
    static RmiSubscriber sub;
    
    /*
     * Thread para que o usuário possa visualizar novos tópicos e ter possibilidade de inscrever-se
     */
    private static Runnable threadSubscriber = new Runnable() {
        public void run() {
    		String line;
    		String idTopic;
    		
    		try{
    			BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
    			
    			while (true) {   
    				   System.out.println("Procurando tópicos...");
    		    	   if(checkTopicList(rmiServer)){    		    		   
    		    		   listAllTopics(rmiServer);
    	 		    	   System.out.println("Selecione um tópico informando o ID correspondente. ");
        			 	   idTopic = in.readLine();  
        			 	   
        			 	   if(idTopic.isEmpty()){
         			 		  System.out.println("Para increver-se é necessário informar um ID válido. ");
         			 	   }else{   
         			 		   	menuAcoesSubscriber(idTopic);
         			 	   }        			 	   
    		    	   }else{
    		    		   System.out.println("Nenhum tópico cadastrado.");
    		    	   } 
    		    	   
	    		       System.out.println("\n Para continuar [Enter] / Para sair digite [quit]");
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
           // get the registry 
           registryServer = LocateRegistry.getRegistry(
										               serverAddress,
										               (new Integer(serverPort)).intValue()
        		   									  );   	                  
           // look up the remote object
           rmiServer = (ReceiveMessageInterface)(registryServer.lookup("rmiServer"));      			
           sub = new RmiSubscriber();           
           new Thread(threadSubscriber).start();
       }
       catch(RemoteException e){
           e.printStackTrace();
       }
       catch(NotBoundException e){
           e.printStackTrace();
       } 
    }
	
	public static void menuAcoesSubscriber(String idTopic){
	  String option;
	  BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	  
	  try{
		  System.out.println("1: Para inscrever-se: ");
		  System.out.println("2: Para cancelar inscrição: ");
		  option = in.readLine();
	
		  switch (option) {
		  	case "1":
	 		  //inscrevendo cliente
	 		   subscribeTopic(idTopic, rmiServer); 							
	 		   break;
			case "2":
	 		  //inscrevendo cliente
	 		   unSubscribeTopic(Integer.parseInt(idTopic)); 
	 		   break;
	     }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	}
	
	/*
	 * Método para cancelar inscrição no tópico
	 */
	public static void unSubscribeTopic(int idTopic) {
	   
	  try {
			rmiServer.unSubscribeTopic(sub, idTopic);
	  }catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	}
	
	/*
	 * Método para inscrever cliente no tópico
	 */
	public static void subscribeTopic(String idTopic, ReceiveMessageInterface rmiServer) {
		try {
			 rmiServer.subscribeTopic(sub, Integer.parseInt(idTopic));
			 System.out.println("Inscrito com sucesso! \nQuando novas mensagens forem cadastradas neste tópico você sera notificado! ");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Método para listar todos os tópicos
	 */
    public static void listAllTopics(ReceiveMessageInterface rmiServer) {
        try{ 
     	   ArrayList<Topic> topicList = new ArrayList<Topic>();  
 		   topicList = rmiServer.getTopics();  
 		   if( !topicList.isEmpty()){
	 		   for (Topic topic : topicList) {
	 			 System.out.println("ID: " + topic.getTopicId()+ " | " + topic.getTopic());
	 		   }   
 		   }
        }catch(IOException e){
     	   e.printStackTrace();
        }
 	}

    public static boolean checkTopicList(ReceiveMessageInterface rmiServer){
  	   ArrayList<Topic> topicList = new ArrayList<Topic>(); 
  	   
	    try {
			topicList = rmiServer.getTopics();
			if(topicList.isEmpty())
			 return false;	  			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    return true;
    }
    
    /*
     * Método para atualizar os subscribers com as mensagens de atualização
     * Implementação da Interface ReceiveNotifyMessageInterface
     * (non-Javadoc)
     * @see ReceiveNotifyMessageInterface#update(java.lang.String, java.lang.String)
     */
	@Override
	public void update(String message, String topicName) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Mensagem: "+message +" tópico: "+ topicName);		
	} 
}
