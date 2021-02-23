/**
 * File		:	MatrixOperation.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Statistical analysis of videos.
 *              Standardize the matrices RGB.
 *              Calculates mean, median and standard deviations 
 *              over all frames (i.e. length = 256) for every 
 *              color value (0-255).  Find  singular values.
 *              Find indexes along color values for max and min variance
 *              Obtains statistical frames for all color values whose var > 0,
 *              such that their color counts are the median of 
 *              the color component. Stores them in a HashMap.    
 *                                       
 */
package jalgo;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Jama.Matrix;
import cern.colt.list.DoubleArrayList;

public class MatrixOperation {
/**
 * Matrix of frames dim=[nofrms][256]
 */
	Matrix mat;
 /**
  * Matrix of standardize frames along columns dim= [nofrms][256]	
  */	
	Matrix matnorm; 
	/**
	 * Median of all frames dim=[1][256] along columns
	 */
	double [][] medianFrm = new double[1][]; 
	/**
	 * Mean of all frames dim=[1][256] along columns
	 */
	double [][] meanFrm = new double[1][]; 
	/**
	 * Standard deviation frames dim=[1][256] along cols of mat
	 */
	double [][] sdFrm = new double[1][]; 
	/**
	 * Variance of frames dim=[1][256] along cols of mat 
	 */
	double [][]varFrm = new double[1][]; 
	/**
	 * Color index with max sd or variance, 
	 * along columns of mat: vals=0-255
	 */
	int indMxSD; 
	/**
	 * Color index of min sd > 0 
	 * (minimum var such that variance > 0), 
	 * along columns of mat: vals =0-255
	 */
	int indMinSD; 
	/**
	 * row index for frame closest to the mean 
	 * with color value of the max variance column of mat
	 */
	int indKey; 
	/**
	 * Collection of row indeces (keys) and corresponding frames (values) 
	 * that are closer to the means of each color value (column) of video mat.  
	 * We store distinct key frames.   
	 */
	HashMap<Integer, double[]>  keyFrmsMap = new HashMap<Integer, double[]>();
	/**
	 * Matrix with all key frames contained in the Map;
	 * rows are frames and columns (0-255) are 
	 * color components or color values. 
	 * The # columns in the matrix is the number of distinct key frames.   
	 */
	Matrix keyFrmsMat; 
	private static final double LOW=0.25;
	private static final double UPPER=0.25;
	private boolean lg = false; //to implement the log of values
	private static Logger mLog = 
        Logger.getLogger(MatrixOperation.class.getName());
	private boolean debug = false;
	
	public Matrix getMatStandard(){
		return matnorm; 
	}
	public double[][] getMeanFrm(){
		return meanFrm; 
	}
	public double[][] getMedianFrm(){
		return medianFrm; 
	}
	public double[][] getSdFrm(){
		return sdFrm; 
	}
	public double[][] getVarFrm(){
		return varFrm; 
	}
	public int getIndMxSD(){
		return indMxSD;
	}
	public int getIndMinSD(){
		return indMinSD;
	}
	
