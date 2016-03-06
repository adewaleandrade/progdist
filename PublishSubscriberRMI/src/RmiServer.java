import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
 
public class RmiServer extends java.rmi.server.UnicastRemoteObject implements ReceiveMessageInterface{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7290639970974790371L;
	int      thisPort;
    String   thisAddress;
    Registry registry;    // rmi registry for lookup the remote objects.
    
    static ArrayList<Topic> topics;
    static Topic topic;
    static int topicId;   //XGH para geração de IDS para os tópicos :) ;) :D
    
    /*
    private static Runnable threadUpdateSubscriber = new Runnable() {
        public void run() {
    		
        	while (true) {
        		
        		  verifyMessageToSubscribers();
        	      // colocando a Thread pra dormir 1 segundo.        
        	      try {
        	        Thread.sleep(1000);
        	      }
        	      catch (InterruptedException iex) { }
        	    }			 		
       }
    };    
    */
    
    static public void main(String args[])
    {
        try{
        	RmiServer s = new RmiServer();
     	    topic = new Topic();
     	    topics = new ArrayList<Topic>();  
     	    topicId = 0;    
    	    //new Thread(threadUpdateSubscriber).start();     	    
	    }
	    catch (Exception e) {
           e.printStackTrace();
           System.exit(1);
	    }
    }
    
    /*
     * Construtor da classe
     */
    public RmiServer() throws RemoteException
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
	        registry = LocateRegistry.createRegistry( thisPort );
            registry.rebind("rmiServer", this);
        }
        catch(RemoteException e){
        	throw e;
        }
    }
    
    /*
     * 
     * @see ReceiveMessageInterface#listAllTopics()
     */
    public void listAllTopics() {
    	if(topics.isEmpty()){
    		System.out.println("Nenhum tópico cadastrado.");
    	}else{
    		topic.listAllMessagesTopic();
    	}
	}
    
    /*
     * Método para adicionar um novo tópico na lista
     * (non-Javadoc)
     * @see ReceiveMessageInterface#addTopic(Topic)
     */
    public void addTopic(Topic newTopic) {
    	newTopic.setTopicId(topicId);
    	topics.add(newTopic);
    	topicId++;
	}   
    
    /*
     * Método para adicionar noca mensagem ao tópico selecionado
     * (non-Javadoc)
     * @see ReceiveMessageInterface#addMessageTopic(java.lang.String, java.lang.String)
     */
    public void addMessageTopic(String message, String idTopic) {	
    	 topics.get(Integer.parseInt(idTopic)).insertMessage(message, 9);
	}   
    
    /*
     * Método para obter lista de tópicos
     * (non-Javadoc)
     * @see ReceiveMessageInterface#getTopics()
     */
    public ArrayList<Topic> getTopics(){
    	return topics;
    }
    
    /*
     * Método para listar todas as mensagens cadastradas para o tópico
     */
    public void getTopicMessages(int idTopic){
        try{  
  		    Map<String, Integer> messages = topics.get(idTopic).getAllMessagesTopic();

  			if(messages.isEmpty()){
  				System.out.println("Nenhuma mensagem cadastrada para este tópico.");
  			}else{
  				for (Map.Entry<String, Integer> entry : messages.entrySet())			{
  				    System.out.println(entry.getKey() + "/" + entry.getValue());
  				}			
  			}  		   
         }catch(Exception e){
      	   e.printStackTrace();
         }
    }
    
    /*
     * Método para inscrever um cliente ao tópico selecionado
     * (non-Javadoc)
     * @see ReceiveMessageInterface#subscribeTopic(ReceiveNotifyMessageInterface, int)
     */
    public synchronized void subscribeTopic(ReceiveNotifyMessageInterface client, int idTopic) throws RemoteException {
    	//inscrever somente se o cliente ainda não estiver inscrito no tópico 
        if (!(topics.get(idTopic).getClients().contains(client))) {
        	topics.get(idTopic).addClient(client);
        	System.out.println("Novo subscriber inscrito! " + client);
        }
      }
    
    /*
     * Método para remover inscrição do cliente ao tópico
     * (non-Javadoc)
     * @see ReceiveMessageInterface#unSubscribeTopic(ReceiveNotifyMessageInterface, int)
     */
	public synchronized void unSubscribeTopic(ReceiveNotifyMessageInterface client, int idTopic) throws RemoteException {
		if ((topics.get(idTopic).getClients().contains(client))) {
			if (topics.get(idTopic).removeClient(client)) {
		    	System.out.println("Cliente " + client + " removido do tópico " + topics.get(idTopic).getTopic());
		    	client.update("Incrição cancelada com sucesso do tópico: ", "" + topics.get(idTopic).getTopic());
		    } else {
	    		System.out.println("Cliente deconhecido: " + client + " não foi registrado.");
		    }
		}else{
        	//enviando nova mensagem para o cliente
        	client.update("Desculpe! Você não está inscrito neste tópico. ", "" + topics.get(idTopic).getTopic());
		}
	}    
    
	/*
	 * Método para notificar os clientes inscritos no tópico
	 * (non-Javadoc)
	 * @see ReceiveMessageInterface#notifyMessageToSubscribers(java.lang.String, int)
	 */
    public void notifyMessageToSubscribers(String newMessage, int idTopic){
	        
        //percoreendo lista de clientes para envio de atulizações
        for (Enumeration e = topics.get(idTopic).getElements() ; e.hasMoreElements() ;) {
        	ReceiveNotifyMessageInterface client = (ReceiveNotifyMessageInterface) e.nextElement();
	        try {
	        	//enviando nova mensagem para o cliente
	        	client.update(newMessage, "" + topics.get(idTopic).getTopic());
	        }
	        catch (RemoteException ex) {
	           //caso a comunicação falhe cliente é removido da lista
	           System.out.println("Comunicação com o cliente " + client + " falhou.");
		       try {
					unSubscribeTopic(client, idTopic);
			    } catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
        }
    }
}