/**
 * File		:	VideoClient.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It retrieves videos from the Mckoi 
 *              database. Print some statistics.     
 *   
 * Uses: MatrixOperation, Video, RetrieveVideo                  
 */
package jclient;
import jVideos.Video;
import jalgo.MatrixOperation;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jmckoi.RetrieveVideo;
import jstat.BinToHisto;
import jstat.DataPointPlots;
import Jama.Matrix;

public class VideoClient {
 RetrieveVideo storage; 
 Video video; 
 int frmnum=-1; 
 private int RED= 0;
 private int GREEN = 1;
 private int BLUE = 2; 
 private boolean histo = true;
 private boolean graph = true; 
 //the unique keyFrams in integer array or a Set
 private int[] uniqueKeys;
 private Set<Integer> kFrms; 
 private String url ="";
 private char colorStat ='R'; 
 private Matrix mat;
 private String descVideo="";
 private boolean lg =false;
 private static Logger mLog = 
	    Logger.getLogger(VideoClient.class.getName());
private boolean debug = false;
 //weighted mean ???
 private boolean weight= false;
 
  public VideoClient(){
	  super();
	  if(!debug)
		  mLog.setLevel(Level.WARNING);
	  frmnum = -1;
  }
  public VideoClient(int frmnum){
	  this();
	  this.frmnum = frmnum; 
  }
  public VideoClient(String sur){
	  this();
	  url=sur.trim(); 
	  this.storage = new RetrieveVideo(url); 
      this.video = this.storage.getVideo();
      int frmno=-1; 
      Timestamp ts = this.storage.getTs(); 
      uniqueKeys = print(ts, url,-1, false, false); 
      mat=  video.getRedMat();
  }
  public VideoClient(String sur, char color){
	  this();
	  url=sur.trim(); 
	  this.storage = new RetrieveVideo(url); 
      this.video = this.storage.getVideo();
      colorStat = color;
      
      if(colorStat =='R')
    	mat=  video.getRedMat();
      else if (colorStat =='G')
	  mat = video.getGreenMat();
      else if (colorStat =='B')
    	mat=   video.getBlueMat();
      else if (colorStat=='D'){
    	  buildMetaString(); 
      } 	 
      
  }
  
  public void buildMetaString(){
	  String spc = "  "; 
      String str = ""; 
      StringBuilder sbr = new StringBuilder(str);
	  String desc = video.getFormatDesc();
 	  String [] descsplit = desc.split(",");
 	 for(int n =0; n < descsplit.length; ++n)
         descsplit[n].trim();
 	 
 	 long nofrm = video.getNumFrm();
 	 double tmelapse = video.getTimeStream();
 	str= String.format("%sPrimaryKey: %s\n%sFormat: RGB\n", spc, url, spc);
 	 sbr.append(str);
 	 str= String.format("%sCodec: %s\n%sFrameDimension: %s\n", 
 			 spc,descsplit[0],spc,descsplit[1]);
 	 sbr.append(str);
 	 if(descsplit.length >=3){
 	    str= String.format("%sNumberFrames: %d\n%s%s\n",spc, nofrm, spc, descsplit[2]);
		sbr.append(str); 
 	 }
	    str= String.format("%sTime Stream: %.2f\n",spc, tmelapse);  
		sbr.append(str);
	if(descsplit.length >=4){
		 str= String.format("%sMaxData%s\n",spc,descsplit[3]);
		 sbr.append(str);
		 }
	if(descsplit.length >=6){
		str= String.format("%sPixel: %s\t%s\n",spc, descsplit[4],descsplit[5]);
		sbr.append(str);
	}
	if(descsplit.length >=8){	
	str = String.format("%s%s\t%s\n ",spc,descsplit[6], descsplit[7]);
		sbr.append(str);
	}
		if(descsplit.length < 10){
		str= String.format("%sHeader & Flags: %s\t%s\n",spc, descsplit[8]);
		sbr.append(str);
		}else if(descsplit.length == 10){
			str= String.format("%sHeader & Flags: %s\t%s\n",spc, descsplit[8], descsplit[9]);	
			sbr.append(str);
		}
 	 descVideo  = sbr.toString();   
  }
  
