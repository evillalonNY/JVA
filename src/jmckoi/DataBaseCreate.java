/**
 * File		:	DataBaseCreate.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It creates the data directory for Mckoi database 
 *                     
 */
package jmckoi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
public class DataBaseCreate {
	private String username = "evillalon"; 
    private String password = "evillalon";
    private static Logger mLog = 
	    Logger.getLogger(DataBaseCreate.class.getName());
	private static boolean debug = false;
    public String getUsername(){
    	return username;
    }
    public String getPassword(){
    	return password;
    }
    public DataBaseCreate(){
    	super();
    }
    public DataBaseCreate(String user, String pass){
    	if(!debug)
    		mLog.setLevel(Level.WARNING);
    	this.username = user;
    	this.password = pass; 
    }   
	    public static void main(String[] args) {
	
	    // Register the Mckoi JDBC Driver
	    try {
	      Class.forName("com.mckoi.JDBCDriver").newInstance();
	      
	    }
	    catch (Exception e) {
	      mLog.severe(
	        "Unable to register the JDBC Driver.\n" +
	        "Make sure the JDBC driver is in the\n" +
	        "classpath.\n");
	      System.exit(1);
	    }
	
	    // This URL specifies we are connecting with a local database
	    // within the file system.  './db.conf' is the path of the
	    // configuration file of the database to embed.
	    String url = "jdbc:mckoi:local://./db.conf?create=true";  
	    
	   /**String username = "user"; 
	    * String password = "pass1212";
	    */
	    Statement stmt = null;
	    ResultSet rs = null; 
	    // Make a connection with the local database.
	    Connection connection;
	    DataBaseCreate db = new DataBaseCreate(); 
	    try {
	      connection = DriverManager.getConnection(url, db.username, db.password);
	  
	      mLog.info("Successful connection"); 
	      stmt = connection.createStatement();
	      rs = stmt.executeQuery("SHOW TABLES"); 
	      mLog.info(""+rs.toString()); 
	    }
	    catch (SQLException e) {
	      mLog.severe(
	        "Unable to make a connection to the database.\n" +
	        "The reason: " + e.getMessage());
	      System.exit(1);
	      return;
	    }
	
	    try {
	    
	      // .... Use 'connection' to talk to database ....
	
	      // Close the connection when finished,
	      connection.close();
	
	    }
	    catch (SQLException e) {
	      mLog.severe(
	        "An error occured\n" +
	        "The SQLException message is: " + e.getMessage());
	      return;
	    }
	
	  }

	}	

