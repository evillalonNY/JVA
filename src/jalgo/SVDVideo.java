/**
 * File		:	SVDVideo.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Calculates for video matrix, mat and each of RGB colors, 
 *              the Singular Value decomposition that yields the rank.  
 *              The PC are stored in array of sub-matrices of rank one;  
 *              each sub-matrix is weighted with the corresponding eigenvalue.  
 *              Compares the video sub-matrices for two videos ,and  
 *              for a certain threshold decides if the PC of the video to test 
 *              have a significant projection on first PC of a second video.  
 *              
 * Uses: ProjSVD.java, SVDMat.java
 *                                 
 */
package jalgo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jclient.VideoFile;
import jmckoi.MatrixRetrieve;
import Jama.Matrix;

public class SVDVideo {
	
	List<String> thevideos = new ArrayList<String>();
	//from array of videos find videos similarities to index of array 
	private static  int totest =0;
	private static  double threshold =0.75;
	/** for the array of videos, the matrices store the submatrices 
	 * of rank 1, which contains the PC of the SVD analysis,  
	 * for each of the three RGB matrices representing the videos, 
	 */	
	
	 List<Matrix[]> Brnk; 
	 List<Matrix[]> Grnk; 
	 List<Matrix[]>  Rrnk; 
	/** for the array of videos, the matrices of 1 row store the  
	 * SVD eigenvalues, for each of the three RGB matrices 
	 * representing the videos, 
	 */	
	
     List<Matrix> BDiag; 
	 List<Matrix> GDiag; 
	 List<Matrix>  RDiag; 
	/** for the arrays of videos, the arrays store the ranks for each of
	 * the three RGB matrices in array of videos
	 */
	 int[] Blamb; 
	 int[] Rlamb;
	int[] Glamb;
	
	//index with PC we want to compute. 
	//By default only the first component, corresponding to largest eigenvalue 
	static int [] PCorder={0}; 
	private static Logger mLog = 
        Logger.getLogger(SVDVideo.class.getName());
    private boolean debug = false; 
    
	public static int[] getPCorder(){
		return PCorder;
	}
	public static void setPCOrder(int [] ord){
		PCorder = ord; 
	}
	
	public List<Matrix[]> getBrnk(){
		return Brnk;
	}
	public  List<Matrix[]> getRrnk(){
		return Rrnk;
	}
	public  List<Matrix[]> getGrnk(){
		return Grnk;
		
	}
	public List<Matrix> getBDiag(){
		return BDiag;
	}
	public List<Matrix> getRDiag(){
		return RDiag;
	}
	public  List<Matrix> getGDiag(){
		return GDiag;
		
	}
	public  int[] getBlamb(){
		return Blamb;
	}
	public  int[] getRlamb(){
		return Rlamb;
	}
	public  int[] getGlamb(){
		return Glamb;
	}
	public SVDVideo(List<String> thevideos, int test){
		this(thevideos); 
		totest = test; 
		
	}
	public SVDVideo(List<String> thevideos){
		if(!debug)
			mLog.setLevel(Level.WARNING);
		this.thevideos= thevideos; 
	}
	public void rnkFrmTest( List<Matrix> red, List<Matrix> green,  
			                        List<Matrix> blue){
		/** All videos all frames
		 * number of rows = frames in video matrix; ncols=256
		 * Number of matrices = number of videos.
		 */	
	    Matrix [] matG = new Matrix[green.size()]; 
	    	matG= green.toArray(new Matrix[green.size()]); 
	    Matrix [] matR = new Matrix[red.size()];
	    	matR= red.toArray(new Matrix[red.size()]); 
	    Matrix [] matB = new Matrix[blue.size()]; 
	    	matB = blue.toArray(new Matrix[blue.size()]); 
	    	 
	   Brnk = new ArrayList<Matrix[]>(); 
	   Blamb = new int[matB.length]; 
	   BDiag = new ArrayList<Matrix>();
	   
	   for(int kk=0; kk < matB.length; ++kk){
		  Matrix[]tmp = reduceMat(matB[kk], Blamb, kk, BDiag); 
	      Brnk.add(tmp);  
	   }  
	   Rrnk = new ArrayList<Matrix[]>(); 
	   Rlamb = new int[matR.length]; 
	   RDiag = new ArrayList<Matrix>();
	   
	   for(int kk=0; kk < matR.length; ++kk){   
		  Matrix[]tmp = reduceMat(matR[kk], Rlamb, kk, RDiag); 
	      Rrnk.add(tmp);  
	   }    
	   Grnk = new ArrayList<Matrix[]>(); 
	   Glamb = new int[matG.length]; 
	   GDiag = new ArrayList<Matrix>();
	   for(int kk=0; kk < matG.length; ++kk){
		  Matrix[]tmp = reduceMat(matG[kk], Glamb, kk,GDiag); 
	      Grnk.add(tmp);  
	    
	   }    
	  
	}
	   public static Matrix PCMatRnk( Matrix tmp, int rnk){
	    	 int r [] = new int[tmp.getRowDimension()];
	    	 for(int n =0; n < r.length; ++n)
	    		 r[n] = n;
	    	 int c[] = new int[rnk];
	    	 for(int n =0; n < rnk; ++n)
	    		 c[n] =n;
	    	 Matrix tmp2 = tmp.getMatrix(r, c); 
	    	 
	    	 return tmp2.transpose(); 
	   }
	  