  public Matrix getMat(){
  return mat;
}
	/**
	 * @param args
	 */
  public void setFrmnum(int f){
	  this.frmnum = f; 
  }
  public int [] getUniqueKeys(){
	  return uniqueKeys; 
  }
  public synchronized String getDescVideo(){
	  return descVideo; 
  }
  public void setWeight(boolean w){
	  weight=w; 
  }
	public static void main(String[] args) {
		
      VideoClient client = new VideoClient();
      String keySelect = ""; 
      mLog.info("lenght" + args.length); 
      if (args.length > 0){
		keySelect = new String(args[0]);
		keySelect = keySelect.trim(); 
		mLog.info("Video url selection is " + keySelect); 
       }
      client.storage = new RetrieveVideo(keySelect); 
      client.video = client.storage.getVideo();
      int frmno=-1; 
      Timestamp ts = client.storage.getTs(); 
     
    	  
      if (args.length >= 2){
    	 frmno= Integer.parseInt(args[1]);  
          
    	  client.setFrmnum(Integer.parseInt(args[1]));
    	  if(args.length==2) {
    	  client.print(ts, keySelect, frmno, true, false);
    	  return; 
    	  }
      }
    
      if(args.length>=4){
    	  boolean graph= Boolean.parseBoolean(args[3]);
     	 client.graph = graph;
     	 client.print(ts, keySelect, -1, false, true);
     	 return;
      }
      
    
     
    
      
	}
	 public int[] print(Timestamp ts, String key, 
			 int frm, boolean histo, boolean graph){
		 Matrix blue = video.getBlueMat(); 
	     Matrix red = video.getRedMat();
	     Matrix green = video.getGreenMat();
	     double [][]ratFrm = new double[3][red.getColumnDimension()]; 
	     if(weight)
	     ratFrm = colorsInFrm(); 
	     
	     int totfrm=0; 
			mLog.info("Object retrieve with timestamp " +  
					ts + "\nand with PK " + key);
		
			
			
			Matrix [] colors = {red, green, blue}; //all frames
			mLog.info(video.toString()); 
			
			Matrix rcopy = new Matrix(red.getArrayCopy()); 
			Matrix bcopy = new Matrix(blue.getArrayCopy());
			Matrix gcopy = new Matrix(green.getArrayCopy()); 
			
			if(frm> 0 && rcopy.getRowDimension()< frm)
			{
				String mess2 =  "Number of frames = " + rcopy.getRowDimension() + 
				      "; histo for mean frame"; 
				frm =-1; 
		    	JOptionPane.showMessageDialog(null, mess2, "Frame not-existent",
						JOptionPane.ERROR_MESSAGE);
			}
			if(frm <0)
				mLog.info("Histo is for Mean frame"); 
			//RED
			double rr[][]=new double[1][]; 
			if(frm> 0){
				rr[0]= rcopy.getArrayCopy()[frm-1]; 
				if(lg)
					for(int n=0; n < rr[0].length; ++n)
						rr[0][n] = Math.log10(rr[0][n]+1.e-10); 
			}
			MatrixOperation R = new MatrixOperation(rcopy, lg);
			if(frm <= 0)
			 rr= R.getMeanFrm();
			
			if(weight)
				rr = weightFrms(rr,ratFrm[RED]);
			Matrix RMeanFrm = new Matrix(rr);
			Matrix RVarFrm = new Matrix(R.getVarFrm()); 
			Set<Integer> rset = R.getKeyFrmsMap().keySet();
			kFrms = rset; 
			totfrm+= rset.size();	
			if(debug)	
			R.getMatStandard().print(1, 0); 
			//GREEN
			double gg[][]=new double[1][]; 
			if(frm> 0){
				gg[0]= gcopy.getArrayCopy()[frm-1]; 
				if(lg)
					for(int n=0; n < gg[0].length; ++n)
						gg[0][n] = Math.log10(gg[0][n]+1.e-10); 
			}
			MatrixOperation G =new MatrixOperation(gcopy, lg);
			if(frm <=0)
			 gg = G.getMeanFrm();
			
			if(weight)
				gg = weightFrms(gg,ratFrm[GREEN]);
			Matrix GMeanFrm = new Matrix(gg);
			Matrix GVarFrm = new Matrix(G.getVarFrm());
			Set<Integer> gset = G.getKeyFrmsMap().keySet(); 
	
			totfrm+= gset.size();
			if(debug)
		 	G.getMatStandard().print(1, 0);
			//BLUE
			double bb[][]=new double[1][]; 
			if(frm> 0){
				bb[0]= bcopy.getArrayCopy()[frm-1]; 
				if(lg)
					for(int n=0; n < bb[0].length; ++n)
						bb[0][n] = Math.log10(bb[0][n]+1.e-10); 
			}
			MatrixOperation B =new MatrixOperation(bcopy, lg);
			if(frm <=0)
			 bb = B.getMeanFrm();
	    	
			if(weight)
				bb = weightFrms(bb,ratFrm[BLUE]);
	    	Matrix BMeanFrm = new Matrix(bb);
	    	Matrix BVarFrm = new Matrix(B.getVarFrm());
	    	Set<Integer> bset = B.getKeyFrmsMap().keySet();
	    
	    	totfrm+= bset.size();
	    	if(debug)
			B.getMatStandard().print(1, 0);
	    	
	    	Matrix[]  b = {RMeanFrm, GMeanFrm, BMeanFrm}; //mean frame
	    	if(debug)
	    	red.print(1, 0); 
	    	mLog.info("Number of keys "+ totfrm 
	    			+"; unique " + kFrms.size()); 
	    	uniqueKeys = new int[totfrm];
	    	int count =0; 
	    	for(Integer kr: rset){
	    		uniqueKeys[count] = (int) kr;
	    		count++;
	    		mLog.info("RED---- " +kr); 
	    	}
	    	for(Integer kg: gset){
	    		uniqueKeys[count] = (int) kg;
	    		count++;
	    		mLog.info("GREEN--- " +kg); 
	    	}
	    		
	    	for(Integer kb: bset){
	    		uniqueKeys[count] = (int) kb;
	    		count++;
	    		mLog.info("BLUE--- " +kb); 
	    	}
	    	
	    	
	    	if(histo)
	    	  histoFromBin(b, 0); 
	    	else if(!histo && graph)
	    		new DataPointPlots(red,green,blue); 
	    	Arrays.sort(uniqueKeys);
	    	
	    	return uniqueKeys;
	    	
			}
	 public void histoFromBin(Matrix[] a, int frmnum){
		  
		   int[] blue; 
		   int [] green;
		   int [] red;
		   int allcolors[][] = new int[a.length][]; 
		   
		  for (int nc = 0; nc < a.length; nc++){
						
					
		   double[][] frms = a[nc].getArrayCopy();
			   
		   int sz = frms[frmnum].length; 
		   int frm[] = new int[sz];
		  
		   for (int n=0; n < sz; ++n){
			   frm[n] = (int) frms[frmnum][n];
			  
		   }
		   allcolors[nc] = frm; 
		   }
		   red = allcolors[RED];
		   blue=allcolors[BLUE];
		   green = allcolors[GREEN];
		   
		  
		    new BinToHisto().BinToPxl(red, green, blue);
	     }
	 
