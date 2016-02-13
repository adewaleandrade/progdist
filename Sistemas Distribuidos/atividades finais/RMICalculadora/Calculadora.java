/**
 *
 * @author Rasec and Juniorug
 */

import java.rmi.Remote;
import java.rmi.RemoteException; 

public interface Calculadora extends Remote { 
    public Long sum(long a, long b) throws RemoteException; 
    public Long sub(long a, long b) throws RemoteException; 
    public Long mul(long a, long b) throws RemoteException; 
    public Long div(long a, long b) throws RemoteException; 
}
