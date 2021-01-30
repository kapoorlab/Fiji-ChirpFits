package functionfitting;

import javax.swing.JFrame;

import ij.ImageJ;

public class Chirp_Fit {

	public static void main(String[] args) {

		new ImageJ();

		JFrame frame = new JFrame("");
		ChirpFileChooser panel = new ChirpFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());

	}

}
