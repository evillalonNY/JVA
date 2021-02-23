/**
 * File		:	FrameAccess.java
 * 
 * Author	:	Elena Villalón after java.sun
 * 
 * Contents	:	The class has been taken from the sample code 
 * 				http://java.sun.com/products/java-media/jmf/2.1.1/solutions/.
 *              It is the driver for the video processing.
 *              Extract the tracks, images and audio. Retrieve the   
 *              Buffer classes of all frames in the video and stores 
 *              them in an ArrayList. At the end of the media processing
 *              it instantiates an object of class VideoAnalyzer 
 *              to further processing the ArrayList of frames.   
 *              It may display the video with JMStudio. 
 * 	
 * Uses: PreAccessCodec, PostAccesCodec, VideoAnalyzer			
 */
package jVideos;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.UnsupportedPlugInException;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/** 
 * Sample program to access individual video frames by using a 
 * "pass-thru" codec.  The codec is inserted into the data flow
 * path.  As data pass through this codec, a callback is invoked
 * for each frame of video data.
 */
public class FrameAccess extends JFrame implements ControllerListener {
	
	static final long serialVersionUID = 42L;
    Processor p;
    Object waitSync = new Object();
    boolean stateTransitionOK = true;
    PreAccessCodec preac= null;
    PostAccessCodec postac = null; 
    String descFormat="";   
    List<Buffer> frameLst; 
    protected String keyURL = ""; 
	private String mess=""; 
	private boolean playonly = false; 
	private static Logger mLog = 
	    Logger.getLogger(FrameAccess.class.getName());
	private boolean debug = false;
    /**
     * Given a media locator, create a processor and use that processor
     * as a player to playback the media.
     *
     * During the processor's Configured state, two "pass-thru" codecs,
     * PreAccessCodec and PostAccessCodec, are set on the video track.  
     * These codecs are used to get access to individual video frames 
     * of the media.
     *
     * Much of the code is just standard code to present media in JMF.
     */
	public void setPlayonly(boolean what ){
		playonly= what; 
	}
	private void debug(){
		if(!debug)
			mLog.setLevel(Level.WARNING);
	}
    public boolean open(MediaLocator ml) {
      debug();
	try {
	    p = Manager.createProcessor(ml);
	} catch (Exception e) {
		mess = "Failed to create a processor from the given url: " + e; 
		JOptionPane.showMessageDialog(null, mess, "URL",
				JOptionPane.ERROR_MESSAGE);
	    System.err.println(mess);
	    return false;
	}

	p.addControllerListener(this);

	// Put the Processor into configured state.
	p.configure();
	if (!waitForState(p.Configured)) {
		mess = "Failed to configure the processor."; 
		JOptionPane.showMessageDialog(null, mess, "Processor",
				JOptionPane.ERROR_MESSAGE);
	    System.err.println(mess);
	    return false;
	}

	// So I can use it as a player.
	p.setContentDescriptor(null);

	// Obtain the track controls.
	TrackControl tc[] = p.getTrackControls();

	if (tc == null) {
		mess = "Failed to obtain track controls from the processor."; 
		JOptionPane.showMessageDialog(null, mess, "Track Control",
				JOptionPane.ERROR_MESSAGE);
	    System.err.println(mess);
	    return false;
	}

	// Search for the track control for the video track.
	TrackControl videoTrack = null;

	for (int i = 0; i < tc.length; i++) {
	    if (tc[i].getFormat() instanceof VideoFormat) {
		videoTrack = tc[i];
		break;
	    }
	}

	if (videoTrack == null) {
		mess = "The input media does not contain a video track."; 
		JOptionPane.showMessageDialog(null, mess, "No video tracks",
				JOptionPane.ERROR_MESSAGE);
	    System.err.println(mess);
	    return false;
	}
	
    Format  formatVideo = videoTrack.getFormat(); 
    descFormat = formatVideo.toString(); 
    float frmRate = ((VideoFormat) formatVideo).getFrameRate(); 
    String code = ((VideoFormat) formatVideo).getEncoding(); 
    Dimension frmDim = ((VideoFormat) formatVideo).getSize();
    int mxln = ((VideoFormat) formatVideo).getMaxDataLength(); 
    mLog.info(code + "  "+ mxln + "  " + frmRate+ "  " + frmDim ); 
    
	mess = "URL "+ keyURL + "\n" + "Format: " + videoTrack.getFormat(); 
	
	JOptionPane.showMessageDialog(null, mess, "Format",
			JOptionPane.INFORMATION_MESSAGE);
	
 	System.err.println(mess);
    
	// Instantiate and set the frame access codec to the data flow path.
	try {
		
		preac = new PreAccessCodec(); 
		postac = new PostAccessCodec();
	    Codec codec[] = { (Codec) preac,(Codec) postac};  
	
	    videoTrack.setCodecChain(codec);
	    
	    
	} catch (UnsupportedPlugInException e) {
		mess= "The process does not support effects."; 
		JOptionPane.showMessageDialog(null, mess, "No Effects",
				JOptionPane.INFORMATION_MESSAGE);
	    System.err.println(mess);
	}

	// Realize the processor.
	p.prefetch();
	if (!waitForState(p.Prefetched)) {
		mess= "Failed to realize the processor.";
		JOptionPane.showMessageDialog(null, mess, "Failed processor",
				JOptionPane.ERROR_MESSAGE);
	    System.err.println(mess);
	    return false;
	}

	// Display the visual & control component if there's one.

	setLayout(new BorderLayout());

	Component cc;

	Component vc;
	if ((vc = p.getVisualComponent()) != null) {
	    add("Center", vc);
	}

	if ((cc = p.getControlPanelComponent()) != null) {
	    add("South", cc);
	}

	// Start the processor.
	p.start();
	mLog.info("Start"+ p.getState()); 
	setVisible(true);
	
    int val = p.getState();
    mLog.info("state is " + val); 
    
	
	return true;
    } //boolean open(MediaLocator ml) {

