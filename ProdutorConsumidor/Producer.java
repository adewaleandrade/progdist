/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ads
 */
public class Producer extends Thread {
  
  Programa a;
  int id;
  int contador;
  public Producer(Programa x, int id) {

   a = x;
   this.id = id;	
   contador =0;
 }

 public void run() {

   try {
     while (true) {
       a.getProducerSemaphore().down();
       while (a.getItemCount() == 10)
         sleep(100);
       contador ++;
       a.getBuffer().add(contador);
       a.incrementItemCount();
       System.out.println("produtor "+ this.id + ": producing item " + contador);
       a.getConsumerSemaphore().up();
       for (int i =0;i<10000;i++);
     }

 }

 catch(InterruptedException e) {

   e.printStackTrace(); 

 }

}


}
