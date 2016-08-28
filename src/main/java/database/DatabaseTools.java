package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTools {
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private static String dbURL = "jdbc:derby:myDB;create=true;user=me;password=mine";
	//private static String dbURL = "jdbc:derby://localhost:1527/myDB;create=true;user=me;password=mine";

	private static Connection connection = null;




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public static Connection getConnection(){

		if (connection != null){
			return connection;
		}

		try{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			//Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();

			connection = DriverManager.getConnection(dbURL); 
			return connection;
		}
		catch (Exception except){
			except.printStackTrace();
		}
		return null;
	}

	
	
	public static Connection getNewConnection(){

		try{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			//Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();

			Connection newConnection = DriverManager.getConnection(dbURL); 
			return newConnection;
		}
		catch (Exception except){
			except.printStackTrace();
		}
		return null;
	}
	


	public static Connection getConnectionImproved(){

		try{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			//Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
		//	BasicDatabase ds = new BasicDatabase();
			//ds.setDriverClassName(org.apache.derby.jdbc.EmbeddedDriver.class.getName());
			 // ds.setUrl(dbURL);
			 // ds.setUsername("james");
			//  ds.setPassword("james");

			  
			connection = DriverManager.getConnection(dbURL); 
			return connection;
		}
		catch (Exception except){
			except.printStackTrace();
		}
		return null;

	}




	public static void shutdown(){
		try{
			if (connection != null){
				//DriverManager.getConnection("jdbc:derby:;shutdown=true");

				//DriverManager.getConnection(dbURL + ";shutdown=true");
				DriverManager.getConnection(dbURL);
				connection.close();
			}           
		}
		catch (SQLException sqlExcept){
			//sqlExcept.printStackTrace();
		}
	}
}
