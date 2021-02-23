/**
 * File		:	ListVideos.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Opens database connection and queries the table.
 *              For each videos, select the url of the video (PK), 
 *              the number of frames, and the duration of time stream.
 *              Saves result of query in class variable ResultSet.   
 *   
 *  Uses: ConnectDatabase,                    
 */
package jmckoi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListVideos extends ConnectDataBase{
	ResultSet rs; //the result of the query
	String tabname=""; //the table to query in the database
	Connection con=null; 
	private static Logger mLog = 
	    Logger.getLogger(ListVideos.class.getName());
    private static boolean debug = false;
	public ListVideos(){
		super();
		if(!debug)
			mLog.setLevel(Level.WARNING);
		rs = null; 
	}
	public ListVideos(String tabname){
		this(); 
		this.tabname= tabname; 
	}
	public ListVideos(String tabname, Connection con){
		this(); 
		this.tabname= tabname; 
		this.con = con;
	}
    public ResultSet getRs(){
    	return rs; 
    }
	public List<String> videoStore(){
	 if(con==null) 
	 con = super.connection;
	 
	   
	      String str="List of videos:";
	      List<String> tostore = new ArrayList<String>(); 
		  Statement  stmt=null;
		  ResultSet rs; 
	      mLog.info("Sucessful connection..."); 
	      try {
	          // Create a Statement object to execute the queries on,
	          stmt = con.createStatement();  
	          String query = "SELECT DISTINCT urlLoc, numFrm, tmStream, stmpt"; 
	          if(this.tabname.equalsIgnoreCase("")){
	               //video url 
	  		  query = query + " FROM TbVideoColt ORDER BY stmpt DESC"; 
	          }else
	        	query = query + " FROM " + tabname + " ORDER BY stmpt DESC";  
	    	  
			  rs= stmt.executeQuery(query);
			  this.rs = rs; 
			  while (rs.next()) {
		           String key =rs.getString(1);
		           int nofrm = rs.getInt(2);
		           double tms = rs.getDouble(3); 
		           str = str+key +"\t"+nofrm+"\t"+tms+ "\n";
		           tostore.add(key); 
		         
		          }
		    
	      } catch (SQLException e) {
	          mLog.severe(
	                  "An error occured\n" +
	                  "The SQLException message is: " + e.getMessage()); 
	          e.printStackTrace();
	          
	                  }
	      
        /**Closing the database connection.
         * Gives troubles with table model
	                try {
	                   con.close();
	                   mLog.info("Connection to database close");
	                 }catch (SQLException e2) {
	                   e2.printStackTrace(System.err);
	                  }
	     */
            finally{
            	
            	mLog.info(str); 
            	
            }
	return tostore;       
	        
}
	 public static void main(String[] args) {
		 ListVideos vget; 
		 if(args.length == 0)
		   vget = new ListVideos("TbVideoColt"); //cern.colt matrices
		 else
			 vget = new ListVideos(args[0].trim());
 
		 vget.videoStore();
	     
	
 }
}
