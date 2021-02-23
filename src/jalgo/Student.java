/**
	 * File		:	Student.java
	 * 
	 * Author	:	Elena Villalón
	 * 
	 * Contents	:	Calculates for Collection of videos the StudentTest
	 *              for each of the video and the video 'totest', which is one 
	 *              index in the Collection. Those videos that 
	 *              have significant levels larger than alpha as defined in 
	 *              StudentTest.java are may be similar to the test video.
	 *              Because the Student distribution is base on 
	 *              normality of the data we perform another test among those 
	 *              videos that are similar.  We may also calculate the rank of the 
	 *              matrices for the test video and all others similar to it.
	 *             
	 *        
	 * Use: StudentTestPaired.java, SVDMat.java       
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

import org.apache.commons.math.MathException;

import Jama.Matrix;

public class Student {
	private List<String> thevideos = new ArrayList<String>();
	private static final int RED =0;
	private static final int GREEN =1;
	private static final int BLUE =2;
	static boolean [][] signLevelG;
    static boolean [][] signLevelB;
    static boolean [][] signLevelR;
    static private int totest =0; //index of array to test against other videos
    static boolean matProj=false; 
    private boolean weight = true; 
    private boolean calculateRnk = false; 
  
    //Results of test after completing main
    private static StringBuffer res[]=  new StringBuffer[4] ; 
    private static Logger mLog = 
        Logger.getLogger(Student.class.getName());
    private boolean debug = true;
    
    public void setTotest(int t){
    	totest =t; 
    }
    public void setmatProj(boolean b){
    	matProj =b; 
    }
    public static StringBuffer[] getRes(){
    	return res; 
    }
    public void setWeight(boolean b){
    	weight = b; 
    }
    public Student(boolean b){
    	if(!debug)
    		mLog.setLevel(Level.WARNING);
    	weight=b;
    }
    public Student(String args[], boolean wght) throws MathException{
    	if(!debug)
    		mLog.setLevel(Level.WARNING);
    	//true is for the mean otherwise it is the median
    	MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt", wght, true);
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
	    StudentTestPaired driver = new StudentTestPaired(pass, totest, wght);
	    try{
	    StringBuffer r[] = driver.meanDifTest(vstore, true, totest);
	    for(int k=0; k<3; ++k)
	    this.res[k]=r[k];	
	    }catch(MathException err){
	    	
	    }
	     
	    String rnkString;
	    
	    signLevelG = StudentTestPaired.getSignfLevelGreen();
	    signLevelB = StudentTestPaired.getSignfLevelBlue();
	    signLevelR = StudentTestPaired.getSignfLevelRed();
	    vstore.allFrmTest(vstore); 
	    Matrix [] blue = vstore.getMatB();
	    Matrix [] green = vstore.getMatG();
	    Matrix [] red =  vstore.getMatR();
	    if(calculateRnk)
	    similar = calculateSimRnk(testvid, red, green, blue);
	    
	    
	    String toprint="\n\nTest video: "+ testvid + 
	    "\nStudent test at significant level= " + StudentTestPaired.alpha;
	    if(calculateRnk){
	    String mess = "\nRGB video matrices with same ranks as test video matrices are: \n";
        toprint =toprint + mess;
	    }
	    StringBuffer sim =new StringBuffer(toprint); 
	    if(calculateRnk){
	    for(String str: similar){
	    	toprint+=str+"\t";
	    	sim.append("\n"+str); 
			
	    }
	    res[3] = sim;
	    }else
	    	res[3]= new StringBuffer("\n\nNo rank calculations performed");
	    
	 mLog.info(toprint);   
    
    }
	public  List<String> calculateSimRnk(String testvid,Matrix [] red,
			Matrix [] green, Matrix [] blue){
		
 
        Matrix btest = blue[totest];
        Matrix rtest = red[totest];
        Matrix gtest = green[totest];
        Matrix svdbtest=btest;
        Matrix svdrtest=rtest;
        Matrix svdgtest=gtest;
        
        int [] rnktest = new int[3]; 
        boolean svd=false; 
        boolean [] ind = new boolean[thevideos.size()];
        boolean flag = false; 
        int cnt=-1; 
        
        for(int nn=0; nn <thevideos.size()-1;++nn ){
        	cnt++; 
         if (nn == totest) {
        	ind[cnt]=true; 
        	 continue; 
         }
        	boolean mismo = signLevelR[cnt][0] && signLevelG[cnt][0] && signLevelB[cnt][0];
        
        	if(mismo) {
        		svd = true;
        		ind[cnt]= true;
        		if(!flag){
            	SVDMat bt =new SVDMat(btest);
        		rnktest[BLUE] =  bt.rnk; //btest.rank();
        		svdbtest = bt.rnkBase; 
        		SVDMat rt = new SVDMat(rtest);
        		rnktest[RED] = rt.rnk; //rtest.rank();
        		svdrtest = rt.rnkBase; 
        		SVDMat gt = new SVDMat(gtest);
        		rnktest[GREEN] = gt.rnk; //gtest.rank();
        		svdgtest = gt.rnkBase; 
        		flag = true; 
        		}
        	}
        }
        List<String> similarurl = new ArrayList<String>(); 
        if(!flag){
        	prUsage("No similar videos found");
            return(similarurl);
        }
        Matrix[]redSVD = new Matrix[thevideos.size()-1];
        Matrix[]greenSVD = new Matrix[thevideos.size()-1];
        Matrix[]blueSVD = new Matrix[thevideos.size()-1];
        cnt=-1; 
		for(int nn=0; nn <thevideos.size();++nn ){
		cnt++; 
			   
			   if(!ind[cnt]|| nn == totest) continue; 
			   if(signLevelR[cnt][0]){
				Matrix tmpr = new Matrix(red[cnt].getArrayCopy());
				SVDMat rt = new SVDMat(tmpr);
				redSVD[cnt]= rt.rnkBase; 
				if( rt.rnk != rnktest[RED])
					signLevelR[cnt][0] = false;//change to non-similar if not same rank
			   }
				
			   
			   if(signLevelG[cnt][0]){
				   Matrix tmpg = new Matrix(green[cnt].getArrayCopy());
					SVDMat gt = new SVDMat(tmpg);
					greenSVD[cnt]= gt.rnkBase; 
					if( gt.rnk != rnktest[GREEN])
						signLevelG[cnt][0] = false;
					
				   }
			   if(signLevelB[cnt][0]){
				   Matrix tmpb = new Matrix(blue[cnt].getArrayCopy());
					SVDMat bt = new SVDMat(tmpb);
					blueSVD[cnt]= bt.rnkBase; 
					if( bt.rnk != rnktest[BLUE])
						signLevelB[cnt][0] = false;
					
				   }
			   
		   }
		
		cnt=0; 
		for(int nn=0; nn <thevideos.size();++nn ){
			
			boolean mismo = signLevelR[cnt][0] && signLevelG[cnt][0] && signLevelB[cnt][0];
			cnt++; 
			if(mismo){
				String url = thevideos.get(nn);
			    similarurl.add( url); }
		}
	
	
		return similarurl; 
	}
		
		
		
	
		 public static void prUsage(String mess) {
		    	String mess2 =  "Wrong number of frames in MedianTest";
		    	JOptionPane.showMessageDialog(null, mess, "Median Failed",
						JOptionPane.ERROR_MESSAGE);
			mLog.warning(mess);
		
		    }
	public static void main(String[] args) throws SQLException, MathException{
		if(args.length<=0){
			prUsage("StudentTest provide the videos to test"); 	
			
			return;
			}
		//Assume that we have weighted means
		Student sv =new Student(true);
		
		Student driver = new Student(args, sv.weight); 
		for(int n=0; n <driver.res.length; ++n)
		mLog.info(driver.res[n].toString()+""); 
		return; 
}
}
