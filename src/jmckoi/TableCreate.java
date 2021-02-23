/**
 * File		:	TableCreate.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It creates the tables, TbVIdeo and TbVideoColt, 
 *              to store instances of 
 *              Video.java class with PK the video URL.
 *              The matrices stored in the tables are 
 *              TbVideo (Jama.Matrix)
 *              or TBVideoColt (cern.colt.SparseMatrix)
 *              Jama do not compress memory for sparse matrices,  
 *                     
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



public class TableCreate extends ConnectDataBase {
private boolean jamaMat = true; 	//matrix for storage 
private static Logger mLog = 
    Logger.getLogger(TableCreate.class.getName());
private static boolean debug = false;	
public TableCreate(){
super();
if(!debug)
	mLog.setLevel(Level.WARNING);
}

public TableCreate(boolean mat){
   
	this(); 
	jamaMat = mat; 
	}
	public static void main(String[] args) 
	throws Exception, SQLException{
		TableCreate tb;
		if(args.length <= 0)
			 tb = new TableCreate();
		else{
			char f = (args[0].trim()).charAt(0);	
		    if(f == 'f' || f=='F')
			tb = new TableCreate(false);
		    else
		    	tb = new TableCreate();
		}
		    // Register the Mckoi JDBC Driver
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		mLog.info(ts.toString());
		 
	    Connection   con = tb.connection; 
	    Statement stmt=null;
	   
	   if(tb.jamaMat == true){//table with Jama matrices
	      try {
	    	  Video vid=(Video) (Class.forName("jVideos.Video").newInstance()); 
	          // Create a Statement object to execute the queries on,
	          stmt = con.createStatement();
	          stmt.executeQuery("DROP TABLE IF EXISTS TbVideo;"); 
	          stmt.executeQuery(
	        		  " CREATE TABLE IF NOT EXISTS TbVideo ( " +
	        		  " stmpt TIMESTAMP NULL, " + 
	        		  " redMat  JAVA_OBJECT(Jama.Matrix) NULL, " +
	        		  " greenMat  JAVA_OBJECT(Jama.Matrix) NULL, " +
	        		  " blueMat  JAVA_OBJECT(Jama.Matrix) NULL, " +
	        		  " formatDesc VARCHAR(100) NULL," +
	        		  " numFrm   BIGINT NULL, " +
	        		  " tmStream REAL NULL," +  
	        		  " urlLoc VARCHAR(100) NOT NULL PRIMARY KEY )" );
	          
              mLog.info("Table TbVideo created");
              
	          ResultSet result;
	          result = stmt.executeQuery(
	                  "SHOW TABLES;" );  
	          while(result.next())
	        	  mLog.info(result.getString(1)); 
	          result =stmt.executeQuery(
	        		  "DESCRIBE TbVideo;"); 
	          while(result.next())
	        	  mLog.info(result.getString(1)); 
	          
	          stmt.close();
	       
	          con.close();

	        }
	        catch (SQLException e) {
	         mLog.severe(
	    	"An error occured\n" +
	    	"The SQLException message is: " + e.getMessage());

	        }catch (Exception e){
	        	e.printStackTrace(); 
	        }
	   }
	   if(tb.jamaMat == false){ //table with Colt matrices
		      try {
		    	  Video vid=(Video) (Class.forName("jVideos.Video").newInstance()); 
		          // Create a Statement object to execute the queries on,
		          stmt = con.createStatement();
		          stmt.executeQuery("DROP TABLE IF EXISTS TbVideoColt;"); 
		          stmt.executeQuery(
		        		  " CREATE TABLE IF NOT EXISTS TbVideoColt ( " +
		        		  " stmpt TIMESTAMP NULL, " + 
		        		  " redColt  JAVA_OBJECT(cern.colt.matrix.impl.SparseDoubleMatrix2D) NULL, " +
		        		  " greenColt  JAVA_OBJECT(cern.colt.matrix.impl.SparseDoubleMatrix2D) NULL, " +
		        		  " blueColt  JAVA_OBJECT(cern.colt.matrix.impl.SparseDoubleMatrix2D) NULL, " +
		        		  " formatDesc VARCHAR(200) NULL," +
		        		  " numFrm   BIGINT NULL, " +
		        		  " tmStream REAL NULL," +  
		        		  " urlLoc VARCHAR(100) NOT NULL PRIMARY KEY )" );
		          
	              mLog.info("Table TbVideoColt created");
	               
		          ResultSet result;
		          result = stmt.executeQuery(
		                  "SHOW TABLES;" );  
		          while(result.next())
		        	  mLog.info(result.getString(1)); 
		          result =stmt.executeQuery(
		        		  "DESCRIBE TbVideoColt;"); 
		          while(result.next())
		        	  mLog.info(result.getString(1)); 
		          
		          stmt.close();
		       
		          con.close();

		        }
		        catch (SQLException e) {
		          mLog.severe(
		    	"An error occured\n" +
		    	"The SQLException message is: " + e.getMessage());

		        }catch (Exception e){
		        	e.printStackTrace(); 
		        }
		   }
	        // Close the the connection.
	        try {
	          con.close();
	        }
	        catch (SQLException e2) {
	          e2.printStackTrace(System.err);
	        }

	      }
	    
}

