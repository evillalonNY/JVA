/**
 * File		:	Video.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Contains for a single video the metadata to store 
 *              in database. Matrices of RGB color components 
 *              with counts of pixels. The description of video format; 
 *              the number of frames that equal the number of rows of 
 *              the matrices. Time length of stream. 
 *              The primary key of the video is the url and is stored  
 *              as an instance of static internal class VideoPK 
 *               
 * Uses:        VideoPK             
 */        
package jVideos;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import Jama.Matrix;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class Video implements  Serializable{
	
	static final long serialVersionUID = 42L;
    private Matrix redMat;
    private Matrix greenMat;
    private Matrix blueMat;
    private SparseDoubleMatrix2D redMatCOLT=null;
    private SparseDoubleMatrix2D greenMatCOLT=null;
    private SparseDoubleMatrix2D blueMatCOLT=null;
    private String formatDesc=""; 
    private long numFrm = 0;
    private float timeStream = 0.0f;
    private Video.VideoPK objKey; 
    private static Logger mLog = 
	    Logger.getLogger(Video.class.getName());
	private boolean debug = false;
   //getter and setter 
    public Matrix getRedMat(){
    	return redMat; 
    }
    public void setRedMat(Matrix mred){
    	redMat = mred; 
    }
    public Matrix getGreenMat(){
    	return greenMat; 
    }
    public void setGreenMat(Matrix mgreen){
    	 greenMat = mgreen; 
    }
    public Matrix getBlueMat(){
    	return blueMat; 
    }
    public void setBlueMat(Matrix mblue){
    	 blueMat = mblue; 
    }
    public SparseDoubleMatrix2D  getRedMatCOLT(){
    	return redMatCOLT; 
    }
    public void setRedMatCOLT(SparseDoubleMatrix2D  mred){
    	redMatCOLT = mred; 
    }
    public SparseDoubleMatrix2D  getGreenMatCOLT(){
    	return greenMatCOLT; 
    }
    public void setGreenMatCOLT(SparseDoubleMatrix2D  mgreen){
    	 greenMatCOLT = mgreen; 
    }
    public SparseDoubleMatrix2D  getBlueMatCOLT(){
    	return blueMatCOLT; 
    }
    public void setBlueMat(SparseDoubleMatrix2D  mblue){
    	 blueMatCOLT = mblue; 
    }
   
    public String getFormatDesc(){
    	return formatDesc; 
    }
    public void setFormatDesc(String fm){
    	formatDesc = fm; 
    }
    public long getNumFrm(){
    	return numFrm; 
    }
    public void setNumFrm(long nofm){
    	numFrm = nofm; 
    }
    public float getTimeStream(){
    	return timeStream; 
    }
    public void setTimeStream(float st){
    	timeStream = st; 
    }
    //constructors
    private void debug(){
    	if(!debug)
    		mLog.setLevel(Level.WARNING);
    }
    public Video(){
    	super(); 
    	debug();
    	redMat = null;
    	greenMat = null;
    	blueMat = null;
    	formatDesc = "MPGE";
    	numFrm = 0;
    	timeStream = 0.0f;
    	
    }
    public Video(String fdesc, long nofrm, float dur){
    	debug();
    	formatDesc = fdesc;
    	numFrm = nofrm;
    	timeStream = dur; 
    }
    public Video(Matrix r, Matrix g, Matrix b){
    	debug();
    	redMat = r;
		greenMat = g;
		blueMat = b;	
		redMatCOLT = new SparseDoubleMatrix2D(redMat.getArrayCopy());
		greenMatCOLT = new SparseDoubleMatrix2D(greenMat.getArrayCopy());
		blueMatCOLT = new SparseDoubleMatrix2D(blueMat.getArrayCopy());
    }
	public Video(Matrix r, Matrix g, Matrix b, 
			String d, long nf, float hd){
		this(d, nf, hd); 
		
		redMat = r;
		greenMat = g;
		blueMat = b;
		redMatCOLT = new SparseDoubleMatrix2D(redMat.getArrayCopy());
		greenMatCOLT = new SparseDoubleMatrix2D(greenMat.getArrayCopy());
		blueMatCOLT = new SparseDoubleMatrix2D(blueMat.getArrayCopy());
		
	}
	public Video(SparseDoubleMatrix2D rCOLT, SparseDoubleMatrix2D gCOLT, 
			SparseDoubleMatrix2D bCOLT, 
			String d, long nf, float hd){
		this(d, nf, hd); 
		
		redMatCOLT = rCOLT;
		greenMatCOLT = gCOLT;
		blueMatCOLT = bCOLT;
	    redMat = new Matrix(redMatCOLT.toArray());
	    greenMat = new Matrix(greenMatCOLT.toArray());
	    blueMat = new Matrix(blueMatCOLT.toArray());
		
	} 
	public Video.VideoPK getVideoPK(){
		return objKey; 
	}
	public void setPrimaryKey(String key){
		objKey = new Video.VideoPK(key);
		mLog.info("Video obj with PK... "+ objKey.getPrimaryKey());
	}
	//primary key class
	public static class VideoPK implements Serializable{
		static final long serialVersionUID = 42L;
		public String mName;
		public VideoPK(){
			mName = null;
		}
		public VideoPK(String spk){
			mName = spk; 
		}
		
		public boolean equals(Object other){
		if(other instanceof VideoPK && 
				this.mName.equals(((VideoPK)other).mName)){
			return true;
		}else
			return false;
				
		}
		public int hashCode(){
			return mName.hashCode(); 
		}
		public String getPrimaryKey(){
			return mName; 
		}
	}
	
	public String toString(){
		String res = "";
		Matrix blue =this.blueMat;
		Matrix red =this.redMat;
		Matrix green =this.greenMat;
		Video.VideoPK ckey = this.getVideoPK(); 
		String key = ckey.getPrimaryKey();
		String fdesc = this.formatDesc;
		long nfrm = this.numFrm;
		float stream = this.timeStream; 
		res = "Video url = " + key;  
		res = "\nDescription: " + fdesc; 
	    res = res + "\nNumber of frames = "+ nfrm;
	    res = res + "\nDuration of stream = " + stream;
	    res = res + "\nBlue: # rows = " + blue.getRowDimension()+
    	  	  " # cols = " + blue.getColumnDimension(); 
	    res = res + "\nRed: # rows = " + red.getRowDimension()+
	  	  " # cols = " + red.getColumnDimension();
	    res = res + "\nGreen: # rows = " + green.getRowDimension()+
	  	  " # cols = " + red.getColumnDimension();
	    if(debug){
         red.print(1, 0); //print the three matrices
	     blue.print(1, 0);
	     green.print(1, 0);
	    }
		return res; 
		
	}
}
