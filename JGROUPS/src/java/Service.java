package com.jgroups.rest;

public class Service {

	static SimpleCluster cluster = new SimpleCluster();
	
	public Service(){
		execute();
	}
	
	public static void getMessage(String message){
        try {
                sendMessage(message);                
            }
            catch(Exception e) {
            }	
	}
	
	public static void execute(){
		try {
			cluster.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sendMessage(String message){
		try {
			cluster.sendMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
