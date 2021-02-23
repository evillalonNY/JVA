/**
 * File		:	SignedRankTest
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Takes two videos matrices to test equality of median 
 *              for each column or color component independently. 
 *              Dimension mat = noframes X 1 column or noframes X 256 
 *              The matrices may have only one column representing 
 *              average values of all frames for each of color values(0-255)
 *              Then, rows are color values (0-255) and the column 
 *              that averages over all frames of the video for each RGB. 
 *              Substract the video columns and ranks their values 
 *              Calculate the test of statistics for equality of videos.
 *              
 *  Uses: SumRankTest.java            
 *     
 *                 
 */
package jalgo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jclient.VideoFile;
import jmckoi.MatrixRetrieve;
import Jama.Matrix;

public class SignedRankTest {
    /**
     * array of video matrices, each matrix has dim=[nofrms][256]
     */
		
    static Matrix []videoMat;
    
    /** Video matrix to test against array videoMat, dim=[nofrms][256]
     */
    static Matrix mat;
    /**
     * Significant level of the two-sided test of statistics
     */
    static double alpha = 0.1; 
    /**the pvalues comparing matrices videomat with mat
     */
    boolean [] pvalue;	
    /**column value along indeces of mat
     */
    private static final int ind =0;//index totest
    static List<String> thevideos = new ArrayList<String>();
    private static boolean lg = false; //no transform the data
//  number of RGB matrices to pass the test
    private static final int CNTVID = 3; 
    private static StringBuffer display = new StringBuffer();
    private static Logger mLog = 
        Logger.getLogger(SignedRankTest.class.getName());
    private boolean debug = true;
    /** displays the String with the results*/
    public StringBuffer getRes(){
    	return display; 
    }
    public SignedRankTest(Matrix[] v, Matrix m, String color){
    	if(!debug)
    		mLog.setLevel(Level.WARNING);
	videoMat =v;
	mat = m;
	
    pvalue=new boolean[v.length];
    
	for(int n=0; n < v.length; ++n){
		if(!debug)
	      videoMat[n].print(2, 1); 
	    pvalue[n] =substractVid(ind,n, color);	 
	    }
    }
    public SignedRankTest(String [] args){
    	if(!debug)
    		mLog.setLevel(Level.WARNING);
    	MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt");
    	if(args.length<=0){
    		prUsage("Provide the videos to test"); 	
    		
    			return;
    		}
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
       	 SignedRankTest.thevideos = VideoFile.readFile(args[0]);
       	 String [] argsf = thevideos.toArray(new String[thevideos.size()]);  
       	 vstore.retreiveMat(argsf);
       	 cnt = argsf.length; 
        }
        
        vstore.allFrmTest(vstore);
        Matrix [] matG = MatrixRetrieve.studentMats(vstore.getMeangreenCnt()); 
        Matrix [] matR = MatrixRetrieve.studentMats(vstore.getMeanredCnt());
        Matrix [] matB = MatrixRetrieve.studentMats(vstore.getMeanblueCnt());
        String test = thevideos.get(ind); 
        String res =  "Vid to test :" + test +"\nSimilar Videos: ";  
       
