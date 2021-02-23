/**
 * ProjSVD.java
 *
 * Created on December 17, 2006, 8:39 PM
 * Accept two matrices of eigenvectors. 
 * Rows are frames and columns color values.  
 * Get the coefficient of projecting vectors of 
 * svdtest matrix onto vectors of svdbasis.  
 * We have three summary measures the three norms as defined 
 * in Java documentation. 
 *
 * @author  Elena  Villalon
 */

package jalgo;
import static java.lang.Math.sqrt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import Jama.Matrix;

public class ProjSVD {
/** Matrix of nrtest rows and nctest columns
 * The rows are frames from svd descomposition
 * or the singular vector, as many as the rank 
 * of the video matrix for one of RGB. The columns
 * are color values equal to all videos (0-255).   
 */
Matrix svdtest;
//matrix of nrbasis rows and ncbasis columns; nctest = ncbasis
Matrix svdbasis;
/** Coefficient matrix of projecting svdtest onto svdbasis
 * Rows are the projection coefficients of vectors rows of svdtest,
 * onto the vector rows of svdbasis: 
 * test(i, k) = sum( j=1, ncol) alpha(i,j)* basis(j, k); ncol=256.
 * The sum is extended to color values, i.e. j = (0-255) +1 
*/
Matrix coeff;
/**the projection of matrix test onto basis for each row.
*Add the squares of all columns for every row and sqrt the result. 
*/
double [] alpha; 
private static Logger mLog = 
    Logger.getLogger(ProjSVD.class.getName());
private boolean debug = false;

public ProjSVD(Matrix test, Matrix basis){
	if(!debug)
		mLog.setLevel(Level.WARNING);
	svdtest = test;
	svdbasis = basis;
	int nrtest  = svdtest.getRowDimension();
	alpha = new double[nrtest]; 
}

/*returns norm calculations of the matrix of projection 
* that helps to determine video equality. 
*/

public double[] project(){
	int nctest  = svdtest.getColumnDimension();
	int ncbasis = svdbasis.getColumnDimension();
	if (nctest != ncbasis){
		String mess ="Error: matrices should have same number of columns";
		JOptionPane.showMessageDialog(null, mess, "URL",
				JOptionPane.ERROR_MESSAGE);
	    mLog.severe(mess);
	}
	int nrtest  = svdtest.getRowDimension();
	int nrbasis = svdbasis.getRowDimension();
	coeff = new Matrix(nrtest, nrbasis);
	Matrix tsvdbasis = new Matrix(ncbasis, nrbasis);
	//tsvdbasis = svdbasis.inverse(); 
	tsvdbasis = svdbasis.transpose(); 
	coeff = svdtest.times(tsvdbasis);
	
    mLog.info("Forbenius is " + coeff.normF());//Frobenius norm
  	mLog.info("Norm1=max col is " + coeff.norm1());//max column sum
  	mLog.info("NormInf=max row is " + coeff.normInf());//max row sum
  	
	
	double [] normas = new double[3];
	normas[0] = coeff.normF();
	normas[1] = coeff.normInf();
	normas[2] = coeff.norm1(); 
	
	double[][] mat = coeff.getArrayCopy(); 
	double [] alpha2 = new double[nrtest];
	double [] alpha = new double[nrtest];
	for(int k=0; k < nrtest; ++k)
	for(int n=0; n < nrbasis; ++n)
		alpha2[k]+= mat[k][n] * mat[k][n];//along rows
	for(int j=0; j < alpha.length; ++j){
		/** alpha is the projection vector for every frame of svdtest 
		 * onto the frames of svdbasis, length(alpha) = no frames in svdtest
		 * Each element is the square root of the squares of 
		 * alpha[i] = sqrt( sum(j=0-255) coeff(i, j)^2 ); 
		*/
		alpha[j] = sqrt(alpha2[j]);
			
	}
	this.alpha = alpha; 
	
	return normas; 
}


	
}	

