package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import functionfitting.InteractiveChirpFit;

public class HighFrequencyListener implements AdjustmentListener {

	final InteractiveChirpFit parent;
	final Label  label;
	final Scrollbar bar;
	
	public HighFrequencyListener(final InteractiveChirpFit parent, final Label label, final Scrollbar bar){
		
		this.parent = parent;
		this.label = label;
		this.bar = bar;
		bar.addMouseListener(new StandardMousieListener(parent));
		
	}
	@Override
	public void adjustmentValueChanged( final AdjustmentEvent event )
	{
	
		
		
		parent.Highfrequ = InteractiveChirpFit.computeValueFromScrollbarPosition(
				event.getValue(),
				
				InteractiveChirpFit.MIN_CHIRP,
				InteractiveChirpFit.MAX_CHIRP,
				InteractiveChirpFit.MAX_SLIDER);

		label.setText( "High Frequency (hrs) = " + parent.Highfrequ );
		
	}
	
}
