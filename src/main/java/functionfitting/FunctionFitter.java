package functionfitting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import chirpModels.ChirpFitFunction;
import chirpModels.LinearChirpPolyAmp;
import chirpModels.UserChirpModel.UserModel;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class FunctionFitter extends SwingWorker<Void, Void> {
	
	final InteractiveChirpFit parent;
	final ArrayList<Pair<Double, Double>> timeseries;
	private final UserModel model;
	public int maxiter = 50000;
	public double lambda = 1e-3;
	public double termepsilon = 1e-4;
	double[] LMparam;
	public double Lowfrequency = 0.02;
	public double Highfrequency = 0.03;
	public final int fileindex;
	public final int totalfiles;
	public final int degree;
	
	
	
	public void setMaxiter(int maxiter) {
		this.maxiter = maxiter;
	}

	public int getMaxiter() {
		return maxiter;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double getLambda() {
		return lambda;
	}

	public void setTermepsilon(double termepsilon) {
		this.termepsilon = termepsilon;
	}

	public double getTermepsilon() {
		return termepsilon;
	}
	
	public void setLowfrequency(double Lowfrequency) {

		this.Lowfrequency = Lowfrequency;

	}

	public double getLowfrequency() {

		return Lowfrequency;
	}
	
	public void setHighfrequency(double Highfrequency) {

		this.Highfrequency = Highfrequency;

	}

	public double getHighfrequency() {

		return Highfrequency;
	}
	
	
	/**
	 * 
	 * @param timeseries input the time series
	 * deltat = spacing in time between succeding points
	 */
	
	public FunctionFitter(final InteractiveChirpFit parent, final ArrayList<Pair<Double, Double>> timeseries, UserModel model, final int fileindex,
			final int totalfiles, final int degree){
		
		this.parent = parent;
		this.timeseries = timeseries;
		this.model = model;
		this.fileindex = fileindex;
		this.totalfiles = totalfiles;
		this.degree = degree;
	
	}
	
	
	public boolean checkInput() {
		
		if (timeseries.size() == 0)
		return false;
		
		return true;
	}

	@Override
	protected Void doInBackground() throws Exception {
		
		// Run the gradient descent using Chirp function fit
		double[] T = new double[timeseries.size()];
		double[] I = new double[timeseries.size()];

		
		
		System.out.println(Lowfrequency + " " + Highfrequency);
		
		for (int i = 0; i < timeseries.size(); ++i){
			
			T[i] = timeseries.get(i).getA();
			I[i] = timeseries.get(i).getB(); 
		}
		
	
		
		ChirpFitFunction UserChoiceFunction = new LinearChirpPolyAmp();

       
       		LMparam = ExtractSeries.initialguess(timeseries, timeseries.size(), degree,  Lowfrequency, Highfrequency, UserModel.LinearPolyAmp);  
           
		System.out.println("paramlength"+ LMparam.length);
		try {
			
			LevenbergMarquardtSolverChirp LMsolver = new LevenbergMarquardtSolverChirp(parent, parent.jpb);
			
			LMsolver.solve(T,timeseries, LMparam, timeseries.size(), I, UserChoiceFunction, lambda,
					termepsilon, maxiter, fileindex, totalfiles, model);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		int totaltime = timeseries.size();
			System.out.println("Frequency :" + ((LMparam[degree + 1]) ));
			System.out.println("Chirp Frequ  :" + ((LMparam[degree + 2]) ));
			System.out.println("Phase:" + ((LMparam[degree + 3])));
			System.out.println("Back:" + ((LMparam[degree + 4])));


			System.out.println("Frequency :" + LMparam[degree + 1]);
			System.out.println("Chirp Frequ  :" + LMparam[degree + 2]);
			System.out.println("Phase:" + ((LMparam[degree + 3])));
			System.out.println("Back:" + ((LMparam[degree + 4])));
			
			parent.rtAll.incrementCounter();
			parent.rtAll.addValue("Low Frequency:" , ((LMparam[degree + 1]) ));
			parent.rtAll.addValue("High Frequency :" , ((LMparam[degree + 2]) ));
			parent.rtAll.show("Frequency by Chirp Model Fits");
		
			if (parent.dataset!=null)
				parent.dataset.removeAllSeries();
			parent.frequchirphist.add(new ValuePair<Double, Double> (((LMparam[degree + 1]) ),((LMparam[degree + 2]))   ));
			
			double poly;
			final ArrayList<Pair<Double, Double>> fitpoly = new ArrayList<Pair<Double, Double>>();
			
			
			
				
				
			for (int i = 0; i < timeseries.size(); ++i) {

				Double time = timeseries.get(i).getA();

				double polynom = 0;
				for (int j = degree; j>=0; --j)
					polynom+= LMparam[j] * Math.pow(time, j);
				
				poly = polynom
						* Math.cos(LMparam[degree + 1] * time
								+ (LMparam[degree + 2] -LMparam[degree + 1]) * time * time
										/ (2 * totaltime)
								+ LMparam[degree + 3]) + LMparam[degree + 4] ;
				fitpoly.add(new ValuePair<Double, Double>(time, poly));
			}
			parent.dataset.addSeries(Mainpeakfitter.drawPoints(timeseries));
			parent.dataset.addSeries(Mainpeakfitter.drawPoints(fitpoly, "Fits"));
			Mainpeakfitter.setColor(parent.chart, 1, new Color(255, 255, 64));
			Mainpeakfitter.setStroke(parent.chart, 1, 2f);
			  Mainpeakfitter.setColor(parent.chart, 0, new Color(64, 64, 64));
		       Mainpeakfitter.setStroke(parent.chart, 0, 2f);
		       Mainpeakfitter.setDisplayType(parent.chart, 0, false, true);
		
		
		return null;
	}

	
	
	@Override
	protected void done() {
		
			parent.jpb.setIndeterminate(false);
			try {
				this.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	}
	
	
	

}
