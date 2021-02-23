/**
 * @author evillalon
 * After Micah Altman code written in C
 * 
 * Description: Calculate MessageDigest for a vector with any 
 *              sub-class that extends Number. The algorithm can 
 *              apply any of the MessageDigest algorithms.
 *              The class of the input data is Number 
 *              and any of the sub-classes.
 *              It can either calculate a different encoding for text
 *              or use class Normalizer of java.text
 *              It also obtains the Base64 string 
 *              representation of the 
 *              bytes that are returned with the digest
 *              
 * **For version 3 encoding UTF-32BE and digest MD5
 * **for version 4 encoding UTF-32BE and digest SHA-256
 * **for version 4.1 encoding UTF-8 and digest SHA-256
 *
 *              
 */
package edu.harvard.iq.vdcnet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class UnfNumber<T extends Number> implements UnfCons{
	
	private static Logger mLog = Logger.getLogger(UnfNumber.class.getName());
	/** the starting encoding */
	private String orencoding=null;
	/** the final encoding */
	private String encoding="UTF-8";//";
	/** relevant to C code only*/
	private static boolean VCPP;
	/** local specific formatting */ 
	private Locale currentlocale = Locale.getDefault();
	/** the MessageDigest algorithm **/
	private String mdalgor = "MD-5";
	private MessageDigest md=null;
 
	
	public UnfNumber(){
		if(!DEBUG)
			mLog.setLevel(Level.WARNING);
		if(orencoding==null)
			orencoding = textencoding;
		try{
			//md5_init in Micah code
		md = MessageDigest.getInstance(mdalgor);
		}catch(NoSuchAlgorithmException err){
			err.getMessage();
		}
	}
	/**
	 * 
	 * @param algor String with the name of algorithm to 
	 * use with the MessageDigest
	 * @exception NoSuchAlgorithmException 
	 */
	public UnfNumber(String algor){
		if(!DEBUG)
			mLog.setLevel(Level.WARNING);
		mdalgor = algor;
		try{
		//another algor different form md5
		md = MessageDigest.getInstance(algor);
		}catch(NoSuchAlgorithmException err){
			err.getMessage();
		}
	}
	
	/**
	 * @param dch String with name of final encoding
	 * @param or String with name of original encoding  
	 * @param no boolean whether to append null byte
	 */
	public UnfNumber(String dch, String or){
		this();
		encoding=dch;
		orencoding=or;
	
	}
	/**
	 * @param dch String with name of final encoding
	 * @param or String with name of original encoding  
	 * @param algor messageDigest algorithm 
	 */
	public UnfNumber(String algor,String dch, String or){
		this(algor);
		encoding=dch;
		orencoding=or;
	
	}
	
	public boolean getNormalizeText(){
		return normalizeText;
	}
	
	
	
	/**
	 * @return String with the default final encoding
	 */
	public String getEncoding(){
		return encoding;
	}
	/**
	 * @param String with final encoding
	 */
	public void setEncoding(String fenc){
		encoding = fenc;
	}
	/**
	 * 
	 * @return String with original encoding
	 */
	public String getMdalgor(){
		return mdalgor;
	}
	/**
	 * @param String with the digest algorithm 
	 */
	public void setMdalgor(String aa){
	    mdalgor =aa;
	    MessageDigest mdm = null;
	    try{
			//another algor different form md5
			mdm = MessageDigest.getInstance(aa);
			this.md= mdm; 
			}catch(NoSuchAlgorithmException err){
				err.getMessage();
			}
			
	}
	/**
	 * 
	 * @return boolean indicating if '\0' is appended end of String
	 */
	public boolean getNullbyte(){
		return nullbyte;
	}
	
	
	/**
	 * 
	 * @param x double
	 * @return int to indicate if x is a special number
	 */
   public static int mysinf(Double x){
	   if(VCPP){
		   if(!x.isInfinite()|| !x.isNaN()) return 0;
		   if(x.isNaN()) return 0;
		   if(x>0) return 1;
		   return -1;
	   }else{
		   Boolean b=  x.isInfinite();
		   if(b) return 1;else return 0;
	   }
   }
   
   /**
    * 
    * @param v vector of class Number or its sub-classes
    * @param digits integer with the numbers of digits for precision
    * @param result  Collection Integer with bytes from messagedigest 
    * @param base64 Character array with base64 encoding
    * @param hex StringBuilder with hexadecimal representation oh digest
    * @throws UnsupportedEncodingException
    * @throws NoSuchAlgorithmException
    * 
    */
   public String RUNF3(final T[]v, int digits, List<Integer>result,Character[]base64, StringBuilder hex)//, String[] resultBase64)  
   throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
	   int nv = v.length;
	   double dub =0;
	   boolean miss= false;
	   int k=0;
	
	   for(k=0; k < nv; ++k ){
		   miss =false;
		   dub= v[k].doubleValue();
		   if(Double.isNaN(dub)) miss=true;
		 
		   //md5_append is called with UNF3
		   md = UNF3(v[k],digits,md,miss);
	   }
	   /**produces, by default, 16 byte digest: equivalent to md5_finish**/
	   byte [] hash = md.digest();
	   md.reset();
	   byte [] inthash = new byte[hash.length];
	   for(k=0; k < hash.length; ++k){
		   int h =(int)(( hash[k] & 0xFF)); 
		   inthash[k] = (byte)h;
		   result.add((Integer) (h+0));
	   }
	   Integer []res =result.toArray(new Integer[16]);
	  
	   //make sure is in big-endian order
	   mLog.info("Base64 encoding in BIG-ENDIAN");
	   String tobase64 = Base64Sun.tobase64(inthash,false);
	   String hexstr = UtilsConverter.getHexStrng(hash);
	   hex.append(hexstr);
	   mLog.info("hex "+hex);
	   if((hash.length > 16) && mdalgor.equals("MD5") )
		   mLog.info("unfNumber: hash has more than 16 bytes.."+hash.length); 
	  
	   
	   for(int n=0; n <tobase64.length(); ++n)
	   base64[n] = new Character(tobase64.charAt(n));
	 
	   return tobase64;
	   }
		   
	/**
	 * 
	 * @param obj Class Number or sub-classes
	 * @param digits integer for precision arithmetic
	 * @param previous MessageDigest 
	 * @param miss boolean for missing values
	 * @return MessageDigest after updating with data in obj
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */   
   
   public MessageDigest UNF3(final T obj, int digits, MessageDigest previous, boolean miss)
   throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException{
	   if(!miss){
		   RoundRoutines<T> rout = new RoundRoutines<T>(digits,false, currentlocale);
		  
		   String tmps = rout.Genround(obj,digits, false); 
		
		   if(tmps==null) {
			   mLog.severe("UNF3: Genround returns null");
			   return previous;
		   }else
			   mLog.info("UNF3: Genround: "+tmps);
		   /** add the null byte */
		   int sz = tmps.length();
		   if(nullbyte && !(tmps.charAt(sz-1)== zeroscape)) tmps+=zeroscape;
		   String dec [] = new String[2];
    	   dec[FINAL_ENC]=encoding;
    	   dec[ORG_ENC]=(orencoding != null)? orencoding:Charset.defaultCharset().name();
	       byte bt[] = null;
	       if(orencoding!=null)
               bt = tmps.getBytes(orencoding);
	       else
	    	   bt =tmps.getBytes();
	       String tmp ="";
	      
	       byte [] tmpu = null;
		  
		   if(!normalizeText) 
		       tmpu = UtilsConverter.byteConverter(bt, dec);
		   
		   if(tmpu==null) {
			   mLog.severe("UNF3: CanonalizeUnicode returns null");
			   return previous;
		   }
		
		  byte [] bint = tmpu;
		  if(nullbyte)
			  bint = UnfDigestUtils.eliminateZeroPadding(tmpu, bt); 
		  String tmp0="";
		  for(int n=0; n < bint.length; ++n)
			  tmp0=tmp0+ "\t"+ bint[n]; 
	    	  mLog.info("after "+ tmp0);
		  previous.update(bint);
	   } 
	  
	   if(miss){
		  byte [] tpu= UtilsConverter.getBytes(missv,null); 
		 
		  previous.update(tpu);
	   }
				  
	   return previous;
	   }
  
 
   }
   
   
