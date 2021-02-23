/**
 * File		:	StudentTestPaired.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Takes the video matrix to test mat. 
 *              Array of video matrices, videoMat, and corresponding url's.  
 *              Calculate the test of statistics for each column 
 *              (or color value) of mat, where dim(mat) = numFrames X 256.
 *              It pairs the examples for each color value of the two matrices
 *              and tests if the mean of differences 
 *              between pair of color values is equal to 0.  
 *              Stores in HashMap, PValuesVideo, the url's of videoMat   
 *              and the results of the t.test for color components 
 *              at significant level alpha. True means pvalue < alpha or different,  
 *              and false means pvalue > alpha or videos color value same means.   
 */
package jalgo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jmckoi.MatrixRetrieve;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TTestImpl;

import Jama.Matrix;

public class StudentTestPaired {
	/**
	 * array of video matrices, each matrix has dim=[nofrms][256]
	 */
		
	 Matrix [] videoMat;
	 
	 /** Video matrix to test against array videoMat, dim=[nofrms][256]
	 */
	 Matrix mat;
	 
	 /**
	  * Significant level of the two-sided test of statistics
	  */
	 static final double alpha = 0.1; 
	
	 /**
	  * static class obj returning three and two dimensional 
	  * arrays for videoMat and mat. 
	  */
	 StudentTestPaired.BuildColorArray obj; 
	 /**
	  * For each video matrix in videoMat, means values 
	  * along columns or color values,; dim=[novideos][256] 
	  */
	 
	 double [][] meanVideoCol;
	 /**
	  * For each video matrix in videoMat, median values 
	  * along columns or color values,; dim=[novideos][256] 
	  */
	 double[][] medianVideoCol; 
	 
	 /**
	  * For each video matrix in videoMat, sd values 
	  * along columns or color values; dim=[novideos][256] 
	  */
	 double [][] sdVideoCol;
	 
	 
	 /**
	  * p.values for every column of mat match with corresponding 
	  * columns of matrices videoMat.  The probability values are 
	  * obtained using the student distribution; 
	  * dim=[#videosToTest][256]
	  * 
	  */
	 double[][] pvalues; 
	 
	 /**
	  * true (equality of videos) or false depending on 
	  * pvalues < alpha or > alpha;  
	  * The first index is along videoMat,
	  * second are the columns along any video matrix
	  */
	 
	 private static boolean signfLevelRed[][];
	 private static boolean signfLevelGreen[][];
	 private static boolean signfLevelBlue[][];
	 
	 List<String> thevideos = new ArrayList<String>(); 
	 static int totest = 0;
	 private static String testvid=""; 
	 private boolean showTest= true;
	 private boolean weight =true; 
	 /**the number of bins in the histogram  */
	 private static int nobins = 256;
	 private static Logger mLog = 
	     Logger.getLogger(StudentTest.class.getName());
	 private boolean debug = false;
	 
	 public static double getAlpha(){
		 return alpha; 
	 }
	 //it will exclude the video to test or index totest
	 public List<String> getThevideos(){
		 return thevideos; 
	 }
	 public void setThevideos(List<String> vt){
		 thevideos=vt;
	 }
	 public String getTestvid(){
		 return testvid;
	 }
	 public void setTestvid(String vt){
		 testvid= vt; 
	 }
	 
	 public static boolean [][] getSignfLevelRed(){
		 return signfLevelRed; 
	 }
	 
	 public void setSignfLevelRed(boolean[][] sig){
		 signfLevelRed = sig; 
	 }
	 public static boolean [][] getSignfLevelGreen(){
		 return signfLevelGreen; 
	 }
	 
	 public void setSignfLevelGreen(boolean[][] sig){
		 signfLevelGreen = sig; 
	 }
	 public static boolean [][] getSignfLevelBlue(){
		 return signfLevelBlue; 
	 }
	 
