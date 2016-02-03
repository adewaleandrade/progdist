/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ads
 */
public class Consumer extends Thread {
    
        Programa a;
	int id; 
        public Consumer(Programa x, int id) {

               a = x;
	       this.id = id;	
        }

        public void run() {

               try {
                   while (true) {
                       a.getConsumerSemaphore().down();
                       while (a.getItemCount() == 0)
                           sleep(100);
                       int item;
                       item = a.getBuffer().get(0);
                       a.getBuffer().remove(0);
                       a.decrementItemCount();
                       System.out.println("consumer "+ this.id + ": consuming item "+item);
                       a.getProducerSemaphore().up();
                       for (int i =0;i<10000;i++);
                   }

               }

               catch(InterruptedException e) {

                       e.printStackTrace(); 

               }

        }
    
    
}
