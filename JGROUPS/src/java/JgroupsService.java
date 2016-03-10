package com.jgroups.rest;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
 
@Path("/jgroups")
public class JgroupsService {
 
	static SimpleCluster cluster = new SimpleCluster();
	static boolean firstRequest = false;
	
	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {
 
		if(firstRequest == false){
			execute();
			firstRequest = true;
		}else{
			getMessage(msg);
		}
		String output = "Jersey say : " + msg;
		return Response.status(200).entity(output).build();
 
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