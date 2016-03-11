package com;
import java.sql.*;
/**
*
* @author mayron
*/
public class Conexao {

    private static final String DB_NAME = "meioambiente";
    
	private static final String DB_DRIVER = "org.postgresql.Driver";	
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "123456";
	private static final String DB_HOST = "cachoeira.inema.intranet";
	private static final String DB_CONNECTION = "jdbc:postgresql://"+DB_HOST+":5432/"+DB_NAME;
	//private static final String DB_HOST = "localhost";
	
	public Connection conn = null;

	public Conexao() {

  	} 

	public ResultSet query(String query){		
		ResultSet rs = null;
		Statement st;
		
		try {					      
	    	  st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    	  rs = st.executeQuery(query);

		}catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		return rs;
	} 
	
	public boolean update(String query){		
		boolean retorno = false;
		Statement st;
		
		try {					      		      			
			  st = conn.createStatement();
	    	  st.execute(query);
	    	  retorno = true;
		}catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		return retorno;
	} 	
	
	  public Connection connect()
	  {	    
	    try
	    {
	      Class.forName(DB_DRIVER);
	      conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
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


