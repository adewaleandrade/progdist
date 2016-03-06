import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
 
public class RmiServer extends java.rmi.server.UnicastRemoteObject implements ReceiveMessageInterface{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7290639970974790371L;
	int      thisPort;
    String   thisAddress;
    Registry registry;    // rmi registry for lookup the remote objects.
    static Registry registrySubs;
    //static ReceiveNotifyMessageInterface rmiSubscriber;
    
    static ArrayList<Topic> topics;
    static Topic topic;
    static int topicId;   //XGH para geração de IDS para os tópicos :) ;) :D
    
    //
    static ArrayList<ReceiveNotifyMessageInterface> subscribersServers; 
    
    static public void main(String args[])
    {
        try{
        	RmiServer s = new RmiServer();
     	    topic = new Topic();
     	    topics = new ArrayList<Topic>();  
     	    topicId = 0;    	    
	    }
	    catch (Exception e) {
           e.printStackTrace();
           System.exit(1);
	    }
    }

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
            
            registrySubs = LocateRegistry.createRegistry( thisPort );
            registrySubs.rebind("rmiSubscriber", this);
        }
        catch(RemoteException e){
        	throw e;
        }
    }
    
    public void listAllTopics() {
    	if(topics.isEmpty()){
    		System.out.println("Nenhum tópico cadastrado.");
    	}else{
    		topic.listAllMessagesTopic();
    	}
	}
    
    public void addTopic(Topic newTopic) {
    	newTopic.setTopicId(topicId);
    	topics.add(newTopic);
    	topicId++;
	}   
   
    public void addMessageTopic(String message, String idTopic) {	
    	 topics.get(Integer.parseInt(idTopic)).insertMessage(message, 9);
	}   
    
    public ArrayList<Topic> getTopics(){
    	return topics;
    }
    
    public void subscribeTopicSubscriber(ReceiveNotifyMessageInterface subscriber){
    	subscribersServers.add(subscriber);
    }
    
    public ArrayList<ReceiveNotifyMessageInterface> getListSubscriberServer(){
    	return subscribersServers;
    }
        
    public void notifyMessageToSubscribers(String message, String idTopic){
    	try {
    		ArrayList<ReceiveNotifyMessageInterface> subs = new ArrayList<ReceiveNotifyMessageInterface>();
    		subs = getListSubscriberServer();
    		for (int i = 0; i < subs.size(); i++) {
    			subs.get(i).receiveMessage(message, idTopic);
			}
				
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // This method is called from the remote client by the RMI.
    // This is the implementation of the “ReceiveMessageInterface”.
    public void receiveMessage(String message, int topicIndex) throws RemoteException
    {
        System.out.println(message);
    }
    
    public void sendMessage(String message, int topicIndex) throws RemoteException
    {
        System.out.println(message);
    }
}