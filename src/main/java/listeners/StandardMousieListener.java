package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import functionfitting.InteractiveChirpFit;

/**
 * Updates when mouse is released
 * 
 * @author spreibi
 *
 */
public class StandardMousieListener implements MouseListener
{
	final InteractiveChirpFit parent;

	public StandardMousieListener( final InteractiveChirpFit parent )
	{
		this.parent = parent;
	}
	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		try { Thread.sleep( 10 ); } catch ( InterruptedException e ) {}
		parent.updateCHIRP();
	}
	@Override
	public void mousePressed( MouseEvent arg0 ){}
	@Override
	public void mouseExited( MouseEvent arg0 ) {}
	@Override
	public void mouseEntered( MouseEvent arg0 ) {}
	@Override
	public void mouseClicked( MouseEvent arg0 ) {}
}