    public void addNotify() {
	super.addNotify();
   if(!playonly)
	super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    else{
    	super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	this.addWindowListener(new WindowAdapter()
    	{
    	public void windowClosing(WindowEvent e)
    	{
    	p.close(); 
    	}
    	});
    	}
       
	pack();
    }

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() != state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {}
	}
	return stateTransitionOK;
    }
    
      
    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {
    
	if (evt instanceof ConfigureCompleteEvent ||
	    evt instanceof RealizeCompleteEvent ||
	    evt instanceof PrefetchCompleteEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = true;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof ResourceUnavailableEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = false;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof EndOfMediaEvent) {
	    p.close();
	    //Access all frame videos and create RGB Lists of frames components
	   
	    frameLst = postac.frameLst;
	    long numFrm = postac.numFrm; 
	    float totalTime = postac.totime; 
	    mLog.info("***" + descFormat + "***"); 
	    String formD = formatString(postac.formatDesc);
	    descFormat = descFormat + formD; 
	    mLog.info("***" + descFormat + "***"); 
	    if(!playonly){
	    	VideoAnalyzer vid = new VideoAnalyzer(frameLst, 
	    		descFormat, numFrm, totalTime, keyURL);
	    }
	    
	   return; 
	}
	return; 
    }

   public String formatString(String str){
	   StringTokenizer tok = new StringTokenizer(str, ",");
	   String res = ""; 
	   int cnt = 0;
	   while(tok.hasMoreTokens()){
         	
        String	kurl = new String(tok.nextToken().toString());
        kurl.trim();
        if(cnt > 3)
        	res = res +"," +kurl;
        cnt++;
	   }
	   
	   
	   return res;
   }
    static void prUsage() {
    	String mess2 =  "Usage: java FrameAccess <url>";
    	JOptionPane.showMessageDialog(null, mess2, "Failed URL",
				JOptionPane.ERROR_MESSAGE);
	System.err.println(mess2);
    }
    
  
   

} //class FrameAccess