	 public void setSignfLevelBlue(boolean[][] sig){
		 signfLevelBlue = sig; 
	 }
	 public static void setNobins(int no){
		 if(no>= 256 || no <= 0) nobins=256;
		 nobins = no; 
	 }
	 public static int getNobins(){
		 return nobins;
	 }
	 public StudentTestPaired(){
		 
	 }
	 public StudentTestPaired(List<String> thevid){
		 if(!debug)
			 mLog.setLevel(Level.WARNING);
		 thevideos= thevid;  
	 }
	 public StudentTestPaired(boolean w){
		 if(!debug)
			 mLog.setLevel(Level.WARNING);
		 weight =w; 
	 }
	 public StudentTestPaired(List<String> thevid, int n, boolean w){
		 this(thevid); 
		 totest =n; 
		 weight = w; 
	 }
	 public StudentTestPaired(Matrix [] v, Matrix m, List<String> thevid){
		 this(thevid); 
		 videoMat =v;
		 mat = m; 
		 
		  
	 }
	 public StringBuffer[] meanDifTest(MatrixRetrieve vstore, boolean lg, 
				int totest) throws MathException{
			String testv = thevideos.get(totest);
			if(!showTest)
			  thevideos.remove(totest);
			testvid = testv;
			StringBuffer res0 = new StringBuffer("Test video  "+ testv);
			StringBuffer[] res = new StringBuffer[3]; 
			String mess1 = "Testing video: " + testv;
			//JOptionPane.showMessageDialog(null, mess1, "Test videos",
				//	JOptionPane.INFORMATION_MESSAGE);
			
		    Matrix [] matG = MatrixRetrieve.studentMats(vstore.getMeangreen()); 
		    Matrix [] matR = MatrixRetrieve.studentMats(vstore.getMeanred());
		    Matrix [] matB = MatrixRetrieve.studentMats(vstore.getMeanblue());
		    //weight has already being taken the log in MatrixRetreive
		    if(lg & !weight){//apply the log
		    	matG = logMats(matG);
		    	matR =logMats(matR);
		    	matB = logMats(matB); 
		    }
		   
		    Matrix Rtest = matR[totest]; 
		    Matrix Gtest = matG[totest]; 
		    Matrix Btest = matB[totest];
		     
		    Matrix [] Rbase = matR; 
		    StudentTestPaired rtest =  new StudentTestPaired(Rbase,Rtest, thevideos);
		    res[0] = new StringBuffer("\n"+res0);
		    res[0].append(rtest.TPairedtest("RED"));
		    
		 	Matrix [] Gbase = matG;  
		 	StudentTestPaired gtest =  new StudentTestPaired(Gbase,Gtest, thevideos);
		 	res[1] = new StringBuffer("\n"+res0);
		 	res[1].append(gtest.TPairedtest("GREEN"));
		 	
		 	Matrix [] Bbase = matB; 
		 	StudentTestPaired btest =  new StudentTestPaired(Bbase,Btest, thevideos);
		 	res[2] = new StringBuffer("\n"+res0);  
		 	res[2].append( btest.TPairedtest("BLUE"));
		 	
		 	
		 	return res; 
		    }
	 public StringBuffer TPairedtest(String col) throws MathException {
			//all videos double arrays 
			int videoInd,cl; 
			obj = new StudentTestPaired.BuildColorArray(videoMat, mat);
			obj.buildColorArrays(); 
			int ncol=	videoMat[0].getColumnDimension();
			
			double n2 = mat.getRowDimension()+1.e-10;
			
			double[][][] colorValBase=(double[][][]) obj.colorValBase;
			double[][] colorValTest = obj.colorValTest;
			
			
			if(debug){
			new Matrix(obj.colorValBase[0]).print(1, 0); 
			new Matrix(colorValTest).print(1,0);
			}
		
			meanVideoCol = new double[videoMat.length][ncol];
			sdVideoCol= new double[videoMat.length][ncol];
			medianVideoCol = new double[videoMat.length][ncol];
			ColorIndStat statcol; 
			for(videoInd=0; videoInd < videoMat.length; videoInd++)
			
			for(cl= 0; cl < ncol; ++ cl)
			{
				//for each col or color value get statistics
				
				double [] comp = colorValBase[videoInd][cl];
				double [] dif = new double[comp.length];
				for(int k=0; k<comp.length; ++k)
					dif[k] = comp[k] - colorValTest[cl][k];
				statcol = new ColorIndStat(dif);
				statcol.calculateStat();
				
				meanVideoCol[videoInd][cl] = statcol.meanCov;
				medianVideoCol[videoInd][cl] = statcol.medianCov; 
				sdVideoCol[videoInd][cl] = Math.sqrt(statcol.variance);
				
				}
			double[][] pvalues = new double[videoMat.length][mat.getColumnDimension()];
			boolean [][]signfLevel = new boolean[videoMat.length][mat.getColumnDimension()];
			  
			StringBuffer res = new StringBuffer();
			res.append("\nTwo-sided paired t.test at significant level= "+
					StudentTestPaired.alpha);
			res.append("\nColor component "+ col + ": ");
			
			for(videoInd=0; videoInd < videoMat.length; videoInd++){
				double nr= videoMat[videoInd].getRowDimension()+1.e-10;
				String str = thevideos.get(videoInd);
				//it is simpler using Adobe software
				double pvaladobe = new TTestImpl().pairedTTest(colorValBase[videoInd][0],
						colorValTest[0]);  
				
				for(cl =0; cl < ncol; ++cl){
				double xbar = meanVideoCol[videoInd][cl];
				double poolsd = sdVideoCol[videoInd][cl]/Math.sqrt(nr);
				double tvalue = xbar/(poolsd+1.e-5); 
				double df = nr-1;
				double pval = cern.jet.stat.Probability.studentT(df, tvalue); 
				//tweo sided t-test
				if (tvalue > 0)
					pvalues[videoInd][cl]= 2*(1.0 - pval);
				else
					pvalues[videoInd][cl]=  2* pval;
				//check that the results agree with Adobe software
				mLog.info("pvaladobe= "+pvaladobe+"\tpvalColt= "+pvalues[videoInd][cl]); 
				
				res.append("\nVideo "+ str+ "\npvalue= " +pvalues[videoInd][cl]); 

				int len = videoMat.length;
				double alpha = new Double(StudentTestPaired.alpha); 
				
				signfLevel[videoInd][cl]= true; //Null hypothesis: same videos 
				
				if ( pvalues[videoInd][cl] <= alpha) {
					signfLevel[videoInd][cl] = false; //different videos 
				res.append("\t"+col+ "...Different videos.\n"); 
				}else 
					res.append("\t"+ col+"...Same videos.\n"); ; 
				}
			}
			
	     return res;
		}
	 public static Matrix[] logMats(Matrix[] matG){
			
			for(int k=0; k <matG.length; ++k){
				double arr [][] = matG[k].getArray();
			for(int c=0; c < matG[k].getColumnDimension(); ++c)
			for(int r=0; r < matG[k].getRowDimension(); ++r){
				arr[r][c] += 0.5;
				arr[r][c] =Math.log10(arr[r][c]); 
				if(arr[r][c]<=1.e-5) arr[r][c]= 0;
				}
			}
			return matG; 
		}
	 public static class BuildColorArray{
		 //all videos double arrays ]
		Matrix [] videoMat; 
		Matrix mat;
		boolean sort = false; 
		double[][][] colorValBase; 
		double[][] colorValTest; 
		
