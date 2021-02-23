/**
 * File		:	DatabaseRunnable.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	It has many static classes, namely VideoRunnable, 
 *              GraphRunnable,HistoRunnable, SeekKeyFrmsRunnable,
 *              TextRunnable,GUIDatabaseRunnable, MckoiQueryRunnable,
 *              JMStudioRunnable, StudentRunnable,  SVDVideoTestRunnable, 
 *              SumRankTestRunnable,.... 
 *              that are executed by selecting the different menu bar 
 *              components of GUIClient.java 
 *              All static classes implements Runnable.           
 *                   
 */
package jclient;
import jVideos.SeekKeyFrms;
import jVideos.VideoAnalyzer;
import jalgo.Anova1;
import jalgo.ColorValStat;
import jalgo.SVDVideoTest;
import jalgo.SignedRankTest;
import jalgo.Student;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jmckoi.DropVideo;
import jmckoi.MckoiQuery;

import org.apache.commons.math.MathException;

import Jama.Matrix;
import edu.harvard.iq.vdcnet.UnfVideo;

public class DatabaseRunnable {
DatabaseRunnable.VideoRunnable vobj;
DatabaseRunnable.GraphRunnable gobj;
DatabaseRunnable.HistoRunnable hobj;
DatabaseRunnable.SeekKeyFrmsRunnable kobj;
DatabaseRunnable.FingerPrintRunnable fobj;
static JTextArea outputarea;
static JScrollPane scroller; 
private static final int DEFAULT_WIDTH= 440;
private static final int DEFAULT_HEIGTH= 300;
private final static int fsz = 12;
private static final Font font3=new Font("Courier", Font.PLAIN,fsz);;

private static Logger mLog = 
    Logger.getLogger(DatabaseRunnable.class.getName());
private boolean debug = false; 
public DatabaseRunnable(){
	if(!debug)
		mLog.setLevel(Level.WARNING);
}
public DatabaseRunnable(DatabaseRunnable.VideoRunnable vobj){
	this();
	this.vobj = vobj;
}
public DatabaseRunnable(DatabaseRunnable.GraphRunnable gobj){
	this();
	this.gobj = gobj;
}
public DatabaseRunnable(DatabaseRunnable.HistoRunnable hobj){
	this();
	this.hobj = hobj;
}
public DatabaseRunnable(DatabaseRunnable.FingerPrintRunnable fobj){
	this();
	this.fobj = fobj;
}
public static class DropVideoRunnable implements Runnable{
	String url;
	int DELAY = 5;
	String play = "true"; 
    public DropVideoRunnable(String url){
    	this.url = url;
    }
    
    
    public void run(){
    	try{
    	
            String [] args = {url}; 
            DropVideo.main(args); 
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    }
}
}


public static class VideoRunnable implements Runnable{
	String url;
	int DELAY = 5;
	String play = "true"; 
    public VideoRunnable(String url){
    	this.url = url;
    }
    public VideoRunnable(String url, String playonly){
    	this.url = url;
    	play = playonly; 
    	
    }
    public void run(){
    	try{
    	
            String [] args = {url,play}; 
            VideoAnalyzer.main(args); 
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    }
}
}

public static class HistoRunnable implements Runnable{

	String url;
	int frmno;
	int DELAY = 5;
	
    public HistoRunnable(String url, int frmno){
    	this.url = url;
    	this.frmno = frmno; 
    }
    public void run(){
    	try{
    		String frm = (""+ frmno).trim();
            String [] urlstr = {url, frm};
           
            VideoClient.main(urlstr); 
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    }
}
}

public static class GraphRunnable implements Runnable{

	String url;
	int DELAY = 5;
	
    public GraphRunnable(String url){
    	this.url = url;
    }
    public void run(){
    	try{
    		String [] urlstr = {url, "-1", "false", "true"};
              VideoClient.main(urlstr); 
          
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    }
}
}

public static class SeekKeyFrmsRunnable implements Runnable {
	String url;
	int DELAY = 5;
	int frmno; 
	static final int INTV=10;
	
