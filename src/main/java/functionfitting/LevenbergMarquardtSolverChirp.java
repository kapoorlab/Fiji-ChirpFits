package functionfitting;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JProgressBar;
import Jama.Matrix;
import chirpModels.ChirpFitFunction;
import chirpModels.UserChirpModel.UserModel;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class LevenbergMarquardtSolverChirp {

	final InteractiveChirpFit parent;
	final JProgressBar jpb ;
	public LevenbergMarquardtSolverChirp(final InteractiveChirpFit parent, JProgressBar jpb){
		
		this.parent = parent;
		this.jpb = jpb;
	}
	 double percent = 0;
	
	

	
	/**
	 * Calculate the current sum-squared-error
	 */
	public  final double chiSquared(
			final double[] x, 
			
			final double[] a, 
			final int totaltime,
			final double[] y, 
			final ChirpFitFunction f)  {
		
		int npts = y.length;
		double sum = 0.;

		for( int i = 0; i < npts; i++ ) {
			double d = y[i] - f.val(x[i], a, totaltime, i, parent.degree);
			sum = sum + (d*d);
		}

		return sum;
	} //chiSquared

	/**
	 * Minimize E = sum {(y[k] - f(x[k],a)) }^2
	 * Note that function implements the value and gradient of f(x,a),
	 * NOT the value and gradient of E with respect to a!
	 * 
	 * @param x array of domain points, each may be multidimensional
	 * @param y corresponding array of values
	 * @param a the parameters/state of the model
	 * @param lambda blend between steepest descent (lambda high) and
	 *	jump to bottom of quadratic (lambda zero). Start with 0.001.
	 * @param termepsilon termination accuracy (0.01)
	 * @param maxiter	stop and return after this many iterations if not done
	 *
	 * @return the number of iteration used by minimization
	 */
	public  final int solve(
			double[] x, 
			ArrayList<Pair<Double, Double>> timeseries,
			double[] a,
			int totaltime,
			double[] y, 
			ChirpFitFunction f,
			double lambda, 
			double termepsilon, 
			int maxiter, int fileindex, int totalfiles, UserModel model
			) throws Exception  {
		int npts = y.length;
		int nparm = a.length;
	
		
		double e0 = chiSquared(x, a, totaltime, y, f);
		
		//System.out.println(e0);
		boolean done = false;

		// g = gradient, H = hessian, d = step to minimum
		// H d = -g, solve for d
		double[][] H = new double[nparm][nparm];
		double[] g = new double[nparm];

		int iter = 0;
		int term = 0;	// termination count test
	
		do {
			++iter;
			// hessian approximation
			for( int r = 0; r < nparm; r++ ) {
				for( int c = 0; c < nparm; c++ ) {
					H[r][c] = 0.;
					for( int i = 0; i < npts; i++ ) {
						double xi = x[i];
						
						H[r][c] += f.grad(xi, a, totaltime, r , i, parent.degree) * f.grad(xi, a, totaltime, c , i, parent.degree);
					}  //npts
				} //c
			} //r
			percent = (Math.round(100 * (iter + 1) / (maxiter)));
			// boost diagonal towards gradient descent
			for( int r = 0; r < nparm; r++ )
				H[r][r] *= (1.0 + lambda);

			// gradient
			for( int r = 0; r < nparm; r++ ) {
				g[r] = 0.;
				for( int i = 0; i < npts; i++ ) {
					double xi = x[i];
					g[r] += (y[i]-f.val(xi,a, totaltime, i, parent.degree)) * f.grad(xi, a, totaltime, r, i, parent.degree);
				
				}
				
			} //npts
		

			FitterUtils.SetProgressBarTime(jpb, iter, percent, fileindex + 1, totalfiles - 1);
			// solve H d = -g, evaluate error at new location
			//double[] d = DoubleMatrix.solve(H, g);
			double[] d = null;
			try {
				d = (new Matrix(H)).lu().solve(new Matrix(g, nparm)).getRowPackedCopy();
				
				
			} catch (RuntimeException re) {
				// Matrix is singular
				lambda *= 10.;
				continue;
			}
			
			
			
			
			
			double[] na = (new Matrix(a, nparm)).plus(new Matrix(d, nparm)).getRowPackedCopy();
			double e1 = chiSquared(x, na, totaltime, y, f);
			//System.out.println(iter+ " " + lambda+ " "+ Math.abs(e1-e0));
			// termination test (slightly different than NR)
			if (Math.abs(e1-e0) > termepsilon) {
				term = 0;
			}
			else {
				
				term++;
				if (term == 4)
					done = true;
				
			}
		
			if (iter > maxiter -1)
			System.out.println("LM solver unable to find extrema after" + iter + " iterations");
			if (iter >= maxiter) done = true;

			
			
			// in the C++ version, found that changing this to e1 >= e0
			// was not a good idea.  See comment there.
			//
			if (e1 > e0 || Double.isNaN(e1) ) { // new location worse than before
				lambda *= 10;
				
			}
			else {		// new location better, accept new parameters
				lambda *= 0.1;
			
				e0 = e1;
				// simply assigning a = na will not get results copied back to caller
				for( int i = 0; i < nparm; i++ ) {
					a[i] = na[i];
					
				}
			}
		
			
			if (iter%10 == 0 || iter == 1){
				if (parent.dataset!=null)
					parent.dataset.removeAllSeries();
				
			
					
				
					if (parent.dataset!=null)
						parent.dataset.removeAllSeries();
					parent.frequchirphist.add(new ValuePair<Double, Double> (((na[parent.degree + 1]) ),((na[parent.degree + 2]) )   ));
					
					double poly;
					final ArrayList<Pair<Double, Double>> fitpoly = new ArrayList<Pair<Double, Double>>();
					
					
					
						
						
					for (int i = 0; i < timeseries.size(); ++i) {
						double polynom = 0;
						Double time = timeseries.get(i).getA();

						
						for (int j = parent.degree; j>=0; --j){
							polynom+= na[j] * Math.pow(time, j);
							
						}
						poly = polynom
								* Math.cos(Math.toRadians(na[parent.degree + 1] * time
										+ (na[parent.degree + 2] -na[parent.degree + 1]) * time * time
												/ (2 * totaltime)
										+ na[parent.degree + 3])) + na[parent.degree + 4] ;
						fitpoly.add(new ValuePair<Double, Double>(time, poly));
					}
					parent.dataset.addSeries(Mainpeakfitter.drawPoints(timeseries));
					parent.dataset.addSeries(Mainpeakfitter.drawPoints(fitpoly, "Fits"));
					Mainpeakfitter.setColor(parent.chart, 1, new Color(255, 255, 64));
					Mainpeakfitter.setStroke(parent.chart, 1, 2f);
					  Mainpeakfitter.setColor(parent.chart, 0, new Color(64, 64, 64));
				       Mainpeakfitter.setStroke(parent.chart, 0, 2f);
				       Mainpeakfitter.setDisplayType(parent.chart, 0, false, true);
				
			}
			
		} while(!done);
	//	bw.close();
	//	fw.close();
	
		return iter;
	} //solve
	
	
	
}
