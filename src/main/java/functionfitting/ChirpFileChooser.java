package functionfitting;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ChirpFileChooser extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panelCont = new JPanel();
	JPanel panelIntro = new JPanel();
	JFileChooser chooserA;
	String choosertitleA;
	File[] AllMovies;
	public NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

	public ChirpFileChooser() {
		new InteractiveChirpFit().run(null);
	}

	protected class MeasureserialListener implements ActionListener {
		final Frame parent;

		public MeasureserialListener(Frame parent) {
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent arg0) {
			chooserA = new JFileChooser();

			chooserA.setCurrentDirectory(new File("."));
			chooserA.setDialogTitle(choosertitleA);
			chooserA.setFileSelectionMode(2);


			chooserA.showOpenDialog(parent);

			AllMovies = chooserA.getSelectedFile().listFiles(new FilenameFilter() {
				public boolean accept(File pathname, String filename) {
					return filename.endsWith(".csv");
				}

			});
			new InteractiveChirpFit(AllMovies).run(null);
		}
	}

	protected class FrameListener extends WindowAdapter {
		final Frame parent;

		public FrameListener(Frame parent) {
			this.parent = parent;
		}

		public void windowClosing(WindowEvent e) {
			close(parent);
		}
	}

	protected final void close(Frame parent) {
		if (parent != null) {
			parent.dispose();
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 300);
	}
}
