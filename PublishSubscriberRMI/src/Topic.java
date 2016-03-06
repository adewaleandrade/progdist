
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Topic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3023995218719689668L;
	private String name;
	private int topicId;
	private Map<String, Integer> messages;
	private Vector<ReceiveNotifyMessageInterface> clients;
	
	public Topic() {
		messages = new HashMap<String, Integer>();
		clients = new Vector<ReceiveNotifyMessageInterface>();
	}
	public String getTopic() {
		return name;
	}
	public void setTopic(String topic) {
		this.name = topic;
	}
	public int getTopicId() {
		return topicId;
	}
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}	
	
	public Map<String, Integer> getMessages() {
		return messages;
	}
	public void insertMessage(String message, Integer publisherId) {
		try {
			this.messages.put(message, publisherId.intValue());
			System.out.println(this.messages.toString());
			
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public Map<String, Integer> getAllMessagesTopic(){
		return messages;
	}	
	
	public void listAllMessagesTopic(){
		if(messages.isEmpty()){
			System.out.println("Nenhuma mensagem cadastrada para este tópico.");
		}else{
			for (Map.Entry<String, Integer> entry : messages.entrySet())			{
			    System.out.println(entry.getKey() + "/" + entry.getValue());
			}			
		}
	}
	public Vector<ReceiveNotifyMessageInterface> getClients() {
		return clients;
	}
	
	public Enumeration<ReceiveNotifyMessageInterface> getElements() {
		return clients.elements();
	}
	
	public void setClients(Vector<ReceiveNotifyMessageInterface> clients) {
		this.clients = clients;
	}
	
	public void addClient(ReceiveNotifyMessageInterface client) {
		this.clients.addElement(client); 
	}	
	
	public boolean removeClient(ReceiveNotifyMessageInterface client) {
		return this.clients.removeElement(client);
	}		
}