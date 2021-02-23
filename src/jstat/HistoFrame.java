/**
 * File		:	HistoFrame.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It takes as input three arrays of RGB color values 
 *              for a single frame of the video. The arrays contain all 
 *              pixel color components of the frame. It also takes  
 *              the number of bins to draw the histogram.
 *              Displays the histogram with three panels for each of 
 *              the colors RGB using the package AIDA.  
 *                     
 */
package jstat;

import hep.aida.IAnalysisFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotter;
import hep.aida.*; 

public class HistoFrame {
	private int colorMx = 255;
	private int[] red;
	private int[] green;
	private int[] blue; 
	private int numBins= 64; 
	
	public HistoFrame(int[] r, int [] g, int [] b){
		red= r;
		blue=b;
		green = g;
		histoGraph(); 
		
	}
	
	public int getColorMx(){
		return colorMx;
	}
	public void setColorMx(int sz){
		colorMx = sz; 
	}
	public int getNumBins(){
		return numBins;
	}
	public void setNumBins(int bins){
		numBins = bins; 
	}
	 public void histoGraph()
	 {
    int n; 
	IAnalysisFactory af = IAnalysisFactory.create();
    IHistogramFactory hf = af.createHistogramFactory(af.createTreeFactory().create());
   
    IHistogram1D hR = hf.createHistogram1D("Red",numBins,0,colorMx);
    IHistogram1D hG = hf.createHistogram1D("Green",numBins,0,colorMx);
    IHistogram1D hB = hf.createHistogram1D("Blue",numBins,0,colorMx);
	for(n=0; n < red.length; ++n){
		hR.fill(red[n]);
		
	}
	for(n=0; n < green.length; ++n){
		hG.fill(green[n]);
		
	}
	for(n=0; n < blue.length; ++n){
		hB.fill(blue[n]);
		
	}
	IPlotter plotter = af.createPlotterFactory().create("Frame Histograms");
	plotter.createRegions(1,3,0);
	IPlotterStyle regStyleR = plotter.region(0).style();
	regStyleR.dataStyle().fillStyle().setColor("red"); 
	regStyleR.titleStyle().textStyle().setFontSize(20);
    regStyleR.titleStyle().textStyle().setColor("orange");
	IPlotterStyle regStyleG = plotter.region(1).style();
	regStyleG.dataStyle().fillStyle().setColor("green");
	regStyleG.titleStyle().textStyle().setFontSize(20);
    regStyleG.titleStyle().textStyle().setColor("orange");
    
	IPlotterStyle regStyleB = plotter.region(2).style();
	regStyleB.dataStyle().fillStyle().setColor("blue");
	regStyleB.titleStyle().textStyle().setFontSize(20);
	regStyleB.titleStyle().textStyle().setColor("orange");
    plotter.region(0).plot(hR);
    plotter.region(1).plot(hG);
    plotter.region(2).plot(hB);
    plotter.show();
}
}
