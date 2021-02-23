package jnouse;

import jalgo.StudentTest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jclient.VideoFile;

import jmckoi.MatrixRetrieve;
import Jama.Matrix;

public class ChiSquareTest {
	 /**
	  * Significant level of the two-sided test of statistics
	  */
	 static final double alpha = 0.1; 
	
	 
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
	
	 
	 public ChiSquareTest(String args[]){
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
		    "\nStudent test at significant level= " + ChiSquareTest.alpha;
		   
		    StringBuffer sim =new StringBuffer(toprint);  
		    
		    mLog.info(toprint);   
	    
	    }
		
			
			
		
	 public StringBuffer[] meanDifTest(MatrixRetrieve vstore,int totest){
		 
			String testvid = thevideos.get(totest);
			if(!showtest)
			  thevideos.remove(totest);
			
			StringBuffer res0 = new StringBuffer("Test video  "+ testvid);
			StringBuffer[] res = new StringBuffer[3]; 
			String mess1 = "Testing video: " + testvid;
			JOptionPane.showMessageDialog(null, mess1, "Test videos",
					JOptionPane.INFORMATION_MESSAGE);
			
		    Matrix [] matG = MatrixRetrieve.studentMats(vstore.getMeangreenCnt()); 
		    Matrix [] matR = MatrixRetrieve.studentMats(vstore.getMeanredCnt());
		    Matrix [] matB = MatrixRetrieve.studentMats(vstore.getMeanblueCnt());
		    
		   
		    Matrix Rtest = matR[totest]; 
		    Matrix Gtest = matG[totest]; 
		    Matrix Btest = matB[totest];
		    
		    Matrix [] Rbase = matR; 
		    StringBuffer rtest =  chitest(Rbase,Rtest, thevideos, "RED");
		    res[0] = new StringBuffer("\n"+res0);
		    res[0].append(rtest);
		    
		 	Matrix [] Gbase = matG;  
		 	StringBuffer gtest =  chitest(Gbase,Gtest, thevideos,"GREEN");
		 	res[1] = new StringBuffer("\n"+res0);
		 	res[1].append(gtest);
		 	
		 	Matrix [] Bbase = matB; 
		 	StringBuffer btest =  chitest(Bbase,Btest, thevideos,"BLUE");
		 	res[2] = new StringBuffer("\n"+res0);  
		 	res[2].append( btest);
		 	
		 	
		 	return res; 
		    }
	 public StringBuffer chitest(Matrix[]base, Matrix test, List<String> thevid, String col ){
		 
		 double[] tfrm = test.transpose().getArrayCopy()[0];
		 double rowtestotal = 0d;
		 for(double d: tfrm) rowtestotal +=d;
		
		 pvalues = new double[base.length];
		 StringBuffer res = new StringBuffer();
			res.append("\nChiSquare test at significant level= "+
					ChiSquareTest.alpha);
			res.append("\nColor component "+ col + ": ");
		 for(int videoInd=0; videoInd < base.length; ++videoInd){
			 
		     Matrix mm= base[videoInd];
		     String str = thevideos.get(videoInd);
			 double[] coltotal = new double[tfrm.length];
			 double [] exptest = new double[tfrm.length];
			 double df =0;
			 double [] bfrm = mm.transpose().getArrayCopy()[0];
			 double [] expbase = new double[bfrm.length];
			 double rowbasetotal = 0d;
			 for(double d: bfrm) rowbasetotal +=d;
			 double N = rowtestotal + rowbasetotal;
			 double x2=0;
			 for(int c=0; c < coltotal.length; ++c){
				 coltotal[c] = bfrm[c] + tfrm[c]; 
				 exptest[c]=coltotal[c]*rowtestotal/N;
				 expbase[c] = coltotal[c]*rowbasetotal/N;
				 if((bfrm[c]+tfrm[c]) >1.e-5){
					 df++;					 
				 
			     x2 += (bfrm[c]-expbase[c])*(bfrm[c]-expbase[c])/(expbase[c]+1.e-10);
			     x2 += (tfrm[c]-exptest[c])*(tfrm[c]-exptest[c])/(exptest[c]+1.e-10);
				 }
			 
			 }
			 
			pvalues[videoInd]=cern.jet.stat.Probability.chiSquareComplemented(2, x2); 
			res.append("\nVideo "+ str+ "   x2="+ x2+"  df="+ df+"\npvalue= " +pvalues[videoInd]); 
			if ( pvalues[videoInd] <= alpha) {
			
			res.append("\t"+col+ "...Different videos.\n"); 
			}else{ 
				res.append("\t"+ col+"...Same videos.\n"); ; 
			}
		 }
				
		 return res;
		 
	 }
	public static void main(String[]args){
		ChiSquareTest chi = new ChiSquareTest(args);
		StringBuffer[] res = chi.getRes();
		for(StringBuffer buf:res)
			mLog.info(buf.toString());
		
	}
}
