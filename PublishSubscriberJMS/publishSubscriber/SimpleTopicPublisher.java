/*
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 * 
 */
/**
 * The SimpleTopicPublisher class consists only of a main method,
 * which publishes several messages to a topic.
 * 
 * Run this program in conjunction with SimpleTopicSubscriber.  
 * Specify a topic name on the command line when you run the
 * program.  By default, the program sends one message.  
 * Specify a number after the topic name to send that number 
 * of messages.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.jms.*;
import javax.naming.*;

public class SimpleTopicPublisher {

    /**
     * Main method.
     *
     * @param args     the topic used by the example and,
     *                 optionally, the number of messages to send
     */
    public static void main(String[] args) {
        String                  topicName = null;
        Context                 jndiContext = null;
        TopicConnectionFactory  topicConnectionFactory = null;
        TopicConnection         topicConnection = null;
        TopicSession            topicSession = null;
        Topic                   topic = null;
        TopicPublisher          topicPublisher = null;
        MapMessage              message = null;
        String                  publisher = "Anonymous";
        String                  content = "";          
        final int               NUM_MSGS = 1;
	    final String 		    jmsuser = "jmsAdmin";
        final String 		    oc4juserpassword = "welcome1";
        final String 		    urlProvider = "opmn:ormi://";
        final String 		    jmsProviderHost = "gsort.ifba.edu.br";
        final String 		    colon = ":";
        final String 		    opmnPort = "6007";
        final String 		    oc4jinstance = "OC4J_JMS";
        
        if ( (args.length < 1) || (args.length > 2) ) {
            System.out.println("Usage: java SimpleTopicPublisher <topic-name> <your-name>");
            System.exit(1);
        } 
        topicName = new String(args[0]);
        System.out.println("Topic name is " + topicName);
        publisher = new String(args[1]);
        System.out.println("publisher name is " + publisher);
        
        /* 
         * Create a JNDI API InitialContext object if none exists
         * yet.
         */
        try {
            jndiContext = new InitialContext();
            System.out.println("JNDI to string: " + jndiContext.toString());
        } catch (NamingException e) {
            System.out.println("Could not create JNDI API context: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
        
        /* 
         * Look up connection factory and topic.  If either does
         * not exist, exit.
         */
        try {
            topicConnectionFactory = (TopicConnectionFactory)jndiContext.lookup("TopicConnectionFactory");
            topic = (Topic) jndiContext.lookup(topicName);
        } catch (NamingException e) {
            System.out.println("JNDI API lookup failed: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        /*
         * Create connection.
         * Create session from connection; false means session is
         * not transacted.
         * Create publisher and text message.
         * Send messages, varying text slightly.
         * Finally, close connection.
         */
        try {
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicPublisher = topicSession.createPublisher(topic);
            message = topicSession.createMapMessage();
            
            
            while(true) {
                try {
                    System.out.println("Write the message you want to publish (To end program, enter Q or q, then <return>):");
                    InputStreamReader isr = new InputStreamReader(System.in);
                    BufferedReader br = new BufferedReader(isr);
                    content = br.readLine();
                    if ((content.equals("q")) || (content.equals("Q"))) {
                        System.out.println("Good Bye, " +  publisher);
                        System.exit(0);
                    }
                    message.setStringProperty("publisher", publisher + "@" + jmsProviderHost);
                    message.setStringProperty("subject", "New Message in topic: \"" + topicName + "\"");
                    message.setStringProperty("content", content);
                    System.out.println("Publishing message: " +  message.getStringProperty("publisher"));
                    System.out.println("Publishing message: " +  message.getStringProperty("subject"));
                    System.out.println("Publishing message: " +  message.getStringProperty("content"));
                    topicPublisher.publish(message);
                } catch (IOException e) {
                    System.out.println("I/O exception: " 
                        + e.toString());
                }
            } 
            
        } catch (JMSException e) {
            System.out.println("Exception occurred: " + e.toString());
        } finally {
            if (topicConnection != null) {
                try {
                    topicConnection.close();
                } catch (JMSException e) {}
            }
        }
    }
}
