
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
		   processCommand(command);
		   System.out.println("topico "+ topicName + " criado com sucesso!");
		} catch (Exception e){
         	System.out.println(getErrorMessage(e));
      	} 
   }
	
   public static void removeTopic(String topicName) {
	   try {
		   command = new String("j2eeadmin -removeJmsDestination " + topicName );
		   processCommand(command);
		   System.out.println("topico "+ topicName + " removido com sucesso!");
		} catch (Exception e){
         	System.out.println(getErrorMessage(e));
      	} 
   }
   public static ArrayList listTopics() {
	   ArrayList topics = new ArrayList();
	   try {
		   
		   command = new String("j2eeadmin -listJmsDestination");
		   Process p = Runtime.getRuntime().exec(checkOS(command));

		   InputStream is = p.getInputStream();
		   InputStreamReader isr = new InputStreamReader(is);
		   BufferedReader br = new BufferedReader(isr);
		   String line;
		   int option = -2;	
		   
		   while ((line = br.readLine()) != null) {
			   if ((option >= 0 ) && (line.indexOf("jms/") < 0)) {
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

   private static void processCommand(String command) {
	   try {
		   Process p = Runtime.getRuntime().exec(checkOS(command));
		   p.waitFor();
		} catch (IOException e){
         	System.out.println(getErrorMessage(e));
      	} catch (InterruptedException e){
         	System.out.println(getErrorMessage(e));
      	}   
   }
   
   private static String[] checkOS(String command){
	   String osName = System.getProperty("os.name" );
	   String[] cmd = new String[3];
	   //if( osName.toLowerCase().contains( "windows" ) ) {
	   if( osName.toLowerCase().indexOf("windows" ) >= 0 ) {
			cmd[0] = "cmd.exe" ;
			cmd[1] = "/C" ;
			cmd[2] = command;
	   }
	   else {
		   cmd[0] = command;
		   cmd[1] = null;
		   cmd[2] = null;
	   }
	   return cmd;
   }
}




















