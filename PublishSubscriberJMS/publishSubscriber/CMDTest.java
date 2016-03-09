import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.*;

public class CMDTest {

	private static final String ERROR = "ERROR: ";
	private static String command;
	private static String topicName;
	
	public static void main(String[] args) {
		   
		try {
		   
		   //command = new String("C:/j2sdkee1.3.1/bin/j2eeadmin -listJmsDestination");
		   command = new String("j2eeadmin -listJmsDestination");
		   //System.out.println(System.getProperty("java.class.path"));
		   //command = new String("echo $PATH");
		   Runtime runtime = Runtime.getRuntime();
		   Process process = runtime.exec(command);

		   /*InputStream is = process.getInputStream();
		   InputStreamReader isr = new InputStreamReader(is);
		   BufferedReader br = new BufferedReader(isr);
		   String line;
		   
		   while ((line = br.readLine()) != null) {
			   System.out.println(line);
		   }*/
		    InputStream stderr = process.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<ERROR>");
            while ( (line = br.readLine()) != null)
                System.out.println(line);
            System.out.println("</ERROR>");
            int exitVal = process.waitFor();
            System.out.println("Process exitValue: " + exitVal);
        } catch (Throwable t) {
            t.printStackTrace();
		} 
	}
}