	   /**Calculates SVD, saves the rank in array rnkall. Obtains
	    * PC sub-matrices all of rank one, which best approximate mat.
	    * Sub-matrices are proportional to eigenvalues and their
	    * contributions are weighted by them. 
	   */
	  public static Matrix[] reduceMat(Matrix mat, int rnkall[], int k,
			  List<Matrix> lst){
		//  mat = mat.transpose(); 
		  SVDMat vv = new SVDMat(mat);
		  double [][]DD = new double[1][vv.eig.length];// 1 row and eginvalues in cols
		  DD[0] = vv.eig;
		  lst.add(new Matrix(DD)); 
		  Matrix V = vv.V;
		  int rnk = vv.rnk;
		  rnkall[k] = rnk; 
		  Matrix U = (vv.V).transpose();
	      U = mat.times(U);
		  double arrU[][] =U.getArrayCopy();   
		  double arrV[][] =V.getArrayCopy();
		  Matrix [] matsum= new Matrix[rnk]; 
		  for(int n= 0; n < rnk; ++n){
			
			double vecU[][] =new double[U.getRowDimension()][1];
			double vecV[][] =new double[V.getRowDimension()][1];
			for(int rw=0; rw < U.getRowDimension(); ++rw)
				for(int cl=0; cl < U.getColumnDimension(); ++cl)
				if(cl ==n) vecU[rw][0]= arrU[rw][n];
			     
			Matrix Un = new Matrix(vecU);//columns; dim = norows X 1
			for(int rw=0; rw < V.getRowDimension(); ++rw)
				for(int cl=0; cl < V.getColumnDimension(); ++cl)
				if(cl ==n) vecV[rw][0]= arrV[rw][n];
             Matrix Vn = new Matrix(vecV);//dim=norows X 1; 1 column 
             Vn = Vn.transpose(); 
			 matsum[n]= Un.times(Vn); 
		  }  
		  //normalized to one
		  for(int kk=0; kk < rnk; ++kk){
			  ProjSVD pk = new ProjSVD(matsum[kk], matsum[kk]);
			  double [] norms = pk.project();
			  matsum[kk]= matsum[kk].timesEquals(Math.sqrt(1./(norms[0]+1.e-5)));
			    
		  }
		  
		  for(int n= 0; n < rnk; ++n){ 
			  for(int l= n; l < rnk; ++l){
				  ProjSVD p = new ProjSVD(matsum[n], matsum[l]);
				double [] proj = p.project();
				double fbnorm = proj[0]; 
				
			 mLog.info("Frobenius norm "+ fbnorm); 
			  }
		  }
		  return matsum;
	  }
	  //Applies calcProj to the elements of the input List, with totest 
	  //the list element to compare with the other videos. 
	  
