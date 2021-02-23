/**
 * File		:	InvSVD.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Takes a matrix ,rmean , and obtaines  
 *              r256=inverse(transpose(rmean) x rmean) x transpose(rmean) 
 *              Not being applied but useful calulation for OLS regression. 
 *      
 */
package jalgo;

import java.util.logging.Logger;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class InvSVD {
	static final double thres = 1.e-5;
	private static Logger mLog = 
        Logger.getLogger(InvSVD.class.getName());
	private static boolean debug = false; 
	
	public static Matrix LeastSquareInv(Matrix rmean){
	int nc = rmean.getColumnDimension();
	Matrix r256 = new Matrix(nc,nc); 
	r256 = ((rmean.transpose().times(rmean)));
	r256 = PseudoInvSVD(r256); 
    r256= r256.times(rmean.transpose()); 
    r256 = r256.transpose(); 
    return r256;
}
	public static Matrix PseudoInvSVD(Matrix r256){
		
    SingularValueDecomposition D = 
	  new SingularValueDecomposition(r256);
    Matrix U = D.getU();
    Matrix V = D.getV();
    double [] lambda = D.getSingularValues();
    double mx = 0; 
    for(int m=0; m < lambda.length; ++m)
  	 if(Math.abs(lambda[m]) > mx) mx = Math.abs(lambda[m]);  
    double [] invlambda = new double[lambda.length];  
    for(int n = 0; n <lambda.length; ++n)
  	  if(lambda[n] <= thres)
  		  invlambda[n] =0;
  	  else{
  		  if(debug)
  		     mLog.info("eigen "+ lambda[n]); 
  		  invlambda[n] =mx/(lambda[n]+1.e-10);
  	  }
     Matrix S = D.getS();
     
     double[][] SS =S.getArrayCopy();
     for(int r=0; r <S.getRowDimension(); ++r )
  	   for(int c=0; c <S.getColumnDimension(); ++c)
  		   if(c == r) SS[r][c] = invlambda[r];
     Matrix invS = new Matrix(SS);            
     r256 = (V.transpose()).times(invS); 
     r256 = r256.times(U.transpose()); 
     return r256; 
}
}
