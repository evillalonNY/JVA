	/**
	 * File		:	SVDVideo.java
	 * 
	 * Author	:	Elena Villalón
	 * 
	 * Contents	:	Calculates for video matrix, mat, the Singular Value
	 *              descomposition. Returns the rank; 
	 *              the eigenvectors in form of a matrix, 
	 *              with columns forming the basis of the
	 *              the range of mat; and the eigenvalues or 
	 *              singular values. 
	 *                                  
	 */
	package jalgo;
	import java.util.logging.Level;
import java.util.logging.Logger;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

	public class SVDMat{
		/**
		 * Matrix of frames dim=[nofrms][256]
	    */
		
		Matrix mat;
		/**
		 * A=UDV, then Lambda = D and 
		 */
		Matrix U;
		Matrix V;
		/**
		 * Matrix with the diagonal eigenvalues
		 */
	    Matrix Lambda;
	    /**
		 * Matrix Basis for the range of mat
	    */
		
		Matrix rnkBase;
		/**
		 * Matrix Basis for null space
	    */
		Matrix zeroBase;
		/**
		 * Rank of mat
		 */
		int rnk =0; 
		/**
		 * The eigenvalues or singular values
		 */
		double eig[]; 
		boolean reduce = true; 
		SingularValueDecomposition D;
		private static Logger mLog = 
		        Logger.getLogger(SVDMat.class.getName());
		private boolean debug = false; 
		public SVDMat(){
			if(!debug)
				mLog.setLevel(Level.WARNING); 
		}
		public SVDMat( Matrix m){
			   this(); 
				mat = m;
				boolean sym = checkSymmetric(mat);
				
					findSingularValues(mat, reduce);							
				
		}
		public SVDMat( Matrix m, boolean r){
			this(); 
			mat = m;
			reduce = r; 
			boolean sym = checkSymmetric(mat);
			
				findSingularValues(mat, reduce);							
			
	}
		/**
		 * check for symmetric matrices
		 */
		public boolean checkSymmetric(Matrix mat){
			double eps = 1.e-6; 
			
			int ncol = mat.getColumnDimension();
			int nrw = mat.getRowDimension();
			if(Math.abs(nrw - ncol) > eps) 
				return false; 
			int dg = ncol;
			if(nrw != dg)
				return false; 
			
			Matrix matT = mat.transpose(); 
		
		
			for(int rw=0; rw < nrw; ++rw)
				for(int cl=0; cl < ncol; ++cl )
					if(Math.abs(matT.get(rw, cl) - mat.get(rw, cl)) > eps)
						return false;
			return true; 
			
		}
		public void findSingularValues(Matrix mat, boolean red){
		  int n = 0;
		  
		  //mat= U W V'
		  int ncol = mat.getColumnDimension();
		  int nrw = mat.getRowDimension();
		 
		  
		  D= new SingularValueDecomposition(mat);
		  Matrix leftSingVec = D.getU(); 
			 
			Matrix rightSingVec = D.getV(); 
			V=rightSingVec;
			U=leftSingVec; 
		/**
		 * One-dimensional array of singular values
	     */
		 double d [] = D.getSingularValues();
		
		 this.eig = d;
		 
		 int ln1 = U.getColumnDimension();
		 int ln2 = V.getRowDimension();
		 
		 double [][] arreig = new double[ln1][ln2];
		 for(int rw=0; rw <ln1; ++rw)
			 for(int cl=0; cl <ln2; ++cl)
				 if (rw != cl || rw >= d.length || cl >= d.length) 
					 arreig[rw][cl]= 0;
				 else{ 
					 arreig[rw][cl]=d[rw];
				   mLog.info("lamb " + d[rw]); 
				 }
		 
		 Matrix Lambda = new Matrix(arreig);
		 this.Lambda = Lambda; 
		 
		 double[][] tod = new double[1][d.length];
		 for(int nn=0; nn < d.length; ++nn)
			 tod[0][nn] = d[nn];
	
		 /**
		  * Number of non-eligible singular values.
		  */
		int rnk = D.rank();
		this.rnk = rnk; 
		mLog.info("Rank " + rnk); 
		 /**
		  * left singular vectors
		  */
		
		int lfrw = leftSingVec.getRowDimension();
	    int lfcl = leftSingVec.getColumnDimension(); 
	    mLog.info("Left matrix rows "+ lfrw +"; left cols " + lfcl); 
		 /**
		  * right singular vectors
		  */
		
				   // return d;
			int [] r = new int[lfrw];
			for(n=0; n < r.length; ++n)
				r[n] = n;
			int [] c = new int[rnk];
			for(n=0; n < rnk; ++n)
				c[n] = n;
			Matrix rnkBase = leftSingVec.getMatrix(r, c);
			this.rnkBase = rnkBase;
			//null space
			int csz = rightSingVec.getColumnDimension();
			int rsz = rightSingVec.getRowDimension(); 
			int [] cc = new int[csz-rnk];
			for(n=rnk; n <csz ; ++n)
				cc[n-rnk] = n;
			int [] rr = new int[rsz];
			for(n=0; n < rr.length; ++n)
				rr[n] = n;
		    Matrix zeroBase = rightSingVec.getMatrix(rr,cc); 
			  this.zeroBase = zeroBase;
			  
			  
		}
				   
		
}
