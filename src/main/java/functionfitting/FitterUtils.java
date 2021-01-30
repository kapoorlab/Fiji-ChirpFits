package functionfitting;

import java.util.List;

import javax.swing.JProgressBar;

import net.imglib2.PointSampleList;
import net.imglib2.type.numeric.real.FloatType;

public class FitterUtils {

	public static void SetProgressBarTime(JProgressBar jpb, int iteration, double percent, int fileindex, int totalfiles) {

		jpb.setValue((int) percent);
		jpb.setOpaque(true);
		jpb.setStringPainted(true);
		jpb.setString("Processing File: " + " " + fileindex +  "/" + " " + totalfiles+ " " + "Iteration = " + iteration);
        jpb.setVisible(true);
        jpb.setIndeterminate(false);
		
	}
	

	
}
