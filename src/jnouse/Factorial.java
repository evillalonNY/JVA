/**
*************************************************************************
	 *  Compilation:  javac Factorial.java
	 *  Execution:    java Factorial N
	 *
	 *  Computes N factorial and prints it to standard output.
	 *
	 *  % java Factorial 0
	 *  1
	 *
	 *  % java Factorial 1
	 *  1
	 *
	 *  % java Factorial 5
	 *  120
	 *
	 *  % java Factorial 12
	 *  479001600
	 *
	 *  % java Factorial 20
	 *  2432902008176640000
	 *
	 *  % java Factorial -10
	 *  java.lang.RuntimeException: Underflow error in factorial
	 *
	 *  % java Factorial 21
	 *  java.lang.RuntimeException: Overflow error in factorial
	 *
	 *
	 *  Remarks
	 *  -------
	 *   - Would overflow a long if N > 20
	 *   - Need to use extended precision arithmetic to handle bigger factorials
	 *
	 *************************************************************************/
package jnouse;

import java.util.logging.Level;
import java.util.logging.Logger;
	public class Factorial {

	    // return n!
	    // precondition: n >= 0 and n <= 20
		long k;
		private static Logger mLog = 
	        Logger.getLogger(Factorial.class.getName());
		private boolean debug = false;
		public Factorial(long k){
			if(!debug)
				mLog.setLevel(Level.WARNING);
			this.k = k;
			
		}
	    public static long recursFactorial(long n) {
	        if      (n <  0) throw new RuntimeException("Underflow error in factorial");
	        else if (n > 20) throw new RuntimeException("Overflow error in factorial");
	        else if (n == 0) return 1;
	        else             return n * factorial(n-1);
	    }
	    public static long factorial(long n){
	    	if (n <  0) 
	    		throw new RuntimeException("Underflow error in factorial");
	    	if(n == 0) return 1;
	    	
	    	long res = 1L; 
	    	for(int k=1; k <= n; ++k)
	    		res = res *k; 
	    		
	    	return res; 
	    }
	    public static double logFactor(long n){
            double res = 0L;
	    	if (n <=  0) 
	    		throw new RuntimeException("Underflow error in factorial");
	    
	    	for(int k=1; k <= n; ++k)
	    		res = res + Math.log10(k); 
	    	
	    	return res;
	    	
	    }
	

	    public static void main(String[] args) {
	       long N = Long.parseLong(args[0]);
	    	
	        mLog.info(""+factorial(N));
	    }

	}



