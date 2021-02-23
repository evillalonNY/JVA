/**
 * VideoRunnable.java
 *
 * Created on April 18, 2007, 09:16 PM
 * Server-side implementation for Thread pool,
 * Clients work-order are stored as objects of class 
 * VideoRunnable, and added to the LinkedList of requests. 
 *    
 * Description: Parses the string with the requests 
 * from clients and passes them to the  
 * method run() to execute the commands. 
 * Uses: Commands.java, interface remoteHandler.java
 *
 * @author  Elena  Villalon
 */
package jremote;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class VideoRunnable extends Thread{
	 private String request;
	    private PrintStream out;
	    private RemoteMethods handler;
	    private static Logger mLog = 
            Logger.getLogger(VideoRunnable.class.getName());
        private static boolean debug = false;
	    public VideoRunnable(String request, PrintStream out, 
	    		RemoteMethods handler) {
	    	if(!debug)
	    		mLog.setLevel(Level.WARNING);
	        this.request = request;
	        this.out = out;
			this.handler = handler;
	    }

	public void run(){
		 String result = null;
		
	        try {
	            String[] commandAndParam = parseCommand();
	            String command = commandAndParam[0];
	       
	            if (command.equalsIgnoreCase(Commands.STATDATABASE.toString())) {
	            	mLog.info(command); 
	                result = handler.databaseStat();
	                mLog.info(result);
			        out.println(result); // Write it back to the client
			        out.flush(); 
			        return; 
	            }
	                
	                    String desc = commandAndParam[1].trim();
	                    mLog.info("VideoRunnable "+desc); 
	                    if (command.equalsIgnoreCase(Commands.DESCRIBEVIDEO.toString())) {
	                     mLog.info(Commands.DESCRIBEVIDEO.toString());
	                    	result =handler.describeVideo(desc);
	                    	
	                    } else if (command.equalsIgnoreCase(Commands.SHOWLABEL.toString())) {
	                      result= handler.videoLabel(desc);
	                    } else if (command.equalsIgnoreCase(Commands.ADDVIDEO.toString())) {
	                      	handler.addVideo(desc); 
	                      	result = "DONE";
	                    }else if(command.equalsIgnoreCase(Commands.SIMILARVIDEO.toString())){
	                    	result=	handler.findSimilarCategory(desc); 
	                    } else {
	                        throw new CommandException("Unrecognized/Unimplemented command: "
	                                + command);
	                    }
	                    out.println(result); // Write it back to the client
				        out.flush();      
	        } catch (CommandException e) {
				e.printStackTrace(System.out);
	        }
	}
	public String toString() {
		String[] com = {null};
		try{
			com = parseCommand();
		}catch(CommandException err){
			err.printStackTrace(System.out);
		}
		
		return com[0];
	}
	 private String[] parseCommand() throws CommandException{

	        // Break out the command line into String[]
	        StringTokenizer tokenizer = new StringTokenizer(request);
	        String commandAndParam[] = new String[tokenizer.countTokens()];
	        int index = 0;
	        while (tokenizer.hasMoreTokens()) {
	            commandAndParam[index++] = tokenizer.nextToken();
	        }
	        String command = commandAndParam[0];

	        // Dispatch STATDATABASE request without further ado.
	        if (command.equalsIgnoreCase(Commands.STATDATABASE.toString())) {
	        	
	            return commandAndParam;
	        }

	        // Must have 2nd arg for video url when processing DESCRIBE/ADD
	        // commands
	        boolean vidbool = command.equalsIgnoreCase(Commands.ADDVIDEO.toString())
	        		||command.equalsIgnoreCase(Commands.DESCRIBEVIDEO.toString())
	        		||command.equalsIgnoreCase(Commands.SIMILARVIDEO.toString()); 
	        
	        if (commandAndParam.length < 2 && vidbool){
	            throw new CommandException("Missing video url for command \"" + command
	                    + "\"");
	        }
	        //Must also have 2nd arg when processing label request command
	        if(commandAndParam.length < 2 && 
	        		command.equalsIgnoreCase(Commands.SHOWLABEL.toString())){
	        	
	        	 throw new CommandException("Missing label for command \"" + command
		                    + "\""); 
	        }
	        
	        return commandAndParam;
	    }
	

final class CommandException extends Exception {
	  
	  private static final long serialVersionUID = 375484561124556808L;

	 String mess = "Wrong command";

public CommandException(String mess) {
   
  	JOptionPane.showMessageDialog(null, mess, "Command Failed",
				JOptionPane.ERROR_MESSAGE);
	mLog.severe(mess);

  }
}
}

