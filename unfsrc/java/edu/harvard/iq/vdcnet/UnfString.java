
/**
 * @author evillalon
 * After Micah Altman code written in C
 * 
 * Description: Calculate MessageDigest for a vector with the 
 *              sub-classes that extend CharSequence. 
 *              The algorithm can apply any of the MessageDigest 
 *              algorithms available in java.
 *              The class of the input data is of type String 
 *              or any related classes.
 *              It can either calculate a different encoding for text
 *              or use class Normalizer of java.text
 *              It also obtains the Base64 string representation 
 *              of the bytes that are returned with the digest
 *              
 * **For version 3 encoding UTF-32BE and digest MD5
 * **for version 4 encoding UTF-32BE and digest SHA-256
 * **for version 4.1 encoding UTF-8 and digest SHA-256
 *
 *              
 */
package edu.harvard.iq.vdcnet;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UnfString<T extends CharSequence> implements UnfCons{
	private static Logger mLog = Logger.getLogger(UnfNumber.class.getName());
	/** the staring encoding */
	private String orencoding=null;
	/** the final encoding */
	private String encoding="UTF-8";//endencoder; 
	/** local specific formatting */ 
	Locale currentlocale = Locale.getDefault();
	/** the MessageDigest algorithm **/
	private String mdalgor = "MD5";//mdalgorithm;
	private MessageDigest md=null;
	
	
	public UnfString(){
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
	public UnfString(String algor){
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
	 * 
	 */
	public UnfString(String dch, String or){
		this();
		encoding=dch;
		orencoding=or;
		
	}
	/**
	 * @param algor String with the name of messageDigest algor 
	 * @param dch String with name of final encoding
	 * @param or String with name of original encoding  
	 * 
	 */
	public UnfString(String digest,String dch, String or){
		this(digest);
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
	 * @param String with the original encoding
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
    * @param buf ByteBuffer array of bytes  
    * @param miss array of boolean
    * @param digits int number of digits for precision
    * @param result array of bytes for the message digest
    * @param resultBase64
    * @param cset String with original character encoding
    * @return byte array with message digest
    */
	  public String RUNF3(ByteBuffer []buf, boolean miss [], int digits, List<Integer>result, 
			  Character[] base64, StringBuilder hex, String ...cset)
	   throws UnsupportedEncodingException, NoSuchAlgorithmException {
		  
		  Charset original=Charset.defaultCharset();
		  Charset to = original; 
				if(cset!=null && Charset.isSupported(cset[0]))
				 to= Charset.forName(cset[0]);
				 
		  if(!to.canEncode()){
			  mLog.severe("RUNF3: Charset not supported");
			  return null;
		  }
	      CharSequence [] chstr = new CharSequence[buf.length];
		  for(int n=0; n < buf.length; ++n){
			  chstr[n] = UtilsConverter.convertnioByteToStr(buf[n].array(), cset[0]);
		  }
	   
	String b64= RUNF3(chstr, miss, digits, result, base64,hex); 
	return b64; 
	}
   
   /**
    * 
    * @param v vector of class CharSequence or its sub-classes:
    *           CharBuffer, StringBuilder, StringBuffer, String, Segment 
    * @param digits int with the numbers of digits for precision
    * @param result  of Class Number 
    * @param miss array of booleans
    * @throws UnsupportedEncodingException
    * @throws NoSuchAlgorithmException
    */
   public String RUNF3(final CharSequence []v, boolean miss [], int digits, List<Integer>result, 
		   Character[] base64, StringBuilder hex) 
   throws UnsupportedEncodingException, NoSuchAlgorithmException {
	   
	   int nv = v.length;
	     
	   int k=0;
	   for(k=0; k < nv; ++k ){
		   //md5_append is called with UNF3
		   mLog.info(""+v[k]+";");
		   mLog.info(digits+";"+md+";");
		   boolean m; 
		   if(miss==null)  
		       md = UNF3(v[k],digits,md,false);
		   else
			   md = UNF3(v[k],digits,md,(boolean) miss[k]);   
	   }
	   /**produces, by default, 16 byte digest: equivalent to md5_finish**/
	   byte [] hash = md.digest();
	   md.reset();
	   if(hash.length > 16 )
		   mLog.info("unfString: hash has more than 16 bytes.."+hash.length); 
	   for(k=0; k < hash.length; ++k)
		   result.add(hash[k] & 0xFF);
	   String  rtobase64 = Base64Sun.tobase64(hash,false);
	   String hexstr = UtilsConverter.getHexStrng(hash);
	   hex.append(hexstr);
	   for(int n=0; n <rtobase64.length(); ++n)
		   base64[n] = new Character(rtobase64.charAt(n));
	   mLog.info(rtobase64);
	   return rtobase64;
	   }
		   
	/**
	 * 
	 * @param obj Class Number or sub-classes
	 * @param digits integer for precision arithmetic
	 * @param previous MessageDigest 
	 * @param miss boolean for missing values
	 * @return boolean
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */   
   
   public MessageDigest UNF3(CharSequence obj, int digits, 
		   MessageDigest previous, boolean miss)
   throws UnsupportedEncodingException, NoSuchAlgorithmException {
	   if(!miss){
		   mLog.info(obj.toString());
		   String res="";
		   if(!obj.equals(""))
		      res = RoundRoutines.Genround(obj,digits, false);
		   else{
			   res+=creturn;
			   if(nullbyte)res+=zeroscape;
		   }
		   mLog.info(res); 
		   if(res==null || (res.equals("") && !obj.equals(obj))) {
			   mLog.severe("UNF3: Genround returns null or empty String");
			   return previous;
		   }
		   int sz = res.length();
		   if(nullbyte && !(res.charAt(sz-1)== zeroscape)) res+=zeroscape;
		   String dec [] = new String[2];
		   dec[FINAL_ENC]=encoding;
	       dec[ORG_ENC]= orencoding;
	       byte[] bt = res.getBytes(orencoding);
	       
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
			  int cnt =0;
			  for(int n=0; n < bint.length; ++n){
				  tmp0=tmp0+ "\t"+ bint[n];
				  cnt++;
				  if(cnt %10 == 0) tmp0+= "\n"; 
			  }
		    	  mLog.info("after "+ tmp0);
		   //md5_append in Micah code
		   md.update(bint);
	   } 
	  
	   if(miss){
		  byte [] topass= UtilsConverter.getBytes(missv,null); 
		  md.update(topass);
	   }
				  
	   return previous;
	   }
  
}
