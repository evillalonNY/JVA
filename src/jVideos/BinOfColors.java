/**
 * File		:	BinOfColors.java
 * Author	:	Elena Villalón
 * Contents	:	It creates the Lists with color bins for the RGB components 
 *              of pixels for all video frames.  
 * 				Every List element represents a frame and has an array 
 *              of either red, or green, or blue frequency counts. 
 *              The length of the array is binNum. The array 
 *              elements contain the number of pixels in the frame 
 *              within given ranges of values in the bins and
 *              the maximum color extent (0-255).   
 *               
 * Uses		:   ColorAnalyzer.java         
 */
package jVideos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.swing.JOptionPane;

public class BinOfColors {
	List<Buffer> frameLst;	
	ColorAnalyzer colan; 
	List<int[]> redBinLst = new ArrayList<int[]>(); 
	List<int[]> greenBinLst = new ArrayList<int[]>();
	List<int[]> blueBinLst = new ArrayList<int[]>();
	
/*	 number of divisions of szColor or bin numbers */ 
	private int binNum = 255; 
	int szColor = 256;
	private static Logger mLog = 
        Logger.getLogger(BinOfColors.class.getName());
    private static boolean debug = false;
	
public void setBinNum(int binNum){
	
		this.binNum = binNum; 	
}
public int getBinNum(){
	return binNum; 	
	}
BinOfColors(List<Buffer> frameLst)
{
	if(!debug)
		mLog.setLevel(Level.WARNING);
	this.frameLst= frameLst; 
	int binSize = findBinSize(binNum);
	int cnt = 0;
	for(Iterator<Buffer> i= frameLst.iterator(); i.hasNext();)
	{
		cnt++;
		 
		Buffer frm = new Buffer(); 
		frm = i.next();
		colan = new ColorAnalyzer(frm);
		mLog.info("Frame # " + cnt); 
		createRGBbins(colan,binSize, cnt);
		colan=null; 		
	}
		
}

private int findBinSize(int binNo){
	
    int binSz = szColor/(binNo +1); 
    int binSz0 = binSz; 
    String sz=""; 
	while(szColor%binNo > 0){
		binNo = binNo + 1;
		binSz = szColor/binNo;
	}
	sz= sz + " the bin size is " + binSz; 
	if( binSz0 != binSz)
		JOptionPane.showMessageDialog(null, sz, "Calculation of Bin Size",
				JOptionPane.INFORMATION_MESSAGE);
	
	return binSz; 
	
}


public void createRGBbins(ColorAnalyzer colan, int binsz, int cnt){
 	    int len = szColor/binsz; 
 	    String sz="Wrong calculations for bin size" ;
 	    
 	    if(szColor%binsz > 0)
 	    	JOptionPane.showMessageDialog(null, sz, "Calculation of Bin Size",
 					JOptionPane.INFORMATION_MESSAGE);
 	    
 	    int [] redPxls  = (int[]) colan.getRedPxls();
 	    int [] greenPxls = (int[])colan.getGreenPxls();
 	    int [] bluePxls = (int[]) colan.getBluePxls();
 	    int [] redBins = new int[len];
 	    
 	    
 	    int [] greenBins = new int[len];
 	    int [] blueBins = new int[len];
 	    int index; 
 	    for(int m= 0; m< redPxls.length ; m++)
 	    {
 	    	index = (int) Math.round(redPxls[m]/binsz);  
 	    	redBins[index]++; 
 	    }
 	    if(debug && cnt % 100 == 0 )
 	    printBins(redBins, "RED"); 
 	    
 	   for(int m= 0; m< greenPxls.length ; m++)
	    {
	    	index = (int) Math.round(greenPxls[m]/binsz);  
	    	greenBins[index]++; 
	    }
 	   if(debug && cnt % 100 == 0)
 	    printBins(greenBins, "GREEN");
 	    
 	  for(int m= 0; m< bluePxls.length ; m++)
	    {
	    	index = Math.round(bluePxls[m]/binsz);  
	    	blueBins[index]++; 
	    }
 	  
 	  if(debug && cnt % 100 == 0 )
       printBins(blueBins, "BLUE");
 	
	    redBinLst.add(redBins); 
	    greenBinLst.add(greenBins);
	    blueBinLst.add(blueBins);	    
	    
}
public static void printBins(int[] arr, String color)
{
	mLog.info("Bin array for "+ color + " is " + arr.length); 
	for(int n=0; n < arr.length; n++){
		if(arr[n] > 0)
		mLog.info("color[" + n + "] =  " + arr[n] +" pixels"); 	}
		
}
}
