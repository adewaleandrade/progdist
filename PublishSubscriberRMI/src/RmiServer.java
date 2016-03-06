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
    static int topicId;   //XGH para gera��o de IDS para os t�picos :) ;) :D
    
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
       
        thisPort = 3232;  // this port(registry�s port)
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
    		System.out.println("Nenhum t�pico cadastrado.");
    	}else{
    		topic.listAllMessagesTopic();
    	}
	}
    
    /*
     * M�todo para adicionar um novo t�pico na lista
     * (non-Javadoc)
     * @see ReceiveMessageInterface#addTopic(Topic)
     */
    public void addTopic(Topic newTopic) {
    	newTopic.setTopicId(topicId);
    	topics.add(newTopic);
    	topicId++;
	}   
    
    /*
     * M�todo para adicionar noca mensagem ao t�pico selecionado
     * (non-Javadoc)
     * @see ReceiveMessageInterface#addMessageTopic(java.lang.String, java.lang.String)
     */
    public void addMessageTopic(String message, String idTopic) {	
    	 topics.get(Integer.parseInt(idTopic)).insertMessage(message, 9);
	}   
    
    /*
     * M�todo para obter lista de t�picos
     * (non-Javadoc)
     * @see ReceiveMessageInterface#getTopics()
     */
    public ArrayList<Topic> getTopics(){
    	return topics;
    }
    
    /*
     * M�todo para listar todas as mensagens cadastradas para o t�pico
     */
    public void getTopicMessages(int idTopic){
        try{  
  		    Map<String, Integer> messages = topics.get(idTopic).getAllMessagesTopic();

  			if(messages.isEmpty()){
  				System.out.println("Nenhuma mensagem cadastrada para este t�pico.");
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
     * M�todo para inscrever um cliente ao t�pico selecionado
     * (non-Javadoc)
     * @see ReceiveMessageInterface#subscribeTopic(ReceiveNotifyMessageInterface, int)
     */
    public synchronized void subscribeTopic(ReceiveNotifyMessageInterface client, int idTopic) throws RemoteException {
    	//inscrever somente se o cliente ainda n�o estiver inscrito no t�pico 
        if (!(topics.get(idTopic).getClients().contains(client))) {
        	topics.get(idTopic).addClient(client);
        	System.out.println("Novo subscriber inscrito! " + client);
        }
      }
    
    /*
     * M�todo para remover inscri��o do cliente ao t�pico
     * (non-Javadoc)
     * @see ReceiveMessageInterface#unSubscribeTopic(ReceiveNotifyMessageInterface, int)
     */
	public synchronized void unSubscribeTopic(ReceiveNotifyMessageInterface client, int idTopic) throws RemoteException {
		if ((topics.get(idTopic).getClients().contains(client))) {
			if (topics.get(idTopic).removeClient(client)) {
		    	System.out.println("Cliente " + client + " removido do t�pico " + topics.get(idTopic).getTopic());
		    	client.update("Incri��o cancelada com sucesso do t�pico: ", "" + topics.get(idTopic).getTopic());
		    } else {
	    		System.out.println("Cliente deconhecido: " + client + " n�o foi registrado.");
		    }
		}else{
        	//enviando nova mensagem para o cliente
        	client.update("Desculpe! Voc� n�o est� inscrito neste t�pico. ", "" + topics.get(idTopic).getTopic());
		}
	}    
    
	/*
	 * M�todo para notificar os clientes inscritos no t�pico
	 * (non-Javadoc)
	 * @see ReceiveMessageInterface#notifyMessageToSubscribers(java.lang.String, int)
	 */
    public void notifyMessageToSubscribers(String newMessage, int idTopic){
	        
        //percoreendo lista de clientes para envio de atuliza��es
        for (Enumeration e = topics.get(idTopic).getElements() ; e.hasMoreElements() ;) {
        	ReceiveNotifyMessageInterface client = (ReceiveNotifyMessageInterface) e.nextElement();
	        try {
	        	//enviando nova mensagem para o cliente
	        	client.update(newMessage, "" + topics.get(idTopic).getTopic());
	        }
	        catch (RemoteException ex) {
	           //caso a comunica��o falhe cliente � removido da lista
	           System.out.println("Comunica��o com o cliente " + client + " falhou.");
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