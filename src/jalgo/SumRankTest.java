/**
 * File		:	SumRankTest
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Apply the Wilcoxon Rank Sum to test for color components 
 *              at significant level alpha. False stands for 
 *              pvalue < alpha or different videos,  
 *              and true is pvalue > alpha or similar videos.   
 *              Dimension mat = noframes X 1 column or noframes X 256 
 *              The matrices may have only one column representing 
 *              average values of all frames for each of color values(0-255)
 *              Then, rows are color values (0-255) and the column 
 *              that averages over all frames of the video for each RGB. 
 *              Join the arrays of videos along the column and after 
 *              eliminating duplicates ranks all entries. 
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

public class SumRankTest {
    /**
     * array of video matrices, each matrix has dim=[nofrms][256]
     */
		
    Matrix []videoMat;
    
    /** Video matrix to test against array videoMat, dim=[nofrms][256]
     */
    Matrix mat;
    double [] pvalue; 
    
    /**
     * Significant level of the two-sided test of statistics
     */
    static double alpha = 0.01; 
    
    /**column value along indeces of mat
     */
    private static final int totest =0;
    List<String> thevideos = new ArrayList<String>();
    
//  number of RGB matrices to pass the test
    private static final int CNTVID = 3; 

    //String with the results of the test
    private StringBuffer res; 
    private static Logger mLog = 
        Logger.getLogger(SumRankTest.class.getName());
    private boolean debug = false;
    
    public StringBuffer getRes(){
    	return res; 
    }
    public SumRankTest( ){
    	super(); 
    	if(false)
    		mLog.setLevel(Level.WARNING);
    }
    public SumRankTest(Matrix[] v, Matrix m){
    this();	
	videoMat =v;
	mat = m;
	
    pvalue = new double[v.length]; 
	for(int n=0; n < v.length; ++n){
		if(debug)
	     videoMat[n].print(2, 1); 
	    pvalue[n] =rankVid(totest,n);	 }
	
    }
    public SumRankTest(String args[]){
    	this();
    	MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt");
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
       	 cnt = argsf.length; 
        }
        
        vstore.allFrmTest(vstore);
        /** get matrices without transforming data*/
        Matrix [] matG = MatrixRetrieve.studentMats(vstore.getMeangreenCnt()); 
        Matrix [] matR = MatrixRetrieve.studentMats(vstore.getMeanredCnt());
        Matrix [] matB = MatrixRetrieve.studentMats(vstore.getMeanblueCnt());
        
      
       
        List<String> similar = performTestRGB(matR, matG, matB); 
        String test = thevideos.get(totest); 
        //checking that similar videos have same rank
        Matrix [] blue = vstore.getMatB();
	    Matrix [] green = vstore.getMatG();
	    Matrix [] red =  vstore.getMatR();
       
    }
    public void setAlpha(double al){
	SumRankTest.alpha = al; 
    }
    public double getAlpha(){
	return alpha; 
    }
	
    public double rankVid(int ind, int videoInd){//ind=0 for one column only 
    
	double[] proj = elimzeros(videoMat[videoInd],ind);
	int nrow1= proj.length;

     double [] test = elimzeros(mat,ind);
     int nrow2 = test.length;

	int nrow = nrow1 + nrow2;
	double [] combo = new double[nrow];
	
     Double[] comboD = new Double[nrow];
    
	for(int nr=0; nr < nrow; ++nr){
	    if(nr < nrow1)
		   comboD[nr] = new Double(proj[nr]);  
         else
           comboD[nr] =new Double(test[nr-nrow1]);
	    combo[nr] = comboD[nr];
	    }
	
     Arrays.sort(combo); 
     Arrays.sort(comboD); 
   
	 //unique values
	List<Double> dummy = new ArrayList<Double>(); 	

	for(int rw=0; rw < nrow; ++rw)
	    dummy.add(comboD[rw]);
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
			  
	     for(int frw=rw; frw <combo.length; ++frw){
                 if(combo[frw] < nodum[rw] )continue;
                      
		 if(combo[frw] > nodum[rw]) 
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
	 
	 //assign ranks to all entries in the array of differences
	 double Wp =0;
	 double Wt =0;
	 for(int rw=0; rw <proj.length; ++rw){
		 if(proj[rw]==0) continue; 
		 int key = Arrays.binarySearch(nodum, proj[rw]);
		 Wp+=rnkval[key];
	 }
	 for(int rw=0; rw <test.length; ++rw){
		 if(test[rw]==0) continue; 
		 int key = Arrays.binarySearch(nodum, test[rw]); 
		 Wt+=rnkval[key];  
	 }
	 
	 
      int N2 = proj.length;
      int N1 = test.length; 
	 double W = 0;
	 double u1 = N1*N2 + 0.5 * N1* (N1 + 1) - Wt;
	 double u2 = N1*N2 + 0.5 * N2 *(N2 + 1) - Wp;
	
	 if(Math.abs(N1*N2/(u1+u2)-1) > 0.01)
		 prUsage("Wrong Calculation of U-test"); 
	 if(u1 > u2)
		W = u2;
	 else 
		W = u1;  
     int nS=N2, nL=N1;
	 if(N1 < N2){
		nS = N1;
		nL = N2;}
	 
	 double mu = 0.5*nL*nS; 
	 double sigma = nL*nS * (nS + nL + 1.0)/12.0;   
	 if(sigma <0)
		 prUsage("Sigma is less 0"); 
	 sigma= Math.sqrt(sigma)+ 1.e-10;
	 double zeta= (W - mu)/sigma; 
	 double z = zeta;
	 double sgn = -1; 
	 if (z < 0) sgn=1; 
	 if(z  > 0) z = -zeta; 
	 double stnorm = cern.jet.stat.Probability.normal(z);
	 		 
	 
     boolean pstat =false; //unequal 
	 
	double pvalue = 2* stnorm;
	mLog.info("The pval is "+ pvalue+"; zscore "+ sgn*zeta); 
	if (pvalue > alpha)pstat =true;//equal
	return pvalue;
	 }
	/** takes the matrix and  index column, gets the corresponding
        * array element for column=ind.  Construct the array with
        * non-zero entries, and sort it 
        */ 
	public double[] elimzeros(Matrix m,int ind){
		  double [][] arr = m.getArrayCopy();
		  int nrow = m.getRowDimension();
		  int cnt =0;
		  for(int nr=0; nr < nrow; ++nr)
			 if(Math.abs(arr[nr][ind])<1.e-5)cnt++;
		  //exclude all zeros in difference matrix
		  double [] proj = new double[nrow-cnt];
    
		  cnt =0;
		  for(int nr=0; nr < nrow; ++nr){
			 
			 if(Math.abs(arr[nr][ind]) <1.e-5)
				continue;
		  
			 proj[cnt] = arr[nr][ind]; 
			 cnt++; 
          
		  } 
	  
		  Arrays.sort(proj);
		 
		  return proj;
	}
    static void prUsage(String mess) {
    	String mess2 =  "Wrong number of frames in MedianTest";
    	JOptionPane.showMessageDialog(null, mess, "Median Failed",
				JOptionPane.ERROR_MESSAGE);
	mLog.warning(mess);
    }
    //build matrices to test 
    public Matrix [] testmat(Matrix mat[], int totest){
    	   Matrix [] vr = new Matrix[mat.length-1];
    	   int cnt=0; 
    	   for(int k=0; k < mat.length; ++k){
    	   	if(k == totest) continue; 
    	   	vr[cnt] = new Matrix(mat[k].getArrayCopy());
    	   	cnt++;
    	   }
    	   return vr;
    	   }
    //add to the collection videos that pass the test
    public  List<String> findsimilar(List<String> thevideos, 
   		 double[]red,double[]green,double[]blue, int totest){     
    String [] argsf = thevideos.toArray(new String[thevideos.size()]); 
    String testvid = argsf[totest];
    String [] others = new String[argsf.length-1];
    boolean[]pred = new boolean[red.length];
    boolean[]pgreen = new boolean[green.length];
    boolean[]pblue = new boolean[blue.length];
    int n=0;
    
    for(n=0; n < pred.length; ++n)
    	if (red[n] >= alpha ) pred[n]=true;  
    for(n=0; n < pgreen.length; ++n)
    	if (green[n] >= alpha ) pgreen[n]=true;
    for(n=0; n < pblue.length; ++n)
    	if (blue[n] >= alpha ) pblue[n]=true;  
    
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
   
    public List<String>  performTestRGB(Matrix[] matr, Matrix[]matg, Matrix[]matb){
    //GREEN	
    	 
    	Matrix mg = new Matrix(matg[totest].getArrayCopy());
    	Matrix vg[] = testmat(matg,totest); 
   	            
        SumRankTest sgtgreen =  new SumRankTest(vg, mg); 
        double pgreen []= sgtgreen.pvalue;
   //RED
  
        Matrix mr = new Matrix(matr[totest].getArrayCopy());
        Matrix vr[] = testmat(matr,totest); 
   	
        SumRankTest sgtred =  new SumRankTest(vr, mr);
        double pred []= sgtred.pvalue;
  //BLUE
        Matrix mb = new Matrix(matb[totest].getArrayCopy());
        Matrix vb[] = testmat(matb,totest); 
 
  	    SumRankTest sgtblue =  new SumRankTest(vb, mb); 
        double pblue []= sgtblue.pvalue;
        res = buildPvalues(thevideos, pred, pgreen, pblue); 
        List<String> similar = findsimilar(thevideos, 
          		pred,pgreen,pblue,totest); 
        
        return similar; 
    }
    
    public StringBuffer buildPvalues(List<String> thevideos, 
    		double[]pred, double[] pgreen, double [] pblue){
    StringBuffer res = new StringBuffer("Test video "+thevideos.get(totest)+
    		" for color RED at significant level " + alpha + "\n");
    int n=0; 
    int cnt =0;
    boolean bool=false; 
    for(n=0; n < thevideos.size(); ++n){
    	
    	if(n == totest)continue; 
    	
    	if(pred[cnt] >= alpha) bool = true; 
    	double p = Math.round(pred[cnt]*1000f)/1000d; 
    	res.append(thevideos.get(n)+ " pvalue= " + p + ". Similar = "+ bool+ "\n");
    	bool = false; 
    	cnt++;
    }
    res.append("\nTest video "+ thevideos.get(totest)+
    		" for color GREEN at significant level " + alpha + "\n");
    cnt = 0;
    bool = false; 
    for(n=0; n < thevideos.size(); ++n){
    	
    	if(n == totest)continue; 
    	if(pgreen[cnt] >= alpha) bool = true; 
    	double p = Math.round(pgreen[cnt]*1000f)/1000d; 
    	res.append(thevideos.get(n)+ " pvalue= " + p + ". Similar = "+ bool+ "\n");
    	bool = false; 
    	cnt++;
    	
    }
    res.append("\nTest video "+ thevideos.get(totest)+
    		" for color BLUE at significant level " + alpha + "\n");
    		
    bool = false; 
    cnt = 0;
    for(n=0; n < thevideos.size(); ++n){
    	
    	if(n == totest)continue; 
    	if(pblue[cnt] >= alpha) bool = true; 
    	double p = Math.round(pblue[cnt]*1000f)/1000d; 
    	res.append(thevideos.get(n)+ " pvalue= " + p+ ". Similar = "+ bool+ "\n");
    	bool = false; 
    	cnt++;
    }
    return res; 
    }
    
    public static void main(String[] args) throws SQLException{
    	if(args.length<=0){
			prUsage("SumRankTest provide the videos to test"); 	
			
			return;
			}
    	if(args.length >1){
		SumRankTest driver = new SumRankTest(args); 
		mLog.info(driver.res.toString()+""); 
		return; 
    	}
    	 if (args.length == 1){ //reading from file
    	   	 args[0].trim();
    	   	 List<String> thevideos = new ArrayList<String>(); 
    	   	 thevideos = VideoFile.readFile(args[0]);
    		  
    	   	 String [] argsf = thevideos.toArray(new String[thevideos.size()]); 
    	   	SumRankTest driver = new SumRankTest(args); 
    		mLog.info(driver.res.toString()+""); 
    		return; 
    	   	
}
      
    }
}
               



