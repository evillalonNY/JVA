package jnouse;

import jalgo.MatrixOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jalgo.StudentTest;

import cern.colt.list.DoubleArrayList;
import jalgo.*;

public class metricDistance {
	/**
	 * array of video matrices, each matrix has dim=[nofrms][256]
	 */
		
	 List<double[]> videoMat;
	 
	 /** Video matrix to test against array videoMat, dim=[nofrms][256]
	 */
	 double[] mat;
	 
	 
	 /**
	  * error for every column of mat match with corresponding 
	  * columns of matrices videoMat.   
	  * dim=[#videosToTest][256]
	  * 
	  */
	 double[] error; 
	 
	 private static boolean showTest = true;
	 
	 static List<String> thevideos = new ArrayList<String>(); 
	 static int totest = 0;
	 private static String testvid=""; 
	 private static Logger mLog = 
	     Logger.getLogger(StudentTest.class.getName());
	 private boolean debug = false;
	 
	
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
	 
	
	 public metricDistance(List<String> thevid){
		 if(!debug)
			 mLog.setLevel(Level.WARNING);
		 thevideos= thevid;  
	 }
	 
	
	 public metricDistance(List<double[]> v, double[] m, List<String> thevid){
		 this(thevid); 
		 videoMat =v;
		 mat = m; 
		 
		  
	 }
	 public StringBuffer[] errorTest(List<double[]>red, 
			                         List<double[]>green,
			                         List<double[]>blue,int totest){
			String testv = thevideos.get(totest);
			if(!showTest)
			  thevideos.remove(totest);
			testvid = testv;
			StringBuffer res0 = new StringBuffer("Test video  "+ testv);
			StringBuffer[] res = new StringBuffer[3]; 
			String mess1 = "Testing video: " + testv;
		
		    double[] Rtest = red.get(totest);
		    double[] Gtest = green.get(totest);
		    double[] Btest = blue.get(totest);
		    
		    metricDistance rtest =  new metricDistance(red,Rtest, thevideos);
		    res[0] = new StringBuffer();
		    res[0].append("\n"+res0); 
		    res[0].append(rtest.RelativeError("RED"));
		    
		   
		 	metricDistance gtest =  new metricDistance(green,Gtest, thevideos);
		 	 res[1] = new StringBuffer("\n"+res0);
		 	 res[1].append(gtest.RelativeError("GREEN"));
		 	
		 	
		 	metricDistance btest =  new metricDistance(blue,Btest, thevideos);
		 	 res[2] = new StringBuffer("\n"+res0);
		 	res[2].append( btest.RelativeError("BLUE"));
		 	
		 	
		 	return res; 
		    }
	 public StringBuffer RelativeError(String col){
			
			int ncol=	videoMat.get(0).length;
			
			error = new double[mat.length];
			StringBuffer res = new StringBuffer();
			Iterator<double[]> it= videoMat.iterator(); 
			int videoInd=-1;
			while(it.hasNext()){
				videoInd++;
				String str = thevideos.get(videoInd);
				int k=0;
				double[] comp = it.next();
				
		//	double corr = calcuta(comp,mat);
			
			error[videoInd] = calcnorms(comp, mat);
			
				
			
			res.append("Color component "+ col + ": ");
		    res.append("\nVideo "+ str+ "\n= mean relative error  =" +error[videoInd]); 

			}
		
	     return res;
		}
	//calculate correlation between rows of two matrices.
	 /**
	  * @param arrtmp: double array 
	  * @param arrtest: double array
	  * @return double with correlation
	  */
	 public  static double calcuta(double[] arrtmp, double[] arrtest){
	 	
	 	
	 	cern.colt.list.DoubleArrayList testD = new cern.colt.list.DoubleArrayList(arrtest);
	 	cern.colt.list.DoubleArrayList tmpD = new cern.colt.list.DoubleArrayList(arrtmp);
	 	double mntest = cern.jet.stat.Descriptive.mean(testD);
	 	   
	 	double vartest = cern.jet.stat.Descriptive.sampleVariance(testD, mntest);
	 	int nln = arrtest.length; 
	 	double sdtest = Math.sqrt(vartest);
	 		//cern.jet.stat.Descriptive.sampleStandardDeviation(nln,vartest);
	 	double mntmp = cern.jet.stat.Descriptive.mean(tmpD);

	 	double vartmp = cern.jet.stat.Descriptive.sampleVariance(tmpD, mntmp);
	 	int nln2 = arrtmp.length; 
	 	double sdtmp = Math.sqrt(vartmp); 
	 		//cern.jet.stat.Descriptive.sampleStandardDeviation(nln2,vartmp);
	 	
	 	double cov = cern.jet.stat.Descriptive.correlation(testD,sdtest, tmpD,sdtmp); 
	 	
	 	mLog.info("cov is ="+ cov);

	 	return cov; 
	 	}
	 /**
	  * Calculate distance of tow mean frames 
	  * @param comp: double array with mean frame 
	  * @param mat: duble array with mean frame to test
	  * @return double with metrix
	  */
	 public double calcnorms(double[]comp, double[] mat){
		 double normcomp=0;
			double normtest=0;
			double normcomp2=0;
			double normtest2=0;
			
		
			for(int k=0; k<comp.length; ++k){
				  	  
				   normcomp += Math.abs(comp[k]);
				   normtest += Math.abs(mat[k]);
				   normcomp2 += comp[k]*comp[k];
				   normtest2 += mat[k]*mat[k];
				 
				}
			int len = mat.length;
			double error[] = new double[len];
			double normdif=0;
			double del, del2, den;
			double mx =0;
			int cnt=0;
			for(int k=0; k<len; ++k){
			   if(comp[k] >1.e-5 && mat[k]>1.e-5)
				cnt++;	
			   del = (comp[k] - mat[k]);
			   del2 = del*del;
			   den= comp[k] * mat[k]+1.e-10;
			   normdif += Math.abs(del)/Math.sqrt(den);
			   error[k] = del/Math.sqrt(den);
			   
			}
			Arrays.sort(error); 
	    	DoubleArrayList derror = new DoubleArrayList(error);
	    	double mediana = cern.jet.stat.Descriptive.median(derror);
			double rat =normdif/len;
	 
			return rat;
	 }
}
