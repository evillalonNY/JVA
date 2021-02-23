/**
 * File		:	PopulateTbVideo.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It inserts a video into table TbVideo (Jama.Matrix)
 *              or TBVideoColt (cern.colt.SparseMatrix)
 *              Jama does not hold sparse matrices,
 *              but they are richer in methods and operations.   
 *              using PreparedStatemnt into the Mckoi database. 
 *                     
 */
package jmckoi;
import jVideos.Video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import Jama.Matrix;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class PopulateTbVideo extends ConnectDataBase{
	
	String username="evillalon"; 
	String password="evillalon"; 
	boolean colt = false; // matrices type jama or cern.colt
	private static Logger mLog = 
	    Logger.getLogger(PopulateTbVideo.class.getName());
 private static boolean debug = false;
	//constructor for storage of Jama.Matrix	
public PopulateTbVideo(Object obj){
	super(); 
	if(!debug)
		mLog.setLevel(Level.WARNING); 
    Video.VideoPK pk= ((Video) obj).getVideoPK();
    String key = pk.getPrimaryKey(); 
    key=key.trim(); 
    Timestamp ts = new Timestamp(System.currentTimeMillis());
	mLog.info(ts.toString()+"");
	//connecting to the database
	Connection con = connection; 
    PreparedStatement vinsert=null;
    Matrix red = ((Video) obj).getRedMat(); 
	Matrix green = ((Video) obj).getGreenMat(); 
	Matrix blue = ((Video) obj).getBlueMat(); 
	String desc = ((Video) obj).getFormatDesc();
	long numFrm = ((Video) obj).getNumFrm();
	float stream = ((Video) obj).getTimeStream(); 
	      try {
	          // Create prepareStatement object to execute the queries on,
	
	          vinsert = con.prepareStatement("INSERT INTO TbVideo " +
	        		  "(urlLoc, redMat, greenMat, blueMat," +
	        		  " formatDesc, numFrm, tmStream, stmpt) " +
	        		  " VALUES ( ?, ?, ?, ?,?, ?, ?, ?)"); 
	          vinsert.setString(1, key);
	          vinsert.setObject(2, red);
	          vinsert.setObject(3, green);
	          vinsert.setObject(4, blue);
	          vinsert.setObject(5,desc);
	          vinsert.setObject(6, numFrm);
	          vinsert.setObject(7,stream);
	          vinsert.setObject(8,ts);
	          
	          int upd = vinsert.executeUpdate();
	          mLog.info("The insertion was " + upd); 
	          
	      }catch (SQLException e) {
           mLog.severe(
           "An error occured\n" +
           "The SQLException message is: " + e.getMessage()); 
           }
	      finally{
	          	
	           ListVideos lst = new	ListVideos("TbVideo", con);  	
	           lst.videoStore(); 
	            }
// Close the the connection.
          try {
             con.close();
          }catch (SQLException e2) {
            e2.printStackTrace(System.err);
           }

}

//constructor to store sparse Matrix of type cern.colt.Matrix
public PopulateTbVideo(Object obj, boolean colt){
	super(); 
	if(!debug)
		mLog.setLevel(Level.WARNING);
	this.colt = colt; 
    Video.VideoPK pk= ((Video) obj).getVideoPK();
    String key = pk.getPrimaryKey(); 
    key=key.trim(); 
    Timestamp ts = new Timestamp(System.currentTimeMillis());
	mLog.info(ts.toString()+"");
	//connecting to the database
	Connection con = connection; 
    PreparedStatement vinsert=null;
    SparseDoubleMatrix2D  red = ((Video) obj).getRedMatCOLT();  
    SparseDoubleMatrix2D  green = ((Video) obj).getGreenMatCOLT(); 
    SparseDoubleMatrix2D  blue = ((Video) obj).getBlueMatCOLT(); 
	String desc = ((Video) obj).getFormatDesc();
	long numFrm = ((Video) obj).getNumFrm();
	float stream = ((Video) obj).getTimeStream(); 
	      try {
	          // Create prepareStatement object to execute the queries on,
	
	          vinsert = con.prepareStatement("INSERT INTO TbVideoColt " +
	        		  "(urlLoc, redColt, greenColt, blueColt," +
	        		  " formatDesc, numFrm, tmStream, stmpt) " +
	        		  " VALUES ( ?, ?, ?, ?,?, ?, ?, ?)"); 
	          vinsert.setString(1, key);
	          vinsert.setObject(2, red);
	          vinsert.setObject(3, green);
	          vinsert.setObject(4, blue);
	          vinsert.setObject(5,desc);
	          vinsert.setObject(6, numFrm);
	          vinsert.setObject(7,stream);
	          vinsert.setObject(8,ts);
	          
	          int upd = vinsert.executeUpdate();
	          mLog.info("The insertion was " + upd); 
	          
	      }catch (SQLException e) {
           mLog.severe(
           "An error occured\n" +
           "The SQLException message is: " + e.getMessage()); 
           }

// Close the the connection.
	      finally{
          	
	           ListVideos lst = new	ListVideos("TbVideoColt", con);
	           lst.videoStore(); 
	            }
          try {
             con.close();
          }catch (SQLException e2) {
            e2.printStackTrace(System.err);
           }

}
}
