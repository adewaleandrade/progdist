/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Interfaces;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Aluno
 */
public interface IReplicatedServer extends Remote{
        
    public void sendMessage (String string) throws UnknownHostException, RemoteException, InterruptedException;
    
    public void setMessage(String message) throws UnknownHostException, RemoteException;

    public void showMessage() throws UnknownHostException, RemoteException, InterruptedException;

    public void setReady(boolean b) throws UnknownHostException, RemoteException;

    public boolean isReady() throws UnknownHostException, RemoteException;

    
    
}
