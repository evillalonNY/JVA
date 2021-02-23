/**
 * SendYourComments.java
 *
 * Created on April 11, 2007, 10:03 PM
 * It writes the selected text of the JTextArea 
 * component of GUIClient to a file  
 * @author  Elena  Villalon
 */
package jclient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.*;
import java.io.File;

public class SendYourComments {
	
	String filename;
	JTextArea outputarea; 
	
public SendYourComments(String file, JTextArea area){
	 filename =file;
	 outputarea = area;
     writeToFile(); 
}
public void writeToFile(){
	FileOutputStream fout;	
	try
	{
	    // Open an output stream
		boolean exists = (new File(filename)).exists();
		if(!exists)
	    fout = new FileOutputStream (filename);
		else
		fout= new FileOutputStream(new File(filename),true); 
			

	    // Print a line of text
	    new PrintStream(fout).println (outputarea.getSelectedText());

	    // Close our output stream
	    fout.close();		
	}
	// Catches any error conditions
	catch (IOException e)
	{
		System.err.println ("Unable to write to file");
		System.exit(-1);
	}
  }
}
