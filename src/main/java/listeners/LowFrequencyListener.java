package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import functionfitting.InteractiveChirpFit;

public class LowFrequencyListener implements AdjustmentListener {

	final InteractiveChirpFit parent;
	final Label  label;
	
	public LowFrequencyListener(final InteractiveChirpFit parent, final Label label, final Scrollbar bar){
		
		this.parent = parent;
		this.label = label;
		
	}
	@Override
	
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
		
		parent.Lowfrequ = InteractiveChirpFit.computeValueFromScrollbarPosition(
				event.getValue(),
				InteractiveChirpFit.MIN_FREQU,
				InteractiveChirpFit.MAX_FREQU,
				
				
				InteractiveChirpFit.MAX_SLIDER
				);
		label.setText( "Low Frequency  = " + parent.Lowfrequ );
	}
	
}
