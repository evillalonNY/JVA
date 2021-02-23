/**
 * File		:	MckoiQuery.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It connect to the database and invokes the 
 *              JDBCQueryTool that comes with Mckoi.
 *              The tool can be used to write SQL statements 
 *              and query the database and its tables. 
 *              
 *               February 2, 2007
 *                     
 */
package jmckoi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MckoiQuery extends ConnectDataBase{
	 String [] what;
	 Connection   connection=null;
	 private static Logger mLog = 
		    Logger.getLogger(MckoiQuery.class.getName());
	 private static boolean debug = false;
 public	MckoiQuery(){
	 super();
	 if(!debug)
		 mLog.setLevel(Level.WARNING);
	 connection = super.connection;
	 what = new String[6];
	 what[0] = "-url"; 
	 what[1] = new String("jdbc:mckoi:local://./db.conf");
	 what[2] = "-u";
	 what[3] = "evillalon";
	 what[4] = "-p"; 
	 what[5] = "evillalon"; 
     try{
     com.mckoi.tools.JDBCQueryTool.main(what);
     } catch (Exception e) {
         mLog.severe(e.getMessage());
     }
     finally{
    	 try {
    		    
   	      // .... Use 'connection' to talk to database ....
   	      // Close the connection when finished,
   	      connection.close();
   	      return; 
   	    }
   	    catch (SQLException e) {
   	        mLog.severe(
   	        "An error occured\n" +
   	        "The SQLException message is: " + e.getMessage());
   	    }
     }
 }
 public	MckoiQuery(String []what){
  super(); 
  if(!debug)
	  mLog.setLevel(Level.WARNING);
  connection = super.connection;
 this.what = what;
 try{
     com.mckoi.tools.JDBCQueryTool.main(what);
     } catch (Exception e) {
         mLog.severe(e.getMessage());
     }
     finally{
    	 try {
    		    
   	      // .... Use 'connection' to talk to database ....
   	      // Close the connection when finished,
   	      connection.close();
   	
   	    }
   	    catch (SQLException e) {
   	       mLog.severe(
   	        "An error occured\n" +
   	        "The SQLException message is: " + e.getMessage());
   	    }
     }
    	 
     }

 public static void main(String[] args) {
	 MckoiQuery query;
	 if(args.length < 6){
	 query = new MckoiQuery();
	 }else{
		 String what[] = new String[6]; 
		 for(int n = 0; n < 6; ++n)
			 what[n] =args[n];
		 query = new MckoiQuery(what);
	 }
	
 }
 
}