    public SeekKeyFrmsRunnable(String url){
    	this.url = url;
    }
    public SeekKeyFrmsRunnable(String url, int frm){
    	this(url);
    	frmno = frm; 
    }
    public void run(){
    	String str = "";
    	int [] fku; 
    	try{
    		
    		String [] urlstr = {url}; 
    		VideoClient vid = new VideoClient(url);
    		int [] k = vid.getUniqueKeys();
    		for (int n=0; n < k.length; ++n)
    			k[n] = (int) Math.round((double) k[n]/INTV) * INTV;    
    		Integer [] kw = new Integer[k.length];
    		for (int n=0; n < k.length; ++n)
              kw[n] = new Integer(k[n]);     			
    		List<Integer> lst = Arrays.asList(kw);  
    		Collection<Integer> uklst = new LinkedHashSet<Integer>(lst);
    		Integer[] uk = uklst.toArray(new Integer[uklst.size()]);
    		fku = new int[uk.length];
            for(int n=0; n < uk.length; ++n){
            	fku[n] = (int) uk[n];
             
            	str +=  fku[n] + "\t";   		
            		
            }
            
    		SeekKeyFrms seek = new SeekKeyFrms(fku, urlstr, frmno); 
            
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    }
    	finally {
    		mLog.info("Video key frames are: " +str);
    		
    	}
}
}
public static void setOutputArea(JTextArea outputarea, boolean f){
	outputarea.setFont(font3);
	  outputarea.setLineWrap(true);
	  outputarea.setWrapStyleWord(true);
	  int dfw = DEFAULT_WIDTH-10;
	  int dfh = DEFAULT_HEIGTH-10;
	  if(f){
		  dfw = dfw-10;
		  dfh = dfh -80;
	  }
		  
	  scroller= new JScrollPane(outputarea);
	  scroller.setPreferredSize(new Dimension(dfw, dfh));
	  JPanel panel = new JPanel();
	  panel.add(scroller); 
	  
	  JFrame frm = new JFrame();
	  frm.setSize(dfw, dfh);
	  frm.add(panel,BorderLayout.CENTER);
	
	  frm.setVisible(true ); 
	

}
public static class TextRunnable implements Runnable{

	String url;
	char rgb; 
	int DELAY = 5;
	VideoClient vid;
    Matrix mat;
    ColorValStat col;
    String summary ="";
   
    
    public TextRunnable(String url, char c){
    	this.url = url;
    	rgb = c; 
    	
    }
    public void run(){
    	
    	try{
    		vid= new VideoClient(url, rgb);
    		if(rgb=='D'){
    			summary =vid.getDescVideo();
    			summary = summary +"\n  Contents: " +(String) ((new VideoDescribe()).getVideoDesc(url)); 
    			outputarea= new JTextArea(summary, 4,15);
    			
    		}else{
			  mat =vid.getMat();
			  col = new ColorValStat(mat, rgb);
			  summary = col.getSummary();
			  summary = "  " +url + "\n" + summary; 
			  mLog.info(summary);
			  outputarea= new JTextArea(summary, 10,15);
			  
    		}
    		setOutputArea(outputarea, false); 
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    }
}
}
public static class GUIDatabaseRunnable implements Runnable{
	
	int DELAY = 5;
	
    public GUIDatabaseRunnable(){
   
    }
    public void run(){
    	try{
    		String args[] =null; 
            GUIDatabase.main(args); 
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    		 ex.printStackTrace();
    	}
}
}

public static class MckoiQueryRunnable implements Runnable{
	
	int DELAY = 5;
	
    public 	MckoiQueryRunnable(){
   
    }
    public void run(){
    	
       try{
    	String args[] =null; 
        new MckoiQuery();
    		
          Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    		 ex.printStackTrace();
    	}catch(Exception e){}
}
}
public static class JMStudioRunnable implements Runnable{
	
	int DELAY = 5;
	
    public 	JMStudioRunnable(){
   
    }
    public void run(){
    	try{
    		Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("java JMStudio"); 
    	    
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    		 ex.printStackTrace();
    	}catch(Exception e){}
}
}
public static class StudentRunnable implements Runnable{

