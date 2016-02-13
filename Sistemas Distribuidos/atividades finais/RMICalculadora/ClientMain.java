/**
 *
 * @author Rasec and Juniorug
 */



import java.lang.ArithmeticException;
import java.lang.NumberFormatException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;



public class ClientMain {
    public static void main(String[] args) { 
       Calculadora rmiServer;
       Registry registry;
       String serverAddress = "127.0.1.1";
       String serverPort = "3232";
       System.out.println("sending request to "+serverAddress+":"+serverPort);


        try { 
            // get the registry 
            registry = LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
            // look up the remote object
            rmiServer = (Calculadora)(registry.lookup("Calculadora"));
            // call the remote method
            
            System.out.println("subtraindo 4 - 3 = " + rmiServer.sub(4, 3) ); 
            System.out.println("somando 4 + 5 = " + rmiServer.sum(4, 5) ); 
            System.out.println("multiplicando 3 * 6 = " + rmiServer.mul(3, 6) ); 
            System.out.println("dividindo 9 / 3 = " + rmiServer.div(9, 3) ); 
        } 
        catch (RemoteException re) { 
            System.out.println(); 
            System.out.println("RemoteException"); 
            System.out.println(re); 
        } 
        catch (NotBoundException nbe) { 
            System.out.println(); 
            System.out.println("NotBoundException"); 
            System.out.println(nbe); 
        } 
        catch (ArithmeticException ae) { 
            System.out.println(); 
            System.out.println("java.lang.ArithmeticException"); 
            System.out.println(ae); 
        } 
    }
} 
  
