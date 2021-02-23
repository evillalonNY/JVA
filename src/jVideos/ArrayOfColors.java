/**
 * File		:	ArrayOfColors.java
 * Author	:	Elena Villalón
 * Contents	:	It creates the Lists with the RGB color components 
 *              of every pixel for either the video frames.  
 *              Every list element contains the red, green, blue  
 *              components of all pixels in one frame.
 *              We have as many elements in the list 
 *              as frames in the video. 
 *              This class is not very useful because the number  
 *              of pixels in any frame is so large that becomes 
 *              impractical to store all of them; it is used to draw 
 *              the histograms of colors. Instead we work
 *              primarily with class BinOfColors that stores 
 *              the metadata of the frames pixels.  
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


public class ArrayOfColors {
	List<Buffer> frameLst;	
	ColorAnalyzer colan; 
	List<int[]> redPxlLst = new ArrayList<int[]>(); 
	List<int[]> greenPxlLst = new ArrayList<int[]>();
	List<int[]> bluePxlLst = new ArrayList<int[]>();
	private static Logger mLog = 
        Logger.getLogger(ArrayOfColors.class.getName());
    private static boolean debug = false;
// add every pixel value for all frames to the lists of red, green, blue. 
private void debug(){
	if(!debug)
		mLog.setLevel(Level.WARNING);
}
ArrayOfColors(List<Buffer> frameLst)
{
	debug();
	this.frameLst= frameLst; 
	 
	for(Iterator i= frameLst.iterator(); i.hasNext();)
	{
        Buffer frm = new Buffer(); 
		frm = (Buffer) i.next();
		colan = new ColorAnalyzer(frm);
		createRGBLst(colan);
		colan=null; 
		frm = null; 
		
	}
		
}

//add every pixel of only one frame to the lists red, green, blue

ArrayOfColors(List<Buffer> frameLst, int frmNo){
debug();
int cnt=0;

for(Iterator<Buffer> i= frameLst.iterator(); i.hasNext();)
{
    Buffer frm = new Buffer(); 
	frm = i.next();
    if(cnt == frmNo)
    {
    	colan = new ColorAnalyzer(frm);
    	createRGBLst(colan);
	    colan=null;
    }	
	cnt++; 
}
}

/**
 * add every pixel value red green blue of the frames whose position
 * numbers are between low and up. 
 */ 
ArrayOfColors(List<Buffer> frameLst, int lowFrm, int upFrm){
	debug();
	int cnt=0;

	for(Iterator<Buffer> i= frameLst.iterator(); i.hasNext();)
	{
        Buffer frm = new Buffer(); 
		frm = (Buffer) i.next();
	    if(cnt >= lowFrm && cnt <= upFrm)
	    {
	    	colan = new ColorAnalyzer(frm);
	    	createRGBLst(colan);
		    colan=null;
	    }	
		cnt++; 
	}	
	
}
public void createRGBLst(ColorAnalyzer colan){
	int [] redVal =  (int[]) colan.getRedPxls();
    int [] greenVal =  (int[]) colan.getGreenPxls();
    int [] blueVal = (int[]) colan.getBluePxls();
	redPxlLst.add(redVal); 
	greenPxlLst.add(greenVal);
	bluePxlLst.add(blueVal);
	    
	  for(int n=0; n <redVal.length; n++){
	    if( n < 5000 && n > 4500 ){	
	    mLog.info("For pixel number "+ n ); 
	    mLog.info(redVal[n]+"");
	    mLog.info(greenVal[n]+"");
	    mLog.info(blueVal[n]+"");
	    }
	    }
	   
	 
}
}



