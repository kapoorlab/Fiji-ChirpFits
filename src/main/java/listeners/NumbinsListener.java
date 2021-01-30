package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import functionfitting.InteractiveChirpFit;


public class NumbinsListener implements TextListener{
	
	final InteractiveChirpFit parent;
	protected double min, max;
	
	public NumbinsListener(final InteractiveChirpFit parent){
		
		this.parent = parent;
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    
			    	String s = tc.getText();
			    	
			    
	                 if (s.length() > 0)		    		
					 parent.numBins = (int)Float.parseFloat(s);
					
					
						
						
			
		
		
		 
		
	}
	
	

}
