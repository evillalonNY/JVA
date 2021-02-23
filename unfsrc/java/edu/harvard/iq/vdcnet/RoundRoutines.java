/**
 * Description: Generalized Rounding Routines
 *              Implements Micah Altman code for rounding Numbers
 *              The implementation is in method Genround
 * Input: int with number of digits including the  decimal point, 
 *        Object obj to apply the rounding routine;  
 *        obj is of class Number and any of its derived sub-classes.
 *        
 * Output: String representation of Object obj in canonical form. 
 * Example :/*
		 * Canonical form:
		 *                -leading + or -
		 *                -leading digit
		 *                -decimal point
		 *                -up to digits-1 no trailing 0
		 *                -'e'
		 *                -sign either + or -
		 *                -exponent digits no leading 0
		 * Number -2.123498e+22, +1.56e+1, -1.3456e-, +3.4222e+
         * mantissa= digits after the decimal point & decimal point
         * exponent= digits after 'e' and the sign that follows
         *
 * Usage: For Number, e.g  Double number and int digits
         *  	roundRoutines<Double> rout = new roundRoutines<Double>();
	     *      rout.Genround(number,digits);
	     * 	    For BigDecimal number,
	     * 	    roundRoutines<BigDecimal> routb = new roundRoutines<BigDecimal>();
		 *      routb.Genround(new BigDecimal(number),digits);
		 *      
		 * For String of chars, e.g. String ss = "news from ado";
		 *      roundRoutines.Genround(ss,digits);
		 *  
 * @Author: Elena Villalon
 * <a heref= email: evillalon@iq.harvard.edu/>
 *       
 */
