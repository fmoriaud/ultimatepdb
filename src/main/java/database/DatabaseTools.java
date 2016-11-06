package database;

import java.sql.*;

public class DatabaseTools {


	public static final int maxCharInVarchar = 30000;


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


	public static void createDBandTableSequence(Connection connection) {

		try {
			Statement stmt = connection.createStatement();
			String sql = "DROP TABLE sequence";
			stmt.execute(sql);
			System.out.println("Drop all tables from myDB !");
			stmt.close();
		} catch (SQLException e1) {
			System.out.println("Table sequence is not existing so cannot be droped");
		}

		// check if table exists if not create it
		try {
			Statement stmt = connection.createStatement();
			//String createTableSql = "CREATE TABLE " + "sequence" + " (fourLettercode varchar(4), chainId varchar(1), sequenceString varchar(" + maxCharInVarchar + "), lastmodificationtime timestamp )";
			String createTableSql = "CREATE TABLE " + "sequence" + " (fourLettercode varchar(4), chainId varchar(2), chainType varchar(2), "
					+ "sequenceString varchar(" + maxCharInVarchar + "), PRIMARY KEY (fourLettercode, chainId) ) ";
			//System.out.println(createTableSql);
			stmt.executeUpdate(createTableSql);
			System.out.println("created table sequence in myDB !");
			stmt.close();
		} catch (SQLException e1) {
			System.out.println("Table sequence already exists in myDB !");
		}
	}


	public static String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(Connection connection, String fourLetterCode, String chainName) {

		String sequenceInDb = null;
		try {
			Statement stmt = connection.createStatement();
			String findEntry = "SELECT * from sequence WHERE fourLettercode = '" + fourLetterCode + "' and chainId = '" + chainName + "'";
			ResultSet resultFindEntry = stmt.executeQuery(findEntry);
			int foundEntriesCount = 0;
			String fourLetterCodeFromDB;
			String chainIdFromDB;
			if (resultFindEntry.next()) {
				foundEntriesCount += 1;

				fourLetterCodeFromDB = resultFindEntry.getString(1);
				chainIdFromDB = resultFindEntry.getString(2);
				sequenceInDb = resultFindEntry.getString(4);
			}

			if (foundEntriesCount != 1) {
				System.out.println("problem isFourLetterCodeAndChainfoundInDatabase " + fourLetterCode + "  " + chainName + "  " + foundEntriesCount);
				return null;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		return sequenceInDb;
	}
}
