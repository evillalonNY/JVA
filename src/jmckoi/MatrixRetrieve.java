/**
	 * File		:	MatrixRetrieve.java
	 * 
	 * Author	:	Elena Villalón
	 * 
	 * Contents	:	Opens database connection and queries a table;
	 *              select the RGB matrices in database 
	 *              for specific url's locations. Stores the mean  
	 *              frames for each video in some Collections. 
	 *              
	 *                
	 *   
	 *  Uses: ConnectDatabase,                    
	 */
package jmckoi;
import jalgo.MatrixOperation;
import jalgo.SVDMat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jclient.VideoFile;
import Jama.Matrix;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class MatrixRetrieve extends ConnectDataBase{

		ResultSet rs; //the result of the query
		String tabname=""; //the table to query in the database
		Connection con=null; 
		boolean colt = true; 
		static final boolean ols = false;//not getting good results 
		private static Logger mLog = 
		    Logger.getLogger(MatrixRetrieve.class.getName());
	    private static boolean debug = false;
		private static int colorno = 256; 
		/**
		 * it will select the mean (true) for each column of the video matrix RGB 
		 * or it will select the median of the column if false
		 */
		boolean media = false; 
		/**mean/median values of videos each color value*/
		List<double[]> meanred; 
		List<double[]> meangreen;
		List<double[]> meanblue;
		/**
		 * same as previous but log is not taken just counts 
		 * of pixels
		 */
		List<double[]> meanredCnt; 
		List<double[]> meangreenCnt;
		List<double[]> meanblueCnt;
		/** the Matrix for requested videos*/
		List<Matrix> redvid;
		List<Matrix> greenvid;
		List<Matrix> bluevid;
		/** All videos all frames
		 * number of rows = frames in video matrix; ncols=256
		 * Number of matrices = number of videos.
		 */ 
		Matrix[] matG; 
		Matrix[] matR;
		Matrix[] matB;
		
        private boolean lg = false;
    
        List<String> thevideos = new ArrayList<String>(); 
        
        
        public void setLg(boolean b){
        	lg = b; 
        }
        
        public List<String> getThevideos(){
        	return thevideos; 
        }
        
        public List<double[]> getMeangreen(){
        	return meangreen;
        }
        public List<double[]> getMeanred(){
        	return meanred; 
        }
        public List<double[]> getMeanblue(){
        	return meanblue;
        }
        public List<double[]> getMeangreenCnt(){
        	return meangreenCnt;
        }
        public List<double[]> getMeanredCnt(){
        	return meanredCnt; 
        }
        public List<double[]> getMeanblueCnt(){
        	return meanblueCnt;
        }
        public List<Matrix> getRedvid(){
        	return redvid;
        }
        public List<Matrix> getGreenvid(){
        	return greenvid; 
        }
        public List<Matrix> getBluevid(){
        	return bluevid;
        }
        public Matrix[] getMatG(){
        	return matG;
        }
        public Matrix[] getMatB(){
        	return matB;
        }
        public Matrix[] getMatR(){
        	return matR;
        }
       
        
		public MatrixRetrieve(){
			super();
			if(!debug)
				mLog.setLevel(Level.WARNING);
			rs = null; 
			redvid= new ArrayList<Matrix>();
			greenvid =new ArrayList<Matrix>();
			bluevid=new ArrayList<Matrix>();
		}
		public MatrixRetrieve(String tabname){
			this(); 
			this.tabname= tabname; 
			if(!tabname.contentEquals("TbVideoColt"))
				colt = false;
			
			this.media = true;
		}
		public MatrixRetrieve(String tabname, boolean w, boolean media){
			this(); 
			this.tabname= tabname; 
			if(!tabname.contentEquals("TbVideoColt"))
				colt = false;
			
			this.media = media;
		}
		public MatrixRetrieve(String tabname, boolean media){
			this(); 
			this.tabname= tabname; 
			this.media = media;
			if(!tabname.contentEquals("TbVideoColt"))
				colt = false;
		}
	    public ResultSet getRs(){
	    	return rs; 
	    }
	/** Connect database and gets the RGB matrices of all store videos	
	 * Calculates the mean for each video column all frames considered
	 * Stores mean vectors in Collections and calls buildarrays.
	 */
	    public void retreiveMat(String[] vid){			
	    		
			meanred = new ArrayList<double[]>(); 
			meangreen = new ArrayList<double[]>();
			meanblue = new ArrayList<double[]>();
			meanredCnt = new ArrayList<double[]>(); 
			meangreenCnt = new ArrayList<double[]>();
			meanblueCnt = new ArrayList<double[]>();
			String ya ="";
			int cnt=0;
		 if(con==null) 
		 con = super.connection;
		   
		      String str="List of videos: \n";  
			  Statement  stmt=null;
			  ResultSet rs; 
		      mLog.info("Sucessful connection..."); 
		      String [] queryall = new String[vid.length]; 
		      
		      try {
		    	  for (int k=0; k < queryall.length; ++k)
			      {
		    		  mLog.info(vid[k]);
		    		 
		    		  ya= vid[k]; 
		    		  cnt = k; 
			    	  if(!colt)
			    	  queryall[k] = new String("SELECT redMat, greenMat, blueMat FROM TbVideo ");  
			    	  else
			    	  queryall[k] = new String("SELECT redColt, greenColt, blueColt FROM TbVideoColt ");   
		          // Create a Statement object to execute the queries on,
		          stmt = con.createStatement();  
		         
		          if(!colt){
		        	
		           queryall[k] += " where urlLoc = " + vid[k].trim(); 
		          }else{
		        	  
			          queryall[k] +=  " where urlLoc = " + vid[k].trim(); 
		          }
		        	  queryall[k]+= ";";
		          
				  rs= stmt.executeQuery(queryall[k]);
				 
				  int tcl=0;
				  
				  int sent =0; 
				  while (rs.next()) {
					  Matrix red;
					  Matrix green;
					  Matrix blue;
					  
			           if(!colt){
			           red = (Matrix) rs.getObject(1); 
			   		   green = (Matrix) rs.getObject(2);
			   		   blue = (Matrix) rs.getObject(3);
			   		   
			   		   tcl = red.getColumnDimension(); 
			           }else{
			        	SparseDoubleMatrix2D red2D = 
			        		(SparseDoubleMatrix2D) rs.getObject(1);
			   			red = new Matrix(red2D.toArray());
			   			SparseDoubleMatrix2D green2D = 
			   				(SparseDoubleMatrix2D) rs.getObject(2);
			   			green = new Matrix(green2D.toArray());
			   			SparseDoubleMatrix2D blue2D = 
			   				(SparseDoubleMatrix2D) rs.getObject(3);
			   			blue = new Matrix(blue2D.toArray());
			   			tcl = red.getColumnDimension();  
			   			sent++;
			   			
			           }
			           rs.close();
			           Matrix redcopy =red.copy();
					   Matrix greencopy=green.copy();
					   Matrix bluecopy=blue.copy();
			           //takes the log of data: t.test
			           meanred.add(extractMean(red, true));
			   		   meangreen.add(extractMean(green,true));
			   		   meanblue.add(extractMean(blue,true));
			   		   redvid.add(red);
			   		   bluevid.add(blue);
			   		   greenvid.add(green);
			   		   //does not take log of data: relative errors
			   		   meanredCnt.add(extractMean(redcopy,false));
			   		   meangreenCnt.add(extractMean(greencopy,false));
			   		   meanblueCnt.add(extractMean(bluecopy,false));
			          }
				   colorno = tcl;   
		          
			      }
		    	  stmt.close();
		      } catch (SQLException e) {
		          mLog.severe(
		                  "An error occured\n" +
		                  "The SQLException message is: " + e.getMessage());
		          mLog.severe(ya+ "....." +cnt); 
		          e.printStackTrace();
		          
		                  }
		      
//		        Close the the connection.
		                try {
		                    con.close();
		                    mLog.info("Connection to database close");
		                 }catch (SQLException e2) {
		                   e2.printStackTrace(System.err);
		                  }
		                 
	            finally{
	            	mLog.info(str); 
	            	
	            }
		      
		        

	}
	    //gets the mean or median for each column of videos
	    public double[] extractMean(Matrix m, boolean weight){
	    	
	           MatrixOperation mat = new MatrixOperation(m,lg); 
	           double[][] mn = new double[1][m.getColumnDimension()]; 
	           if(media)
	   		     mn = mat.getMeanFrm();
	           else
	        	 mn = mat.getMedianFrm();
	           //using weight with Student test and take the log
	           if(weight){
	        	   double cnt[]= percentageFrms(m);
	        	   double tot =0.0d; 
	        	   //adding up all weights
	        	   for(int n =0; n <cnt.length; ++n)
	        		   tot+= cnt[n]; 
	        	   //weight is only available with StudentTest, so takes log
	        	   for(int n =0; n <mn[0].length; ++n){
	        		   mn[0][n] = Math.log10(mn[0][n]+1);
	        		   if(mn[0][n] <= 1.e-5) mn[0][n] = 0;
	        		   mLog.info("correction: "+cnt[n]*cnt.length/tot);
	        	   }
	           }else{
	        	   for(int n =0; n <mn[0].length; ++n){
	        		   mn[0][n] = mn[0][n]+0.5;
	        	   }
	           }
	   		   double [] smn = new double[mn[0].length]; 
	   		   for(int n =0; n <mn[0].length; ++n){
	   			   smn[n] = mn[0][n];
	   		   }
	   		   return smn;
	   		   } 
	   	
	    
	    /**Get the color value vector of an RGB matrix
		  * find how many frames have entries different from 0
		  * Returns the relative counts of frames with values > 0 in the 
		  * specific color-value.  
		  * The relative counts of frames 
		  * (divided by total # frames) for color value,  
		  * that have non-zero pixels counts.  
		 */
		 public double[] percentageFrms(Matrix m){
			 //total number of frames
			 double arr[][] =m.getArrayCopy(); 
			 int R= m.getRowDimension();
	         double counts[]=new double[m.getColumnDimension()]; 
		      
		     for(int c=0; c < m.getColumnDimension(); ++c) { 
		    	 counts[c] = 0.0;
		     for(int n=0; n < m.getRowDimension(); ++n){
		    	 
		    	 if(arr[n][c]>0)
		            	counts[c]+= 1.0;//frames with non-zero color-value
		     } 
		     counts[c] = counts[c]/(R+1.e-8);
		     }
		     
		     return counts;
		     }
       /**
        * Given the list with the videos that contain for each video 
        * the overall mean values of frames color components (0-255).
        * It collects them into an array of matrices of one column
        * Each matrix is one video with as many rows as color values (0-255)
        * Color values for each video are averaged overall frames.   
        */
	    
		public static Matrix [] studentMats(List<double[]> lst){
	        int sz = lst.size();
	        
			double[][][] arr= new double[sz][colorno][1];
			Matrix [] mat = new Matrix[sz]; 
			int cnt=0;
			for(double[] vmn : lst){
				for(int n=0; n < vmn.length; ++n)
					arr[cnt][n][0] = vmn[n]; 
			   mat[cnt] = new Matrix(arr[cnt]);
			   cnt++; 
			}
			return mat;
					
		}
		
		
		public void allFrmTest( MatrixRetrieve vstore ){
		    List<Matrix> lstG = vstore.getGreenvid();
		    matG = lstG.toArray(new Matrix[lstG.size()]);
		    
		    List<Matrix> lstR = vstore.getRedvid(); 
		    matR = lstR.toArray(new Matrix[lstR.size()]); 
		    List<Matrix> lstB =  vstore.getBluevid(); 
		    matB = lstB.toArray(new Matrix[lstB.size()]);
		}	
		/**
		 * 
		 * @param basearr double bi-dimensional array 
		 * @param len integer for the length of first index, row
		 * @param nobins integer for the length second index columns
		 * @return double bi-dimensional array of len x nobins
		 */	
		public static double[][] groupbins(double[][]basearr,int len, int nobins){
			 double [][] basebin = new double[basearr.length][nobins];
			 int sz = 256/nobins;
			 int cnt =0;
			 
			 if(sz >1 && basearr[0].length > 1){
			 for(int n=0; n < len;++n){
				cnt=0;
				 for(int c=0; c < 256;++c){
					 if(c <=0){
						basebin[n][cnt]=0;
					 }else if(c > 0 && c%sz <= 0) {
						cnt++;
						basebin[n][cnt]= basearr[n][c]; 
					 }else{		 
						 basebin[n][cnt] += basearr[n][c];
					 }
				 }
			 }
			 return basebin;
			 }
			 if(sz >1){
				 cnt=0;
				 for(int c=0; c < 256;++c){
					 if(c <=0){
						basebin[cnt][0]=0;
					 }else if(c > 0 && c%sz <= 0) {
						cnt++;
						basebin[cnt][0]= basearr[c][0]; 
					 }else{		 
						 basebin[cnt][0] += basearr[c][0];
					 }
				 }
				 return basebin; 
			 }
				 basebin = basearr;
			 
			
			 
			 return basebin;
			 }
	
		 public static void main(String[] args) throws SQLException{
			 
			 MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt",true, true); 
			 if(args.length > 1) vstore.retreiveMat(args);
			 int cnt = args.length;
	         if (args.length == 1){ //reading from file
	        	 args[0].trim(); 
	        	 List<String> thevideos = VideoFile.readFile(args[0]);
	        	 vstore.thevideos = thevideos; 
	        	 String [] argsf = thevideos.toArray(new String[thevideos.size()]); 
	        	 
	        	 vstore.retreiveMat(argsf);
	        	 cnt = argsf.length; 
	         }
	         
	        	Matrix [] mats = studentMats(vstore.meangreen); 
	        	
	        	vstore.allFrmTest(vstore);  
	        	Matrix [] matG = vstore.matG;
	        	new SVDMat(matG[0]);
	        	Matrix [] matR = vstore.matR;
	        	Matrix [] matB = vstore.matB;
	        	
	         }
	 }
	