		public void setSort(boolean b){
			sort = b;
		}
		public BuildColorArray(Matrix[] vidMat, Matrix vTest){
			videoMat= vidMat;
			mat= vTest;
			int sz =mat.getColumnDimension();
			colorValBase = new double[videoMat.length][sz][];
			int c = mat.getRowDimension();
			if(c > nobins ) c= nobins;
			colorValTest = new double[sz][c]; 
			
			
		}
		public BuildColorArray(Matrix vidMat, Matrix vTest){
			videoMat = new Matrix[1]; 
			videoMat[0]= vidMat;
			mat= vTest;
			int sz =mat.getColumnDimension();
			colorValBase = new double[videoMat.length][sz][];
			int c = mat.getRowDimension();
			if(c > nobins ) c= nobins;
			colorValTest = new double[sz][c]; 
			
			
		}	
		public void buildColorArrays(){
			
		 int ncol=	videoMat[0].getColumnDimension();
		 
		 double[][][] base= new double[videoMat.length][][];
		 int videoInd, rw, cl; 
		 for(int n=0; n < videoMat.length; ++n){
			 int ln1 = videoMat[n].getRowDimension();
			 int ln2 = videoMat[n].getColumnDimension(); 
		  
			 double [][] baseb = new double[ln1][ln2]; 
			 baseb = videoMat[n].getArrayCopy(); //for each video
			 
			 int nobins = getNobins();
			 if(nobins < ln1){
				 base[n] = MatrixRetrieve.groupbins(baseb, ln2, nobins);
				 
			 }else
				 base[n] = baseb; 
				 
		 }
		 //for each video the color components or columns of mat 
		 
		 //all videos same # columns 
				
				  
		 for(videoInd=0; videoInd < videoMat.length; ++videoInd){
			 //each video matrix may have different number of frames or rows
			 int nrow = videoMat[videoInd].getRowDimension(); 
			 if(nrow > nobins)nrow= nobins; 
			 for(cl=0; cl < ncol; ++cl ){
				 colorValBase[videoInd][cl] = new double[nrow]; 
				 for(rw=0; rw< nrow; ++rw){
					 //columns or color values for each video; transpose
				    
					 colorValBase[videoInd][cl][rw] = base[videoInd][rw][cl]; 
					
			 }
		 }
		sort = false; 	 
			 if(sort)
				 Arrays.sort(colorValBase[videoInd][cl]);			 
	}
//		video to test
			double [][] arrp = mat.getArrayCopy();
			 
			int tcl = mat.getColumnDimension();
			int trw = mat.getRowDimension();
			double [][] arr = new double[trw][nobins];
			if(nobins < trw){
				 arr = MatrixRetrieve.groupbins(arrp,trw,nobins);
				 trw = nobins; 
			 }else
				 arr = arrp; 
			for(cl=0; cl < tcl; ++cl){
				for(rw=0; rw< trw; ++rw){
				colorValTest[cl][rw]= arr[rw][cl];//transpose
			
				}
				if(sort)
					Arrays.sort(colorValTest[cl]); 
				}
			//up to here common with StudentTest
			
	}	
		}
	 
}
