
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Topic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3023995218719689668L;
	private String topic;
	private int topicId;
	private Map<String, Integer> messages;
	
	public Topic() {
		messages = new HashMap<String, Integer>();
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
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
	
	public void listAllMessagesTopic(){
		if(messages.isEmpty()){
			System.out.println("Nenhuma mensagem cadastrada para este tópico.");
		}else{
			for (Map.Entry<String, Integer> entry : messages.entrySet())			{
			    System.out.println(entry.getKey() + "/" + entry.getValue());
			}			
		}
	}
}