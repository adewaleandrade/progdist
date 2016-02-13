/**
 *
 * @author Rasec and Juniorug
 */


import java.lang.ArithmeticException;
import java.lang.NumberFormatException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JOptionPane;

public class CalculadoraImpl extends UnicastRemoteObject implements Calculadora {
           
    public CalculadoraImpl() throws RemoteException { 
        super();
    } 

    @Override
    public Long sum(long a, long b) throws RemoteException {        
       try { 
         
            return a + b;

        } catch (NumberFormatException e) {  
            JOptionPane.showMessageDialog(null, "Dado informado deve ser numerico!");  
            e.printStackTrace();
        }
        return null;          
    }

    @Override
    public Long sub(long a, long b) throws RemoteException {
       try { 
         
            return a - b;

        } catch (NumberFormatException e) {  
            JOptionPane.showMessageDialog(null, "Dado informado deve ser numerico!");  
            e.printStackTrace();
        } 
        return null;
    }

    @Override
    public Long mul(long a, long b) throws RemoteException {
        try { 
         
            return a * b;

        } catch (NumberFormatException e) {  
            JOptionPane.showMessageDialog(null, "Dado informado deve ser numerico!");  
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long div(long a, long b) throws RemoteException {
        
        try { 
         
            return a / b;

        } catch (ArithmeticException e) {  
            JOptionPane.showMessageDialog(null, "Erro de divisão por 0!");  
            e.printStackTrace();
        } catch (NumberFormatException e) {  
            JOptionPane.showMessageDialog(null, "Dado informado deve ser numerico!");  
            e.printStackTrace();
        }
        return null;
    }
} 
