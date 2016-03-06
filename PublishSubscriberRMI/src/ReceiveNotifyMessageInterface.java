import java.rmi.Remote;
import java.rmi.RemoteException;
public interface ReceiveNotifyMessageInterface extends Remote
{
	void receiveMessage(String message, String idTopic) throws RemoteException;
	void sendMessage(String message, int topicIndex) throws RemoteException;
	
}
