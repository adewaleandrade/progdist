
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
 * The SimpleTopicSubscriber class consists only of a main
 * method, which receives one or more messages from a topic using
 * asynchronous message delivery.  It uses the message listener
 * TextListener.  Run this program in conjunction with
 * SimpleTopicPublisher.  
 *
 * Specify a topic name on the command line when you run the 
 * program. To end the program, enter Q or q on the command line.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SubscriberTest {
	public static void main(String[] args) {
		SubscriberImpl subscriber = new SubscriberImpl(args);
		subscriber.execute();
	}
}


class SubscriberImpl {
	private static InputStreamReader isr;
	private static BufferedReader br;
	private static String content = "";
	private ArrayList topics;
	
    /**
     * Main method.
     *
     * @param args     the topic used by the example
     */
    public SubscriberImpl(String[] args) {
        String topicName = null;
        topics = new ArrayList();
                
        /*
         * Read topic name from command line and display it.
         */
        if (args.length > 1) {
            System.out.println("Executar: java Subscriber <topic-name> [opcional]");
            System.exit(1);
        } else if (args.length == 1) {
        	topicName = new String(args[0]);
        	System.out.println("Topico:" + topicName);
        	SubscriberThread subscriberThread = new SubscriberThread(topicName,true);
        	subscriberThread.start();
        	topics.add(subscriberThread);
        }
    }
    protected void execute() {
    	MainMenuThread mainMenuThread = new MainMenuThread(topics);
    	mainMenuThread.start();
    }
}

class MainMenuThread extends Thread {
	private static InputStreamReader isr;
	private static BufferedReader br;
	private static String content = "";
	private ArrayList topics;
	
	public MainMenuThread(ArrayList topics) {
    	this.topics = topics;
    }
	public void run() {
		try {
			while (true) {
				System.out.println("Opcoes (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
				System.out.println("1: Subscrever a um novo topico");
				isr = new InputStreamReader(System.in);
				br = new BufferedReader(isr);
				content = br.readLine();
				checkExit(content);
				switch (Integer.parseInt(content)) {
				case 1:
					subscribe(content);
					break;
				default:
					System.out.println("Opcao Invalida");
				}
			}
		} catch (IOException e) {
			System.out.println("I/O exception: " + e.toString());
		} 
	}
	private  void subscribe(String topicName) {
    	topicName = chooseTopic();
		if (topicName == null) {
			return;
		}
		boolean isStarted = (topics.size() == 0);
		
    	SubscriberThread subscriberThread = new SubscriberThread(topicName,isStarted);
    	subscriberThread.start();
    	topics.add(subscriberThread);
    	System.out.println("inscrito no topico " + topicName +" com sucesso!");
	}
    private String chooseTopic() {
		int index = -1;
		ArrayList allTopics = ManipulateTopics.listTopics();
		if (allTopics.size() == 0) {
			System.out.println("Não há tópicos disponiveis.");
			return null;
		}
		System.out.println("Topicos Disponiveis: ");
		for (int i = 0; i < allTopics.size(); i++) {
			System.out.println(i + ": " + allTopics.get(i));
		}
		System.out.println("Digite o nome do Topico qual deseja se inscrever (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		try {
			content = br.readLine();
			checkExit(content);
			index = Integer.parseInt(content);
		} catch (IOException e) {
			System.out.println("I/O exception: " + e.toString());
		} catch (NumberFormatException e) {
			System.out.println("Opcao invalida.");
			return null;
		}
		if ((index >= 0) && (index < allTopics.size())) {
			return (String) allTopics.get(index);
		} else {
			System.out.println("Opcao invalida.");
			return null;
		}
	}

    private  void closeReaders() {
		try {
			isr.close();
			br.close();
		} catch (IOException e) {
			System.out.println("I/O exception: " + e.toString());
		}

	}

	private void checkExit(String content) {
		if ((content.equals("q")) || (content.equals("Q"))) {
			System.out.println("Good Bye, ");
			System.exit(0);
		}
	}
}
class SubscriberThread extends Thread {
	
	String                  topicName = null;
    Context                 jndiContext = null;
    TopicConnectionFactory  topicConnectionFactory = null;
    TopicConnection         topicConnection = null;
    TopicSession            topicSession = null;
    Topic                   topic = null;
    TopicSubscriber         topicSubscriber = null;
    MapListener             topicListener = null;
    InputStreamReader       inputStreamReader = null;
    char                    answer = '\0';
    boolean isStartedContext = false;
    
    public SubscriberThread(String topicName, boolean isStartedContext) {
    	this.topicName = topicName;
    	this.isStartedContext = isStartedContext;
    }
    public void run() {
    	/* 
         * Create a JNDI API InitialContext object if none exists
         * yet.
         */
    	if ( isStartedContext) {
    		System.out.println("ISSTARTED");
	        try {
	            jndiContext = new InitialContext();
	            topicConnectionFactory = (TopicConnectionFactory)jndiContext.lookup("TopicConnectionFactory");
	        } catch (NamingException e) {
	            System.out.println("Nao foi possivel criar o contexto API JNDI: " + e.toString());
	            e.printStackTrace();
	            System.exit(1);
	        } 
    	} else {
    		System.out.println("IS NOT STARTED");
    	}
    	

        /* 
         * Look up connection factory and topic.  If either does
         * not exist, exit.
         */
        try {
            
            topic = (Topic) jndiContext.lookup(topicName);
        } catch (NamingException e) {
            System.out.println("JNDI API lookup falhou: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        /*
         * Create connection.
         * Create session from connection; false means session is
         * not transacted.
         * Create subscriber.
         * Register message listener (TextListener).
         * Receive text messages from topic.
         * When all messages have been received, enter Q to quit.
         * Close connection.
         */
        try {
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicSubscriber = topicSession.createSubscriber(topic);
            topicListener = new MapListener();
            topicSubscriber.setMessageListener(topicListener);
            topicConnection.start();
            inputStreamReader = new InputStreamReader(System.in);
            //System.out.println("222Opcoes (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
			//System.out.println("1: Subscrever a um novo topico");
            while (!((answer == 'q') || (answer == 'Q'))) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    System.out.println("I/O exception: " 
                        + e.toString());
                }
            }
            System.out.println("111Opcoes (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
			System.out.println("1: Subscrever a um novo topico");
        } catch (JMSException e) {
            System.out.println("Exception occurred: " + e.toString());
        } finally {
            if (topicConnection != null) {
                try {
                    topicConnection.close();
                } catch (JMSException e) {}
            }
        }
        System.out.println("111Opcoes (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
		System.out.println("1: Subscrever a um novo topico");
    }
}
