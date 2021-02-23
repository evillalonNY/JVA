/**
 * @(#)SeekKeyFrms.java	1.2 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 * Elena Villalon
 * 
 * Adapted from the program Seek.java 
 * http://java.sun.com/products/java-media/jmf/2.1.1/solutions/.
 * Finds the keyframes for a video and display them
 * The keys to the frames are given with array keys[].
 * 
 * 
 */
package jVideos;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Duration;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.SizeChangeEvent;
import javax.media.Time;
import javax.media.control.FramePositioningControl;
import javax.media.protocol.DataSource;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * Sample program to demonstrate FramePositioningControl.
 */
public class SeekKeyFrms extends JFrame implements ControllerListener, ActionListener {
	static final long serialVersionUID = 423L;
    Processor p; 
    FramePositioningControl fpc;
    Object waitSync = new Object();
    boolean stateTransitionOK = true;
    int totalFrames = FramePositioningControl.FRAME_UNKNOWN;

    Panel cntlPanel;
    Button fwdButton;
    Button bwdButton;
    
    static int [] keys; 
    static int[]steps; 
    int [] count={0,0}; 
    String [] url;  
    int frmno=0; 
    private static Logger mLog = 
	    Logger.getLogger(SeekKeyFrms.class.getName());
	private boolean debug = true;
    
    /**
     * Given a DataSource, create a player and use that player
     * as a player to playback the media.
     */
    public SeekKeyFrms(int[] k, String[]url, int frm){
    	this(); 
    	int cc=0;
    	for(int mm=0; mm <k.length; ++mm)
    		if (k[mm]<=0) cc++;
    	
    	if(cc > 0){ //eliminate frame no =0
    	keys = new int[k.length-1]; 
    	for(int m=1; m < k.length; ++m)
        	keys[m-1]= k[m];
    	}else
    		keys=k; 
    	//keys= k;
    	this.url = url; 
    	steps= new int[keys.length]; 
    	frmno=frm;  
    	steps[0] = keys[0]; 
    	int cnt;
    	for(int n=1; n < keys.length ; ++n){
    		if(n > 1) cnt=steps[n-1];
    		
    		steps[n] = keys[n] - keys[n-1];
    		
    	}
    	displayFrms(url); 
    }
    public SeekKeyFrms(){
    super();
    if(!debug)
    	mLog.setLevel(Level.WARNING);
    }
    public boolean open(DataSource ds) {

	mLog.info("create player for: " + ds.getContentType());

	try {
	    p = Manager.createProcessor(ds);
	} catch (Exception e) {
	    mLog.info("Failed to create a player from the given DataSource: " + e);
	    return false;
	}
	
	p.addControllerListener(this);

	// Put the Processor into configured state.
	p.configure();
	if (!waitForState(p.Configured)) {
	String	mess = "Failed to configure the processor."; 
		JOptionPane.showMessageDialog(null, mess, "Processor",
				JOptionPane.ERROR_MESSAGE);
	    mLog.severe(mess);
	    return false;
	}
//	 So I can use it as a player.
	p.setContentDescriptor(null);

	p.prefetch();
	if (!waitForState(p.Prefetched)) {
	String	mess= "Failed to realize the processor.";
		JOptionPane.showMessageDialog(null, mess, "Failed processor",
				JOptionPane.ERROR_MESSAGE);
	    mLog.severe(mess);
	    return false;
	}

	// Realize the processor.

	p.realize();
	if (!waitForState(p.Realized)) {
	    mLog.severe("Failed to realize the player.");
	    return false;
	}
	
	// Try to retrieve a FramePositioningControl from the player.
	fpc = (FramePositioningControl)((Player) p).getControl("javax.media.control.FramePositioningControl");

	if (fpc == null) {
	    mLog.warning("The player does not support FramePositioningControl.");
	    mLog.warning("There's no reason to go on for the purpose of this demo.");
	    return false;
	}

	Time duration =  p.getDuration();
      
	if (duration != Duration.DURATION_UNKNOWN) {
	    mLog.info("Movie duration: " + duration.getSeconds());

	    totalFrames = fpc.mapTimeToFrame(duration);
	    if (totalFrames != FramePositioningControl.FRAME_UNKNOWN)
		mLog.info("Total # of video frames in the movies: " + totalFrames);
	    else
		mLog.info("The FramePositiongControl does not support mapTimeToFrame.");

	} else {
	    mLog.info("Movie duration: unknown"); 
	}
	
	// Pre-fetch the player.
	p.prefetch();
	if (!waitForState(p.Prefetched)) {
	    mLog.severe("Failed to prefetch the player.");
	    return false;
	}

	// Display the visual & control component if there's one.

	setLayout(new BorderLayout());

	cntlPanel = new Panel();

	fwdButton = new Button("Next>>");
    bwdButton = new Button("Random");
	

	fwdButton.addActionListener(this);
	bwdButton.addActionListener(this);
	
	cntlPanel.add(fwdButton);
	

	Component vc;
	if ((vc = p.getVisualComponent()) != null) {
	    add("Center", vc);
	}

	add("South", cntlPanel);

	setVisible(true);
	super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	this.addWindowListener(new WindowAdapter()
	{
	public void windowClosing(WindowEvent e)
	{
	p.close(); 
	}
	});
	
	return true;
    }


