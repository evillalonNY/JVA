/**
 * VideoThread.java
 * 
 * Server-side that executes Clients requests 
 * in one of the Threads of the Thread-pool. 
 * The Thread is implemented and uses wait()
 * when the requestQueue is empty and otherwise invokes 
 * an object of class VideoRunnable 
 * to handle the client requests. 
 * http://www-128.ibm.com/developerworks/library/j-jtp0730.html
 * 
 * Elena Villalon
 * April 21, 2007
 */
package jremote;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoThread extends Thread{
	
		boolean waiting = true;

		private int threadNum;

		private LinkedList<VideoRunnable> requestQueue = 
			new LinkedList<VideoRunnable>();

		private VideoRunnable request;
		private static Logger mLog = 
	            Logger.getLogger(VideoThread.class.getName());
	    private static boolean debug = false;
		public VideoThread(LinkedList<VideoRunnable> requestQueue, int threadNum) {
			if(!debug)
				mLog.setLevel(Level.WARNING);
			this.requestQueue = requestQueue;
			this.threadNum = threadNum;
		}

		public void run() {
			while (waiting) {
				try {
					synchronized (requestQueue) {
		                    while (requestQueue.isEmpty()) {
		                        try
		                        {
		                            requestQueue.wait();
		                        }
		                        catch (InterruptedException ignored)
		                        {
		                        	ignored.printStackTrace(System.out); 
		                        }
		                    }

		                    request = (VideoRunnable) requestQueue.removeFirst();
		                }

		                // If we don't catch RuntimeException, 
		                // the pool could leak threads
		                try {
		                	mLog.info("Running request on: " +getName());
		                	request.start();
		                }
		                catch (RuntimeException e) {
		                	e.printStackTrace(System.out);
		                }
		             sleep(5000);     
				}catch(Exception err){
					waiting = false;
					err.printStackTrace(System.out);
				}
				 			
		}
	}
}

