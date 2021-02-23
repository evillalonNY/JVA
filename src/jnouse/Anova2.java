package jnouse;

import jalgo.StudentTest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jclient.VideoFile;
import jmckoi.MatrixRetrieve;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.FDistributionImpl;

import Jama.Matrix;

public class Anova2 {
	/**
	  * Significant level of the two-sided test of statistics
	  */
	 static final double alpha = 0.1;
	 
	 /**group the 256 color values into nobins */
	 private static final int nobins = 256; 
	
	 
	/**
	  * p.values for every column of mat match with corresponding 
	  * columns of matrices videoMat.  The probability values are 
	  * obtained using the student distribution; 
	  * dim=[#videosToTest][256]
	  * 
	  */
	 double[]pvalues; 
	 
	 boolean showtest = true; 
	 
	 List<String> thevideos = new ArrayList<String>(); 
	 static int totest = 0;
	
	
	//Results of test after completing test
	 private static StringBuffer res[]=  new StringBuffer[3] ; 
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
	
	 public static StringBuffer[] getRes(){
		 return res;
	 }
	
	 
	 public Anova2(String args[])throws MathException{
	    	if(!debug)
	    		mLog.setLevel(Level.WARNING);
	    	//true is for the mean otherwise it is the median
	    	MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt", true, true);
	    	List<String> similar = new ArrayList<String>(); 
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
		  //  
		    int sz = thevideos.size();
		    String testvid = thevideos.get(totest);
		   
		    List<String> pass = new ArrayList<String>();
		    pass.addAll(thevideos); 
		   
		    StringBuffer r[] = meanDifTest(vstore, totest);
		    for(int k=0; k<3; ++k)
		    this.res[k]=r[k];	
		    
		    String toprint="\n\nTest video: "+ testvid + 
		    "\nStudent test at significant level= " + Anova2.alpha;
		   
		    StringBuffer sim =new StringBuffer(toprint);  
		    
		    mLog.info(toprint);   
	    
	    }
		
			
			
		
	 public StringBuffer[] meanDifTest(MatrixRetrieve vstore,int totest) 
	 throws MathException{
		 
			String testvid = thevideos.get(totest);
			if(!showtest)
			  thevideos.remove(totest);
			
			StringBuffer res0 = new StringBuffer("Test video  "+ testvid);
			StringBuffer[] res = new StringBuffer[3]; 
			String mess1 = "Testing video: " + testvid;
			//JOptionPane.showMessageDialog(null, mess1, "Test videos",
				//	JOptionPane.INFORMATION_MESSAGE);
			
		    Matrix [] matG = MatrixRetrieve.studentMats(vstore.getMeangreen()); 
		    Matrix [] matR = MatrixRetrieve.studentMats(vstore.getMeanred());
		    Matrix [] matB = MatrixRetrieve.studentMats(vstore.getMeanblue());
		    
		    StringBuffer rtest =  anovatest(matR,thevideos, "RED");
		    res[0] = new StringBuffer("\n"+res0);
		    res[0].append(rtest);
		    
		 
		 	StringBuffer gtest =  anovatest(matG,thevideos,"GREEN");
		 	res[1] = new StringBuffer("\n"+res0);
		 	res[1].append(gtest);
		 	
		 	StringBuffer btest =  anovatest(matB,thevideos,"BLUE");
		 	res[2] = new StringBuffer("\n"+res0);  
		 	res[2].append( btest);
		 	
		 	
		 	return res; 
		    }
	 public StringBuffer anovatest(Matrix[]base, List<String> thevid, String col )
	 throws MathException{
		 
		 double [][] arr = new double[base.length][256];
		 double []rwsum = new double[base.length];
		 double M= 0; 
		 int cnt=0;
		 double SST =0;
		 for(Matrix mm : base){
		   arr[cnt] = mm.transpose().getArrayCopy()[0];
		   cnt++;
		 }
		 int sz = base.length;
		 double[][] basearr=groupbins(arr,sz);
		
		 double r = 256/nobins;	
		 for(cnt=0; cnt < base.length; ++cnt){	 
		 for(double d:basearr[cnt]){
			 
			   rwsum[cnt]=rwsum[cnt]+(d);
			   M= M+d;
			   SST =SST + d*d;
		   }
		 }
		
		double norow = base.length;
		double [] colsum = new double[nobins];
		double nocol = 0;
		for(int cl=0; cl< nobins; ++cl ){
		   for(int n=0; n < base.length; ++n) {
			  
			   colsum[cl] += basearr[n][cl];
			  
		   }
		   
		  if(colsum[cl] > 1.e-5) nocol+=1; 	
		}
		 	 
		 double CM = M*M/(r*norow*nocol + 1.e-10);
		 SST = SST-CM;
		 double SSA = 0;
	     for(double d: rwsum) SSA = SSA+ d*d;
		 SSA = SSA/(r*nocol+1.e-10)-CM;
         double SSB = 0;
         cnt=0;
         for(double dd:colsum){
        	 SSB = SSB+ dd*dd;
        	
         }
         SSB = SSB/(r*norow+1.e-10)-CM;
         double SSAB =0;
       
         for(int n=0; n < base.length; ++n)
        	 for(int k=0; k < nobins; ++k)
        		 SSAB += basearr[n][k]* basearr[n][k];
         SSAB =SSAB/r-CM-SSA-SSB;
         double SSE  = SST- SSA - SSB - SSAB; 
         double MSA  = SSA/(norow-1);
         double MSB  = SSB/(nocol-1);
         double MSAB = SSAB/((nocol-1)*(norow-1));
         double df = r>1 ?nocol*norow*(r-1):nocol*norow;
         double error = SSE/df;
         double FA = MSA/(error+1.e-5);
         double FB = MSB/(error+1.e-5);
         double FAB = MSAB/(error+1.e-5);
         System.out.println(""+FA+"\t"+FB+"\t"+FAB+"\t"+df+"\t"+CM);
         FDistributionImpl paf = new  FDistributionImpl(norow-1,df);
         double pA=1- paf.cumulativeProbability(FA);
        
         FDistributionImpl pbf = new  FDistributionImpl(nocol-1,df);
         double pB=1- pbf.cumulativeProbability(FB);
         
         FDistributionImpl pabf = new  FDistributionImpl((nocol-1)*(norow-1),df);
         double pAB=1- pabf.cumulativeProbability(FAB);
         
		 pvalues = new double[3];
		 pvalues[0] = pA;
		 pvalues[1] = pB;
		 pvalues[2] = pAB;
		 StringBuffer res = new StringBuffer();
			res.append("\nanova test at significant level= "+
					Anova2.alpha);
			res.append("\nColor component "+ col + ": ");
		
			res.append("\nMean frames "+pA);
			res.append("\nColor values "+pB);
			res.append("\nInteraction "+pAB);
		 
				
		 return res;
		 
	 }
	 private double[][] groupbins(double[][]basearr,int len){
		 double [][] basebin = new double[basearr.length][nobins];
		 int sz = 256/nobins;
		 int cnt =0;
		 if(sz >1){
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
		 }else
			 basebin = basearr;
		 double[] basemn = new double[nobins];
		
		/*	 for(int c=0; c < nobins;++c)
				 for(int r= 0; r < len; ++r)
				 basemn[c]+= basebin[r][c]/len;
			 
	         for(int n=0; n < len; ++n)
	        	 for(int c= 0; c < nobins; ++c)
	        	 basebin[n][c] =  Math.abs(basebin[n][c]-basemn[c]);
	      */   
		 return basebin;
		 }
	public static void main(String[]args) throws MathException{
		Anova2 chi = new Anova2(args);
		StringBuffer[] res = chi.getRes();
		for(StringBuffer buf:res)
			mLog.info(buf.toString());
		
	}
}
