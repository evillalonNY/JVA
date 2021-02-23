/**
 * File		:	ColorAnalyzer.java
 * Author	:	Elena Villalón
 * Contents	:	It analyzes the RGB color components for 
 * 				individual video frames and for every pixel.
 */
 
package jVideos;
import java.awt.image.DirectColorModel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;

public class ColorAnalyzer {
	
	private Buffer frmVideo; //input to class
	private DirectColorModel dcm;  // from datint
	private int [] datint;  //data array of numbers for video frame
	private int [] redPxls; //red pixels values from datint
	private int [] greenPxls; //green pixels values 
	private int [] bluePxls;  //blue pixels values 
	private int[] alphaPxls; //alpha (transparency) values
	
	private static Logger mLog = 
        Logger.getLogger(ColorAnalyzer.class.getName());
    private static boolean debug = false;
	public ColorAnalyzer(Buffer frm) {
	 if(!debug)
		 mLog.setLevel(Level.WARNING);
     this.frmVideo = frm;
     datint = analyzeFrame(frmVideo.getData());
     Format ff = frmVideo.getFormat();
     getRGBPixels(ff); 
	}
	
	public int[] getDatInt(){
		return datint; 
	}
	public int[] getRedPxls(){
		return redPxls; 
	}
	
	public int[] getGreenPxls(){
		return greenPxls; 
	}
	public int[] getBluePxls(){
		return bluePxls; 
	}

	public int[] getAlphaPxls(){
		return alphaPxls; 
	}
	
	public DirectColorModel getDcm()
	{ return dcm;
	
	}
	public Buffer getFrmVideo(){
		return frmVideo; 
	}
	public void setFrmVideo(Buffer frmVideo){
		this.frmVideo= frmVideo; 
	}

/**
 * Input the data from frmVideo and 
 * creates the array of integers.
 */	
	 public int[] analyzeFrame(Object obj)
	{
		int [] datint = null;
		byte[] datbyte;
		short[] datshort; 
		try{
			if(obj instanceof int[]){
				datint = (int[]) obj;
			 
			}else if (obj instanceof byte[]){
				
				datbyte = (byte[]) obj;
				datint = new int[datbyte.length]; 
				for(int i=0; i < datbyte.length; ++i)
					datint[i] = (int) datbyte[i];
				
			}else if (obj instanceof short[]){
				
				datshort = (short[]) obj;
				datint = new int[datshort.length]; 
				for(int i=0; i < datshort.length; ++i)
					datint[i] = (int) datshort[i];
			}else 
				throw new InvalidDataTypeException("Supported data is short, int, byte"); 
		}catch(InvalidDataTypeException o){
			mLog.severe(o.getMessage()); 
			o.printStackTrace(); 
		}
		return( datint); 
		}
	/**
	 * Input the format from frmVideo and creates the dcm
	 * and arrays of RGB color components for every pixel.   
	 */
	public void getRGBPixels(Format ff)
	{
		RGBFormat inputFormat;  
		DirectColorModel dcm; 
		
	 try{
		 if(ff instanceof RGBFormat){
			
			inputFormat = (RGBFormat) ff;
			int blueMask= inputFormat.getBlueMask();
			int greenMask = inputFormat.getGreenMask();
			int redMask = inputFormat.getRedMask();
			int bitsPxl = inputFormat.getBitsPerPixel(); 
			if(debug)
			printMasks(blueMask, greenMask, redMask, bitsPxl, inputFormat);
			
			dcm = new 
			DirectColorModel(bitsPxl, redMask, greenMask, blueMask);
			int ln = datint.length; 
			
			redPxls = new int[ln];
			greenPxls = new int[ln];
			bluePxls = new int[ln];
			alphaPxls = new int[datint.length];
			
			for(int n=0; n < ln; n++)
			{
				redPxls[n]= dcm.getRed(datint[n]);
				greenPxls[n]= dcm.getGreen(datint[n]);
				bluePxls[n]= dcm.getBlue(datint[n]);
				

		//alphaPxlsLst[n] = dcm.getAlpha(datint[n]);
			if(debug)	
			printPxls(redPxls[n], greenPxls[n],bluePxls[n],0,n); 
			}
			datint = null; 
			dcm = null; 
		 }else
			 throw new InvalidDataTypeException ("Supported format is RGB");
			 
			 
	}catch(InvalidDataTypeException  o){
	mLog.severe(o.getMessage());
	o.printStackTrace(); 
	}
	}
/**
 * For the purpose of debugging prints the masks for 
 * the RGB components of the video frame. 
 */	
static void printMasks(int blue, int  green, int red, int bpp, RGBFormat rgb) 
throws InvalidDataTypeException
{
	mLog.info(rgb.getDataType()+"");
	mLog.info("blue mask is " + blue);
	mLog.info("green mask is " + green);
	mLog.info("red mask is " + red);
	mLog.info("bits per pixel is " + bpp);
}
/**
 * Debugging, prints for each pixel number px the RGB values. 
 */
static void printPxls(int redPxl, int greenPxl,int bluePxl,int alphaPxl, int px) 
throws InvalidDataTypeException
{
	mLog.info("Pixel number = " + px + ".  Color components are");
	mLog.info("RED: " + redPxl);
	mLog.info("GREEN: " + greenPxl);
	mLog.info("BLUE: " + bluePxl);
	
}
}				


	
	

