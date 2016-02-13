
import BancodeDados.ReplicatedServer;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Aluno
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random rand = new Random();
        try {
            System.out.println("Criando primeiros servers.");
            //Lider inicial
            ReplicatedServer rs = new ReplicatedServer("Server1", 3231, rand.nextInt(10));
            rs.addServer(new ReplicatedServer("Server2", 3232, rand.nextInt(10)));
            rs.addServer(new ReplicatedServer("Server3", 3233, rand.nextInt(10)));        
            
            //enviando primeira mensagem
            Thread.sleep(2000);
            System.out.println("Lider manda primeira mensagem!"); 
            rs.sendMessage("primeira mensagem aqui ó!");
            
            Thread.sleep(2000);
            System.out.println("\nLider falha");            
            rs.removeServer(rs);
            
            ReplicatedServer newleader = (ReplicatedServer)ReplicatedServer.getleader();
            
            Thread.sleep(2000);
            System.out.println("Lider manda nova mensagem!"); 
            newleader.sendMessage("Lider novo manda mensagem, tudo ok!");
            
            Thread.sleep(2000);
            System.out.println("\nCriando novos servers.");
            newleader.addServer(new ReplicatedServer("Server4", 3234, rand.nextInt(10)));
            newleader.addServer(new ReplicatedServer("Server5", 3235, rand.nextInt(10)));
            newleader.addServer(new ReplicatedServer("Server6", 3236, rand.nextInt(10)));
            newleader.addServer(new ReplicatedServer("Server7", 3237, rand.nextInt(10)));
            
            Thread.sleep(2000);
            System.out.println("Lider novo falha"); 
            newleader.removeServer(newleader);
            
            newleader = (ReplicatedServer)ReplicatedServer.getleader();
            
            Thread.sleep(2000);
            System.out.println("Lider manda nova mensagem!");
            newleader.sendMessage("Lider novo manda mensagem, tudo ok!");            
            
            Thread.sleep(2000);
            System.out.println("\nReplica Qualquer Falha (pode ser o lider também)");      
            
            newleader.mataReplica(rand.nextInt(3));
            
            newleader = (ReplicatedServer)ReplicatedServer.getleader();
            
            Thread.sleep(2000);
            System.out.println("Lider manda nova mensagem!");
            newleader.sendMessage("Vamos mandar mais uma mensagem pra ver quem está vivo.");  
            
            System.out.println("\nFim!!!");
            
        } catch (UnknownHostException ex) {
            System.out.println("UnknownHostException");            
        } catch (RemoteException ex) {
            System.out.println("RemoteException");            
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
