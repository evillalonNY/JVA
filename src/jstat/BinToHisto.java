/**
 * File		:	BinToHisto.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It takes as input three arrays of RGB pixel counts
 *              for a single frame of the video. The length of 
 *              the arrays is the color range, ie. 256 for color 
 *              values (0-255); each element counts the pixels in 
 *              a color value. It converts the color counts 
 *              into arrays of pixel values of length the number 
 *              of pixels in the frame, and uses the class HistoFrame
 *              to draw the three panels RGB histograms.  
 *              
 * Uses: HistoFrame             
 *                     
 */
package jstat;


public class BinToHisto {
    int [] redPxl;
    int [] greenPxl;
    int [] bluePxl;
    boolean histo = true;
    //to plot not the entire range but for color-value >= start
    //or for color-value <= stop 
    int start =0;
    int stop = 250; 
    public BinToHisto(){
    	super();
    }
    public BinToHisto(boolean h){
    	super();
    	this.histo = h; 
    }
	
	public int[] BinToPxlColor(int [] color){
		int n, m, j=0;
		int cntPxlColor=0;
		int[] colorPxl;  
		for(n=0; n < color.length; n++){
			
			cntPxlColor += color[n];
		}
		
		colorPxl = new int[cntPxlColor];
		
		 colors:for(m = 0; m < color.length; m++){
			if(color[m] <= 0){
				
				continue colors; 
			}
			 
		for(n=0; n < color[m]; n++){			
			colorPxl[j] = m;		
			j++;		
		}
		}
		return colorPxl;
	}
	public void BinToPxl(int[] red, int[]green, int[] blue)
	{
		if(start >0){
			for(int n=0; n<= start; ++n)
					red[n] = green[n] = blue[n] =0;
					
			}
		
		if(stop <255){
			for(int n=stop; n< red.length; ++n)
					red[n] = green[n] = blue[n] =0; 
				
		}
			
			
		redPxl = BinToPxlColor(red);
		greenPxl = BinToPxlColor(green);
		bluePxl = BinToPxlColor(blue);
		if (histo == true)
			new HistoFrame(redPxl, greenPxl, bluePxl); 
		 
			
	}
	}
	