        List<String> similar = performTestRGB(matR, matG, matB); 
        
        
        for(String str: similar)
        	res+= "\n" +str; 
        mLog.info(res+"\n"+ " "); 
        display.append(res);
      
    }
    public void setAlpha(double al){
	this.alpha = al; 
    }
    public double getAlpha(){
	return alpha; 
    }
	
    public static boolean substractVid(int ind, int videoInd, String color){//ind=0 for one column only 
    
     Matrix diffMat = new Matrix(mat.getRowDimension(), mat.getColumnDimension()) ;
     
	 diffMat = videoMat[videoInd].minus(mat);
	 double [][] arr = diffMat.getArrayCopy();
	 int nrow = diffMat.getRowDimension();
	 int cnt =0;
	 for(int nr=0; nr < nrow; ++nr)
		 if(Math.abs(arr[nr][ind])<1.e-5)cnt++;
	 
	 //exclude all zeros in difference matrix
	 double [] proj = new double[nrow-cnt];
     boolean [] sgn = new boolean[nrow-cnt];
     Double[] projD = new Double[proj.length];
     
     //store signs of differences 
     cnt =0;
	 for(int nr=0; nr < nrow; ++nr){ 
		 if(Math.abs(arr[nr][ind]) <1.e-5)
			 continue;
	     proj[cnt] = arr[nr][ind]; 
	     if(proj[cnt] > 0) sgn[cnt] = true;
	     if(proj[cnt] < 0) proj[cnt] = -proj[cnt]; 
	     cnt++; 
          
	 } 
	  
	 Arrays.sort(proj);
	 nrow= proj.length; 
	 for(int nr=0; nr < nrow; ++nr)
		 projD[nr] = new Double(proj[nr]);
	 //unique values
	 List<Double> dummy = new ArrayList<Double>(); 	

	 for(int rw=0; rw < nrow; ++rw)
	     dummy.add(projD[rw]);
	 Collection<Double> noDupsDummy = new LinkedHashSet<Double>(dummy);
	 int ln1 = noDupsDummy.size();
	 Double [] nodumD = new Double[ln1];
	 nodumD = noDupsDummy.toArray(new Double[ln1]);
	 double[] nodum = new double[ln1];
	 for(int rw=0; rw < ln1; ++rw)
	     nodum[rw] = nodumD[rw];
	 Arrays.sort(nodum); 
	 int[]howmany = new int[nodum.length]; 
	 //counts unique values in proj or array of differences
	 for(int rw=0; rw < nodum.length; ++rw){
		 if(nodum[rw] ==0)continue;
			  
	     for(int frw=rw; frw <proj.length; ++frw){
                 if(proj[frw] < nodum[rw] )continue;
		     
                      
		 if(proj[frw] > nodum[rw]) 
		     break;
		 else
		     howmany[rw]++;
		 
	     }
	 } 
	//ranks the differences array according to unique values	    
	 double [] rnkval = new double[nodum.length]; 
	 double rnk=0;
	 for(int rw=0; rw <nodum.length; ++rw){
	     if(nodum[rw] <1.e-5) continue;
	     double rnkb = rnk; 
	    rnk += (double) howmany[rw];
	   
	    double mn =0;
	    for(int k=1; k <= howmany[rw]; ++k){
             double tmp = rnkb + k;
	    	mn  +=  tmp;
            } 
	    	
	    mn = mn /(howmany[rw]+1.e-5);
	  
	     rnkval[rw] = mn;        		   
	 }
	 double[] sgnrnk = new double[proj.length];
	 //assign ranks to all entries in the array of differences
	 
	  for(int rw=0; rw <proj.length; ++rw){  
      int key = Arrays.binarySearch(nodum, proj[rw]);
      double val = rnkval[key];
      if(Math.abs(val)<= 1.e-10)prUsage("Bad calculation of ranks"); 
      sgnrnk[rw] = rnkval[key];
	  if(!sgn[rw]) sgnrnk[rw] = -  sgnrnk[rw];
	  }
      
	 //add positive and negative ranks
	 double Wp =0;
	 double Wn =0;
	 double W = 0; 
     int N=0; 
	 for(int frw=0;frw <proj.length; ++frw){
		 if(sgnrnk[frw]>0)
			 Wp += sgnrnk[frw];
		 if(sgnrnk[frw]<0)
			 Wn += sgnrnk[frw];
	 }
	 //calculate the zscore  
	 N= nrow; 
	 W = Wp-0.5; 
	 if(Math.abs(Wn) < Wp)W = Math.abs(Wn+0.5);
	 double mu = N*(N+1)/4.0; //mean
	 double sigma = (N+1) * (2*N+1)* N/24.0+1.e-5;//standard deviation 
	 if(sigma <0)
		 prUsage("Sigma is less 0"); 
     sigma= Math.sqrt(sigma); 
   
            
	 double zscore = (W-mu)/sigma; 
	 double z = zscore;
	 if(z  > 0) z = -zscore; 
	 double stnorm = cern.jet.stat.Probability.normal(z);
     boolean pstat =false; //unequal 
	 
         double pvalue = 2* stnorm;
         String mess = "Color "+ color+" and video "+ thevideos.get(videoInd+1)+": pvalue is "+ pvalue;
         mLog.info(mess);
         display.append(mess+"\n"); 
         if (pvalue > alpha)pstat =true;//equal
         return pstat;
    }
	

    
    public static List<String>  performTestRGB(Matrix[] matr, Matrix[]matg, Matrix[]matb){
    
   //RED
        Matrix mr = new Matrix(matr[ind].getArray());
        Matrix vr[] = testmat(matr,ind); 
	            
        SignedRankTest sgtred =  new SignedRankTest(vr, mr, "RED");
        boolean pred []= sgtred.pvalue;
   //GREEN	
    	Matrix mg = new Matrix(matg[ind].getArray());
    	Matrix vg[] = testmat(matg,ind);   
    	            
        SignedRankTest sgtgreen =  new SignedRankTest(vg, mg, "GREEN"); 
        boolean pgreen []= sgtgreen.pvalue;
  //BLUE
        Matrix mb = new Matrix(matb[ind].getArray());
        Matrix vb[] = testmat(matb,ind); 
  	 
        SignedRankTest sgtblue =  new SignedRankTest(vb, mb, "BLUE"); 
        boolean pblue []= sgtblue.pvalue;
        List<String> similar = findsimilar(thevideos, 
   		pred,pgreen,pblue,ind); 
 return similar;
 
    }
    //add to the collection videos that pass the test
    public  static List<String> findsimilar(List<String> thevideos, 
   		 boolean[]pred,boolean[]pgreen,boolean[]pblue, int totest){     
    String [] argsf = thevideos.toArray(new String[thevideos.size()]); 
    String testvid = argsf[totest];
    String [] others = new String[argsf.length-1];
    int cnt=0; 
    for(int k=0; k < argsf.length; ++k){ 
   	 if(k == totest) continue; 
   	 others[cnt] = argsf[k];
   	 cnt++;
       }
   	
    List<String> similar = new ArrayList<String>();  	 
    for(int k=0; k < pblue.length; ++k) {
   	 int pnt =0;
   	 if(pblue[k]) pnt++;
   	 if(pred[k]) pnt++;
   	 if(pgreen[k]) pnt++; 
   	 if(pnt >= CNTVID)
   	 
   	similar.add(others[k]); 	 
   		 
    }
    
    return similar; 
    }
    static void prUsage(String mess) {
    	String mess2 =  "Wrong number of frames in MedianTest";
    	JOptionPane.showMessageDialog(null, mess, "Median Failed",
				JOptionPane.ERROR_MESSAGE);
	    mLog.severe(mess);
    }
  //build matrices to test 
    public static Matrix [] testmat(Matrix mat[], int totest){
    	   Matrix [] vr = new Matrix[mat.length-1];
    	   int cnt=0; 
    	   for(int k=0; k < mat.length; ++k){
    	   	if(k == totest) continue; 
    	   	vr[cnt] = new Matrix(mat[k].getArrayCopy());
    	   	cnt++;
    	   }
    	   return vr;
    	   }
    public static void main(String[] args) throws SQLException{
    	MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt");
    	if(args.length<=0){
    		prUsage("Provide the videos to test"); 	
    		
    			return;
    		}
    	
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
       	 SignedRankTest.thevideos = VideoFile.readFile(args[0]);
       	 String [] argsf = thevideos.toArray(new String[thevideos.size()]);  
       	 vstore.retreiveMat(argsf);
       	 cnt = argsf.length; 
        }
        
        vstore.allFrmTest(vstore);
        Matrix [] matG = MatrixRetrieve.studentMats(vstore.getMeangreenCnt()); 
        Matrix [] matR = MatrixRetrieve.studentMats(vstore.getMeanredCnt());
        Matrix [] matB = MatrixRetrieve.studentMats(vstore.getMeanblueCnt());
        if(lg){//apply the log
        	matG = StudentTest.logMats(matG);
        	matR =StudentTest.logMats(matR);
        	matB = StudentTest.logMats(matB); 
        }
        
       List<String> similar = performTestRGB(matR, matG, matB); 
        String test = thevideos.get(ind); 
        String res =  "Vid to test :" + test +"\nSimilar Videos: ";  
      
        for(String str: similar)
        	res+= "\n" +str; 
        mLog.info(res+"\n"+ " "); 
        
    }
               
} 


