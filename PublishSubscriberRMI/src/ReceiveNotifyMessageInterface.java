import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReceiveNotifyMessageInterface extends Remote
{
	void update(String message, String topicName) throws RemoteException;
	
}
