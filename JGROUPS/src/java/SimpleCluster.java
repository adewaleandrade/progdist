package com.jgroups.rest;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class SimpleCluster extends ReceiverAdapter {
	
	
    public JChannel channel;
    String user_name = System.getProperty("user.name", "n/a");
    final List<String> state = new LinkedList<String>();
    
    
    private static final String DB_NAME = "[DATABASE-NAME]";
        
	private static final String DB_DRIVER = "org.postgresql.Driver";
	private static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/"+DB_NAME;
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "123465";    
    
    public SimpleCluster(){
    	
    }
    
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
    	
    	String line="";
    	try{
    		//executeTransactionQuery(msg.getObject().toString());
            line = msg.getSrc() + ": " + msg.getObject();
            System.out.println(line);
            
            synchronized(state) {
                state.add(line);
            }            
    	}catch(Exception e){
    		this.channel.disconnect();
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

    /*
    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("DBCluster");    

        channel.getState(null, 10000);
        eventLoop();
        
        channel.close();
    }
*/
    public void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("DBCluster");    

        channel.getState(null, 10000);
        //eventLoop();
        
        //channel.close();
    }
    
    public void closeChannel(){
    	channel.close();
    }
    public void sendMessage(String message) throws Exception{
        Message msg = new Message(null, null, message);
        channel.send(msg); 
    }    
  /*  
    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> "); System.out.flush();
                String line = in.readLine().toLowerCase();
                String origLine = line;
                if(line.startsWith("quit") || line.startsWith("exit")) {
                    break;
                }
                line="[" + user_name + "] " + line;
                Message msg = new Message(null, null, line);
                channel.send(msg);                
            }
            catch(Exception e) {
            }
        }
    }
*/
    public static void main(String[] args) throws Exception {
        //new SimpleCluster().start();
    }
    
    public void executeTransactionQuery(String query) throws SQLException{
    	
		Connection dbConnection = null;
		PreparedStatement preparedStatementInsert = null;
		PreparedStatement preparedStatementUpdate = null;	
		
		try {
			dbConnection = getDBConnection();

			dbConnection.setAutoCommit(false);

			preparedStatementInsert = dbConnection.prepareStatement(query);
			preparedStatementInsert.setInt(1, 999);
			preparedStatementInsert.setString(2, "mkyong101");
			preparedStatementInsert.setString(3, "system");
			preparedStatementInsert.setTimestamp(4, getCurrentTimeStamp());
			preparedStatementInsert.executeUpdate();

			dbConnection.commit();

			System.out.println("Done!");

		} catch (SQLException e) {

			System.out.println(e.getMessage());
			dbConnection.rollback();

		} finally {

			if (preparedStatementInsert != null) {
				preparedStatementInsert.close();
			}

			if (preparedStatementUpdate != null) {
				preparedStatementUpdate.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}
	}    
    
	private static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}    
	
	private static Connection getDBConnection() {

		Connection dbConnection = null;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}	
}
