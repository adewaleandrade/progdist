
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

public class Admin {
	private static String topicName = null;
	private static Context jndiContext = null;
	private static TopicConnectionFactory topicConnectionFactory = null;
	private static TopicConnection topicConnection = null;
	private static String publisher = "Anonymous";
	private static String content = "";
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

		if (args.length != 1) {
			System.out.println("Executar: java Admin <Seu-nome> ");
			System.exit(1);
		}
		
		publisher = new String(args[0]);
		System.out.println("publisher: " + publisher);
		
		/*try {
			jndiContext = new InitialContext();
			topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup("TopicConnectionFactory");
			topicConnection = topicConnectionFactory.createTopicConnection();
			TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
		} catch (NamingException e) {
			System.out.println("Nao foi possivel criar o contexto API JNDI: " + e.toString());
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
		}*/
		mainMenu();
		
	}
	
	private static void mainMenu() {
		try {
			while (true) {
				System.out.println("Opcoes (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
				System.out.println("1: Criar Topico");
				System.out.println("2: Remover Topico");
				isr = new InputStreamReader(System.in);
				br = new BufferedReader(isr);
				content = br.readLine();
				checkExit(content);
				switch (Integer.parseInt(content)) {
				case 1:
					createTopic();
					break;
				case 2:
					removeTopic();
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
		System.out.println("Digite o nome do Topico a ser criado (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		try {
			content = br.readLine();
			checkExit(content);
			ManipulateTopics.addTopic(content);
			System.out.println("topico " + content + " criado com sucesso!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O exception: " + e.toString());
		}
	}

	private static void removeTopic() {

		topicName = chooseTopic();
		if (topicName == null) {
			return;
		}

		ManipulateTopics.removeTopic(topicName);
		System.out.println("topico " + topicName + " removido com sucesso!");

	}
	
	private static String chooseTopic() {
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
		System.out.println("Digite o nome do Topico a ser removido (Para encerrar o programa, digite Q ou q, e pressione <Enter>):");
		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		try {
			content = br.readLine();
			checkExit(content);
			index = Integer.parseInt(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	private static void closeReaders() {
		try {
			isr.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O exception: " + e.toString());
		}

	}

	private static void checkExit(String content) {
		if ((content.equals("q")) || (content.equals("Q"))) {
			System.out.println("Good Bye, " + publisher);
			System.exit(0);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
