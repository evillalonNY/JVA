/**
 * File		:	DataPointPlots.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It takes as input three matrices of RGB pixel counts
 *              Rows are frames of the video, 
 *              and columns color values (0-255).
 *              The matrix entries are counts 
 *              for the frame and color component.
 *              Calculates for each column of the matrix, the median, and 
 *              two quartiles for percentages (0.25, 0.5, 0.75).  
 *              Draws for the three panels RGB plots with x-axis 
 *              color components(0-255) and y axis 
 *              the sets of the distribution for
 *              (0.25, 0.5, 0.75) median, first and third quartile.  
 *              
 * Uses: jalgo.ColorValStat.java            
 *                     
 */
package jstat;
import hep.aida.IAnalysisFactory;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IPlotter;
import hep.aida.IPlotterFactory;
import hep.aida.IPlotterStyle;
import hep.aida.ITree;
import Jama.*;
import jalgo.ColorValStat;


public class DataPointPlots
{
	/**
	 * Matrices with color counts for Red, Green, Blue
	 * The rows are frames of the video 
	 * and the columns are color values (0-255). 
	 * The entries are pixel counts for each frame and color component. 
	 */
	
	Matrix R;
	Matrix G;
	Matrix B;
	private int LOWLIMIT=10; 
	public DataPointPlots(Matrix R, Matrix G, Matrix B){
		this.R = R;
		this.G = G;
		this.B= B;
		drawBoxPlots(); 
	}
	
   public void drawBoxPlots()
   {   
	   
      IAnalysisFactory af = IAnalysisFactory.create();
      ITree tree = af.createTreeFactory().create();
      IDataPointSetFactory dpsf = af.createDataPointSetFactory(tree);
      
      // Create a two dimensional IDataPointSet for red matrix.
      IDataPointSet dps2D_R = dpsf.create("dps2D_R","Red",2);

      // Fill the two dimensional IDataPointSet
      ColorValStat red = new ColorValStat(R);
      red.buildDataSets();
      double[] yVals2D_R = red.getYVals2D();
      double[] yErrM2D_R = red.getYErrM2D();
      double[] yErrP2D_R = red.getYErrP2D();
      double[] xVals2D_R = red.getXVals2D();
      double[] xErrP2D_R = red.getXErrP2D();
      int cnt=0; 
      for ( int i = 0; i<yVals2D_R.length; i++ ) {
    	  if(yVals2D_R[i] >0.5)
    		  cnt++;
      }
      
      for ( int i = 0; i<yVals2D_R.length; i++ ) {
      
      dps2D_R.addPoint();
      if( yVals2D_R[i]< LOWLIMIT)
       	continue; 
 
        dps2D_R.point(i).coordinate(0).setValue( xVals2D_R[i] );
        dps2D_R.point(i).coordinate(0).setErrorPlus(xErrP2D_R[i] );
        dps2D_R.point(i).coordinate(1).setValue( yVals2D_R[i]);
        dps2D_R.point(i).coordinate(1).setErrorPlus( yErrP2D_R[i]);
        dps2D_R.point(i).coordinate(1).setErrorMinus( yErrM2D_R[i] );
       
    	
      }
      // Create a two dimensional IDataPointSet for green
      IDataPointSet dps2D_G = dpsf.create("dps2D_G","Green",2);

      // Fill the two dimensional IDataPointSet
      ColorValStat green = new ColorValStat(G);
      green.buildDataSets();
      double[] yVals2D_G = green.getYVals2D();
      double[] yErrM2D_G = green.getYErrM2D();
      double[] yErrP2D_G = green.getYErrP2D();
      double[] xVals2D_G = green.getXVals2D();
      double[] xErrP2D_G = green.getXErrP2D(); 

      for ( int i = 0; i<yVals2D_G.length; i++ ) {
    	    
        dps2D_G.addPoint();
        if( yVals2D_R[i]< LOWLIMIT)
        	continue; 
        dps2D_G.point(i).coordinate(0).setValue( xVals2D_G[i] );
        dps2D_G.point(i).coordinate(0).setErrorMinus( xErrP2D_G[i] );
        dps2D_G.point(i).coordinate(1).setValue( yVals2D_G[i] );
        dps2D_G.point(i).coordinate(1).setErrorPlus( yErrP2D_G[i] );
        dps2D_G.point(i).coordinate(1).setErrorMinus( yErrM2D_G[i] );
    	  
      }

//    Create a two dimensional IDataPointSet for blue
      IDataPointSet dps2D_B = dpsf.create("dps2D_B","Blue",2);

      // Fill the two dimensional IDataPointSet
      ColorValStat blue = new ColorValStat(B);
      blue.buildDataSets();
      double[] yVals2D_B = blue.getYVals2D();
      double[] yErrM2D_B = blue.getYErrM2D();
      double[] yErrP2D_B = blue.getYErrP2D();
      double[] xVals2D_B = blue.getXVals2D();
      double[] xErrP2D_B = blue.getXErrP2D(); 

      for ( int i = 0; i<yVals2D_B.length; i++ ) {
    	   
        dps2D_B.addPoint();
        if( yVals2D_R[i]< LOWLIMIT)
        continue; 
        dps2D_B.point(i).coordinate(0).setValue( xVals2D_B[i] );
        dps2D_B.point(i).coordinate(0).setErrorPlus( xErrP2D_B[i] );
        dps2D_B.point(i).coordinate(1).setValue( yVals2D_B[i] );
        dps2D_B.point(i).coordinate(1).setErrorPlus( yErrP2D_B[i] );
        dps2D_B.point(i).coordinate(1).setErrorMinus( yErrM2D_B[i] );
    	  
      }
     
      // Display the results with formatting
      IPlotter plotter = af.createPlotterFactory().create("Plot IDataPointSets");
      plotter.createRegions(1,3,0);
  	IPlotterStyle regStyleR = plotter.region(0).style();
  	
  	regStyleR.titleStyle().textStyle().setFontSize(25);
      regStyleR.titleStyle().textStyle().setColor("purple");
      regStyleR.dataStyle().lineStyle().setColor("");  
      regStyleR.dataStyle().fillStyle().setColor("red");  
  	IPlotterStyle regStyleG = plotter.region(1).style();
  	regStyleG.dataStyle().lineStyle().setColor("green"); 
  	regStyleG.titleStyle().textStyle().setFontSize(25);
      regStyleG.titleStyle().textStyle().setColor("purple");
  	IPlotterStyle regStyleB = plotter.region(2).style();
  	regStyleB.dataStyle().lineStyle().setParameter("color", "blue"); 
  	regStyleB.titleStyle().textStyle().setFontSize(25);
      regStyleB.titleStyle().textStyle().setColor("purple");
      
      plotter.createRegions(1,3);
      plotter.region(0).plot( dps2D_R,regStyleR, "red" );
      plotter.region(1).plot( dps2D_G, regStyleG, "green" );
      plotter.region(2).plot( dps2D_B, regStyleB, "blue" );
      plotter.show();


   }
}