	public int getIndKey(){
		return indKey; 
	}
	public HashMap<Integer, double[]> getKeyFrmsMap()
	{
		 return keyFrmsMap; 
	}
	public MatrixOperation(){
		if(!debug)
		mLog.setLevel(Level.WARNING);
	}
	public MatrixOperation( Matrix m, boolean lg){
		/**constructor invoking all statistics 
		*  calculation for matrix of videos m and one 
		*  color RGB. 
		*/ 
	this();
     mat =	m;
     this.lg = lg; 
     int rw = mat.getRowDimension();
     int cl = mat.getColumnDimension(); 
     matnorm= new Matrix(rw, cl); 
     this.matnorm = standardize(); 
     indMxSD = calcMaxVar(sdFrm[0]); 
     indMinSD = calcMinVar(sdFrm[0], sdFrm[0][indMxSD], indMxSD); 
     this.indKey = frmKey(indMxSD)[0];
     int [] indKeyfrms = allfrmKey(sdFrm[0]);

     
     mLog.info("Key Frame index of larger variance is " + indKey);
     
		
	 mLog.info("Color value max SD is = " 
	    		 + (int) Math.floor(sdFrm[0][indMxSD])  
	             + "; the min SD greater than 0 is = "+ 
	             (int) Math.floor(sdFrm[0][indMinSD]));

	   
	   //More std-out but no estimations   
	    keyFrameStdout(); 
	    findMeanSdVideo(); 
	     
	}
	
	
     public Matrix standardize()
     {
    	 /** Matrix of frames mat with columns color values 0-255
    	  * Calculate mean of all frames and sd and variance
    	  * standardize mat along the columns or color values. 
    	  *  */
     int nrow = mat.getRowDimension(); 
     int ncol = mat.getColumnDimension();
     meanFrm  = new double[1][ncol];
     sdFrm    = new double[1][ncol]; 
     varFrm   = new double[1][ncol];
     medianFrm  = new double[1][ncol];
     double [][] arr = mat.getArrayCopy();
     
	
	
     for (int nc =0; nc < ncol; ++nc){
    	 double [] cov = new double[nrow]; //each column or color value for mat
    	 double sortcov [] = new double[nrow];
    	 for(int nr=0; nr < nrow; ++nr){
      	   cov[nr] = arr[nr][nc];
      	   sortcov[nr] = arr[nr][nc];
    	 }
    	 if(lg)
    	 {
    		 for(int nr=0; nr < nrow; ++nr){
    			   cov[nr]+=1.e-10; 
    	      	   cov[nr] = Math.log10(cov[nr]);
    	      	   sortcov[nr] = cov[nr];
    	    	 } 
    	 }
    	 Arrays.sort(sortcov); 
    	 DoubleArrayList dcov = new DoubleArrayList(cov);
    	 DoubleArrayList sortdcov = new DoubleArrayList(sortcov);
    	 double actmn = mean(cov); 
         mLog.info("Actual Mean is : "+ actmn);
    	 double mn = cern.jet.stat.Descriptive.mean(dcov);
    	 mLog.info("Mean for color : "+ nc + " is = " + mn);
    	 double var = cern.jet.stat.Descriptive.sampleVariance(dcov, mn);
    	 double sd = cern.jet.stat.Descriptive.sampleStandardDeviation(nrow,var);  
    	 double mediana = cern.jet.stat.Descriptive.median(sortdcov);
    	 
    
    	 for(int nr=0; nr < nrow; ++nr){
    		 cov[nr] = (cov[nr] -mn )/(sd + 1.e-10); 
    	     arr[nr][nc] = cov[nr];
    	     
    	 }
    	 
    	 meanFrm[0][nc] = mn; //for each color value with nc=0-255
    	 sdFrm[0][nc] = sd; 
    	 varFrm[0][nc] = var; 
    	 medianFrm[0][nc]= mediana; 
     }
     Matrix norm = new Matrix(arr); 
     return norm;
     }
    
     public double mean(double[] arr){
    	 /**calculate the mean frame*/
    	 double mn = 0; 
    	 for(int n=0; n < arr.length; ++n)
    		 mn = mn + arr[n]; 
    	 mn = mn /(arr.length + 1.e-10); 
    	 return mn;
     }
     
     /** Using standard deviations of color values, length=256; 
      * calculate the maximum SD and the column index 
      */
     public int calcMaxVar(double[]var){
    	 double mx = 0.0;
    	 int indMx = 0; 
    	for(int n=0; n < var.length; ++n){
    		if(var[n] > mx){
    			mx = var[n]; 
    			indMx = n;
    		}
    	}
    	 return indMx; 
     }
     /** Using variance vector of color values, length=256; 
      * calculate the minimum variance and the column index 
      */
     public int calcMinVar(double[]var, double mx, int ix){
    	 double minx = mx;
    	 int indMin = ix; 
    	for(int n=0; n < var.length; ++n){
    		if(var[n] < minx && var[n] >= 1){
    			minx = var[n]; 
    			indMin = n;
    		}
    	}
    	 return indMin; 
     }
     public int[] frmKey(int ind){
    	 
    	/** Given ind along a column of matrix mat with video frames
    	 * Get the column component of mat and obtained the median
    	 * Find the index row of the frame whose color value along ind
    	 * is the closest to the calculated median value along the column ind. 
    	 */ 
    	 double [][] arr = mat.getArrayCopy();
    	
    	  
    	 int nrow = mat.getRowDimension();
    	 double [] proj = new double[nrow]; 
    	 for(int nr=0; nr < nrow; ++nr)
    		 proj[nr] = arr[nr][ind]; //max var column 
    		 
    	 Arrays.sort(proj); 	      
		 DoubleArrayList dcov = new DoubleArrayList(proj);
		
		 double medianCov= cern.jet.stat.Descriptive.median(dcov);
		 int indFrm=closeFrm(proj,medianCov,nrow); 
		 double meanCov = cern.jet.stat.Descriptive.mean(dcov);
		 int indMn = closeFrm(proj,meanCov,nrow); 
		 double q25 = cern.jet.stat.Descriptive.quantile(dcov,LOW);
		 int ind25 = closeFrm(proj,q25,nrow); 
		 double q75 = cern.jet.stat.Descriptive.quantile(dcov,UPPER);
		 int ind75 = closeFrm(proj,q25,nrow); 
		 double mxCov = cern.jet.stat.Descriptive.max(dcov);
		 double minCov = cern.jet.stat.Descriptive.min(dcov);
	     int indmax = closeFrm(proj,minCov,nrow); 
	     int indmin  = closeFrm(proj,mxCov,nrow); 
            int[] ret ={indFrm, indMn, ind25, ind75, indmax, indmin}; 
				return ret; //closest to median
     }
     //given a statistics measure, what, find the entry of array proj closest value
     private int closeFrm(double[] proj, double what, int nrow){
		 int indFrm = 0; 
		 double tocomp = Math.abs(proj[0]- what);
		 for(int n= 1; n < nrow; ++n)
               if(Math.abs(proj[n]- what) < tocomp){
               indFrm = n;
               tocomp = Math.abs(proj[n]- what);
               }
		 return indFrm;
	 }
     
