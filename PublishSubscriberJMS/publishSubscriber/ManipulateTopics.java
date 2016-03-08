
import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ManipulateTopics {

	private static final String ERROR = "ERROR: ";
	private static String command;
	private static String topicName;
	
	public static void main(String[] args) {
		   
		listTopics();
	}
	
   public static void addTopic(String topicName) {
	   try {
		   command = new String("j2eeadmin -addJmsDestination " + topicName + " topic");
		   Process p = Runtime.getRuntime().exec(command);
		   p.waitFor();
		   System.out.println("tópico "+ topicName + " criado com sucesso!");
		} catch (Exception e){
         	System.out.println(getErrorMessage(e));
      	} 
   }
	
   public static void removeTopic(String topicName) {
	   try {
		   command = new String("j2eeadmin -removeJmsDestination " + topicName );
		   Process p = Runtime.getRuntime().exec(command);
		   p.waitFor();
		   System.out.println("tópico "+ topicName + " removido com sucesso!");
		} catch (Exception e){
         	System.out.println(getErrorMessage(e));
      	} 
   }
   public static ArrayList listTopics() {
	   ArrayList topics = new ArrayList();
	   try {
		   
		   command = new String("j2eeadmin -listJmsDestination");
		   Runtime runtime = Runtime.getRuntime();
		   Process process = runtime.exec(command);

		   InputStream is = process.getInputStream();
		   InputStreamReader isr = new InputStreamReader(is);
		   BufferedReader br = new BufferedReader(isr);
		   String line;
		   int option = -2;	
		   
		   while ((line = br.readLine()) != null) {
			   if (option >= 0 ) {
				  int beginTopic = line.indexOf(":") + 2; 
				  int endTopic = line.indexOf(",") - 1;
				  topics.add(line.substring( beginTopic, endTopic));
			   }
			   option ++;
		   }
		} catch (Exception e){
         	System.out.println(getErrorMessage(e));
      	}
	   return topics;
   }
   private static String getErrorMessage(Exception e){
      return ERROR.concat(e.getMessage());
   }
}