	  public  boolean[] allproj(List<Matrix[]> color, int totest, char clr){
		  Matrix test[] = color.get(totest); 
		  boolean[] proj= new boolean[color.size()];
		 
		  int [] ap = {0};
		  List<Matrix> range = RDiag;
		  if(clr =='G' || clr == 'g')
				 range = GDiag;
		  if(clr =='B' || clr == 'b')
				 range = BDiag; 
		  int cnt=0; 
		  for(Matrix[]matsm: color){
			  Matrix mc = range.get(cnt); 
			  if(cnt==totest)proj[cnt]=true;
			  
			  else proj[cnt] = calcProj(test,matsm,clr, mc );// only the first PC of test
			  cnt++; 
		  }
		  return proj; 
	  }
	  /**Applies allproj to the RGB lists of videos
	   *Return the results of comparing the videos in the list with 
	   * element index totest.
	   */
	  public  List<String> allvideos(){
		  boolean blue[] =allproj(Brnk, totest, 'B');
		  boolean red[] =allproj(Rrnk, totest, 'R');
		  boolean green[] =allproj(Grnk, totest, 'G');
		  List<String> similar = new ArrayList<String>(); 
		  for(int n=0; n < blue.length; ++n)
			  if(blue[n] & red[n] & green[n] & n != totest)
				  similar.add(thevideos.get(n));
		  return similar; 
				  
	  }
	  /**
	   * Compares the projection of the elements of the two matrices arrays.
	   * The PC of each matrix RGB are stored in array of matrices test and basis
	   * The first PC corresponds to index = 0 and by restricting howmany=1,
	   * we only calculate the projection of basis with the first PC of test.  
	   * Returns the result of projection according to comparisison to threshold 
	   */
	  public boolean calcProj(Matrix[] test, Matrix[] basis,
			  char color, Matrix matchcolor){
		  int tln = test.length;
		  int bln = basis.length;
		  int howmany [] = PCorder; 
		  if(tln != bln || howmany.length > test.length){
			mLog.warning("Error: matrices should have same rank");
			  return false; 
		  }
		  boolean sameVideo = true; 
		  
		  
		 double tot=0;
		 double [] ccc = (matchcolor.getArrayCopy())[0];
		 
		 double [] ttt = RDiag.get(totest).getArrayCopy()[0]; 
		 if(color=='B'|| color == 'b')
			 ttt = BDiag.get(totest).getArrayCopy()[0];
		 if(color=='G'|| color == 'g')
			 ttt = GDiag.get(totest).getArrayCopy()[0];
		 double fttt = ttt[0]+1.e-10; 
		 
		 double fff = ccc[0]; //largest eigenvalue
		 independent:for(int nb : howmany){
			 
		 double eignb = ccc[nb];
		 eignb = eignb/(fff+1.e-10); 
		 mLog.info("Eigenb " +eignb); 
		 if(eignb < 1.e-20) break;
	     mLog.info("Weight= "+ eignb);
		 
		 double [] projbasis = new double[bln];  
		 double fbnorm =0; 
		for(int nt=0; nt < tln; ++nt){
		     double scal = ttt[nt]/fttt;
		     if(scal < 1.e-20) break;
		          mLog.info("Scale "+ scal); 
				  ProjSVD p = new ProjSVD(test[nt], basis[nb]);
                  double tmp= p.project()[0]; 
				   
                  if(tmp > fbnorm)
                	  fbnorm = tmp*scal;//weigth largest eigenvalues  
                  projbasis[nb]+= tmp*tmp; 
               
		}
			  projbasis[nb] = Math.sqrt(projbasis[nb])*eignb;//weigth the largest eigenvalues
			  mLog.info("Res PC order= "+ nb + "; projection = " +projbasis[nb]);
			 if(projbasis[nb] < threshold && howmany.length ==1) sameVideo = false; 

			 tot += projbasis[nb]; 
			  }
			  
	     if (tot < threshold)sameVideo = false; 
		  return sameVideo; 
}
	  public static void prUsage(String mess) {
	    	String mess2 =  "Wrong number of frames in MedianTest";
	    	JOptionPane.showMessageDialog(null, mess, "Median Failed",
					JOptionPane.ERROR_MESSAGE);
		mLog.warning(mess);
	    }
	  /**Because the algorithm is memory intensive we can only compare
	   * two videos each computation.  
	  */
	  public  static List<String> memoryHeap(int howmany, 
			  List<String> thevideos)
	  {
		  List<String> tmpvid = new ArrayList<String>(); 
	   		 
	   		prUsage("Memory-heap:Only the first "+ howmany +" videos are tested");
	   		for(int p=0; p < howmany; ++p)
	   		tmpvid.add(thevideos.get(p));
	   		
	   		return tmpvid; 
	  }
	 
	public static void main(String[] args) throws SQLException{
		MatrixRetrieve vstore = new MatrixRetrieve("TbVideoColt");
		if(args.length<=0){
			prUsage("Provide the videos to test"); 	
			
				return;
			}
		List<String> thevideos = new ArrayList(); 
		//videos are input as arguments of main
		 if(args.length > 1) {
			 vstore.retreiveMat(args);
			 for(int a=0; a < args.length; ++a)
			 thevideos.add(args[a]);
		 }
		 int cnt = args.length;
		 //videos are input in a file
	    if (args.length == 1){ //reading from file
	   	 args[0].trim(); 
	   	 thevideos = VideoFile.readFile(args[0]);
	   	 if(thevideos.size()> 2){
	   	    thevideos=memoryHeap(2, thevideos); 
	   	 }
	    }
	   		 
	   	 
	   	 String [] argsf = thevideos.toArray(new String[thevideos.size()]);  
	   	 vstore.retreiveMat(argsf);
	   	 cnt = argsf.length; 
	    
	    List<Matrix> green = vstore.getGreenvid();
	    List<Matrix> red = vstore.getRedvid();
	    List<Matrix> blue = vstore.getBluevid();
	    SVDVideo vid = new SVDVideo(thevideos); 
	    vid.rnkFrmTest(red,green, blue);

	   List<String> similar = vid.allvideos();
	   //output the results
	   String vidtotest = thevideos.get(totest);
	   
	   String res ="Video to test: "+ vidtotest; 
	   for(String str:similar)
		   res += "\n" + str; 
	   mLog.info(res.toString()); 
	   
	   }
	}

