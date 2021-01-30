package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import functionfitting.InteractiveChirpFit;
import chirpModels.UserChirpModel.UserModel;



	
	
	public class ModelListener implements ActionListener {

		
		
		final InteractiveChirpFit parent;
		final JComboBox<String> choice;
		
		public ModelListener(final InteractiveChirpFit parent, final JComboBox<String> choice){
			
			this.parent = parent;
			this.choice = choice;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			
			int selectedindex = choice.getSelectedIndex();
			
			
				
				parent.model = UserModel.LinearPolyAmp;
				parent.setdegreeenabled(true);
			
			
			
			
			
			
			
			
			
		}
	
}
