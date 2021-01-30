
 package functionfitting;

import ij.IJ;
import ij.ImageJ;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import listeners.AutoListener;
import listeners.DegreeListener;
import listeners.FitListener;
import listeners.HighFrequencyListener;
import listeners.LowFrequencyListener;
import listeners.MakehistListener;
import listeners.MeasureserialListener;
import listeners.NumIterListener;
import listeners.NumbinsListener;
import listeners.RunPolyListener;
import listeners.WidthListener;
import net.imglib2.util.Pair;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import chirpModels.UserChirpModel;
import chirpModels.UserChirpModel.UserModel;

public class InteractiveChirpFit
  implements PlugIn
{
  public String usefolder = IJ.getDirectory("imagej");
  public String addToName = "ChirpFits";
  public final int scrollbarSize = 1000;
  public final int scrollbarSizebig = 1000;
  
  public static int standardSensitivity = 4;
  public int sensitivity = standardSensitivity;
  public ArrayList<Pair<Double, Double>> timeseries;
  public ArrayList<Pair<Double, Double>> frequchirphist;
  int FrequInt;
  int ChirpInt;
  int PhaseInt;
  int BackInt; public boolean polymode = true;
  public boolean randommode = false;
  public boolean isDone;
  public static int MIN_SLIDER = 0;
  public static int MAX_SLIDER = 500;
  public int row;
  public static double MIN_FREQU = 0.0D;
  public static double MAX_FREQU = 30.0D;
  
  public static double MIN_CHIRP = 0.0D;
  public static double MAX_CHIRP = 40.0D;
  public boolean enableHigh = false;
  public double Lowfrequ = 2.6166666666666667D;
  public double Highfrequ = Lowfrequ / 2.0D;
  public double phase = 0.0D;
  public double back = 0.0D;
  
  public int numBins = 10;
  public int degree = 2;
  public JLabel degreelabel = new JLabel("Amplitude Polynomial degree");
  
  public TextField degreetext;
  public int maxiter = 5000;
  public JProgressBar jpb;
  public JLabel label = new JLabel("Fitting..");
  public int Progressmin = 0;
  public int Progressmax = 100;
  public int max = Progressmax;
  public File userfile;
  Frame jFreeChartFrame;
  public NumberFormat nf;
  public XYSeriesCollection dataset;
  JFreeChart chart;
  ResultsTable rtAll;
  public File inputfile;
  public File[] inputfiles;
  public String inputdirectory;
  public JLabel inputLabelwidth;
  public TextField inputFieldwidth;
  public JLabel inputLabelBins;
  public TextField inputFieldBins;
  public JLabel inputLabelIter;
  public TextField inputFieldIter;
  public JTable table;
  
  public InteractiveChirpFit() {
    nf = NumberFormat.getInstance(Locale.ENGLISH);
    nf.setMaximumFractionDigits(3);
  }
  
  public InteractiveChirpFit(File[] file)
  {
    inputfiles = file;
    inputdirectory = file[0].getParent();
    

    nf = NumberFormat.getInstance(Locale.ENGLISH);
    nf.setMaximumFractionDigits(3);
  }
  
  public void run(String arg0) {
    frequchirphist = new ArrayList();
    rtAll = new ResultsTable();
    jpb = new JProgressBar();
    model = UserModel.LinearPolyAmp;
    Card();
  }
  

  public JFrame Cardframe = new JFrame("Welcome to Chirp Fits ");
  public JPanel panelCont = new JPanel();
  public JPanel panelFirst = new JPanel();
  public JComboBox<String> ChooseModel;
  public JPanel Panelmodel = new JPanel();
  private JPanel Panelparam = new JPanel();
  public JPanel Panelfile = new JPanel();
  public static final Insets insets = new Insets(10, 0, 0, 0);
  JPanel PanelDirectory = new JPanel();
  public final GridBagLayout layout = new GridBagLayout();
  public final GridBagConstraints c = new GridBagConstraints();
  public Border selectfile = new CompoundBorder(new TitledBorder("Select file"), new EmptyBorder(c.insets));
  Border selectparam = new CompoundBorder(new TitledBorder("Select Chirp Fit parameters"), new EmptyBorder(c.insets));
  Border Model = new CompoundBorder(new TitledBorder("Select model"), new EmptyBorder(c.insets));
  Border selectdirectory = new CompoundBorder(new TitledBorder("Load directory of TxT files"), new EmptyBorder(c.insets));
  public JScrollPane scrollPane;
  public UserChirpModel.UserModel model;
  public JFileChooser chooserA;
  public String choosertitleA;
  public final JButton AutoFit = new JButton("Auto-Fit all files");
  public final Button Frequhist = new Button("Frequency Histogram");
  

  public void Card()
  {
    CardLayout cl = new CardLayout();
    
    DefaultTableModel userTableModel = new DefaultTableModel(new Object[0], 0)
    {
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    
    if (inputfiles != null) {
      for (int i = 0; i < inputfiles.length; i++)
      {
        String[] currentfile = { inputfiles[i].getName() };
        userTableModel.addRow(currentfile);
      }
    }
    
    table = new JTable(userTableModel);
    
    table.setFillsViewportHeight(true);
    
    table.setAutoResizeMode(0);
    
    scrollPane = new JScrollPane(table);
    scrollPane.setMinimumSize(new Dimension(200, 200));
    scrollPane.setPreferredSize(new Dimension(200, 200));
    

    scrollPane.getViewport().add(table);
    scrollPane.setAutoscrolls(true);
    
    panelCont.setLayout(cl);
    
    panelCont.add(panelFirst, "1");
    
    panelFirst.setName("Chirp Fits");
    Panelmodel.setLayout(this.layout);
    Panelparam.setLayout(this.layout);
    Panelfile.setLayout(this.layout);
    PanelDirectory.setLayout(this.layout);
    

    CheckboxGroup mode = new CheckboxGroup();
    
    Checkbox Polynomial = new Checkbox("Polynomial Amplitude", mode, polymode);
    Checkbox Random = new Checkbox("Random Amplitude", mode, randommode);
    


    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    
    Scrollbar FREQU = new Scrollbar(0, FrequInt, 1, MIN_SLIDER, MAX_SLIDER + 1);
    
    Scrollbar CHIRP = new Scrollbar(0, ChirpInt, 1, MIN_SLIDER, MAX_SLIDER + 1);
    
    Label FREQULabel = new Label("Lower Frequency (hrs) = " + nf.format(Lowfrequ), 1);
    Label CHIRPLabel = new Label("Higher Frequency (hrs) = " + nf.format(Highfrequ), 1);
    
    JButton Fit = new JButton("Fit current file");
    
    inputLabelwidth = new JLabel("Enter expected peak width in hours");
    inputFieldwidth = new TextField();
    inputFieldwidth.setColumns(5);
    inputFieldwidth.setText(String.valueOf(1));
    
    inputLabelBins = new JLabel("Set number of Bins, presss enter to display mean Frequency histogram");
    inputFieldBins = new TextField();
    inputFieldBins.setColumns(5);
    inputFieldBins.setText(String.valueOf(numBins));
    
    inputLabelIter = new JLabel("Set max iteration number");
    inputFieldIter = new TextField();
    inputFieldIter.setColumns(5);
    inputFieldIter.setText(String.valueOf(maxiter));
    
    degreetext = new TextField();
    degreetext.setColumns(5);
    degreetext.setText(String.valueOf(degree));
    


    Highfrequ = (Lowfrequ - Float.parseFloat(inputFieldwidth.getText()));
    
    panelFirst.setLayout(layout);
  
    Button Measureserial = new Button("Select directory of Cell Intensity track files");
    PanelDirectory.add(Measureserial, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    
    PanelDirectory.setBorder(selectdirectory);
    
    panelFirst.add(PanelDirectory, new GridBagConstraints(0, 1, 3, 1, 0.0D, 0.0D, 17, 
      -1, new Insets(10, 10, 0, 10), 0, 0));
    
    Panelmodel.add(Polynomial, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
   
    Panelmodel.add(degreelabel, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelmodel.add(degreetext, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelmodel.setBorder(Model);
    Panelmodel.setPreferredSize(new Dimension(200, 200));
    panelFirst.add(Panelmodel, new GridBagConstraints(0, 2, 3, 1, 0.0D, 0.0D, 17, 
      -1, new Insets(10, 10, 0, 10), 0, 0));
    

    Panelparam.add(FREQULabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelparam.add(FREQU, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelparam.add(inputLabelwidth, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelparam.add(inputFieldwidth, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelparam.add(inputLabelIter, new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelparam.add(inputFieldIter, new GridBagConstraints(0, 5, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    
    Panelparam.setBorder(selectparam);
    
    Panelparam.setPreferredSize(new Dimension(250, 200));
    
    panelFirst.add(Panelparam, new GridBagConstraints(2, 2, 3, 1, 0.0D, 0.0D, 13, 
      13, new Insets(10, 10, 0, 10), 0, 0));
    
    Panelfile.add(scrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelfile.add(AutoFit, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelfile.add(inputLabelBins, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelfile.add(inputFieldBins, new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelfile.add(Frequhist, new GridBagConstraints(0, 5, 1, 1, 0.0D, 0.0D, 17, 
      2, insets, 0, 0));
    Panelfile.setBorder(selectfile);
    panelFirst.add(Panelfile, new GridBagConstraints(2, 3, 3, 1, 0.0D, 0.0D, 17, 
      -1, new Insets(10, 10, 0, 10), 0, 0));
    




    table.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          JTable target = (JTable)e.getSource();
          row = target.getSelectedRow();
          
          if (row > 0) {
            displayclicked(row);
          } else {
            displayclicked(0);
          }
        }
      }
    });
    FREQU.addAdjustmentListener(new LowFrequencyListener(this, FREQULabel, FREQU));
    CHIRP.addAdjustmentListener(new HighFrequencyListener(this, CHIRPLabel, CHIRP));
    Fit.addActionListener(new FitListener(this));
    AutoFit.addActionListener(new AutoListener(this));
    Measureserial.addActionListener(new MeasureserialListener(this));
    Polynomial.addItemListener(new RunPolyListener(this));
    Frequhist.addActionListener(new MakehistListener(this));
    inputFieldwidth.addTextListener(new WidthListener(this));
    inputFieldBins.addTextListener(new NumbinsListener(this));
    degreetext.addTextListener(new DegreeListener(this));
    inputFieldIter.addTextListener(new NumIterListener(this));
    Cardframe.add(panelCont, "Center");
    Cardframe.add(jpb, "Last");
    
    Cardframe.setDefaultCloseOperation(2);
    Cardframe.pack();
    Cardframe.setVisible(true);
    Cardframe.pack();
  }
  
  public void displayclicked(int trackindex)
  {
    inputfile = inputfiles[trackindex];
    inputdirectory = inputfiles[trackindex].getParent();
    timeseries = ExtractSeries.Normalize(ExtractSeries.gatherdata(inputfiles[trackindex]));
    
    dataset = new XYSeriesCollection();
    chart = Mainpeakfitter.makeChart(dataset, "Cell Intensity", "Timepoint", "Normalized Intensity");
    jFreeChartFrame = Mainpeakfitter.display(chart, new Dimension(600, 600));
    
    row = trackindex;
    updateCHIRP();
  }
  


  public void setdegreeenabled(boolean state)
  {
    if (state) {
      if (!degreetext.isEnabled()) {
        degreelabel.setEnabled(state);
        degreetext.setEnabled(state);
      }
      

    }
    else if (degreetext.isEnabled())
    {
      degreelabel.setEnabled(false);
      degreetext.setEnabled(false);
    }
  }
  





  public void displaymuteclicked(int trackindex)
  {
    inputfile = inputfiles[trackindex];
    inputdirectory = inputfiles[trackindex].getParent();
    timeseries = ExtractSeries.Normalize(ExtractSeries.gatherdata(inputfiles[trackindex]));
    
    dataset = new XYSeriesCollection();
    chart = Mainpeakfitter.makeChart(dataset, "Cell Intensity", "Timepoint", "Normalized Intensity");
    jFreeChartFrame = Mainpeakfitter.display(chart, new Dimension(600, 600));
    
    row = trackindex;
    updateCHIRPmute();
  }
  

  public void updateCHIRPmute()
  {
    FunctionFitterRunnable chirp = new FunctionFitterRunnable(this, timeseries, model, row, inputfiles.length, degree);
    chirp.setMaxiter(maxiter);
    chirp.checkInput();
    chirp.setLowfrequency(6.283185307179586D / (Lowfrequ * 60.0D));
    chirp.setHighfrequency(6.283185307179586D / (Highfrequ * 60.0D));
    

    chirp.run();
  }
  



  public void updateCHIRP()
  {
    FunctionFitter chirp = new FunctionFitter(this, timeseries, model, row, inputfiles.length, degree);
    chirp.setMaxiter(maxiter);
    chirp.checkInput();
    chirp.setLowfrequency(6.283185307179586D / (Lowfrequ * 60.0D));
    chirp.setHighfrequency(6.283185307179586D / (Highfrequ * 60.0D));
    
    chirp.execute();
  }
  

  public static double computeValueFromScrollbarPosition(int scrollbarPosition, int scrollbarMax, double minValue, double maxValue)
  {
    return minValue + scrollbarPosition / scrollbarMax * (maxValue - minValue);
  }
  
  public static int computeScrollbarPositionFromValue(int scrollbarMax, double value, double minValue, double maxValue)
  {
    return (int)Math.round((value - minValue) / (maxValue - minValue) * scrollbarMax);
  }
  
  public int computeScrollbarPositionFromValue(double sigma, float min, float max, int scrollbarSize)
  {
    return round((sigma - min) / (max - min) * scrollbarSize);
  }
  
  public static int round(double value) {
    return (int)(value + 0.5D * Math.signum(value));
  }
  
  public static void main(String[] args)
  {
    new ImageJ();
    
    JFrame frame = new JFrame("");
    ChirpFileChooser panel = new ChirpFileChooser();
    
    frame.getContentPane().add(panel, "Center");
    frame.setSize(panel.getPreferredSize());
  }
}

