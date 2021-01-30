package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import functionfitting.ExtractSeries;
import functionfitting.InteractiveChirpFit;
import functionfitting.Split;

public class AutoListener implements ActionListener {
	
	final InteractiveChirpFit parent;
	
	public AutoListener(final InteractiveChirpFit parent){
		
		this.parent = parent;
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		int nThreads = 1;
		// set up executor service
		final ExecutorService taskexecutor = Executors.newFixedThreadPool(nThreads);
		for (int trackindex = parent.row + 1; trackindex < parent.inputfiles.length; ++trackindex){
			
			taskexecutor.execute(new Split(parent, trackindex));
			
		
		}
        taskexecutor.shutdown();
       
		
	}
	
	
	
	
	

}
