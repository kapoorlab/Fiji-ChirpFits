package listeners;

import functionfitting.InteractiveChirpFit;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;

public class MeasureserialListener implements ActionListener {
	final InteractiveChirpFit parent;

	public MeasureserialListener(InteractiveChirpFit parent) {
		this.parent = parent;
	}

	public static class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
		WordWrapCellRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText(value.toString());
			setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
			if (table.getRowHeight(row) != getPreferredSize().height) {
				table.setRowHeight(row, getPreferredSize().height);
			}
			return this;
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		parent.chooserA = new JFileChooser();

		parent.chooserA.setCurrentDirectory(new File("."));
		parent.chooserA.setDialogTitle(parent.choosertitleA);
		parent.chooserA.setFileSelectionMode(2);

		parent.chooserA.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Track Files", new String[] { "txt" });

		parent.chooserA.setFileFilter(filter);
		parent.chooserA.showOpenDialog(parent.Cardframe);

		parent.inputfiles = parent.chooserA.getSelectedFile().listFiles(new FilenameFilter() {

			public boolean accept(File pathname, String filename) {
				return filename.endsWith(".txt");

			}

		});
		Object[] colnames = { "Track File" };

		Object[][] rowvalues = new Object[parent.inputfiles.length][colnames.length];

		for (int i = 0; i < parent.inputfiles.length; i++) {
			rowvalues[i][0] = parent.inputfiles[i].getName();
		}

		parent.table = new JTable(rowvalues, colnames);
		parent.table.setFillsViewportHeight(true);
		parent.table.setAutoResizeMode(0);
		parent.table.getColumnModel().getColumn(0).setPreferredWidth(100);
		parent.table.getColumnModel().getColumn(0).setResizable(true);
		parent.table.setCellSelectionEnabled(true);
		parent.scrollPane = new JScrollPane(parent.table);
		parent.scrollPane.setVerticalScrollBarPolicy(22);
		parent.scrollPane.setHorizontalScrollBarPolicy(32);
		parent.table.setFillsViewportHeight(true);

		parent.table.getColumnModel().getColumn(0).setPreferredWidth(200);

		parent.scrollPane.setMinimumSize(new Dimension(300, 200));
		parent.scrollPane.setPreferredSize(new Dimension(300, 200));
		parent.table.setFillsViewportHeight(true);
		parent.scrollPane.getViewport().add(parent.table);
		parent.scrollPane.setAutoscrolls(true);

		parent.Panelfile.removeAll();
		parent.Panelfile.add(parent.scrollPane,
				new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 2, InteractiveChirpFit.insets, 0, 0));
		parent.Panelfile.add(parent.AutoFit,
				new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 17, 2, InteractiveChirpFit.insets, 0, 0));
		parent.Panelfile.add(parent.inputLabelBins,
				new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 17, 2, InteractiveChirpFit.insets, 0, 0));
		parent.Panelfile.add(parent.inputFieldBins,
				new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 17, 2, InteractiveChirpFit.insets, 0, 0));
		parent.Panelfile.add(parent.Frequhist,
				new GridBagConstraints(0, 5, 1, 1, 0.0D, 0.0D, 17, 2, InteractiveChirpFit.insets, 0, 0));
		parent.Panelfile.setBorder(parent.selectfile);
		parent.panelFirst.add(parent.Panelfile,
				new GridBagConstraints(1, 3, 3, 1, 0.0D, 0.0D, 17, -1, new Insets(10, 10, 0, 10), 0, 0));

		if (parent.inputfiles != null) {
			parent.table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						JTable target = (JTable) e.getSource();
						parent.row = target.getSelectedRow();

						if (parent.row > 0) {
							parent.displayclicked(parent.row);
						} else {
							parent.displayclicked(0);
						}
					}
				}
			});
		}
		parent.Panelfile.validate();
		parent.panelFirst.validate();
		parent.Cardframe.validate();
	}
}
