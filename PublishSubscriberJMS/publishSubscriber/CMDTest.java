
import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CMDTest {

	private static final String ERROR = "ERROR: ";
	private static String command;
	private static String topicName;
	
	public static void main(String[] args) {
		   
		command = new String("ls -la");
		processCommand(command);
		//String[] cmd = checkOS(command);
		//System.out.println( "Inserido: [0: " + cmd[0] + "] [1: " + cmd[1] + "] [2: " + cmd[2] + "]");
	}
	
	private static void processCommand(String command) {
	   try {
		   System.out.println( "inside processCommand");
		   String[] cmd = checkOS(command);
		   System.out.println( "Inserido: [0: " + cmd[0] + "] [1: " + cmd[1] + "] [2: " + cmd[2] + "]");
		   Runtime r = Runtime.getRuntime();
		   System.out.println( "criado runtime");
		   Process p = r.exec(cmd);
		   System.out.println( "inside processCommand: process p = Runtime.getRuntime().exec(command)");
		   p.waitFor();
		} catch (NullPointerException e){
         	System.out.println(e.getMessage());
      	} catch (IOException e){
         	System.out.println(getErrorMessage(e));
      	} catch (InterruptedException e){
         	System.out.println(getErrorMessage(e));
	   }   
   }
	
	private static String[] checkOS(String command){
	   String osName = System.getProperty("os.name" );
	   System.out.println("os.name: " + osName );
	   String[] cmd = new String[3];
	   //if( osName.toLowerCase().contains( "windows" ) ) {
	   if( osName.toLowerCase().indexOf("windows" ) >= 0 ) {
			cmd[0] = "cmd.exe" ;
			cmd[1] = "/C" ;
			cmd[2] = command;
	   }
	   else {
		   cmd[0] = "/bin/bash";
		   cmd[1] = "-c";
		   cmd[2] = command;
	   }
	   return cmd;
   }
	
	private static String getErrorMessage(Exception e){
      return ERROR.concat(e.getMessage());
   }

}