     public int[] allfrmKey(double var[]){
    	/**
    	 * array of frames whose color values along any of the columns
    	 * of the matrix of frames, best approximate the median of the 
    	 * component for variances > 0; var[] vector of variances
    	 */
    	 int medidas = 6;
    	 double [][] arr = mat.getArrayCopy();
    	 int totFrm = 0;
    	 double [] sum = new double[mat.getColumnDimension()];
    	 for (int k=0; k < mat.getColumnDimension(); ++k)
    	 {
    	 	for(int m=0; m < mat.getRowDimension(); ++m)
    	 		sum[k]+= arr[m][k];
    		if(sum[k] > 0.5)
    		   totFrm = totFrm + 1;
    	 } 
    			 
    	 int keys [] = new int[medidas*totFrm]; //only if var > 0 
    	 int cnt=0; 
    	 int keyholder = 0; 
    	 int count =0; 
    	 double[][] dummy = new double[medidas*totFrm][mat.getColumnDimension()]; 
    	 for(int n = 0; n < mat.getColumnDimension(); ++n){
    	  int[]dos=new int[medidas];
         
    	 if(sum[n] <= 0.5)
    	  continue;
    	  dos= frmKey(n);         
        //frame index entry closest to median
    	  for(int m=0; m <dos.length; ++m){
          keyholder = dos[m];   
          mLog.info(keyholder + ""); 
          if(keyFrmsMap.containsKey(keyholder)==false)
        	  keyFrmsMap.put(keyholder, arr[keyholder]); 
           dummy[cnt] = arr[keyholder]; 
           keys[cnt] = keyholder; //median
           cnt++;    
    	  }
    	 } 
    	 Matrix keyFrmsMat = new Matrix(dummy);
    	 this.keyFrmsMat = keyFrmsMat; 
    	 
    	 return keys; 
    	  
     }
     
    
     public void keyFrameStdout()
     {
    	 /**
    	  * It iterates the HashMap keyFrmsMap and finds the pair key, values 
    	  * and output them to stdout. Keys correspond to row indeces 
    	  * of the video matrix, mat. Values are the frames corresponding
    	  * to the key values.     
    	  */
    	 
    	 int n=0; 
    	 if(keyFrmsMap.isEmpty()){
    		 mLog.warning("No key frames found");
    		 return;
    	 }
    	 String res = "Size of Hashmap is " + keyFrmsMap.size(); 
	     mLog.info(res);
	     int ln = keyFrmsMap.size(); 
    	 Collection<double[]> frms= keyFrmsMap.values(); 
    	 Set<Integer> keys = keyFrmsMap.keySet(); 
    	 Integer[] rowInd = (Integer[]) keys.toArray(new Integer[keys.size()]);
    	 mLog.info("Frame closest to mean for each color value\n" 
    			            + "Row Index: "); 
    	 String numbers= "\t"; 
    	 if(debug){
    	 for(n=0; n<keys.size(); ++n )
    		 numbers= numbers + (int) rowInd[n]+ "\t"; 
    	 mLog.info(numbers);
    	 }
         String frmVal = ""; 
    	 for(double[] frm: frms){
    		 for(n=0; n < frm.length; ++n)
    			 frmVal = frmVal + "\t" + frm[n];
    		 frmVal = frmVal + "\n"; 
    	 }
    	mLog.info("Frames: \n"+ frmVal); 
     }
     
     
     
     public void  findMeanSdVideo()
 	{
 		/** print to stdout the means and standard 
 		 * deviations of all columns for the matrix mat
 		 * of video frames   
 		 */
 			double [][] mnFrmR = new double[1][];
 			double [][] sdFrmR = new double[1][]; 
 			double[][] varFrmR = new double[1][]; 
 			mnFrmR = this.getMeanFrm(); 
 			sdFrmR = this.getSdFrm();
 			varFrmR = this.getVarFrm(); 
 			if(debug){
 		     mLog.info("  Matrix"); 
 			new Matrix(mnFrmR).print(1, 0); 
 			new Matrix(sdFrmR).print(1, 0);
 			new Matrix(varFrmR).print(1, 0);
 			}
 			
 	}
    	 
}
  


