/**
 * Description: the ANOVA test to find similarities among a group
 * of two or more videos. 
 * Uses Apache Commons Math Library and JVA.jmckoi
 *  
 * evillalon@iq.harvard.edu
 * 
 */
package jalgo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jclient.VideoFile;
import jmckoi.MatrixRetrieve;


import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.OneWayAnovaImpl;

import Jama.Matrix;

public class Anova1 {
	
	/**
	  * Significant level of the two-sided test of statistics
	  */
	 static final double alpha = 0.1;
	 
	 /**group the 256 color values into nobins */
	 private static int nobins = 32; 
	
	/** use nobins or calculate them */
	 private boolean calcnobins = true;
	/**
	  * p.values for every column of mat match with corresponding 
	  * columns of matrices videoMat.  The probability values are 
	  * obtained using the student distribution; 
	  * dim=[#videosToTest][256]
	  * 
	  */
	 double pvalues; 
	 
	 boolean showtest = true; 
	 
	 static List<String> thevideos = new ArrayList<String>(); 
	 static int totest = 0;
	
	
	//Results of test after completing test
	 private StringBuffer res[]; 
	 private static Logger mLog = 
	     Logger.getLogger(Anova1.class.getName());
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
	
	 public  StringBuffer[] getRes(){
		 return res;
	 }
	
	 public  void setRes(StringBuffer[] r){
		 res=r;
	 } 
	 public Anova1(String args[])throws MathException{
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
		   
		    res = meanDifTest(vstore, totest);
		   
		   
		    String toprint= 
		    "\nAnova test at significant level= " + Anova1.alpha;
		   
		    StringBuffer sim =new StringBuffer(toprint);  
		    
		    mLog.info(toprint);   
	    
	    }
		
			
			
		
	 public StringBuffer[] meanDifTest(MatrixRetrieve vstore,int totest) 
	 throws MathException{
		 
			String testvid = thevideos.get(totest);
			if(!showtest)
			  thevideos.remove(totest);
			
			
			String mess1 = "Testing video: " + testvid;
			//JOptionPane.showMessageDialog(null, mess1, "Test videos",
				//	JOptionPane.INFORMATION_MESSAGE);
			
		    Matrix [] matG = MatrixRetrieve.studentMats(vstore.getMeangreen()); 
		    Matrix [] matR = MatrixRetrieve.studentMats(vstore.getMeanred());
		    Matrix [] matB = MatrixRetrieve.studentMats(vstore.getMeanblue());
		    
		    res = new StringBuffer[3];
		    res[0] = new StringBuffer("\n");
		    StringBuffer rtest =  anovatest(matR,thevideos, "RED");
		    res[0].append(rtest);
		    
		    res[1] = new StringBuffer("\n");
		 	StringBuffer gtest =  anovatest(matG,thevideos,"GREEN");
		 	res[1].append(gtest);
		 	
		 	res[2] = new StringBuffer("\n");  
		 	StringBuffer btest =  anovatest(matB,thevideos,"BLUE");
		 	res[2].append( btest);
		 	
		 	
		 	return res; 
		    }
	 public StringBuffer anovatest(Matrix[]base, List<String> thevid, String col )
	 throws MathException{
		 
		 double [][] arr = new double[base.length][256];
		 
		 double M= 0; 
		
	     int cnt=0;
		 for(Matrix mm : base){
		   arr[cnt] = mm.transpose().getArrayCopy()[0];
		   cnt++;
		 }
		 int sz = base.length;
		 
		 if(calcnobins)
		     nobins = howmanybins(arr, sz);
		 mLog.info("Number of bins ...."+nobins);
		 double[][] basearr=groupbins(arr,sz);
		 Collection<double[]> data = new ArrayList<double[]>();
		 for(int n= 0; n < sz; ++n)
			 data.add(basearr[n]);
		 OneWayAnovaImpl anova = new OneWayAnovaImpl();
		 double fval =anova.anovaFValue(data);
		 double pvalues = anova.anovaPValue(data);
		 
		 StringBuffer res = new StringBuffer();
			res.append("\nanova test at significant level= "+
					Anova1.alpha);
			res.append("\nColor component "+ col + ": ");
		
			res.append("\nMean frames pvalue= "+pvalues);
			//res.append("\nFvalues= "+fval);
		
		 
				
		 return res;
		 
	 }
	 /**
	  * <p> Calculate for each mean-frame the number of bins that 
	  * are non-zero and return the max for all mean-frames
	  * </p>
	  * @param basearr double bi-dimensional array with the mean-frames
	  * @param ln integer with number of mean-frames (videos)
	  * @return int the no bins that are not zero in the 0-256 colors
	  */
	 public int howmanybins(double[][]basearr, int ln){
		 
	     int[] cnt = new int[ln];
	     int n=0;
		 for(n=0; n < ln;++n){
				cnt[n]=0;;
				 for(int c=0; c < 256;++c){
					 if(basearr[n][c] >= 0.5) cnt[n]++;
			 }
				 
		 }
		 int res=nobins;
		 for(n=0; n < ln; ++n)
		 if(cnt[n] > res)res=cnt[n];
		 if(res <= 8) return 8; 
		 if(res > 8 && res <= 16) return 16;
		 if(res > 16 && res <= 32) return 32;
		 if(res > 32 && res <= 64) return 64;
		 if(res > 64 && res <= 128) return 128;
		 if(res > 128) return res;
		
		 return res;
	 }
	 private double[][] groupbins(double[][]basearr,int len){
		 
		 int sz =256/nobins;
		 double [][] basebin = new double[basearr.length][nobins];
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
		
		
		 return basebin;
		 }
	public static void main(String[]args) throws MathException{
		 if (args.length == 1){ //reading from file
		   	 args[0].trim(); 
		   	 thevideos = VideoFile.readFile(args[0]);
		   	 MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt", true, true); 
		   	 String [] argsf = thevideos.toArray(new String[thevideos.size()]);  
		   	 vstore.retreiveMat(argsf);
		   	 int cnt = argsf.length;
		 	 Anova1 chi = new Anova1(argsf);
			 StringBuffer[] res = chi.getRes();
			 for(StringBuffer buf:res)
				mLog.info(buf.toString());
			 return; 
		    }
		Anova1 chi = new Anova1(args);
	    
		for(StringBuffer buf:chi.getRes())
			for(int n=0; n < 3; ++n)
				mLog.info(buf.toString());
			
		
	}
}
