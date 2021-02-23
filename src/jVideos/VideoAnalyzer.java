/**
 * File		:	VideoAnalyzer.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	The main method takes a video for analysis.   
 *              It gets the frames, analyzes the RGB color
 *              components, and stores bins of color values in ArrayList
 *              collections. Every list element correspond to a frame, 
 *              and contains the counts of pixels in the frame 
 *              of a given color value (0-255) for each of red, green, blue. 
 *              We have as many elements in each list as frames in the video. 
 *              It may also draw RGB histograms for a single frame. 
 *              It has a static internal class FrameMatrix that processes 
 *              the ArrayLists of frames and convert them into matrices 
 *              for each of red, green, blue.  The matrices have 
 *              dimensions with number of rows equal to number 
 *              of frames in the video and number 
 *              of columns the range of color values, i.e. 256.    
 *              Instantiates object of class Video with the metadata. 
 *              
 * Uses		:   FrameAccess, FrameMatrix, ColorAnalyzer, BinOfColors, Video        
 */

package jVideos;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.MediaLocator;
import javax.swing.JOptionPane;

import jmckoi.PopulateTbVideo;
import jstat.BinToHisto;
import jstat.HistoFrame;
import Jama.Matrix;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;


public class VideoAnalyzer extends FrameAccess{
	
	static final long serialVersionUID = 42L;
	List<Buffer> frameLst; 
	List<int[]> redBinLst; 
	List<int[]> greenBinLst;
	List<int[]> blueBinLst;
	
	int frmNum = 100; // the frame number 
	ArrayOfColors RGBcolors; 
    BinOfColors binRGB;
    private boolean colt = true;
    private static Logger mLog = 
	    Logger.getLogger(VideoAnalyzer.class.getName());
	private boolean debug = false;
	public VideoAnalyzer(){
		super(); 
		if(!debug)
			mLog.setLevel(Level.WARNING);
	}
	public boolean getColt(){
		return colt;
	}
	public void setColt(boolean c){
		colt = c; 
	}
	//constructor calls from FrameAccess.java 
	public VideoAnalyzer(List<Buffer> frameLst, String desc, 
			long numf, float totdur, String key)
	{
		this();
		this.frameLst = frameLst;
		
		this.fillBinsRGB(frameLst); 
		FrameMatrix frmMat = new FrameMatrix(binRGB.getBinNum()+ 1);
		
		/**For debugging purposes tell me what is in the list*/
		mLog.info("Elements Red bins for all frames."); 
		loopLst(redBinLst); 
		mLog.info("Elements Green bins for all frames."); 
		loopLst(greenBinLst);
		mLog.info("Elements Blue bins for all frames."); 
		loopLst(blueBinLst);
		/** end of debugging information */
		
		frmMat.frameMatrix(redBinLst, greenBinLst, blueBinLst);
		Matrix red =  frmMat.redMat;
		
		SparseDoubleMatrix2D red2D = 
			new SparseDoubleMatrix2D(frmMat.redMat.getArrayCopy()); 
		SparseDoubleMatrix2D green2D = 
			new SparseDoubleMatrix2D(frmMat.greenMat.getArrayCopy());
		SparseDoubleMatrix2D blue2D = 
			new SparseDoubleMatrix2D(frmMat.blueMat.getArrayCopy()); 
		
		Matrix green = frmMat.greenMat;
		Matrix blue = frmMat.blueMat;
		if(debug)
	     red.print(1, 0); 
		Video vid = null; 
		if(!colt)
		 vid = new Video(red, green, blue,desc,numf,totdur);
		else
		 vid = new Video(red2D, green2D, blue2D,desc,numf,totdur);
		vid.setPrimaryKey(key); 
		
		if(!colt)
			new PopulateTbVideo(vid);
		else
	    new PopulateTbVideo(vid, true); 
		
	    mLog.info("" + red2D.toString());
		mLog.info("Get PK for the video... "
				+ (vid.getVideoPK()).getPrimaryKey()); 
		if(debug){
		green.print(1,0);
		blue.print(1, 0);
		} 
		this.histoFromBin(); 
		System.exit(0); 
	}
	/**runs the video url and plays it, and if play = false 
	* then extract the data and save it in database
	*/
	 public VideoAnalyzer (String url, boolean play) {
			FrameAccess video = new VideoAnalyzer();     
	        video.setPlayonly(play);
			MediaLocator ml;

				if ((ml = new MediaLocator(url)) == null) {
					String mess1 = "Cannot build media locator from: " + url;
					JOptionPane.showMessageDialog(null, mess1, "Failed URL",
							JOptionPane.ERROR_MESSAGE);
				    System.exit(0);
				}
				 
	            url = checkURL(url);
				video.keyURL = url.trim(); 
	            boolean bool = video.open(ml); 
				if (!bool)
				    System.exit(0);
				
			    } 
		 
