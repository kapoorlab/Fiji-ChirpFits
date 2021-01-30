package listeners;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import functionfitting.InteractiveChirpFit;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class MakehistListener implements ActionListener {

	final InteractiveChirpFit parent;
	protected double min, max;

	public MakehistListener(final InteractiveChirpFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		makehistogram();
	}

	public void makehistogram() {
		final XYSeriesCollection dataset = new XYSeriesCollection();
		List<Double> Lowfrequvalues = new ArrayList<Double>();
		List<Double> Highfrequvalues = new ArrayList<Double>();
		List<Double> Meanfrequvalues = new ArrayList<Double>();
		XYSeries seriesname = new XYSeries("Frequency Histogram");

		for (final Pair<Double, Double> key : parent.frequchirphist) {

			seriesname.add(key.getA(), key.getB());
			Lowfrequvalues.add(key.getA());
			Highfrequvalues.add(key.getB());
			Meanfrequvalues.add((key.getA() + key.getB()) / 2);

		}

		dataset.addSeries(seriesname);

		
		final JFreeChart histMeanchart = makehistChart(Meanfrequvalues, "Histogram", "MeanFrequ", "Count");
		
		display(histMeanchart, new Dimension(500, 500));
		

	}

	
	
	
	public JFreeChart makehistXChart(final List<Double> Xdataset) {
		return makehistXChart(Xdataset, "Histogram", "LowFrequ", "Count");
	}

	public static ValuePair<Double, Double> getMinMax(final List<Double> data) {
		// compute min/max/size
		double min = data.get(0);
		double max = data.get(0);

		for (final double v : data) {
			min = Math.min(min, v);
			max = Math.max(max, v);
		}

		return new ValuePair<Double, Double>(min, max);
	}

	public IntervalXYDataset createDataset(final List<Double> values, final String title) {
		final XYSeries series = new XYSeries(title);

		final ValuePair<Double, Double> minmax = getMinMax(values);
		this.min = minmax.getA();
		this.max = minmax.getB();

		final List<ValuePair<Double, Integer>> hist = binData(values, min, max);

		for (final ValuePair<Double, Integer> pair : hist)
			series.add(pair.getA(), pair.getB());

		final XYSeriesCollection dataset = new XYSeriesCollection(series);
		dataset.setAutoWidth(true);

		return dataset;
	}

	public JFreeChart makehistChart(final List<Double> Xdataset, final String title, final String x, final String y) {

		final IntervalXYDataset SigmaXdataset = createDataset(Xdataset, title);

		final JFreeChart sigmaXchart = createChart(SigmaXdataset, title, x);

		return sigmaXchart;
	}
	
	public JFreeChart makehistXChart(final List<Double> Xdataset, final String title, final String x, final String y) {

		final IntervalXYDataset SigmaXdataset = createDataset(Xdataset, title);

		final JFreeChart sigmaXchart = createChart(SigmaXdataset, title, x);

		return sigmaXchart;
	}

	public JFreeChart makehistYChart(final List<Double> Ydataset) {
		return makehistXChart(Ydataset, "Histogram", "HighFrequ", "Count");
	}

	public JFreeChart makehistYChart(final List<Double> Ydataset, final String title, final String x, final String y,
			final int numBins) {

		final IntervalXYDataset SigmaYdataset = createDataset(Ydataset,  title);

		final JFreeChart sigmaYchart = createChart(SigmaYdataset, title, x);

		return sigmaYchart;
	}

	public  List<ValuePair<Double, Integer>> binData(final List<Double> data, final double min, final double max) {
		// avoid the one value that is exactly 100%
		final double size = max - min + 0.000001;

		// bin and count the entries
		final int[] bins = new int[parent.numBins];

		for (final double v : data)
			++bins[(int) Math.floor(((v - min) / size) * parent.numBins)];

		// make the list of bins
		final ArrayList<ValuePair<Double, Integer>> hist = new ArrayList<ValuePair<Double, Integer>>();

		final double binSize = size / parent.numBins;
		for (int bin = 0; bin < parent.numBins; ++bin)
			hist.add(new ValuePair<Double, Integer>(min + binSize / 2 + binSize * bin, bins[bin]));

		return hist;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	protected JFreeChart createChart(final IntervalXYDataset dataset, final String title, final String units) {
		final JFreeChart chart = ChartFactory.createXYBarChart(title,  units +  "[hours]", false, "Count", dataset,
				PlotOrientation.VERTICAL, false, // legend
				false, false);

		NumberAxis range = (NumberAxis) chart.getXYPlot().getDomainAxis();
		range.setRange(getMin(), getMax());

		XYPlot plot = chart.getXYPlot();
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

		renderer.setSeriesPaint(0, Color.red);
		renderer.setDrawBarOutline(true);
		renderer.setSeriesOutlinePaint(0, Color.black);
		renderer.setBarPainter(new StandardXYBarPainter());

		return chart;
	}

	public static void setColor(final JFreeChart chart, final int seriesIndex, final Color col) {
		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(seriesIndex, col);
	}

	public static void setStroke(final JFreeChart chart, final int seriesIndex, final float stroke) {
		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesStroke(seriesIndex, new BasicStroke(stroke));
	}

	public static void setShape(final JFreeChart chart, final int seriesIndex, final Shape shape) {
		final XYPlot plot = chart.getXYPlot();
		final XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesShape(seriesIndex, shape);
	}



	public static void setDisplayType(final JFreeChart chart, final int seriesIndex, final boolean line,
			final boolean shape) {
		final XYPlot plot = chart.getXYPlot();
		final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setSeriesLinesVisible(seriesIndex, line);
		renderer.setSeriesShapesVisible(seriesIndex, shape);
	}

	public static JFrame display(final JFreeChart chart) {
		return display(chart, new Dimension(800, 500));
	}

	public static JFrame display(final JFreeChart chart, final Dimension d) {
		final JPanel panel = new JPanel();
		final ChartPanel chartPanel = new ChartPanel(chart, d.width - 10, d.height - 35,
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,
				ChartPanel.DEFAULT_BUFFER_USED, true, // properties
				true, // save
				true, // print
				true, // zoom
				true // tooltips
		);
		panel.add(chartPanel);

		final JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.validate();
		frame.setSize(d);

		frame.setVisible(true);
		return frame;
	}

}
