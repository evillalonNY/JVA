/**
 * File: VideoFile.java
 * 
 * Contents: Takes a file name and reads line by line
 *           Each line has the url of videos 
 *           the separation is tab, return, new line
 *           Creates ArrayList with videos url's to 
 *           query database.
 *   
 *  Author: Elena Villalon          
 */
package jclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class VideoFile {
	
	private final static int LIMITVID= 12; 
	private static Logger mLog = 
	    Logger.getLogger(VideoFile.class.getName());
	private static boolean debug = false;
	
	public static List<String> readFile(String ff){
		if(!debug)
			mLog.setLevel(Level.WARNING);
		mLog.info("Input file: " + ff);
		BufferedReader inputStream = null;
        ArrayList<String> tags = new ArrayList<String>();
        String l;
        
		try {
            inputStream = 
                new BufferedReader(new FileReader(ff));
          
            while ((l = inputStream.readLine()) != null) {
            String [] res=	l.split("\n\r");
            
            if(res.length > 10){
            String	mess = "Database queries for up to " + LIMITVID + " videos"; 
    		JOptionPane.showMessageDialog(null, mess, "URL",
    				JOptionPane.ERROR_MESSAGE);
            }
            int ln = LIMITVID; 
            if(res.length < LIMITVID) ln = res.length; 
            for(int n=0; n < ln; ++n){
            	
            	String str = res[n].trim() +"...";
            	if(!str.contentEquals("..."))
            	tags.add(res[n].trim());
            }
            }     
            
		}catch(FileNotFoundException ef){
			ef.printStackTrace();
		}catch(IOException eio){
			eio.printStackTrace();
        } finally {
        	if (inputStream != null) 
            	try{
            		
                inputStream.close();
            	}catch(IOException cio){
            		cio.printStackTrace(); 
            	}
            }
        for(String str:tags){
        	mLog.info(str); 
        }
         return tags;    
        }
	
}
