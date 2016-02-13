/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancodeDados;

import Interfaces.IReplicatedServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuario
 */
public class ReplicatedServer extends java.rmi.server.UnicastRemoteObject implements IReplicatedServer {

    private static final ArrayList<IReplicatedServer> serverlist = new ArrayList<>();
    private static Integer leaderIndex;
    private int priority;
    private String message;
    private boolean leader;
    private boolean ready;
    private String nameService;
    private String thisAddress;
    private int port;

    private Registry registry;

    //constructor
    public ReplicatedServer(String nameService, int port, int priority) throws UnknownHostException, RemoteException {
        super();

        if (serverlist.isEmpty()) {
            this.leader = true;
            serverlist.add(this);
            leaderIndex = 0;
        }
        this.priority = priority;
        this.port = port;
        //se lista vazia o primeiro item eh lider

        this.ready = false;
        this.nameService = nameService;
        try {
            thisAddress = (InetAddress.getLocalHost()).toString();
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
            throw new RemoteException("can't get inet address.", e);
        }

        //System.out.println("this address=" + thisAddress + ",port=" + this.port);
        try {
            registry = LocateRegistry.createRegistry(this.port);
            registry.rebind(nameService, this);
        } catch (RemoteException e) {
            throw e;
        }

    }

    private int getPriority() throws UnknownHostException, RemoteException {
        return this.priority;
    }

    private void setPriority(int priority) throws UnknownHostException, RemoteException {
        this.priority = priority;
    }

    @Override
    public void sendMessage(String message) throws UnknownHostException, RemoteException, InterruptedException {
        //System.out.println("dentro do send. isleader? " + isleader());

        if (isleader()) {
            for (IReplicatedServer t : serverlist) {
                t.setMessage(message);
                Thread.sleep(2000);
                System.out.println("enviando mensagem para replica: " + ((ReplicatedServer)t).nameService);
            }
            //apos enviar a mensagem, verifica se pode fazer commit
            verifyReadyAndCommit();
        }
    }

    private void chooseLeader() throws UnknownHostException, RemoteException {
        if (!serverlist.isEmpty()) {
            ((ReplicatedServer) serverlist.get(0)).setleader(true);
            leaderIndex = 0;
            for (int i = 0; i < serverlist.size() - 1; i++) {
                if (((ReplicatedServer) serverlist.get(i + 1)).getPriority() > ((ReplicatedServer) serverlist.get(i)).getPriority()) {
                    ((ReplicatedServer) serverlist.get(i + 1)).setleader(true);
                    ((ReplicatedServer) serverlist.get(i)).setleader(false);
                    leaderIndex = i + 1;
                }
            }
        }
        
        System.out.println("Novo lider: " + ((ReplicatedServer) serverlist.get(leaderIndex)).getNameService());
    }

    private void verifyReadyAndCommit() throws UnknownHostException, RemoteException, InterruptedException {
        boolean ok = true;
        for (IReplicatedServer t : serverlist) {
            if (!t.isReady()) {
                ok = false;
            } else {
                Thread.sleep(2000);
                System.out.println("replica " + ((ReplicatedServer)t).nameService + " informa recebimento da mensagem");
            }
            
        }
        if (ok) {
            commitMessage();
        }
    }

    private void commitMessage() throws InterruptedException{
        Thread.sleep(2000);
        System.out.println("Mensagem comitada com sucesso, verificado valor nas réplicas:");

        //mandar todas replicas imprimir mensagem
        for (IReplicatedServer t : serverlist) {
            try {
                t.showMessage();
                t.setReady(false);
            } catch (UnknownHostException ex) {
                Logger.getLogger(ReplicatedServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(ReplicatedServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public void showMessage() throws UnknownHostException, RemoteException, InterruptedException{
        Thread.sleep(2000);
        System.out.println("Replica: " + this.getNameService() + " Mensagem: " + this.getMessage());
    }

    private String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String mensagem) {
        this.message = mensagem;
        this.setReady(true);
    }

    private boolean isleader() {
        return this.leader;
    }

    private void setleader(boolean leader) {
        this.leader = leader;
    }

    public static IReplicatedServer getleader() {
        for(int i=0;i<serverlist.size();++i){
            ReplicatedServer atual = (ReplicatedServer) serverlist.get(i);
            if(atual.isleader()){
                return atual;                
            }
        }
        return null;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void addServer(ReplicatedServer newServer) {
        serverlist.add(newServer);
    }

    public void removeServer(ReplicatedServer server) throws UnknownHostException, RemoteException {
        if (serverlist.contains(server)) {
            boolean lider = server.isleader();
            serverlist.remove(server);            
            if (lider) {
                chooseLeader();
            } else {
                leaderIndex = lookupLeaderIndex();
            }
        }
    }

    private  Integer lookupLeaderIndex(){
         for(int i=0;i<serverlist.size();++i){
            ReplicatedServer atual = (ReplicatedServer) serverlist.get(i);
            if(atual.isleader()){
                return i;                
            }
        }
        return null;
    }
    public Integer getLeaderIndex() {
        return leaderIndex;
    }

    public static void setLeaderIndex(Integer leaderIndex) {
        ReplicatedServer.leaderIndex = leaderIndex;
    }

    /**
     * @return the nameService
     */
    public String getNameService() {
        return nameService;
    }

    public void mataReplica(int index) throws InterruptedException{
        try {
            Thread.sleep(2000);
             System.out.println("replica " + ((ReplicatedServer)ReplicatedServer.serverlist.get(index)).getNameService() +" falhou!");
            this.removeServer((ReplicatedServer) ReplicatedServer.serverlist.get(index));
        } catch (UnknownHostException | RemoteException ex) {
        }
    }

    /**
     * @param nameService the nameService to set
     */
    public void setNameService(String nameService) {
        this.nameService = nameService;
    }
}
