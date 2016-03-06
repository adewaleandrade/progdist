import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
public interface ReceiveMessageInterface extends Remote
{
	void receiveMessage(String message, int topicIndex) throws RemoteException;
	void sendMessage(String message, int topicIndex) throws RemoteException;
	
	void listAllTopics() throws RemoteException;
	void addTopic(Topic topic) throws RemoteException;
	void addMessageTopic(String message, String idTopic) throws RemoteException;
	ArrayList<Topic> getTopics() throws RemoteException;
	ArrayList<ReceiveNotifyMessageInterface> getListSubscriberServer() throws RemoteException;
	void subscribeTopicSubscriber(ReceiveNotifyMessageInterface subscriber) throws RemoteException;
	
	void notifyMessageToSubscribers(String message, String idTopic) throws RemoteException;
}
