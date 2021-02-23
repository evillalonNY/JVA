/**
 * Opens the connection to database
 * evillalon@iq.harvard.edu
 */
package jmckoi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectDataBase {
	protected String username = "evillalon";
	protected String password = "evillalon"; 
	protected Connection   connection=null; 
	private static Logger mLog = 
	    Logger.getLogger(ConnectDataBase.class.getName());
	private static boolean debug = false;
    public String getUsername() {
    	return username;
    }
    public String getPassword() {
    	return password;
    }
    public Connection getConnection() {
    	return connection;
    }
    public void setUsername(String user){
    	this.username = user;
    	
    }
    public void setPassword(String passw){
    	this.password = passw;
    	
    } 
    public void setConnection(Connection conn){
    	this.connection = conn;
    	
    }
    public ConnectDataBase (){
    if(!debug)
    	mLog.setLevel(Level.WARNING);
    // Register the Mckoi JDBC Driver
    try {
      Class.forName("com.mckoi.JDBCDriver").newInstance();
    }
    catch (Exception e) {
      mLog.info(
        "Unable to register the JDBC Driver.\n" +
        "Make sure the JDBC driver is in the\n" +
        "classpath.\n");
      System.exit(1);
    }
    String url = "jdbc:mckoi:local://./db.conf";
    // The username & password to connect under.
     
    //  Make a connection with the local database.
    
    try {
     connection = DriverManager.getConnection(url, username, password);
        
      }
      catch (SQLException e) {
        mLog.severe(
  	 "Unable to create the database.\n" +
  	 "The reason: " + e.getMessage());
        return;
      }

}
}
