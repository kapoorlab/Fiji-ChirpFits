package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import functionfitting.InteractiveChirpFit;
import chirpModels.UserChirpModel.UserModel;




public class RunPolyListener implements ItemListener {

	
	public InteractiveChirpFit parent;
	
	public RunPolyListener(InteractiveChirpFit parent) {
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		parent.model = UserModel.LinearPolyAmp;
		parent.setdegreeenabled(true);
		
	}

}
