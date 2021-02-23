/**
 * File		:	StudentTest.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Takes the video matrix to test mat. 
 *              Array of video matrices, videoMat, and corresponding url's.  
 *              Calculate the test of statistics for each column 
 *              (or color value) of mat, where dim(mat) = numFrames X 256. 
 *              Stores in HashMap, PValuesVideo, the url's of videoMat   
 *              and the results of the t.test for color components 
 *              at significant level alpha. True means pvalue < alpha or different,  
 *              and false means pvalue > alpha or videos color value same means.   
 */
package jalgo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jclient.VideoFile;
import jmckoi.MatrixRetrieve;
import Jama.Matrix;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.*;

public class StudentTest {
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
	  * Bonferroni correction for multiple tests 
	  * If true then alpha= alpha/((nvideos-1)*nvideos)
	  */
	 boolean bonfCorrection = false; 
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
	  * array of mean values along columns of mat; length=256
	  */
	 double [] meanCol;
	 double medianCol[]; 
	 /**
	  * array of sd values along columns of mat; length = 256
	  */
	 double [] sdCol;
	 /**
	  * p.values for every column of mat match with corresponding 
	  * columns of matrices videoMat.  The probability values are 
	  * obtained using the student distribution; 
	  * dim=[#videosToTest][256]
	  * 
	  */
	 double[][] pvalues; 
	 /** the scores to obtain pvalues*/
	 double[][] zscores;
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
	 private boolean weight = true; 
	 private static Logger mLog = 
	     Logger.getLogger(StudentTest.class.getName());
	 private boolean debug = true;
	 
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
	 public StudentTest(List<String> thevid){
		 if(!debug)
			 mLog.setLevel(Level.WARNING);
		 thevideos= thevid;  
	 }
	 public StudentTest(boolean w){
		 if(!debug)
			 mLog.setLevel(Level.WARNING);
		 weight =w; 
	 }
	 public StudentTest(List<String> thevid, int n, boolean w){
		 this(thevid); 
		 totest =n; 
		 weight = w; 
	 }
	 public StudentTest(Matrix [] v, Matrix m, List<String> thevid){
		 this(thevid); 
		 videoMat =v;
		 mat = m; 
		 Ttest(); 
		  
	 }
	
public void Ttest(){
	//all videos double arrays 
	int videoInd,cl; 
	obj = new StudentTestPaired.BuildColorArray(videoMat, mat);
	obj.buildColorArrays(); 
	
	double[][][] colorValBase=(double[][][]) obj.colorValBase;
	double[][] colorValTest = obj.colorValTest; 
	if(false){
	new Matrix(obj.colorValBase[0]).print(1, 0); 
	new Matrix(colorValTest).print(1,0);
	}
	int ncol=	videoMat[0].getColumnDimension();
	meanVideoCol = new double[videoMat.length][ncol];
	sdVideoCol= new double[videoMat.length][ncol];
	medianVideoCol = new double[videoMat.length][ncol];
	ColorIndStat statcol; 
	for(videoInd=0; videoInd < videoMat.length; ++videoInd)
	for(cl= 0; cl < ncol; ++ cl){
		//for each col or color value get statistics
		try{
		double b = new TTestImpl().t(colorValBase[videoInd][cl], colorValTest[0]);
		mLog.warning("Test Apache zscore "+b);
		}catch(Exception err){
			
		}
		statcol = new ColorIndStat(colorValBase[videoInd][cl]);
		statcol.calculateStat();
		
		meanVideoCol[videoInd][cl] = statcol.getMeanCov();
		medianVideoCol[videoInd][cl] = statcol.getMedianCov(); 
		sdVideoCol[videoInd][cl] = Math.sqrt(statcol.getVariance());
		
		}
	
	int tcl = mat.getColumnDimension(); 
	meanCol = new double[tcl]; 
	sdCol= new double[tcl]; 
	medianCol = new double[tcl]; 
	
	for(cl= 0; cl < tcl; ++ cl){
	statcol = new ColorIndStat(colorValTest[cl]);
	statcol.calculateStat(); 
	   meanCol[cl] = statcol.getMeanCov();
	   medianCol[cl] = statcol.getMedianCov(); 
	   sdCol[cl] = Math.sqrt(statcol.getVariance());
	}
		
	
}
 
//return the significant levels of the test as true or false
public StringBuffer calculatePValues(String col) throws ArithmeticException{
	
int videoInd, cl;
int ncol = meanVideoCol[0].length; 
double n2 = mat.getRowDimension()+1.e-10;
double num=0.0;
double den=0.0; 
/**
 * for each matrix in VideoMat array of pvalues, 
 * one double for each column
 */
double[][] pvalues = new double[videoMat.length][mat.getColumnDimension()];
double[][] zscores = new double[videoMat.length][mat.getColumnDimension()];
boolean [][]signfLevel = new boolean[videoMat.length][mat.getColumnDimension()];
StringBuffer res = new StringBuffer();
res.append("\nTwo-sided t.test at significant level= "+StudentTest.alpha);
res.append("\nColor component "+ col + ": ");

for(videoInd=0; videoInd < videoMat.length; ++ videoInd){
	double n1 = videoMat[videoInd].getRowDimension()+1.e-10;
	 
	for(cl =0; cl < ncol; ++cl){
		
	double s2 = sdCol[cl];
	double s1 = sdVideoCol[videoInd][cl];
	//assuming unequal variance, find degree of freedom 
	num = (s1*s1/n1 + s2*s2/n2)*(s1*s1/n1 + s2*s2/n2);
	den = (s1*s1/n1)* (s1*s1/n1)/(n1-1) + (s2*s2/n2)* (s2*s2/n2)/(n2-1);
	
	double df = (num/(den + 1.e-20))+1.e-10; 
	//pool sd for the two videos 
	
	double poolsd = Math.sqrt(s1*s1/n1 + s2*s2/n2) + 1.e-20; 
	double xbar = (meanVideoCol[videoInd][cl] - meanCol[cl]);
	
	double tvalue = xbar/(poolsd+1.e-5); 
	mLog.warning("Test score "+tvalue);
	zscores[videoInd][cl]= tvalue;
	double pval = cern.jet.stat.Probability.studentT(df, tvalue);
	//System.out.println("Test pval "+pval);
	if (tvalue > 0)
		pvalues[videoInd][cl]= 2*(1.0 - pval);
	else
		pvalues[videoInd][cl]=  2* pval;
	
	String str = thevideos.get(videoInd);  
		
	res.append("\nVideo "+ str+ "\npvalue= " +pvalues[videoInd][cl]); 

	//apply Bonferoni correction for multiple comparisons
	int len = videoMat.length;
	double alpha = new Double(StudentTest.alpha); 
	if(bonfCorrection && len > 1){
	    double corr = (len -1)*len/2.0;  	
		alpha = alpha /corr; 
	}
	signfLevel[videoInd][cl]= true; //Null hypothesis: same videos 
	
	if ( pvalues[videoInd][cl] <= alpha) {
		signfLevel[videoInd][cl] = false; //different videos 
	res.append("\t"+col+ "...Different videos.\n"); 
	}else 
		res.append("\t"+ col+"...Same videos.\n"); 
	}

}
if(col.equalsIgnoreCase("RED"))
		signfLevelRed = signfLevel; 

if(col.equalsIgnoreCase("GREEN"))
	signfLevelGreen= signfLevel;

if(col.equalsIgnoreCase("BLUE"))
	signfLevelBlue= signfLevel;
mLog.info(res.toString()+""); 
return res; 
}

public static Matrix[] logMats(Matrix[] matG){
	
	for(int k=0; k <matG.length; ++k){
		double arr [][] = matG[k].getArray();
	for(int c=0; c < matG[k].getColumnDimension(); ++c)
	for(int r=0; r < matG[k].getRowDimension(); ++r){
		arr[r][c] += 0.5;
		arr[r][c] =Math.log10(arr[r][c]); 
		}
	}
	return matG; 
}

public StringBuffer meanFrmTest(MatrixRetrieve vstore, boolean lg, 
		int totest){
	String testv = thevideos.get(totest);
	if(!showTest)
	  thevideos.remove(totest);
	testvid = testv;
	StringBuffer res0 = new StringBuffer("Test video  "+ testv);
	StringBuffer res = new StringBuffer(res0); 
	String mess1 = "Testing video: " + testv;
	JOptionPane.showMessageDialog(null, mess1, "Test videos",
			JOptionPane.INFORMATION_MESSAGE);
	
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
    if(!showTest)
    	Rbase = baseMats(matR, totest);
  
    StudentTest rtest =  new StudentTest(Rbase,Rtest, thevideos);
    
    res.append(rtest.calculatePValues("RED"));
   
    
 	Matrix [] Gbase = matG;  
 	if(!showTest)
    	Gbase =baseMats(matG, totest);
 	StudentTest gtest =  new StudentTest(Gbase,Gtest, thevideos);
 	res.append("\n"+res0); 
 	res.append(gtest.calculatePValues("GREEN"));
 	
 	
 	Matrix [] Bbase = matB; 
 	if(!showTest)
    	Bbase = baseMats(matB, totest);
 	 StudentTest btest =  new StudentTest(Bbase,Btest, thevideos);
 	 res.append("\n"+res0); 
 	 res.append(btest.calculatePValues("BLUE"));
 	
 	return res; 
    }
public static Matrix [] baseMats(Matrix [] matR, int totest)
{
	Matrix [] Rbase = new Matrix[matR.length-1];
	int cnt=0; 
	for(int n=0; n < matR.length; ++n){
		if(n==totest)
			continue; 
		Rbase[cnt] = matR[n];
		cnt++;
		}
	 
	return Rbase;
}


public static void main(String[] args) throws SQLException{
	StudentTest st = new StudentTest(true);
	MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt", st.weight,true);
	if(args.length<=0){
		SVDVideo.prUsage("Provide the videos to test"); 	
		
			return;
		}
	List<String> thevideos=new ArrayList<String>();;   
	//videos are input as arguments of main
	 if(args.length > 1) {
		
		 vstore.retreiveMat(args);
		 for(int a=0; a < args.length; ++a)
		 thevideos.add(args[a]);
	 }
	 int cnt = args.length;
	 //videos are input in a file
    if (args.length == 1){ //reading from file
   	 args[0].trim(); 
   	 thevideos = VideoFile.readFile(args[0]);
	  
   	 String [] argsf = thevideos.toArray(new String[thevideos.size()]);  
   	
   	 
    vstore.retreiveMat(argsf);
   	
    }
    
    StudentTest stest = new StudentTest(thevideos); 
    StringBuffer res = stest.meanFrmTest(vstore, true, totest);
   
   mLog.info(res.toString()+"");  
}



}
	

	

