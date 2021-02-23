/**
 * File		:	ColorValStat.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Takes one matrix of colors for video 
 *              Calculates for each column the median,
 *              and the two quartiles corresponding 
 *              to distribution percentages of 
 *              (0.25, 0.5, 0.75).  
 *              Stores statistics in arrays of length   
 *              the number of color values (0-255). 
 *              
 * Uses      :  ColorIndStat.java     
 */
package jalgo;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import Jama.Matrix;

public class ColorValStat {
	/**
	 * Color matrix for video; dim = #frms X #color components (0-255)
	 * Each of Red, Green, Blue is a different matrix
	 */
	Matrix R; 
	
	char color; 
	boolean sort = false;
	private final static double XERR = 0; 
	private String summary="";  //summary of statistics
	/**
	 * the median for every column of mat; dim = 256
	 */
	double[] yVals2D;
	private static Logger mLog = 
	        Logger.getLogger(ColorValStat.class.getName());
	private boolean debug = false;
	 
	public double[] getYVals2D(){
		 return yVals2D; 
	 }
	/**
	 * first quartiles at percentage 0.75, 
	 * one for each column of mat
	 */
	double[] yErrP2D;
	 public double[] getYErrP2D(){
		 return yErrP2D; 
	 }
	/**
	 * second quartile at 0.25 for each column of mat
	 */
	double[] yErrM2D;
	 public double[] getYErrM2D(){
		 return yErrM2D; 
	 }
	/**
	 * xvalues or color values from 0-255
	 */
	double[] xVals2D;
	 public double[] getXVals2D(){
		 return xVals2D; 
	 }
	/**
	 * error set at 0.25
	 */
	double[] xErrP2D; 
	 public double[] getXErrP2D(){
		 return xErrP2D; 
	 }
	
	public String getSummary(){
		return summary; 
	}
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean dbg){
		debug= dbg; 
	}
	public ColorValStat(Matrix R){
		if(!debug)
			mLog.setLevel(Level.WARNING);
		this.R = R;
		
		int ncl = R.getColumnDimension();
		//one for each column of matrix R
		yVals2D = new double[ncl];
		yErrP2D = new double[ncl];
		yErrM2D = new double[ncl];
		xVals2D = new double[ncl];
		for(int n=0; n <ncl; ++n)
			xVals2D[n] = n; 
		xErrP2D= new double[ncl];
		for(int n=0; n <ncl; ++n)
			xErrP2D[n] = XERR; 
	}
public ColorValStat(Matrix R, char color){
	this(R);
	this.color = color;
	summary = buildDataSets();	
		
}
public String buildDataSets(){
	int n;
	int ncl = R.getColumnDimension();
	String str=""; 
  
    double [][] Rarray = buildColorArrays(R);
    for(n=0; n < ncl; ++n){
    	ColorIndStat statR = new ColorIndStat(Rarray[n], (double) n,color);
    	str+= statR.calculateStat(); 
    	double med = statR.medianCov;
    	double[] quarts = statR.quantiles;
    	yVals2D[n] = med;
        yErrM2D[n] = Math.abs(quarts[0]-med);
        yErrP2D[n] = Math.abs(quarts[1]-med);
      
        mLog.info("med "+ med + "quarts0.25= "+ yErrM2D[n]+
        		"quarts0.75= "+ yErrP2D[n]);
    
        
    }
    mLog.info("ColorValStat: " +str); 
    return str; 
}
public double[][] buildColorArrays(Matrix mat){
	
//	video to test
	int ncl = mat.getColumnDimension();
	int nrw = mat.getRowDimension();
	
	double colorValTest[][] = new double[ncl][nrw]; 
	int cl, rw; 
		double [][] arr = mat.getArrayCopy();
		int tcl = mat.getColumnDimension();
		int trw = mat.getRowDimension();
		
		for(cl=0; cl < tcl; ++cl){
			for(rw=0; rw< trw; ++rw)
			colorValTest[cl][rw]= arr[rw][cl];
			if(sort)
				Arrays.sort(colorValTest[cl]); 
			}
		//up to here common with StudentTest
		return colorValTest; 
}
			
	
	}

