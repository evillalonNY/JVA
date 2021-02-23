/**
 * File		:	SVDVideoTest.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	: Uses the results of SVDVideo.java constructing the 
 *            sub-matrices of rank 1 or principal components 
 *            for the RGB matrices of videos. 
 *            For each video and color, adds the sub-matrices rank 1,and 
 *            weight their contribution with corresponding singular values.
 *            Calculates the weighted mean  and uses the StudentTest.java
 *            to obtain the pvalues and significant levels to establish 
 *            similarity of video matrices.
 *            It is an accurate and lengthy algorithm. May be in C or C++
 *            
 *             
 * Uses: SVDVideo.java, SVDTets.java
 *                                 
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

public class SVDVideoTest {
	List<String> thevideos = new ArrayList<String>();
	//from array of videos find videos similarities to index of array 
	private static  int totest =0;
	//the cut-off for singular values ratio to first component
	private static double thresh = 0.1; 
	//Regardless of the ratio or value of thres take only 
	//the first principal component. 
    private static boolean FPC = true; //pnly the largest PC
	//how many videos we want to compare Memory heap is a problem 
	private static final int LIMITVID = 2; 
	private static Logger mLog = 
        Logger.getLogger(SVDVideoTest.class.getName());
    private boolean debug = false; 
	private static StringBuffer res; 
	public static StringBuffer getRes(){
		return res; 
	}
	 public SVDVideoTest(){
		 if(!debug)
			 mLog.setLevel(Level.WARNING);
	 }
	 public SVDVideoTest(List<String> thevideos){
		 this();
		 this.thevideos = thevideos; 
		 
	 }
	 public SVDVideoTest(String[] args){
		    this();
			List<String>thevideos =  new ArrayList<String> ();
			mLog.severe("SVDVideoTest "+args.length); 
			String [] argsf  = new String[LIMITVID];
			
			 int cnt = args.length;
			 //videos are input in a file
		    if (args.length == 1){ //reading from file
		   	 args[0].trim(); 
		   	 thevideos = VideoFile.readFile(args[0]);
		   	 List<String> tmp = new ArrayList<String>();
		   	 tmp.addAll(thevideos); 
		   	 thevideos.clear(); 
		   	 if(tmp.size()> LIMITVID){
		   		for(int k=0; k < LIMITVID; ++k){
		   		String ff =tmp.get(k);
		   		thevideos.add(ff);
		   		}
		   		prUsage("We only test first " + LIMITVID + " videos\n" +
		   				"test is = " + thevideos.get(totest)); 
		   		
		   	 }
		   	argsf	= thevideos.toArray(new String[thevideos.size()]);  
		    }
		   	
		   
//		  videos are input as arguments of main
			if(args.length > 1 ) {
				for(int k=0; k < LIMITVID; ++k){
					argsf[k] = args[k];
				thevideos.add(argsf[k]); 
				}
			 }
		   	
			 MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt");
		   
		   	 cnt = argsf.length; 
			 vstore.retreiveMat(argsf);
		    List<Matrix> green = vstore.getGreenvid();
		    List<Matrix> red = vstore.getRedvid();
		    List<Matrix> blue = vstore.getBluevid();
		    SVDVideo svdvid = new SVDVideo(thevideos); 
		    svdvid.rnkFrmTest(red,green, blue);
		    SVDVideoTest vid = new SVDVideoTest(thevideos); 
	       
	        StringBuffer testing = vid.process(svdvid.getRrnk(), svdvid.getGrnk(), svdvid.getBrnk(),
	        		svdvid.getRDiag(), svdvid.getGDiag(), svdvid.getBDiag()); 
		
	       int[] lambB = svdvid.getBlamb();
	       int[] lambR = svdvid.getRlamb();
	       int[] lambG = svdvid.getGlamb();
	       String str=""; 
	       for(int n=0; n < lambB.length; ++n)
	    	   str+=thevideos.get(n) + "\tRanks "+
	    	   lambR[n] + "; "+lambG[n] +"; " +lambB[n]+ "\n";  
		   String vidtotest = thevideos.get(totest);
		   testing.append(str); 
		   this.res = testing;
		  
		   
	 }
	 /** Takes the list with the rank sub-matrices, every element is a video
	   * and the array has the sub-matrices with length equal to the 
	   * rank of the video matrix RGB. Creates a list with matrices of rows=256
	   * and 1 column after averaging over all   
	  */
	  public  List<Matrix> FrobeniusNorm(List<Matrix[]> Brnk, List<Matrix> Bdiag){
		  //calculate the mean 
		 List<Matrix> apprx = new ArrayList<Matrix>(); 
		  int cnt = -1; 
		  for(Matrix[] m : Brnk){
			double dweigth = 0; 
		      cnt++; 
		    
		    //for each video add sub-matrices  
			  Matrix tot = new Matrix(m[0].getRowDimension(), m[0].getColumnDimension());
			  double lamb0 = Bdiag.get(cnt).getArrayCopy()[0][0]+1.e-5;
			   
			  for(int n=0; n < m.length; ++n){
				  if(FPC && n>0) break; //only first Principal Component  
				  double lamb = Bdiag.get(cnt).getArrayCopy()[0][n];
				  dweigth += lamb/lamb0; 
				  if(lamb/lamb0 <= thresh)
					  break; 
				 
				  m[n] = m[n].timesEquals(lamb/lamb0); 
			      mLog.info("Rows = " + m[n].getRowDimension()+ 
						  "Cols "+ m[n].getColumnDimension()); 
			      tot = tot.plusEquals(m[n]); 
			       
			  }  
		
			apprx.add(tot);   
		  }
		  return apprx;
	  }
		  //calculate the mean 
		  public List<Matrix> meansvdvectors(Matrix tot, double dweigth)
		  {  
		   List<Matrix>  sumrnkB= new ArrayList<Matrix>(); 
           double[][] mn = new double[1][tot.getColumnDimension()]; 
          for(int cl=0; cl<tot.getColumnDimension(); ++cl ){
        	  mn[0][cl] = 0.0; 
          dweigth+=1.e-5;  
          for(int rw=0; rw<tot.getRowDimension(); ++rw ) 
        	 mn[0][cl] = tot.getArrayCopy()[rw][cl]/dweigth +mn[0][cl]; 
   		     Matrix aveg = new Matrix(mn);
   		     aveg = aveg.transpose(); 
             sumrnkB.add(aveg); 
           
          } 
          return(sumrnkB); 
   		   } 		  
	public StringBuffer process( List<Matrix[]> Rrnk,  
	 List<Matrix[]> Grnk, List<Matrix[]>  Brnk, List<Matrix> RDiag,  
	 List<Matrix> GDiag, List<Matrix>  BDiag){
		List<Matrix> redSVDmean = this.FrobeniusNorm(Rrnk, RDiag); 
		List<Matrix> greenSVDmean= this.FrobeniusNorm(Grnk, GDiag);
		List<Matrix> blueSVDmean= this.FrobeniusNorm(Brnk, BDiag);
		int vv =0; 
		String tmp = "Test video: "+ thevideos.get(totest) +"\n";
		tmp+= "Frobenius norms: \n";
		
		StringBuffer res = new StringBuffer(tmp);
		
		for(vv=0; vv < redSVDmean.size(); ++vv){
	
	    res.append(thevideos.get(vv)+"\n"); 	
		Matrix basisR [] = {redSVDmean.get(vv)}; 
		ProjSVD pR = new ProjSVD(basisR[0], redSVDmean.get(totest));
		double [] projR = pR.project();
		double fbnormR = projR[0]; 
		res.append("Red " + fbnormR+"\n"); 
		
	
	    Matrix basisG [] = {greenSVDmean.get(vv)}; 
	    ProjSVD pG = new ProjSVD(basisG[0], greenSVDmean.get(totest));
		double [] projG = pG.project();
		double fbnormG = projG[0]; 
		res.append("Green " + fbnormG+"\n");  
		
	    Matrix basisB [] = {blueSVDmean.get(vv)}; 
	    ProjSVD pB = new ProjSVD(basisB[0], blueSVDmean.get(totest));
		double [] projB = pB.project();
		double fbnormB = projB[0]; 
		res.append("Blue " + fbnormB+"\n\n"); 
		 
		
		}
		mLog.info(res.toString()); 
		return res; 
	}
	 static void prUsage(String mess) {
	    	String mess2 =  "Wrong number of frames in MedianTest";
	    	JOptionPane.showMessageDialog(null, mess, "Median Failed",
					JOptionPane.ERROR_MESSAGE);
		mLog.warning(mess+"");
		
	    } 
	public static void main(String[] args) throws SQLException{
		if(args.length<=0){
			prUsage("StudentTest provide the videos to test"); 	
			
			return;
			}
		SVDVideoTest driver = new SVDVideoTest(args);
		mLog.info(""+driver.getRes().toString());
	   return; 
	   }
}