	 /**Get the RGB-matrices and for each color value
	  * find how many frames have entries different from 0
	  * Returns a two dimensional array, the first dimension
	  * is for the three colors and the second for 256 color-values.  
	  * It contains the relative counts of pixels 
	  * (divided by total # pixels for all frames) for each color value 
	  * that have non-zero pixels counts.  
	 */
	 public double[][] colorsInFrm(){
		 
		 Matrix blue = video.getBlueMat(); 
	     Matrix red = video.getRedMat();
	     Matrix green = video.getGreenMat();
	     //all matrices same dimension
	     int N = red.getRowDimension();
	     int C =red.getColumnDimension();
	     double allmat[][][] = new double[3][N][C];

        double counts[][] = new double[3][C]; 
		
	     allmat[RED] = red.getArrayCopy();
	     allmat[GREEN] = green.getArrayCopy();
	     allmat[BLUE] = blue.getArrayCopy();
	     for(int n=0; n < 3; ++n){
	    	 double tot=0; 
	     for(int c=0; c < red.getColumnDimension(); ++c){
	    	 counts[n][c] =0.0d; 
	     for(int r=0; r < red.getRowDimension(); ++r){
	    	 if(allmat[n][r][c]>0)
	            	counts[n][c]+= 1;//1allmat[n][r][c];
	            tot += allmat[n][r][c];
	     }
	     
	    mLog.info("counts[" +n +","+c+"]= "+ counts[n][c]);
	     }
	     for(int c=0; c < red.getColumnDimension(); ++c)
	     counts[n][c]= counts[n][c]/N;//(tot+1.e-5); 
	     }
	     
	     return counts;
	 }
	 public double [][] weightFrms(double onerow[][],double []ratio){
		 for(int c=0; c < onerow[0].length; ++c){
			mLog.info("onerow[" +0 +","+c+"]= "+ onerow[0][c]); 
		 
			 onerow[0][c] = onerow[0][c]*ratio[c];
			mLog.info("onerow[" +0 +","+c+"]= "+ onerow[0][c]); 
		 }
		 return onerow; 
	 }
}