	public int getFrmNum(){
		return frmNum;
		
	}
	public void setFrmNum(int frmNum){
	    this.frmNum = frmNum;
		
	}
	public void loopLst(List<int[]> color){
		String res=""; 
		for(int [] lst: color){
			res = ""; 
			for(int n =0; n < lst.length; ++n){
				if( lst[n] > 0)
			res = res  +"lst[" + n + "] = " + lst[n] + "\t"; 
			}
			mLog.info(res); 	
		}
		
	}
	static class FrameMatrix {
		int szcolor; 
		Matrix redMat;
		Matrix blueMat;
		Matrix greenMat;
		
		public FrameMatrix(int szcolor)
		{
			this.szcolor = szcolor; 
		}
		public void frameMatrix(List<int[]> red, 
				List<int[]> green, List<int[]> blue){
			this.redMat = colorMatrix(red);
			this.greenMat = colorMatrix(green);
			this.blueMat = colorMatrix(blue); 
			displayDim(redMat, "red"); 
			displayDim(greenMat, "green");
			displayDim(blueMat, "blue"); 
		}
		public Matrix colorMatrix(List<int[]> color){
			int ln = color.size();
			double array[][] = new double[ln][szcolor];
		
		    int cnt = 0;
		     int lst[]; 
		   for(Iterator<int[]> i= color.iterator(); i.hasNext();){
		    lst = i.next();
		    double[] lstd = new double[szcolor];    	
				 
				for(int n =0; n < szcolor; ++n){
					lstd[n] = (double) lst[n]; 
				array[cnt] = lstd;
				}
			    	cnt++;
			}
			Matrix colorMat = new Matrix(array); 
			
			return colorMat; 
			}
		static public void displayDim(Matrix redMat, String str){
			String mess = "For "+ str + " # columns = "; 
			mess = mess + redMat.getColumnDimension(); 
			mess = mess + " # rows = ";                     
			mess = mess + redMat.getRowDimension();
			mLog.info(mess); 
			
		}
		}
	
	 public static void main(String [] args) {
		 
			if (args.length == 0) {
			    prUsage();
			    System.exit(0);
		 	}

			String url = args[0];
			if (url.indexOf(":") < 0) {
			    prUsage();
			    System.exit(0);
			}
            if(args.length >=2  && 
            	args[1].trim() == String.valueOf(true))
            	new VideoAnalyzer(url,true);  
            else
            	new VideoAnalyzer(url,false); 
			
		    } 
	 
	 private static String checkURL(String url){
		 url = url.trim();
		 String http =url.substring(0, 4);
		 if(http.contentEquals("http"))
				 return url;
		 StringTokenizer tok = new StringTokenizer(url, "\\");
         int n = tok.countTokens(); 
         String kurl = url;
         
         while(tok.hasMoreTokens())
          	
         	kurl = tok.nextToken().toString(); 
        
         if(!kurl.startsWith("file:videos/"))
         kurl = "file:videos/".concat(kurl); 			 
		 
		 return kurl;
	 }
	 public void fillBinsRGB(List<Buffer> framesLst){
	    	
	    	binRGB = new BinOfColors(framesLst); 
	    	blueBinLst = binRGB.blueBinLst;
	    	redBinLst = binRGB.redBinLst;
	    	greenBinLst = binRGB.greenBinLst;
	    			 
	    
	    }	
	
	    public void drawHisto(int frmNum, List<Buffer> frameLst){
	    	RGBcolors= new ArrayOfColors(frameLst, frmNum);
	    	int cnt=0; 
	    	for(Buffer frm: frameLst){
	    		
	    	if( cnt == frmNum){
	    	ColorAnalyzer colan = new ColorAnalyzer(frm);
	    	int [] red = colan.getRedPxls();
	    	int [] green = colan.getGreenPxls();
	    	int [] blue = colan.getBluePxls();
	    
	    	new HistoFrame(red, green, blue);
	    	break; 
	    	}
	    	cnt++; 
	    	}
	    }

     public void histoFromBin(){
       int cnt = 0;
	   int[] blue={0}; 
	   int [] green={0};
	   int [] red={0}; 
	   
	   for(int[] cm: blueBinLst){
	    if(cnt == frmNum)
	      blue = cm;
	    cnt++; 
	   }
	   cnt = 0; 
	    for(int[] cm: greenBinLst){
	     if(cnt == frmNum)
	    	green = cm;
	     cnt++; 
	    }
	    cnt = 0; 
	    for(int[] cm: redBinLst){
	     if(cnt == frmNum)
	    	red = cm;
	     cnt++; 
	    }
	    new BinToHisto().BinToPxl(red, green, blue);
     }
		    static void prUsage() {
		    	String mess2 =  "Usage: java FrameAccess <url>";
		    	JOptionPane.showMessageDialog(null, mess2, "Failed URL",
						JOptionPane.ERROR_MESSAGE);
			System.err.println(mess2);
		    }
		    
}
