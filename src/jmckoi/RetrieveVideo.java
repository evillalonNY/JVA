/**
 * File		:	RetrieveVideo.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It retrieves videos in table TbVideo of the Mckoi 
 *              database (see TableCreate) using PreparedStatemnt. 
 *              
 *   
 *  Uses: Video,                    
 */
package jmckoi;
import jVideos.Video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import Jama.Matrix;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
public class RetrieveVideo extends ConnectDataBase {
	
	Video vid; 
	Timestamp ts = new Timestamp(System.currentTimeMillis()); 
	String  keySelect = "all"; 
	String tabname ="TbVideoColt"; 
	boolean colt = true; 
	private static Logger mLog = 
	    Logger.getLogger(RetrieveVideo.class.getName());
    private static boolean debug = false;
	public Video getVideo(){
		return vid;
	}
	public String getkeySelect(){
		return keySelect;
	}
	public Timestamp getTs()
	{
		return ts; 
	}
	public void debug(){
		if(!debug)
			mLog.setLevel(Level.WARNING);
	}
	public RetrieveVideo(){
		debug();
		vid = null;
		getVideo(); 
	}
	public RetrieveVideo(String key){
		debug();
		keySelect = key.trim(); 
		findVideo(); 
	}
	public RetrieveVideo(String key, String tabname){
		debug();
		keySelect = key.trim(); 
		this.tabname = tabname; 
		if(!tabname.contentEquals("TbVideoColt")) this.colt = false; 
		findVideo(); 
	}
	public void findVideo( ){
		tabname.trim();
		
		if(!keySelect.equals("all"))
			mLog.info("Video url " + keySelect);
		
		else
			mLog.info("All videos in database selected");
		
		String key=""; 
		
		String desc=""; 
		long nofrm=0;
		float stream = 0.0f;
		
		//connect to database
 
    Connection con = super.getConnection(); 
    if(con == null)
    	con = super.connection; 
     
	  Statement  stmt=null;
      mLog.info("Sucessful connection..."); 
      try {
          // Create a Statement object to execute the queries on,
          stmt = con.createStatement();  
          String query = "SELECT stmpt, ";  //time-stamp
          if(!colt){
        	query = query + " redMat, " +  //matrix R
        	" greenMat, " +  //matrix G
        	" blueMat, ";   //matrix B
          }else{
        	 
        	query = query + " redColt, " +  //matrix R
    		" greenColt, " +  //matrix G
    		" blueColt, " ;   //matrix B  
          }
          query = query + 
          " formatDesc," +  //format description
		  " numFrm, " +  //number of frames
		  " tmStream," +    //duration of stream
		  " urlLoc";         //video url 
  		  
          if(!colt)
        	  query = query + " FROM TbVideo "; 
          else
        	  query = query + " FROM TbVideoColt ";  
        mLog.info(""+ keySelect.trim().length()); 
    	if  (keySelect.trim().length() > 0 && !keySelect.contentEquals("all")){
    
    		query = query + "WHERE urlLoc LIKE '" + keySelect + "'"; 
    	}
    		
		  ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        ts = rs.getTimestamp(1); 
        Matrix red;
        Matrix green; 
        Matrix blue;
		if(!colt){
		   red = (Matrix) rs.getObject(2); 
		   green = (Matrix) rs.getObject(3);
		   blue = (Matrix) rs.getObject(4);
		}else{
			SparseDoubleMatrix2D red2D = 
				(SparseDoubleMatrix2D) rs.getObject(2);
			red = new Matrix(red2D.toArray());
			SparseDoubleMatrix2D green2D = 
				(SparseDoubleMatrix2D) rs.getObject(3);
			green = new Matrix(green2D.toArray());
			SparseDoubleMatrix2D blue2D = 
				(SparseDoubleMatrix2D) rs.getObject(4);
			blue = new Matrix(blue2D.toArray());
		}
		desc = (String) rs.getString(5);
		nofrm = (long) rs.getLong(6); 
		stream = (float) rs.getFloat(7);
		key = (String) rs.getString(8); 
		if(debug)
		  blue.print(1, 0); 
		this.vid = new Video(red, green, blue, 
				desc, nofrm, stream); 
		this.vid.setPrimaryKey(key); 	
      }
      
      }catch (SQLException e) {
          mLog.severe(
                  "An error occured\n" +
                  "The SQLException message is: " + e.getMessage()); 
          e.printStackTrace();
                  }

//        Close the the connection.
      try {
         con.close();
         mLog.info("Connection to database close");
      }catch (SQLException e2) {
        e2.printStackTrace(System.err);
       }
               

	}
	
	 public static void main(String[] args) {
		 RetrieveVideo vget;  
 
		 String keySelect = ""; 
		 String tabname = "TbVideoColt"; 
		 if(args.length == 0){
			 vget = new RetrieveVideo("all");  
		 }else
	      if (args.length == 1){
			keySelect = new String(args[0]);
			keySelect = keySelect.trim(); 
			mLog.info("Video url selection is " + keySelect);
			vget = new RetrieveVideo(keySelect); 
		
			
	       }else 
	    	   if (args.length > 1){
				keySelect = new String(args[0]);
				keySelect = keySelect.trim(); 
				tabname = args[1].trim(); 
				mLog.info("Video url " + keySelect + "; table "+ tabname); 
				vget = new RetrieveVideo(keySelect, tabname); 
		
				
		       }
	      System.exit(0); 
		   
	
 }
}