package edu.harvard.iq.vdcnet;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RoundRoutines<T extends Number> implements UnfCons{
public static final long serialVersionUID=1111L;
private static Logger mLog = Logger.getLogger(RoundRoutines.class.getName());
    /**number of digits with decimal point*/
    private int digits; 
    /**the Locale language and country*/ 
    private Locale loc; 
    /**some formatting for special numbers*/
    private FormatNumbSymbols symb = new FormatNumbSymbols();
    /**no leading and trailing 0 digits in exponent and mantissa*/
    private static final boolean nozero=true; //default is true
    /** radix for numbers*/
    private int radix=10; 
    /**unicode characters*/ 
    private static final char dot=Ucnt.dot.getUcode();//decimal separator "."
    private static final char plus=Ucnt.plus.getUcode(); //"+" sign 
    private static final char min=Ucnt.min.getUcode(); //"-" 
    private static final char e=Ucnt.e.getUcode(); //"e"
    private static final char percntg= Ucnt.percntg.getUcode(); //"%"
    private static final char pndsgn=Ucnt.pndsgn.getUcode(); //"#"
    private static final char zero =Ucnt.zero.getUcode();
    private static final char s =Ucnt.s.getUcode();//"s"
    private static final char ffeed = Ucnt.frmfeed.getUcode(); 
    private static final char creturn =Ucnt.psxendln.getUcode();
   
    /** whether to append the null byte ('\0') the end of string */
    private static boolean nullbyte=true;
    /** check conversion from string to numeric for mix 
     * columns values (i.e. column can have chars and numbers)
     * */
    private static boolean convertToNumber = false;
    
   
    /**
     * Default constructor 
     */
   
	public RoundRoutines(){
		if(!DEBUG)
			mLog.setLevel(Level.WARNING);
		this.digits=DEF_NDGTS;
		this.symb = new FormatNumbSymbols();
		
	}
	public RoundRoutines(boolean no){
		this();
		nullbyte = no; 
	}
	/**
	 * 
	 * @param digitsint 
	 * Number of decimal digits including the decimal point
	 */
	public RoundRoutines(int digits,boolean no){
		this(no);
		if(digits < 1) digits=1; //count for decimal separator
		//upper value is limited 
		this.digits = digits <= INACCURATE_SPRINTF_DIGITS ? digits : INACCURATE_SPRINTF_DIGITS;
	
	}
	/**
	 * 
	 * @param digitsint
	 * @param loc the default locale
	 */
	public RoundRoutines(int digits, boolean no,Locale loc){
		this(digits,no);
		this.loc = loc; 
	}
	
	/**
	 * 
	 * @return boolean indicating if null byte 
	 * is appended end of String
	 */
	public boolean getNullbyte(){
		return nullbyte;
	}
	public void setNullbyte(boolean b){
		nullbyte = b;
	}

	/**
	 * @param obj Object of class Number and sub-classes 
	 * @param digits int total decimal digits including decimal point
	 * @return String with canonical formatting
	 */
	public String Genround(T obj, int digits){
		return Genround(obj, digits, nullbyte);
	}
	/**
	 * @param obj Object of class Number and sub-classes 
	 * @param digits int Number of decimal digits with decimal point
	 * @param no boolean indicating whether null byte ('\0') is appended
	 */
	public String Genround(T obj, int digits, boolean no) {	
		RoundRoutines.nullbyte=no;
		// TODO Auto-generated method stub
		BigDecimal objbint=null; 
		boolean bigNumber = (obj instanceof BigDecimal) ? true:false; 
		if(obj instanceof BigInteger){
		    bigNumber = true;
			objbint = new BigDecimal((BigInteger) obj);
		}
		
		StringBuilder build = new StringBuilder(); 
		String fmt, fmtu, tmp; 
		//the decimal separator symbol locally 
		char sep = symb.getDecimalSep();
	  
		if(digits< 0) digits = this.digits;
		
		Double n = obj.doubleValue();
		
		if(sep != dot){
			mLog.warning("RoundRoutines: Decimal separator is not " +
					"'\u002E' or a dot:.");
			sep='.';
		}
			
		//check infinity or NaN for Double inputs 
	    
	    if(!(obj instanceof BigDecimal) && (objbint==null) &&
	         (tmp = RoundRoutinesUtils.specialNumb(n)) != null)
	    	return tmp;
			 
		char[] str= {percntg, plus,pndsgn,sep}; //{'%','+', '#', '.'}	
		
		int dgt=(INACCURATE_SPRINTF)?INACCURATE_SPRINTF_DIGITS:(digits-1); 
			
		fmt= new String("%+#."+dgt+"e"); 
		//using the Unicode character symbols 
		build.append(str);
		build.append(dgt);
		build.append(e);
		fmtu = build.toString();
		build = null;
		if(!fmtu.equalsIgnoreCase(fmt)&& loc==new Locale("en", "US"))
			mLog.severe("RoundRoutines: Unicode & format strings do not agree"); 
		
		if(obj instanceof BigDecimal)
			tmp = String.format(loc, fmtu, obj);
		else if (objbint != null)
		    tmp= String.format(loc, fmtu, objbint);
		else
			tmp = String.format(loc, fmtu, n);//double representation with full precision
		
		//check infinity or NaN for BigDecimal 	
		if(tmp.equalsIgnoreCase("Infinity") ||
		   tmp.equalsIgnoreCase("-Infinity") ||
		   tmp.equalsIgnoreCase("NaN")){
			mLog.warning("RoundRoutines: infinite or nan encounter");
			return tmp;
		}
	
		String atoms [] =tmp.split(e+"");
		//e.g., Number -2.123498e+22; atoms[0]=-2.123498 & atoms[1]=+22
		
		build= calcMantissa(atoms[0],sep); 
		build.append(e);
		build.append(atoms[1].charAt(0)); //sign of exponent
		build.append(calcExponent(atoms[1])); 
		return build.toString();
	}
	/**
	 * 
	 * @param obj  Object of class Number and sub-classes 
	 * @param digits int number of decimal digits to keep
	 * @param charsetString with optional charset to encode bytes
	 * @return byte array encoded with charset
	 */
	
	public byte[] GenroundBytes(T obj, int digits, String... charset)
	throws UnsupportedEncodingException{
		String str = Genround(obj, digits);
		String finstr = charset[0];
		if(str==null || str.equals(""))
			return null;
		Charset original= Charset.defaultCharset();
		Charset to = original;
		
		if(charset.length>0 && Charset.isSupported(charset[0])){
			to = Charset.forName(charset[0]);
			if(!to.canEncode()) {
				finstr = textencoding;
				to=original;
			}
		}
		return str.getBytes(finstr); 
	}
			
	/**
	 * 
	 * @param atom String with the exponent including the sign
	 * @return StringBuffer representing exponent with no leading 0
	 *         and appending the end of line. 
	 */
		private StringBuffer calcExponent(String atom){
			
			StringBuffer build = new StringBuffer();
			
			String expnt = atom.substring(1); 
			long lngmant = Long.parseLong(expnt);//remove leading 0's
		
			if(lngmant > 0 || (lngmant == 0 && !nozero))
			build.append(lngmant);
		    
		    	
			//adding end-line :"\n"
		//	String nl = System.getProperty("line.separator");
			
			//build.append(nl);  //end of line
			//build.append(ffeed);
			/**
			 * per specs append the end of line "\n" and  
			 * null terminator
			 */
			build.append(creturn);
			if(nullbyte)
			  build.append(nil);
			return build;
		}
	/**
	 * 
	 * @param atom String with the mantissa 
	 * @param sep char the decimal point 
	 * @param f boolean for number between (-1,1)
	 * @return StringBuilder with mantissa after removing trailing 0
	 */
	private StringBuilder calcMantissa(String atom, char sep){
		StringBuilder build = new StringBuilder();
    //sign and leading digit before decimal separator
	char mag[] = {atom.charAt(0), atom.charAt(1)};
	//canon[] :double check you have correct results 
	String canon[] = atom.split("\\"+sep);
	if(!canon[0].equalsIgnoreCase(new String(mag)))
			mLog.severe("RoundRoutines:decimal separator no in right place");	
	build.append(mag);//sign and leading digit
	build.append(sep);//decimal separator
	String dec = atom.substring(3);//decimal part
	if(!dec.equalsIgnoreCase(canon[1]))
		mLog.severe("RoundRoutines: decimal separator not right");

	String tmp= new StringBuffer(dec).reverse().toString();
	long tmpl = Long.parseLong(tmp); //remove trailing 0's 
	tmp = new StringBuffer((Long.toString(tmpl))).reverse().toString();
	
	//removing trailing 0
	if(tmpl== 0 && nozero) return build;
	
	return build.append(tmp);
	
}
	
	
	
	/**
	 * @param cobj CharSequence to format
	 * @param digitsint number of characters  to keep
	 * @return String formatted
	 */
	public String Genround(CharSequence cobj, int digits){
		return Genround(cobj,digits, nullbyte);
	}
	public static String Genround(CharSequence cobj, int digits, boolean no){
		if((((String)cobj).trim()).equals("")){
			String res=null;
			if(cobj.length() > digits)
				res = (String) cobj.subSequence(0, digits-1);
			res+=creturn;
			if(no) res+= nil; 
			return res;
		}
		boolean numeric =false;	
		if(convertToNumber)	
		numeric=RoundRoutinesUtils.checkNumeric(cobj);
	
		if(numeric){
			//only digits in obj use a BigInteger representation
			BigInteger bg = new BigInteger(cobj.toString());
			RoundRoutines<BigInteger> rout = new RoundRoutines<BigInteger>();
		    return rout.Genround(bg, digits,no);
		}
		
		//if is not digits
	
		return (new RoundString().Genround((String) cobj, digits,no));
		}
	private static void testme() throws Exception {
		RoundRoutines<Double> rout = new RoundRoutines<Double>();
		mLog.info("*********************");
		mLog.info(rout.Genround(3344556677.786549,15, false));
		RoundRoutines<BigDecimal> routb = new RoundRoutines<BigDecimal>();
		mLog.info("**********************");
		mLog.info(routb.Genround(new BigDecimal(3344556677.786549),15,false));
		RoundRoutines<BigInteger> routn = new RoundRoutines<BigInteger>();
		mLog.info("**********************");
		mLog.info(routn.Genround(new BigInteger("334455667788991122"),15,false));
		rout = new RoundRoutines<Double>();
		mLog.info("**********************");
		mLog.info(rout.Genround(23.78,7,false));
		rout = new RoundRoutines<Double>();
		mLog.info("***********************");
		mLog.info(rout.Genround(3.78,7,false));
		mLog.info("***********************");
		mLog.info(rout.Genround(3d,7,false));
		mLog.info("**************************");
		mLog.info(rout.Genround(0.000345,7,false));
		mLog.info("**************************");
		mLog.info(rout.Genround(-0.000345,7,false));
		mLog.info("**************************");
		mLog.info(rout.Genround(-1.0,7,false));
		mLog.info("**************************");
		mLog.info(rout.Genround(1.0,-1,false));
		String ss = "news from ado";
		mLog.info(RoundRoutines.Genround(ss,7, false));
		mLog.info(rout.Genround(1.0,-1,false));
		mLog.info("**************************");
		ss = "1122334455 6677";
		mLog.info(ss);
		mLog.info(RoundRoutines.Genround(ss,7,false));
		
		byte[] issb ={'\u0073', 101, 119, 115, 32};
		byte [] nb = {111,(byte) 222,(byte) 333,(byte) 444};
		
		mLog.info(""+ new String(issb));
		mLog.info(""+ new String(nb));
		String str = "mmmm ggggg hhhh jjj";
		mLog.info("*********************");
		BigDecimal bd = new BigDecimal(12345678.12345678);
		mLog.info("Eng String: "+ bd.toEngineeringString());
		mLog.info("String: "+ bd.unscaledValue());
		mLog.info("*********************");
		MathContext mth = new MathContext(7);
		mLog.info("Mth Context: "+ mth.getRoundingMode());
		mLog.info("*********************");
		String strt ="000012345678";
		mLog.info(RoundRoutinesUtils.trimZeros(strt,true));
		mLog.info(RoundRoutinesUtils.trimZeros(strt,false));
		mLog.info("*********************");
		String strr ="123456780000";
		mLog.info(RoundRoutinesUtils.trimZeros(strr,true));
		mLog.info(RoundRoutinesUtils.trimZeros(strr,false));
		mLog.info("**********************");
		mLog.info(rout.Genround(367.89345e8,7,false));
		mLog.info("**********************");
		mLog.info(rout.Genround(367.89345,7,false));
		mLog.info("**********************");
		mLog.info(rout.Genround(23.78e-10,7,false));
		rout = new RoundRoutines<Double>();
	}
	//checking some inputs.  Need some JUnit Tests 
	public static void main(String args[]) throws Exception{
		testme();
		
	}
	}


