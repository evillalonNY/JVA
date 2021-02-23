/**
 * VideoHandler.java
 * Server-side implementation of remote methods.
 * The methods are supported by synchronization to 
 * avoid data corruption. 
 * Description: it implements some of the 
 * functions that are used by remote clients  
 * to retrieve information about the database and its videos.
 * * Created on April 18, 2007, 08:31 PM
 *
 * @author  Elena  Villalon
 */
package jremote;


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jclient.VideoClient;
import jclient.VideoDescribe;

public class VideoHandler extends RemoteMethodsAdapter{
	private static Logger mLog = 
        Logger.getLogger(VideoHandler.class.getName());
    private static boolean debug = true;
	/**
	 * @param args
	 */
	public VideoHandler(){
		super();
		if(!debug)
			mLog.setLevel(Level.WARNING);
	}
	/** Takes a label/tag for classification and returns 
	*  the videos in database classified under the label
    */
	public synchronized String videoLabel(String tag){
	    String videos=""; 
		try{
		VideoDescribe desc = new VideoDescribe(tag);
		
		videos = desc.getDescribeTag();
		if(videos.contentEquals(new StringBuffer("")))
		 throw new TagNotFoundException("Label not found"); 
		}catch(TagNotFoundException err){
		err.printStackTrace(); 
		}	
		videos+="\n" + "FIN"; 	
		mLog.info("HANDLER..." + videos); 
		return videos;
		}
	/** Takes a string with the primary key of a video 
	*  and return the metadata and description 
    */
	public  String describeVideo(String url){
	String res = ""; 
	mLog.info("describeVideo " + url);
	try{
	VideoClient vid= new VideoClient(url, 'D');
	StringBuffer summary = new StringBuffer(vid.getDescVideo()); 
	String add = "\n  Contents: ";  
	summary.append(add); 
    add = (String) ((new VideoDescribe()).getVideoDesc(url));
    summary.append(add); 
    summary.append("\n"+"FIN"); 
	res=  summary.toString();
	}catch(NullPointerException err){
	new TagNotFoundException("Video '" + url + "' does not exist"); 
	mLog.warning(err.getMessage());
	err.printStackTrace();
	res="Video '" + url + "' does not exist";
	res+="\n" + "FIN"; 
	mLog.info("HANDLER..." + res); 
	return res;
	}
	
	return res;
	}

	/**
	 * General description of video statistics stored in database
	 * @see jremote.RemoteHandlerAdapter#databaseStat()
	 */
public String databaseStat(){
VideoDescribe desc = new VideoDescribe(true);
	String res = desc.getDatabaseStat();
	res +="\n" +"FIN";
	return res; 
 
}
/**given a video url that is accessible to the Server
 * it can be added to the database. 
 * @see jremote.RemoteHandlerAdapter#addVideo(java.lang.String)
 */ 
public void addVideo(String url){
	url.trim(); 
	Object vidan = new Object(); 
	synchronized(vidan){
	vidan = new jVideos.VideoAnalyzer(url, false); 
	} 
	
}
//Given video PK, str, returns the labels it belongs to
//and the videos under those labels 
public String findSimilarCategory(String str){
	VideoDescribe des = new VideoDescribe(false);
	String rs= des.getSimilar(str.trim());
	rs += "\n"+ "FIN"; 
	mLog.info("Handler..."+rs);
	return rs; 
}
	private static class TagNotFoundException extends Exception{
		TagNotFoundException(String mess){
		prUsage(mess); 
			
		}
	}


		
		public static void prUsage(String mess) {
		    	String mess2 =  "Wrong number of frames in MedianTest";
		    	JOptionPane.showMessageDialog(null, mess, "Median Failed",
						JOptionPane.ERROR_MESSAGE);
			mLog.warning(mess);
		
		    }
		    
		    
		    public static void main(String[] args) {

        VideoHandler vid =  new VideoHandler();
        mLog.info("DESCRIBE indoor" + vid.videoLabel("indoor")); 
        mLog.info("METADATA " + vid.describeVideo("file:videos/frens2.mpeg"));
        mLog.info("NOTHING " + vid.describeVideo("file:nothing")); 
        mLog.info(vid.databaseStat()); 
	}
	}
	 