	String [] url;
	JTextArea outputarea; 
	int DELAY = 5;
	StringBuffer results[]; 
    public StudentRunnable(String []url, JTextArea outputarea){
    	this.url = url;
    	results = new StringBuffer[2]; 
        this.outputarea = outputarea; 
        
    }
    public void run(){
    	try{
    		       
    		Student.main(url); 
            Thread.sleep(DELAY);
            results = Student.getRes(); 
            outputarea.setText(results[0].toString()+"\n"); 
            outputarea.append(results[1].toString()+"\n"); 
            outputarea.append(results[2].toString()+"\n");
            outputarea.append(results[3].toString());
    	}catch(MathException err){
       	 err.printStackTrace();
       
    	}catch(InterruptedException ex){
    		 ex.printStackTrace();
    }catch(SQLException e) {
		          mLog.severe(
		                  "An error occured\n" +
		                  "The Exception message is: " + e.getMessage()); 
		          e.printStackTrace();
		          
		                  
    }
}
}



public static class SVDVideoTestRunnable implements Runnable{
	
	int DELAY = 5;
	String []url; 
	JTextArea outputarea; 
	StringBuffer results; 
    public 	SVDVideoTestRunnable(String []url,JTextArea outputarea){
      this.url = url;
      this.outputarea = outputarea; 
    }
    public void run(){
    	try{
    		           
    		SVDVideoTest.main(url); 
            Thread.sleep(DELAY);
            results= SVDVideoTest.getRes(); 
            outputarea.setText(results.toString()); 
    	}catch(InterruptedException ex){
    		 ex.printStackTrace();
        }catch(SQLException e) {
		          mLog.severe(
		                  "An error occured\n" +
		                  "The message is: " + e.getMessage()); 
		          e.printStackTrace();
		          
		 }
    	
}
}

public static class SumRankTestRunnable implements Runnable{
	
	int DELAY = 5;
	String []url; 
	JTextArea outputarea; 
	StringBuffer results; 
    public 	SumRankTestRunnable(String []url, JTextArea outputarea){
      this.url = url;
      this.outputarea = outputarea; 
    }
    public void run(){
    	try{
    		           
          //SumRankTest driver = new SumRankTest(url);
    		SignedRankTest driver = new SignedRankTest(url);
            results= driver.getRes(); 
            outputarea.setText(results.toString()); 
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    		 ex.printStackTrace();
        }catch(Exception e) {
		          mLog.severe(
		                  "An error occured\n" +
		                  "The SQLException message is: " + e.getMessage()); 
		          e.printStackTrace();
		          
		 }
    	
}
}
public static class ANOVATestRunnable implements Runnable{
	
	int DELAY = 5;
	String []url; 
	JTextArea outputarea; 
	StringBuffer results; 
    public 	ANOVATestRunnable(String []url, JTextArea outputarea){
      this.url = url;
      this.outputarea = outputarea; 
    }
    public void run(){
    	try{
    		           
    		Anova1 driver = new Anova1(url);
            StringBuffer [] res= driver.getRes();
            StringBuffer results = new StringBuffer(res[0]);
            for(int n=1; n < res.length;++n)
            	results.append("\n"+res[n]);
            outputarea.setText(results.toString()); 
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    		 ex.printStackTrace();
        }catch(Exception e) {
		          mLog.severe(
		                  "An error occured\n" +
		                  "The SQLException message is: " + e.getMessage()); 
		          e.printStackTrace();
		          
		 }
    	
}
}
public static class FingerPrintRunnable implements Runnable{
	String thevideo= null;
	int frmno;
	int DELAY = 5;
    String summary ="";
   
    public FingerPrintRunnable(String url){
    	thevideo = new String("\t'"+url+"'");
    	 

    }
    public void run(){
    	
    	try{
    		 UnfVideo unf= new UnfVideo(thevideo);
    		 unf.calcUNF(thevideo); 
    		 String [][] rgb = unf.getUnfs();
    		  
    		    summary = thevideo+"\n\n";
    			summary = summary +"\t" +rgb[0][0]+"\n\t"+rgb[0][1]+"\n\t"+rgb[0][2];
    				
            Thread.sleep(DELAY);
    	}catch(InterruptedException ex){
    		 summary = thevideo+"\n\n";
    		summary = summary + "Failed to calculate fingerprints for video"; 
    			
    }
    	mLog.info(summary); 
    	outputarea= new JTextArea(summary, 4,15);
    	setOutputArea(outputarea, true); 
    	
}
	
}
}
