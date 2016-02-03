/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ads
 */
import java.util.ArrayList;

public class Programa {
    
        private int itemCount;
        private ArrayList<Integer> buffer;
        private Semaphore consumerSemaphore;
        private Semaphore producerSemaphore;
        private int compartilhada;

        Programa () {
            this.itemCount = 0;
            this.buffer = new ArrayList<Integer>();
            this.consumerSemaphore = new Semaphore(0);
            this.producerSemaphore = new Semaphore(1);
        }
    
        
    
        public static void main(String[] args) {
               Programa t = new Programa();
               t.run();
        } 
        
        public void run() {
            Consumer c = new Consumer(this,1);
            Producer p = new Producer(this,1);
            c.start();
            p.start();
            Consumer c1 = new Consumer(this,2);
            Producer p1 = new Producer(this,2);
            c1.start();
            p1.start();
        }
	
	/* Setters*/
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;	
	}
	
	public void setBuffer(ArrayList<Integer> buffer) {
		this.buffer = buffer;	
	}
	
	public void setConsumerSemaphore(Semaphore consumerSemaphore) {
		this.consumerSemaphore = consumerSemaphore;
	}
	
	public void setProducerSemaphore(Semaphore producerSemaphore) {
		this.producerSemaphore = producerSemaphore;	
	}
    	
	public void setcCompartilhada(int compartilhada) {
		this.compartilhada = compartilhada;	
	}

	/*Getters*/
	public int getItemCount() {
		return this.itemCount;	
	}
	
	public ArrayList<Integer> getBuffer() {
		return this.buffer;	
	}
	
	public Semaphore getConsumerSemaphore() {
		return this.consumerSemaphore;
	}
	
	public Semaphore getProducerSemaphore() {
		return this.producerSemaphore;	
	}
	
	public int getCompartilhada() {
		return this.compartilhada;	
	}
	public void incrementItemCount() {
		this.itemCount ++;
	}
	
	public void decrementItemCount() {
		this.itemCount --;
	}
    
}