    public void addNotify() {
	super.addNotify();
	pack();
    }


    /**
     * Block until the player has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() < state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {}
	}
	return stateTransitionOK;
    }


    public void actionPerformed(ActionEvent ae) {
	String command = ae.getActionCommand();
	
	if (command.equals("Next>>")) {
		int g=0; 
		int dest=0; 
		//keeping track of key-frames
		if(steps.length<=1 || count[0] ==0){
			fpc.skip(keys[count[0]]);
			count[1] = count[0]; 
			count[0]++; 
			return;
		}
		
		if(count[0] >=0 && count[0] <steps.length){
		
	     dest = fpc.skip(steps[count[0]]);
	     count[1]=count[0]; 
	     count[0]++; 
	     if (count[0] >= (keys.length-1))
	 	    count[0]=-1;
	      
	     return; 
		}
	
		
				
		 if(count[0] < steps.length){
			for(int j=0; j < steps.length; ++j)
			g+=steps[j];
			g = g - steps[0]; 
		    fpc.skip(-g);
		    count[0] = 1; 
		     
		    return;
		 }
	    
	    mLog.info("Step forward " + dest + " frame.");
		
	}else if (command.equals("Random")) {
		int randomFrame = (int)(frmno * Math.random());
		int g=0; 
		if(count[0] > 0)
		for(int j=0; j < count[0]; ++j)
			g+=steps[j];
		randomFrame = fpc.skip(-g+1 +randomFrame);
		
	}

	int currentFrame = fpc.mapTimeToFrame(p.getMediaTime());
	
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
	    p.setMediaTime(new Time(0));
	   
	    System.out.println("fdone");
	   
	} else if (evt instanceof SizeChangeEvent) {
	}
    }

  
    public void displayFrms(String [] args){
	if (args.length == 0) {
	    prUsage();
	    System.exit(0);
 	}

	MediaLocator ml;

	if ((ml = new MediaLocator(args[0])) == null) {
	    mLog.severe("Cannot build media locator from: " + args[0]);
	    prUsage();
	    System.exit(0);
	}

	DataSource ds = null;

	// Create a DataSource given the media locator.
	try {
	    ds = Manager.createDataSource(ml);
	} catch (Exception e) {
	    mLog.severe("Cannot create DataSource from: " + ml);
	    System.exit(0);
	}

	//SeekKeyFrms seek = new SeekKeyFrms();
	if (!this.open(ds))
	    System.exit(0);
    }

    static void prUsage() {
	mLog.info("Usage: java Seek <url>");
    }
}