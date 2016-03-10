package com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="jgroups")
public class Service extends ReceiverAdapter{
		
	
	public JChannel channel;
	final List<String> state = new LinkedList<String>();
	public Conexao conn;	
	
	@RequestMapping(value = "/executeQuery", method = RequestMethod.GET)
	public List<Map<String, Object>> execute(String query) throws Exception {
		conn = new Conexao();
		conn.connect();
		
		start();
		ResultSet result = sendMessage(query); 	
		return showResult(result);		
	}

	public List<Map<String, Object>> showResult(ResultSet rs) throws SQLException{
		
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		while (rs.next()) {
		    Map<String, Object> columns = new LinkedHashMap<String, Object>();

		    for (int i = 1; i <= columnCount; i++) {
		        columns.put(metaData.getColumnLabel(i), rs.getObject(i));
		    }
		    rows.add(columns);
		}
		return rows;
	}
	
    public void start() {
        try {
        	if(channel == null){
        		channel = new JChannel();
        		channel.setReceiver(this);
                channel.connect("DBCluster");    
    			channel.getState(null, 10000);
        	}
		} catch (Exception e) {			
			e.printStackTrace();
		}
    }	
    
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
    	
    	String line="";
    	try{    		
            line = msg.getSrc() + ": " + msg.getObject();
            System.out.println(line);
            
            synchronized(state) {
                state.add(line);
            }            
    	}catch(Exception e){
    		channel.disconnect();
    		e.printStackTrace();    		
    	}        
    }

    public void getState(OutputStream output) throws Exception {
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        List<String> list = (List<String>)Util.objectFromStream(new DataInputStream(input));
        synchronized(state) {
            state.clear();
            state.addAll(list);
        }
        System.out.println("received state (" + list.size() + " messages in chat history):");
        for(String str: list) {
            System.out.println(str);
        }
    }
    
    public void closeChannel(JChannel channel){
    	channel.close();
    }    
    
    public ResultSet sendMessage(String message){                
    	ResultSet resultado = null;
    	
    	try {
        	List<Address> members = channel.getView().getMembers();
        	Address target = members.get(0);
        	Message msg = new Message(target, null, message);
			channel.send(msg);				
			resultado =  conn.query(message);
		} catch (Exception e) {
			channel.disconnect();
			e.printStackTrace();		
		} 
        //executando a query e retornando resultado
        return resultado;
    }        
    
}



