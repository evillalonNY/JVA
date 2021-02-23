/**
 * File		: InvalidDataTypeException 
 * Author	: Elena Villalón
 * Contents	: Extends Exception and print a message. 
 */
package jVideos;

import javax.swing.JOptionPane;

public class InvalidDataTypeException extends Exception{
	static final long serialVersionUID = 42L;
		InvalidDataTypeException(String mess){
		super(mess);
		JOptionPane.showMessageDialog(null, mess, "error message",
				JOptionPane.ERROR_MESSAGE);  
		}
}	
