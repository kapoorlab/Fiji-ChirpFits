package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import functionfitting.InteractiveChirpFit;


public class Enablehigh implements ItemListener {

	final InteractiveChirpFit parent;

	
	
	public Enablehigh(final InteractiveChirpFit parent) {

		this.parent = parent;
		
	}

	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.enableHigh = false;

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.enableHigh = true;

			

		}

	}
	
}
