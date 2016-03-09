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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Publisher {
	private static String topicName = null;
	private static Context jndiContext = null;
	private static TopicConnectionFactory topicConnectionFactory = null;
	private static TopicConnection topicConnection = null;
	private static TopicSession topicSession = null;
	private static Topic topic = null;
	private static TopicPublisher topicPublisher = null;
	private static MapMessage message = null;
	private static String publisher = "Anonymous";
	private static String content = "";
	private static final String jmsProviderHost = "gsort.ifba.edu.br";
	private static InputStreamReader isr;
	private static BufferedReader br;

	/**
	 * Main method.
	 *
	 * @param args
	 *            the topic used by the example and, optionally, the number of
	 *            messages to send
	 */

	public static void main(String[] args) {

		if ((args.length < 1) || (args.length > 2)) {
			System.out.println("Executar: java Publisher <Seu-nome> <topico-nome>[Opcional] ");
			System.exit(1);
		}

		publisher = new String(args[0]);
		System.out.println("publisher: " + publisher);
		if (args.length > 1) {
			topicName = new String(args[1]);
			System.out.println("Topico:" + topicName);
		}

		/*
		 * Create a JNDI API InitialContext object if none exists yet.
		 */
		try {
			jndiContext = new InitialContext();
			System.out.println("JNDI to string: " + jndiContext.toString());
		} catch (NamingException e) {
			System.out.println("Nao foi possivel criar o contexto API JNDI: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}

		/*
		 * Look up connection factory and topic. If either does not exist, exit.
		 */
		try {
			topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup("TopicConnectionFactory");
			topicConnection = topicConnectionFactory.createTopicConnection();
			topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

			mainMenu();
		} catch (NamingException e) {
			System.out.println("JNDI API lookup falhou: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		} catch (JMSException e) {
			System.out.println("Excecao ocorreu: " + e.toString());
		} finally {
			if (topicConnection != null) {
				try {
					topicConnection.close();
				} catch (JMSException e) {
				}
			}
		}
	}

	private static void mainMenu() {
		try {
			while (true) {
				System.out.println("Opcoes: (Para encerrar o programa, digite Q ou q, e pressione <Enter>)");
				System.out.println("1: Criar Topico");
				System.out.println("2: Publicar mensagem");
				isr = new InputStreamReader(System.in);
				br = new BufferedReader(isr);
				content = br.readLine();
				if ((content.equals("q")) || (content.equals("Q"))) {
					System.out.println("Good Bye, " + publisher);
					System.exit(0);
				}
				switch (Integer.parseInt(content)) {
				case 1:
					createTopic();
					break;
				case 2:
					createMessage();
					break;
				default:
					System.out.println("Opcao Invalida");
				}
			}
		} catch (IOException e) {
			System.out.println("I/O exception: " + e.toString());
		} finally {
			closeReaders();
		}
	}

	private static void createTopic() {
		System.out.println("Digite o nome do Topico a ser criado: ");
		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		try {
			content = br.readLine();
			ManipulateTopics.addTopic(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O exception: " + e.toString());
		}
	}

	private static void createMessage() {
		try {

			topicName = chooseTopic();
			topic = (Topic) jndiContext.lookup(topicName);
			topicPublisher = topicSession.createPublisher(topic);
			message = topicSession.createMapMessage();

			System.out.println("Escrever a mensagem que voce deseja publicar (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
			isr = new InputStreamReader(System.in);
			br = new BufferedReader(isr);
			content = br.readLine();
			if ((content.equals("q")) || (content.equals("Q"))) {
				System.out.println("Good Bye, " + publisher);
				System.exit(0);
			}
			message.setStringProperty("publisher", publisher + "@" + jmsProviderHost);
			message.setStringProperty("subject", "New Message in topic: \"" + topicName + "\"");
			message.setStringProperty("content", content);
			System.out.println("Publishing message: " + message.getStringProperty("publisher"));
			System.out.println("Publishing message: " + message.getStringProperty("subject"));
			System.out.println("Publishing message: " + message.getStringProperty("content"));
			topicPublisher.publish(message);
		} catch (IOException e) {
			System.out.println("I/O exception: " + e.toString());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O exception: " + e.toString());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception occurred: " + e.toString());
		} 
	}

	private static String chooseTopic() {
		System.out.println("Topicos Disponiveis: ");
		ArrayList allTopics = ManipulateTopics.listTopics();
		for (int i = 0; i < allTopics.size(); i++) {
			System.out.println(i + ": " + allTopics.get(i));
		}
		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		try {
			content = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O exception: " + e.toString());
		}
		int index = Integer.parseInt(content);
		return (String) allTopics.get(index);
	}

	private static void closeReaders() {
		try {
			isr.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O exception: " + e.toString());
		}

	}
}
