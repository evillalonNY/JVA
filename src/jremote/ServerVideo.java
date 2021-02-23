/**
 * Server.java
 * Opens a connection and listens for clients.
 * Creates a work-order with the Client request, 
 * which is an object of class VideoRunnable.
 * Adds Client request operations to the LinkedList
 * of requestQueue. 
 *  
 * Creates a Thread pool with NUMTHREADS to handle   
 * synchronized connections on  the requestQueue,
 * and avoiding data corruption.
 * 
 * Elena Villalon
 * Aprl 20th, 2007
 * 
*/		
package jremote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
	

	public class ServerVideo {

	    private ServerSocket serverSocket;
	    private BufferedReader bufferedReader;
	    private LinkedList<VideoRunnable> requestQueue;
	    private RemoteMethods vidimp; 	
        private static final int NUMTHREADS = 5; 
    	protected Thread[] vidThreads = new Thread[NUMTHREADS];
    	private static Logger mLog = 
            Logger.getLogger(ServerVideo.class.getName());
        private static boolean debug = true;
	    public ServerVideo(int port) throws java.io.IOException {
	    	if(!debug)
	    		mLog.setLevel(Level.WARNING); 
	        serverSocket = new ServerSocket(port);
	        requestQueue = new LinkedList<VideoRunnable>();
			vidimp = new VideoHandler();
			Thread vidThread;
			for (int n = 0; n < NUMTHREADS; ++n) {
				vidThreads[n]  = new VideoThread(requestQueue, n);
				vidThreads[n].start();
			}
	       
	        
	        
	    }
	    public void close() throws IOException {
	        serverSocket.close();
	    }

	    /**
	     * serviceClient accepts a client connection and reads lines from the
	     * socket. Each line is handed to executeCommand for parsing and execution.
	     */
	    public void serviceClient(Socket clientConnection) throws java.io.IOException {
	    	
	         // Arrange to read input from the Socket
	         InputStream inputStream = clientConnection.getInputStream();
	         bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	         // Arrange to write result across Socket back to client
	         OutputStream outputStream = clientConnection.getOutputStream();
	         PrintStream printStream = new PrintStream(outputStream);

	         mLog.info("Client acquired on port #"
	                 + serverSocket.getLocalPort() + ", reading from socket\n");

	         String commandLine=null;
	        
	         while( (commandLine = bufferedReader.readLine()) !=null) {
	        	 if(commandLine == null)
	        		 break; 
	        	 mLog.info("Command from client: "+ commandLine); 
	             VideoRunnable request = new VideoRunnable(commandLine, printStream, vidimp);
	 			synchronized (requestQueue) {
	 				requestQueue.add(request);
	 				requestQueue.notifyAll();
	 			}
	         }
	        clientConnection.close();  
	     }

	    public static void main(String argv[]) {
	        int port = 1099;
	        if (argv.length > 0) {
	            try {
	                port = Integer.parseInt(argv[0]);
	            } catch (Exception e) {
	            }
	        }
	        try {
	            ServerVideo server = new ServerVideo(port);
	            mLog.info("Waiting clients to connect..."); 
		        while(true){
		        	mLog.info("Accepting clients now on port " + 
		        			server.serverSocket.getLocalPort());
			        Socket clientConnection = server.serverSocket.accept();
		        	server.serviceClient(clientConnection);
		        	mLog.info("Client serviced");
		        	
		        }
	            
	            
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	}
