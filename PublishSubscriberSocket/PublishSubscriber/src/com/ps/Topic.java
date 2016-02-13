package com.ps;

import java.util.HashMap;
import java.util.Map;

public class Topic {
	private String topic;
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
	
	public void listAllMessages(){

		for (Map.Entry<String, Integer> entry : messages.entrySet())
		{
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		}
	}
	

}
