/**
 * File		:	ColorIndStat.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Takes one column of the video matrix,mat,  
 *              corresponding to one color value.  
 *              Recall dim(mat) = numberFrames X 256. 
 *              Calculates mean and standard deviations, median,
 *              maximum and minimum values and the quantiles. 
 *      
 */

package jalgo;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.list.DoubleArrayList;



public class ColorIndStat {
	/**
	 * Array with one column of video matrix color 
	 * (either R, G, or B) for one color value component
	 */
	double [] cov;
	/**
	 * Color value=RGB
	 */
	char rgb; 
	/**
	 * Percentages to calculate quartiles
	 */
	private static Logger mLog = 
        Logger.getLogger(ColorIndStat.class.getName());
	
	final static int NOCOLORS  = 256;
	final static double LOW    = 0.25;
	final static double UPPER  = 0.75;
	final static double MEDIAN = 0.5;
	static double totcnt =0; 
	final static double [] percentages = {LOW, MEDIAN, UPPER};
	double meanCov;
	double meanDev;
	double medianCov;
	double variance;
	double [] quantiles= new double[percentages.length -1]; //exclude the median;
	double col; 
	private boolean logtrans = false;
	//weight for the mean
	private boolean weight = false;
	/** */
	private boolean debug = false;
	public void setLogtrans(boolean b){
		logtrans = b;
		
	}
	public void setWeight(boolean b){
		weight = b;
		
	}
	public boolean getLogtrans(){
		return logtrans; 
	}
	public double getMeanCov(){
		return meanCov;
	}
	public void setMeanCov(double m){
		meanCov = m; 
	}
	public double getMeanDev(){
		return meanDev;
	}
	public void setMeanDev(double m){
		meanDev = m; 
	}
	public double getMedianCov(){
		return medianCov;
	}
	public void setMedianCov(double m){
		medianCov = m; 
	}
	public double getVariance(){
		return variance;
	}
	public void setVariance(double m){
		variance = m; 
	}
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean dbg){
		debug= dbg; 
	}
	public ColorIndStat()
	{
	if(!debug)
    mLog.setLevel(Level.WARNING);
	cov= new double[NOCOLORS];
	rgb = 'R'; 
	col=0;
	}
	public ColorIndStat(double [] clcomp){
		if(!debug)
		mLog.setLevel(Level.WARNING);
		cov = clcomp; 
		
	}
	public ColorIndStat(double [] clcomp, boolean w){
		this(clcomp); 
		weight = w; 
		
	}
			
	public ColorIndStat(double [] clcomp, boolean trans, boolean w){
		this(clcomp, w); 
		logtrans= trans;
		if(logtrans)
			for(int n=0; n <cov.length; ++n){
				cov[n] = cov[n] + 1.e-10;
				cov[n] = Math.log10(cov[n]);
			}
		
				
	}
	public ColorIndStat(double [] clcomp, double col){
		this(clcomp); 
		this.col = col; 
	}
	
	public ColorIndStat(double [] clcomp,  double col,char color)
	{
	this(clcomp,col); 
	rgb = color;
	
 
	}

	public String calculateStat(){
	/**
	 * Calculates the mean, sd, max and min values, median, and 
	 * quartiles for the percentages vector of 
	 * the array of data cov.
	 */
		double[] sortcov = new double[cov.length];
		for(int n=0; n < cov.length; ++n)
			sortcov[n] = cov[n];
		 Arrays.sort(sortcov); 
    	 DoubleArrayList sortdcov = new DoubleArrayList(sortcov);
		int nrow =  cov.length;
		DoubleArrayList dcov = new DoubleArrayList(cov);
		 String colores = "RED"; 
		 if(rgb=='B')
			 colores="BLUE";
	     if(rgb=='G')
	    	 colores="GREEN"; 
		String str=""; 
		String spc="  "; 
		str = String.format("\n%sSummary of Statistics for color %s\n",spc,colores);
		StringBuilder sbr = new StringBuilder(str);
		 //mean of data set
		 double mn = cern.jet.stat.Descriptive.mean(dcov);
	
		 setMeanCov(mn); 
		 double var = cern.jet.stat.Descriptive.sampleVariance(dcov, mn);
    	 double sd = cern.jet.stat.Descriptive.sampleStandardDeviation(nrow,var); 
    	
		 this.meanDev = cern.jet.stat.Descriptive.meanDeviation(dcov, meanCov);
		 this.variance = var;
		 double mediana = cern.jet.stat.Descriptive.median(sortdcov);
		 medianCov = mediana; 
		 double mxCov = cern.jet.stat.Descriptive.max(dcov);
		 double minCov = cern.jet.stat.Descriptive.min(dcov);
	
		 double [] sortCov= new double[cov.length]; 
		
	     this.quantiles[0] = cern.jet.stat.Descriptive.quantile(sortdcov,LOW);
	     this.quantiles[1] = cern.jet.stat.Descriptive.quantile(sortdcov,UPPER);
	     int cc = (int) col; 		 	 
		str= String.format("%sColor component: %d\n",spc, cc);
		sbr.append(str); 
	    str= String.format("%smean: %.2f\t standard deviation: %.2f\n",spc, meanCov, meanDev);   
		 sbr.append(str);
		 str = String.format("%sminimum: %.2f\t maximum: %.2f\n",spc, minCov, mxCov); 
		 sbr.append(str);
		 str=String.format("%sQuantiles %%:\t%.2f\t%.2f\t%.2f\n",spc, percentages[0], 0.5, percentages[1]);
		 
		 sbr.append(str); 
		 str= String.format("%sQuartiles: \t%.2f\t%.2f\t%.2f\n",spc, quantiles[0],medianCov, quantiles[1]); 
		 sbr.append(str); 
		 mLog.info(str.toString());

		if (meanCov <= 0.5 || medianCov <=0.5)
			sbr =  new StringBuilder(); 
	return sbr.toString(); 
}
	
	     
}
