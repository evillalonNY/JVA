/**
 * File		:	PostAcessCodec.java
 * Author	:	Elena Villalón after java.sun
 * Contents	:	The class has been taken from the sample code and extended 
 *              for our own needs. See  
 * 				http://java.sun.com/products/java-media/jmf/2.1.1/solutions/.
 *              It accesses the video frames and stores them in an ArrayList
 *              of type Buffer. Extends class PreAccessCodec 
 * 			
 */
package jVideos;
 
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;


public class PostAccessCodec extends PreAccessCodec{
	
	List<Buffer> frameLst = new ArrayList<Buffer>(); 
	long numFrm = 0; 
	float totime = 0; 
	int numPxlFrm = 0; 
	String formatDesc = ""; 
	private static Logger mLog = 
	    Logger.getLogger(PostAccessCodec.class.getName());
	private boolean debug = true;
//	 We'll advertize as supporting all video formats.
	public PostAccessCodec() {
		if(!debug)
			mLog.setLevel(Level.WARNING);
	    supportedIns = new Format [] {
		new RGBFormat()
	    };
	}
 
	/**
         * Callback to access individual video frames.
         */
	void accessFrame(Buffer frame) {
      
	    // For demo, we'll just print out the frame #, time &
	    // data length.

	    long t = (long)(frame.getTimeStamp()/10000000f);
	    if(frame.getHeader()!= null)
	    formatDesc = frame.getFormat().toString()+ ", " + 
	                 ", " + frame.getHeader() + ", " + frame.getFlags();
	    else
	    	formatDesc = frame.getFormat().toString()+ ", " + 
            ", " + frame.getFlags();
	    
	    mLog.info("Post: frame #: " + frame.getSequenceNumber() + 
			", time: " + ((float)t)/100f + 
			", len: " + frame.getLength());
	    mLog.info("More Post: frame #: format " + frame.getFormat() +
	    		"; header: " + frame.getHeader() + "; flags : " + 
	    		frame.getFlags()); 
	    numFrm = frame.getSequenceNumber(); 
	    totime = ((float)t)/100f; 
	    numPxlFrm = frame.getLength(); 
	    
	    if(frame.getSequenceNumber() <= 1) {
	    
	    	printName(frame); 
	    	   
	     }
	    
	   Buffer frm = new Buffer();
	   frm.copy(frame); 
	    frameLst.add(frm);
	   
	
	} //void accessFrame

	public String getName() {
	    return "Post-Access Codec";
	}
/**
 * It gets the class that frame belongs to
 * using Java Reflection. 
 */	
	static void printName(Buffer frame) {
		Object o = frame.getData();	 
	       Class c = o.getClass();
	       String s = c.getName();
	       mLog.info(s);
	while(c.isArray())
	c = c.getComponentType();
	String name = c.getName();
	mLog.info(name); 
	
    
	}

    } //class PostAccessCodec
   


