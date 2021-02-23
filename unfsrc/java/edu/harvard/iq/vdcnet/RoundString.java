/**
 * Description: Generalized Rounding Routines for Strings
 *              Implements Micah Altman code for rounding 
 *              characters Strings
 *              The implementation is in method Genround
 * Input: int with number of characters to keep  
 *        String obj to apply the rounding routine;  
 *        
 * Output: String representation of formatted String.
 * 
 * @Author Elena Villalon
 *  email:evillalon@iq.harvard.edu
 *  
 */
package edu.harvard.iq.vdcnet;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoundString implements UnfCons{
	   private static Logger mLog = Logger.getLogger(RoundRoutines.class.getName());
	//ucode characters
	    private static final char dot=Ucnt.dot.getUcode();//decimal separator "."
	    private static final char percntg=Ucnt.percntg.getUcode(); //"%"
	    private static final char s =Ucnt.s.getUcode();//"s"
	    //language and country to format strings
	    Locale loc = new Locale("en", "US");
	   
	    /** whether to append null byte ('\0') the end of String */
	    private boolean nullbyte=true;
	 
	    public RoundString(){
	    	if(!DEBUG)
	    		mLog.setLevel(Level.WARNING);
	    }
	    /**
	     * @param loc Locale to set character encodings
	     */
	    public RoundString( boolean b){
	        this();
	    	nullbyte=b;
	    	}
	    public RoundString(Locale loc, boolean b){
	    	this(b);
	    	this.loc = loc;
	    
	    	}
	    
	    /**
	     * 
	     * @param str String for formatting
	     * @param digits int for number of characters 
	     * @return String formatted
	     */
	    public String Genround(String str, int digits){
	    	return Genround(str, digits,nullbyte);
	    }
	    /**
	     * 
	     * @param str String for formatting
	     * @param digits int for number of characters 
	     * @param no boolean whether to append null byte ('\0')
	     * @return String formatted
	     */
	    public String Genround(String str, int digits, boolean no){
	    	//String nl = System.getProperty("line.separator");
	    	nullbyte = no;
	    	String fmtu= ""+ percntg + dot + digits + s;
	    	String fmt = "%."+digits + "s";
	    	if(!fmtu.equalsIgnoreCase(fmt)&& loc==new Locale("en", "US"))
				mLog.severe("RoundString: Unicode & format strings do not agree"); 
	    	String tmp = String.format(loc, fmtu, str).trim();
	    	tmp+= creturn;
	    	if(nullbyte) tmp+= zeroscape;
	    	
	    	return tmp; 
			
	    }
	    /**
	     * 
	     * @param bb Byte array from String
	     * @param digits int with  significant digits
	     * @return String
	     */
	    public String Genround(byte[] bb, int digits){
	    	byte [] str = bb;
	    	//String nl = System.getProperty("line.separator");
	    	String fmtu= ""+ percntg + dot + digits + s;
	    	String fmt = "%."+digits + "s";
	    	if(!fmtu.equalsIgnoreCase(fmt)&& loc==new Locale("en", "US"))
				mLog.severe("RoundString: Unicode & format strings do not agree"); 
	    	String tmp = String.format(loc, fmtu, str).trim();
	    	tmp+= creturn;
	    	if(nullbyte) tmp+= zeroscape;
	        
	    	return tmp; 
			
	    }
	    private static void testme() throws Exception {
	    	RoundString rout = new RoundString();
			mLog.info("*********************");
			String str = "The quick brown dog jumps over the lazy dog";
			//'\u0680','\u21E2','\u0204',
			char [] data = {'\u00F1','\u00E1','\u00E3','\u017F'};
			mLog.info("*********************");
			
		//	str = new String(data);
			
			mLog.info(str);
			mLog.info(rout.Genround(str, 5,false));
			mLog.info(rout.Genround(str, 2,false));
			mLog.info(rout.Genround(str, 128,false));
			mLog.info(rout.Genround("", 128,false));
	    	
	    }
		public static void main(String args[]) throws Exception{
			testme(); 	
}
}
