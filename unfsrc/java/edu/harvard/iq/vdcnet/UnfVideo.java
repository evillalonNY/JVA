/**
 * Description: Calculates the UNF for the three RGB matrices
 *              for the videos in the Mckoi database. 
 *              It obtains the UNF for every row
 *              or frame of each of the three RGB matrices and, then,
 *              it combines the UNF's of all the frames for 
 *              every color component into a single UNF.
 *              Thus every video has three UNF's for the three colors RGB. 
 *              By default it uses SHA-256 digest algorithm.  
 *                           
 *   @author evillalon : Elena Villalon          
 *              
 */
package edu.harvard.iq.vdcnet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import jmckoi.MatrixRetrieve;
import Jama.Matrix;

public class UnfVideo implements UnfCons{
	/**List elements are video matrices with the
	 * arrays for the red, green, blue color pixel counts.
	 * Array rows (1st index) are frames and 
	 * columns (2nd index) are color values 0-255 
	 */
	List<double [][]> red = new ArrayList<double [][]>();
	List<double [][]> blue = new ArrayList<double [][]>();
	List<double [][]>  green= new ArrayList<double [][]>();
    /** number of digits with decimal point*/
    private int digits = DEF_NDGTS; 
    /** indexes for RGB*/
    private static final int RED=0;
    private static final int GREEN =1;
    private static final int BLUE = 2;
    /** the unf's of the three matrices and each video
     * first index (row) is the video index
     * second index (column) takes three values for 
     * the RED, GREEN, BLUE color components
     * 
     * */
    String[] [] unfs =null;
    /**
     * array with the primary key of the videos 
     */
    String [] vid=null;
    /**
     * version of unf to use
     */
    private float vers=4.1f; 
    
    private static Logger mLog = Logger.getLogger(UnfVideo.class.getName());
    /**
     * 
     * @return two-dimensional array with the unf's 
     * for each video and each color RGB  
     */
    public String[][] getUnfs(){
    	return unfs;
    }
    /**
     * 
     * @return integer with digits for 
     * unf mantissa calculations. 
     */
    public int getDigits(){
    	return digits;
    }
    /**
     * 
     * @param d integer with digits for unf 
     * mantissa calculations.
     */
    public void setDigits(int d){
    	digits=d;
    }
    
    /**
     * 
     * @param thevideo String array with pk of videos
     */
    public UnfVideo(String[ ]thevideo){
    	vid = thevideo;
    //	calcUNF(vid);
    }
    public UnfVideo(String[ ]thevideo, float v){
    	vid = thevideo;
    	vers = v;
   // 	calcUNF();
    }
    /**
     * 
     * @param thevideo String with primary key of video
     */
    public UnfVideo(String thevideo){
    	vid = new String[1];
    	vid[0] = thevideo;
    //	calcUNF();
    	
    }
    public UnfVideo(String thevideo, float v){
    	vid = new String[1];
    	vid[0] = thevideo;
    	vers= v; 
    //	calcUNF();
    	
    }
    public void calcUNF(String thevideo){
    	vid = new String[1];
    	vid[0] = thevideo;
    	calcUNF();
    }
    /**
     * 
     * @param thevideo String array with pk of videos 
     */
    public void calcUNF(){
    	String [] thevideo = vid; 
    	int[][] dim = videoMat(thevideo);
    	//one unf for each color RGB
    	unfs = new String[vid.length][3];
    	for(int n=0; n <vid.length; ++n){
    		double[][] r = red.get(n);
    		double[][] g = green.get(n);
    		double[][] b = blue.get(n);
    		int nrw = dim[n][0];
    		int ncl = dim[n][1];
    	
    	try{
    	unfs[n][RED] = calcunf(r,nrw,ncl);
    	unfs[n][GREEN] = calcunf(g,nrw,ncl);
    	unfs[n][BLUE] = calcunf(b,nrw,ncl);
    	unfs[n][RED] = fixunf(unfs[n][RED],":RED:");
    	unfs[n][GREEN] = fixunf(unfs[n][GREEN],":GREEN:");
    	unfs[n][BLUE] = fixunf(unfs[n][BLUE],":BLUE:");	
    	
    	}catch(Exception err){
    		mLog.severe("UNF calculation fail");
    		err.printStackTrace();
    		unfs[n][RED] = "";
        	unfs[n][GREEN] = "";
        	unfs[n][BLUE] = "";
    	}
    	}
    }
    /**
     * 
     * @param vid String array with videos pk
     * @return bi-dimensional array of int with 
     * nrows and ncols of RGB matrix for each video 
     */
	public  int[][]videoMat(String []vid){
		MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt");
		vstore.retreiveMat(vid);
	    List<Matrix> bluelst = vstore.getBluevid();
	    List<Matrix> greenlst = vstore.getGreenvid();
	    List<Matrix> redlst = vstore.getRedvid();
	    Iterator<Matrix> itblue = bluelst.iterator();
	    Iterator<Matrix> itgreen = greenlst.iterator();
	    Iterator<Matrix> itred = redlst.iterator();
	    Matrix[] bmat = getMatrix(itblue,bluelst.size());
	    Matrix[] rmat = getMatrix(itred,redlst.size());
	    Matrix[] gmat = getMatrix(itgreen,greenlst.size());
	    int [][] dim = new int[rmat.length][2];
	    int cnt=0;
	    for(Matrix m: rmat){
	    dim[cnt][0] = m.getRowDimension();
	    dim[cnt][1]= m.getColumnDimension();
	    cnt++;
	    double[][]r = m.getArrayCopy();
	    red.add(r);
	    }
	    for(Matrix m: gmat){
		    double[][]g = m.getArrayCopy();
		    green.add(g);
		}
	    for(Matrix m: bmat){
		    double[][]b = m.getArrayCopy();
		    blue.add(b);
		}
	    
     
        return dim;
	    
	}
	/**
	 * 
	 * @param it Iterator of Class Matrix for one color value
	 * @param sz int elements in iterator
	 * @return array of class Matrix 
	 */
	Matrix [] getMatrix(Iterator<Matrix> it, int sz){
		Matrix[] col = new Matrix[sz];
		int cnt=0;
		while(it.hasNext()){
			Matrix mat = it.next();
			col[cnt]= mat;
			cnt++;
		}
		return col;
	}
	/**
	 * 
	 * @param arr double array of 2 dimensions 
	 * for one color of each video  
	 * @param nrw  int length along 1st index
	 * @param ncl  int length along 2nd index
	 * @return String with unf
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws UnfException
	 */
	public String calcunf(double[][] arr,int nrw,int ncl)throws 
	   UnsupportedEncodingException, IOException, 
	   NoSuchAlgorithmException, UnfException{
		Double[][] obj = new Double[nrw][ncl];
		
		for(int rw=0; rw<nrw; ++rw)
			for(int cl=0; cl <ncl; ++cl)
				obj[rw][cl] = arr[rw][cl];
		
		Integer [] dg = {digits};
		//it should return an array with unf's for every frame
		String [] b64= UnfDigest.unf(obj, vers, dg);
		String combunf = UnfDigest.addUNFs(b64); 
		return combunf;	
	   
     }
	/**
	 * 
	 * @param col unf String with unf
	 * @param col  String with color 
	 * @return   String with unf modified
	 */
	public String fixunf(String colunf,String col){
    	String tosplit=  ":";
		 
		String res[] =  colunf.split(tosplit);
		String fin = res[0].concat(col+res[res.length-1]);
		
		return fin;
	}
	
	
}

