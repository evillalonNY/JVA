/**
 * File		:	TablesVideo.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Opens database connection and gets the database metadata
 *              Method videoStore takes  swing.JComboBox as argument 
 *              that stores the names of all tables in the database,
 *              and returns the JComboBox with names of tables.    
 *   
 *  Uses: ConnectDataBase                   
 */
package jmckoi;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;


public class TablesVideo extends ConnectDataBase{
	ResultSet rs; 
	boolean scrolling; 
    boolean closeCon=true; 
    Connection con;
    private static Logger mLog = 
        Logger.getLogger(TablesVideo.class.getName());
    private static boolean debug = false;
	public TablesVideo(){
		super();
		if(!debug)
			mLog.setLevel(Level.WARNING);
	}
	public TablesVideo(boolean cl ){
		this();
		closeCon = cl;
	}
	
   public ResultSet getRs(){
	   return rs; 
   }
   
   public boolean getScrolling(){
	   return scrolling; 
   }
   public Connection getCon(){
   return con;
   }
   
   
	public JComboBox videoStore(JComboBox tableNames){
	
	 con = super.connection; 
	 String tbnames = "";
	      mLog.info("Sucessful connection..."); 
	      try {	  
	    	 
	    	  	  
			  DatabaseMetaData meta =  con.getMetaData();  
			  if(meta.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)){
				  scrolling = true;
				  Statement   stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						  ResultSet.CONCUR_READ_ONLY);
				  
			  }else {
				  Statement stmt = con.createStatement(); 
				  scrolling = false; 
			  }
					  		  
	          // Create a Statement object to execute the queries on,
	    	   
			  ResultSet tables= meta.getTables(null, null, null, 
					  new String[]{"TABLE"});
			  while(tables.next()){
			  String str = tables.getString(3);
			  if(str.trim().contentEquals("TbVideoColt"))
			 tableNames.addItem(tables.getString(3));
			 tbnames = tbnames + "\t" + tables.getString(3);
			  }
			 tables.close(); 
	         
	      } catch (SQLException e) {
	          mLog.severe(
	                  "An error occured\n" +
	                  "The SQLException message is: " + e.getMessage()); 
	          e.printStackTrace();
	                  }

//	        Close the the connection.
	     if(closeCon)
	                 try {
	                    con.close();
	                    mLog.info("Connection to database close");
	                 }catch (SQLException e2) {
	                   e2.printStackTrace(System.err);
	                  }
	      
	      finally{
	    	  mLog.info(tbnames); 
	      }
	        return tableNames; 

}
	 public static void main(String[] args) {
		 TablesVideo vget = new TablesVideo(); 
         JComboBox comb = new JComboBox(); 
		 System.out.println(vget.videoStore(comb));
	 
}
}
