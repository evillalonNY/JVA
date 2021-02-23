/**
* File		:	DropVideo.java
* 
* Author	:	Elena Villalón
* 
* Contents	:  Opens database connection and queries it to drop 
*              one row or specific number of rows from a Table 
*              The primary key or urlLoc is the column of the video 
*              to be deleted from the table.   
*   
*  Uses: ConnectDatabase, 
*  January 28, 2007.
*                     
*/
package jmckoi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class DropVideo extends ConnectDataBase{
	ResultSet rs; //the result of the query
	String tabname= "TbVideoColt"; //the table to query in the database
	Connection con=null; 
	String strpk = ""; //the video url or primary key to drop
	String colname = "urlLoc";
	private static Logger mLog = 
		    Logger.getLogger(DropVideo.class.getName());
	private static boolean debug = false;
	public void setTabname(String tab){
		tabname = tab; 
	}
	public void setUrlLoc(String cl){
		colname = cl; 
	}
	public DropVideo(String strpk){
		super();
		if(!debug)
			mLog.setLevel(Level.WARNING);
		if(con==null) 
			 con = super.connection;
		
	      mLog.info("Sucessful connection..."); 
	      delete(strpk, tabname); 
	}
	public DropVideo(String strpk, String tab){
		super();
		tabname=tab;
		if(con==null) 
			 con = super.connection;
		
	      mLog.info("Sucessful connection..."); 
	      delete(strpk, tab); 
	}
	public void delete(String strpk, String tabname){
	Statement  stmt=null;
	  ResultSet rs= null; 
	      try {
	          // Create a Statement object to execute the queries on,
	          stmt = con.createStatement();  
	     
		String query = "DELETE" + " FROM " + tabname + " WHERE ";
		query = query + colname +" LIKE " + "'"+ "%" +strpk+"'";
		rs= stmt.executeQuery(query);
	      } catch (SQLException e) {
	          mLog.severe(
	                  "An error occured\n" +
	                  "The SQLException message is: " + e.getMessage()); 
	          e.printStackTrace();
	          
	                  }

//	        Close the the connection.
	                
            finally{
            	
           ListVideos lst = new	ListVideos(tabname, con);
            	
           lst.videoStore(); 
            }
            
	      
}

	public static void main(String[] args) {
		String mess1 = "Provide a video to drop";
		if(args.length<=0){
			JOptionPane.showMessageDialog(null, mess1, "Failed Query",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		DropVideo vdrop; 
		if (args.length <= 1){
		 vdrop = new DropVideo(args[0]);
		 return;
		}
		mLog.info(args[0] + "...." +args[1]); 
		vdrop = new DropVideo(args[0], args[1]);
		
	
}

}

