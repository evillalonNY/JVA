/**
 * Draws histograms as a training example; 
 * It is not part of the Video software
 * 
 * Author : Elena Villalon
 * 
 */
package jstat;
import hep.aida.*; 

import java.util.Random;

public class Histogram {
	private int sz = 255;
	private Random generator; 
	Histogram(){
		generator = new Random(); 
	}
	Histogram(int sz){
		this();
		this.sz= sz; 
	}
	public int cast(){
	 return generator.nextInt(sz); 	
	}
	public static void main(String[] argv){
		IAnalysisFactory af = IAnalysisFactory.create();
	      IHistogramFactory hf = af.createHistogramFactory(af.createTreeFactory().create());
	      
	      IHistogram1D h1d = hf.createHistogram1D("test 1d",50,0,255);
		
		for (int i=0; i < 10000; i++) { 
			Histogram h = new Histogram(); 
			int no = h.cast();
			h1d.fill(no); 
		}
		System.out.println(""+ h1d.toString()); 
		IPlotter plotter = af.createPlotterFactory().create("Plot");
	      plotter.createRegions(1,3,0);
	      plotter.region(0).plot(h1d);
	   
	      plotter.show();
	}
}
