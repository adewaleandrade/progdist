/**
 *
 * @author Rasec and Juniorug
 */

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ServerMain extends UnicastRemoteObject{
    String thisAddress;
    int thisPort;
    // rmi registry for lookup the remote objects.
    Registry registry;
    
    public ServerMain() throws RemoteException { 
      try { 
           // get the address of this host.
          thisAddress= (InetAddress.getLocalHost()).toString();

      } catch (Exception e) {
          System.out.println("Trouble: " + e);
          throw new RemoteException("can't get inet address.",e);
      }
      thisPort = 3232;  // this port(registry's port)
      System.out.println("this address="+thisAddress+",port="+thisPort);

      try{
        Calculadora calc = new CalculadoraImpl(); 
        // create the registry and bind the name and object.
        registry = LocateRegistry.createRegistry( thisPort );
        registry.rebind("Calculadora",calc);
      }
      catch(RemoteException e){
           throw e;
      }

   }

  
   public static void main(String args[]) throws RemoteException { 
      try{
          ServerMain s = new ServerMain();
      }
      catch (Exception e) {
           e.printStackTrace();
           System.exit(1);
      }

   }
} 
    
    
