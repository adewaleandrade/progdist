package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.jgroups.Message;

@RestController
@RequestMapping(value="jgroups")
public class Service {
		
	//@Autowired	
	@RequestMapping(value = "/executeQuery", method = RequestMethod.GET)
	public String execute(String query) throws Exception {
		Cluster cluster =  Cluster.getInstance();
		Message msg = new Message(null, null, query);		
		cluster.channel.send(msg); 	
		return "";
	}
		
	@RequestMapping(value = "/iniciar", method = RequestMethod.GET)
	public void iniciar() throws Exception {
		//Cluster cluster = new Cluster();		
		//cluster.start();
	}
		
	
	/*
	public Service() throws Exception{
		//cluster.start();	
		cluster = new Cluster();
		cluster.start();
	}	*/

}


