package com;
import java.sql.*;
/**
*
* @author mayron
*/
public class Conexao {

    private static final String DB_NAME = "meioambiente";
    
	private static final String DB_DRIVER = "org.postgresql.Driver";
	private static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/"+DB_NAME;
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "123465";
	private static final String DB_HOST = "cachoeira.inema.intranet"; 
	
	public Connection conn = null;

	public Conexao() {

  	} 

	public ResultSet query(String query){		
		ResultSet rs;
		Statement st;
		
		try {			
		      st = conn.createStatement();
		      rs = st.executeQuery(query);			
			
		}catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		return rs;
	} 
	
	  public Connection connect()
	  {	    
	    try
	    {
	      Class.forName("org.postgresql.Driver");
	      String url = "jdbc:postgresql://"+DB_HOST+"/"+DB_NAME;
	      conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
	    }
	    catch (ClassNotFoundException e)
	    {
	      e.printStackTrace();
	      System.exit(1);
	    }
	    catch (SQLException e)
	    {
	      e.printStackTrace();
	      System.exit(2);
	    }
	    return conn;
	  }
	
}


