package functionfitting;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import chirpModels.ChirpFitFunction;
import chirpModels.UserChirpModel;
import chirpModels.UserChirpModel.UserModel;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class ExtractSeries {

	
	public static ArrayList< Pair< Double, Double > > gatherdata( final File file )
	{
		final ArrayList< Pair< Double, Double > > points = new ArrayList< Pair< Double, Double > >();

		try
		{
			BufferedReader in = Util.openFileRead( file );

			while( in.ready() )
			{
				String line = in.readLine().trim();

				while ( line.contains( "\t\t" ) )
					line = line.replaceAll( "\t\t", "\t" );

				if ( line.length() >= 3 && line.matches( "[0-9].*" ) )
				{
					final String[] split = line.trim().split( "," );

					final double timepoint = Double.parseDouble( split[ 0 ] );
					final double value = Double.parseDouble( split[ 1 ] );

					points.add( new ValuePair< Double, Double >( timepoint, value ) );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;
		}

		Collections.sort( points, new Comparator< Pair< Double, Double > >()
		{
			
			public int compare( final Pair< Double, Double > o1, final Pair< Double, Double > o2 )
			{
				return o1.getA().compareTo( o2.getA() );
			}
		} );

		return points;
	}
	
	

	
	public static Pair<Double, Double> minmax (ArrayList< Pair< Double, Double > > points){
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		
		for (final Pair< Double, Double > p : points){
			
			min = Math.min(min, p.getB());
			max = Math.max(max, p.getB());
		}
		
		Pair<Double, Double> minmax = new ValuePair<Double,Double>(min, max);
		
		return minmax;
	}

	public static ValuePair<double[], double[]> initialguess(ArrayList< Pair< Double, Double > > points,
			final int totaltime, final int degree, double Lowfrequency, double Highfrequency, UserModel model){
		
	
		if (model == UserModel.Linear){
		double[] initialparameters = new double[4];
		double[] fixedparameters = new double[totaltime];
		
		
		
		double Frequency = Lowfrequency;
		double endChirp =  Highfrequency ;
		
		double phase = 0;
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		
		for (final Pair< Double, Double > p : points){
			
			min = Math.min(min, p.getB());
			max = Math.max(max, p.getB());
		}
		
			
			
		for (int index = 0; index < points.size(); ++index){
			fixedparameters[index] = points.get(index).getB();
		}
		initialparameters[0] = Frequency;
		initialparameters[1] = endChirp;
		initialparameters[2] = phase;
		initialparameters[3] = min;
		
		return new ValuePair<double[], double[]>(fixedparameters, initialparameters);
		}
		
	    if (model == UserModel.LinearPolyAmp){
			
			double[] initialparameters = new double[degree + 5];
			double[] fixedparameters = null;
			
			
			
			double Frequency = Lowfrequency;
			double endChirp =  Highfrequency ;
			
			double phase = 0;
			
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			
			
			for (final Pair< Double, Double > p : points){
				
				min = Math.min(min, p.getB());
				max = Math.max(max, p.getB());
			}
			
		
				
			for (int j = degree; j > 0; --j){
				
				initialparameters[j] = (max - min);
			}
			
			initialparameters[0] = (max - min);
			
			
			initialparameters[degree + 1] = Frequency;
			initialparameters[degree + 2] = endChirp;
			initialparameters[degree + 3] = phase;
			initialparameters[degree + 4] = min;
			
			return new ValuePair<double[], double[]>(fixedparameters, initialparameters);
			
			
		}
			
	    else return null;
		
    

		
		
	}
	
	
}
