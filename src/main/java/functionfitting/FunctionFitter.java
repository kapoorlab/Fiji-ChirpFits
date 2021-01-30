package functionfitting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import chirpModels.ChirpFitFunction;
import chirpModels.LinearChirp;
import chirpModels.LinearChirpPolyAmp;
import chirpModels.UserChirpModel.UserModel;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class FunctionFitter extends SwingWorker<Void, Void> {
	
	final InteractiveChirpFit parent;
	final ArrayList<Pair<Double, Double>> timeseries;
	private final UserModel model;
	public int maxiter = 50000;
	public double lambda = 1e-4;
	public double termepsilon = 1e-9;
	ValuePair<double[], double[]> FLMparam;
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
		if (parent.dataset!=null)
			parent.dataset.removeAllSeries();
		// Run the gradient descent using Chirp function fit
		double[] T = new double[timeseries.size()];
		double[] I = new double[timeseries.size()];

		
		
		System.out.println(Lowfrequency + " " + Highfrequency);
		
		for (int i = 0; i < timeseries.size(); ++i){
			
			T[i] = timeseries.get(i).getA();
			I[i] = timeseries.get(i).getB(); 
		}
		
	
		ChirpFitFunction UserChoiceFunction = null;
		if (model == UserModel.Linear){
			
			UserChoiceFunction = new LinearChirp();
			
		}
		int totaltime = timeseries.size();
		
		if (model == UserModel.LinearPolyAmp){
     	   
     	   UserChoiceFunction = new LinearChirpPolyAmp();
        }

        
        if (model!= UserModel.LinearPolyAmp)
		FLMparam = ExtractSeries.initialguess(timeseries, timeseries.size(), 0,  Lowfrequency, Highfrequency, model);
        
        else
    		FLMparam = ExtractSeries.initialguess(timeseries, timeseries.size(), degree,  Lowfrequency, Highfrequency, UserModel.LinearPolyAmp);  
        double[] Fixedparam = FLMparam.getA();
    	double[] LMparam = FLMparam.getB();
        if (model == UserModel.Linear){
			
        	
        	
			System.out.println("Frequency (hrs):" + ((LMparam[0]) ));
			System.out.println("Chirp Frequ  (hrs):" + ((LMparam[1]) ));
			System.out.println("Phase:" + ((LMparam[2])));
			System.out.println("Back:" + ((LMparam[3])));


			System.out.println("Frequency :" + LMparam[0]);
			System.out.println("Chirp Frequ  :" + LMparam[1]);
			System.out.println("Phase:" + ((LMparam[2])));
			System.out.println("Back:" + ((LMparam[3])));
			
			
		
			
			parent.frequchirphist.add(new ValuePair<Double, Double> (((LMparam[0])),((LMparam[1]) )   ));
			
		
			
		       
		}
           
		try {
			
			LevenbergMarquardtSolverChirp LMsolver = new LevenbergMarquardtSolverChirp(parent, parent.jpb);
			
			LMsolver.solve(T,timeseries, LMparam, Fixedparam, timeseries.size(), I, UserChoiceFunction, lambda,
					termepsilon, maxiter, fileindex, totalfiles, model);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
			
		if (model == UserModel.LinearPolyAmp){
			parent.rtAll.incrementCounter();
			parent.rtAll.addValue("Low Frequency:" , ((LMparam[degree + 1]) ));
			parent.rtAll.addValue("High Frequency :" , ((LMparam[degree + 2]) ));
			parent.rtAll.show("Frequency by Chirp Model Fits");
		
		}
		else {
			parent.rtAll.incrementCounter();
			parent.rtAll.addValue("Fixed Amp Low Frequency:" , ((LMparam[0]) ));
			parent.rtAll.addValue("Fixed Amp High Frequency :" , ((LMparam[1]) ));
			parent.rtAll.show("Frequency by Chirp Model Fits");
			
		}
			
		
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
