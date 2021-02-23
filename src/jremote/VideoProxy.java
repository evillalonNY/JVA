/**
 * VideoProxy.java
 * 
 * Client-side proxy class that manages the 
 * connection to the server and forwards the 
 * client's requests to the server by writing 
 * the text of requests to the stream on top 
 * of the socket established at creation time when the
 * constructor is called. 
 * Also reads the response from the client as text
 * streams.
 * 
 * Elena Villalon
 * April 21, 2007
 */
package jremote;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class VideoProxy extends RemoteMethodsAdapter{
	
	    private Socket socket;
	    private PrintStream printStream;
	    BufferedReader inputReader;
	    private static Logger mLog = 
            Logger.getLogger(VideoProxy.class.getName());
        private static boolean debug = true;
        public Socket getSocket(){
        	return socket; 
        }
	    public VideoProxy(String host, int port) throws UnknownHostException,
	            java.io.IOException {
	    	if(!debug)
	    		mLog.setLevel(Level.WARNING);
	        socket = new Socket(host, port);
	        OutputStream outputStream = socket.getOutputStream();
	        printStream = new PrintStream(outputStream);
	        InputStream inputStream = socket.getInputStream();
	        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	        inputReader = new BufferedReader(inputStreamReader);
	    }
	    public StringBuffer response(){
	    	StringBuffer buf = new StringBuffer("Response from the Server:\n"); 
	    	String response=null;
            mLog.info("Waiting from response...."); 
	    	try{	
	    
	    	while(true){
	    		 response= "" +inputReader.readLine();	
	    		 mLog.info(response); 
	    		if(response.equalsIgnoreCase("FIN")||response == null)
	    			break;
	    		
	    			buf.append("\n" + response);
	    	}
	    	
	    	}catch(IOException err){
	    		err.printStackTrace(System.out);
	    	}
	    	return buf;
	    }
	    public String videoLabel(String tag) {
	    	printStream.println(Commands.SHOWLABEL+ " " + tag);
	    	StringBuffer buf = response();
	    	
	    	return buf.toString(); 
	    }
	    public String describeVideo(String url){
	    	mLog.info("VIDEO PROXY" + Commands.DESCRIBEVIDEO+ " " + url);
	    	printStream.println(Commands.DESCRIBEVIDEO+ " " + url);
	    	printStream.flush();
	    	StringBuffer buf = response();
	    	
	    	return buf.toString(); 
	    }
	    public String databaseStat(){
	    	
	    	printStream.println(Commands.STATDATABASE.toString());
	    	printStream.flush();
	    	StringBuffer buf = response();
	    	
	    	return buf.toString(); 
	    }
	    public void addVideo(String url){
	    	printStream.println(Commands.ADDVIDEO +" "+ url);
	    	printStream.flush();
	    }
	    public String findSimilarCategory(String url){
	    	printStream.println(Commands.SIMILARVIDEO +" "+ url);
	    	printStream.flush();
            StringBuffer buf = response();
	    	return buf.toString(); 
	    }


	}


