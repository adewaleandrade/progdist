import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
public interface ReceiveMessageInterface extends Remote
{
	void listAllTopics() throws RemoteException;
	void addTopic(Topic topic) throws RemoteException;
	void addMessageTopic(String message, String idTopic) throws RemoteException;
	ArrayList<Topic> getTopics() throws RemoteException;
	void notifyMessageToSubscribers(String message, int idTopic) throws RemoteException;
	void subscribeTopic(ReceiveNotifyMessageInterface subscriber, int idTopic) throws RemoteException;
	void unSubscribeTopic(ReceiveNotifyMessageInterface subscriber, int idTopic) throws RemoteException;

}
