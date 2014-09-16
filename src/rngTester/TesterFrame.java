package rngTester;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import rngs.IRng;

public class TesterFrame extends JFrame implements ICallbackReceiver {
	private ArrayList<IRng> rngs;
	private JList rngSelectablelist;
	private JTextArea rngOutputTP;
	private JTextPane statResults;
	private JTextField populationSizeField;
	private StatAnalyser statAnalyser;
	private JProgressBar progressBar;
	private IRng lastUsedRng;
	private GeneratorThread gt;
	
	private Boolean occupied = false;
	private long startTime;
	
	private static final int STYLE_HEADER = 1;
	private static final int STYLE_SEVERE = 2;
	private static final int STYLE_NOTIFY = 3;
	private static final int STYLE_NORMAL = 4;
	
	private static final int STAT_MEAN = 1;
	private static final int STAT_MIN_RANGE = 2;
	private static final int STAT_MAX_RANGE = 3;
	private static final int STAT_STD_DEV = 4;
	private static final int STAT_NORMAL_TEST_1 = 5;
	private static final int STAT_NORMAL_TEST_2 = 6;
	private static final int STAT_MEDIAN = 7;
	private static final int STAT_MODE = 8;
	private static final int STAT_MODE_PERCENT = 9;
	private static final int STAT_NEXT_GREATER = 10;
	private static final int STAT_10_NEXT_GREATER = 11;
	
	private ArrayList<Remark> remarks = new ArrayList<Remark>();
	
	

	public TesterFrame(ArrayList<IRng> rngs) {
		super("Tester RNG");
		this.setLayout(new BorderLayout(10, 10));
		this.rngs = rngs;
		init();
	}

	private void init() {
		JButton goBtn = new JButton("Start test");
		
		goBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					try {
						generateResultsForRng(rngs.get(rngSelectablelist.getSelectedIndex()));
					}
					catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Proszę najpierw wybrać generator z listy");
					}
			}
		});
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				generateRngList(), generateRngAnalysisPanel());
		JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
		panel.add(goBtn);
		panel.add(generateProgressBar());
		
		this.add(splitPane, BorderLayout.CENTER);
		this.add(panel, BorderLayout.PAGE_END);
	}
	

	private Component generateProgressBar() {
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		return progressBar;
	}

	private Component generateRngAnalysisPanel() {
		JPanel analysisPane = new JPanel(new BorderLayout(5 , 5));
		
		populationSizeField = new JTextField(10);
		populationSizeField.setText("1000");
		
		statResults = new JTextPane();	
		statResults.setMargin(new Insets(5, 5, 5, 5));
		statResults.setText("--- Please select a generator and population size (note: small population size may result in failing many tests) ---");
		
		analysisPane.add(populationSizeField, BorderLayout.NORTH);
		analysisPane.add(generateRngOutputTA(), BorderLayout.CENTER);
		analysisPane.add(statResults, BorderLayout.SOUTH);
		
		
		return analysisPane;
	}

	private Component generateRngOutputTA() {
		rngOutputTP = new JTextArea();
		
		JScrollPane scrollPane = new JScrollPane(rngOutputTP);
		rngOutputTP.setEditable(false);
		rngOutputTP.setLineWrap(true);
		return scrollPane;
	}

	private Component generateRngList() {
		Object[] rngsList = new Object[rngs.size()];
		
		for (int i = 0; i < rngs.size(); i++) {
			rngsList[i] = rngs.get(i).getClass().getName();
		}
		
		rngSelectablelist = new JList(rngsList);
		rngSelectablelist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		rngSelectablelist.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		rngSelectablelist.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(rngSelectablelist);
		
		
		
		listScroller.setPreferredSize(new Dimension(250, 80));
		return listScroller;
	}

	private void clearTester() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		this.startTime = System.currentTimeMillis();
		this.occupied = true;
		clearTextPane(statResults);
		progressBar.setValue(0);
		remarks = new ArrayList<Remark>();
		remarks.add(new Remark("\n\nRemarks: \n", STYLE_HEADER));
	}
	
	protected void generateResultsForRng(IRng iRng) {
		int populationSize; 
		
		if (this.occupied) {
			return;
		}
		
		try {
			populationSize = Integer.parseInt(populationSizeField.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Wielkosc populacji wprowadzona niepoprawnie, prosze zweryfikowac format", "Bledny format danych", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		this.lastUsedRng = iRng;
		
		gt = new GeneratorThread(this);
		gt.generate(populationSize, rngs.get(rngSelectablelist.getSelectedIndex()));
		
		clearTester();
	}

	private void getResultsForRng(double[] results, IRng iRng) {
		//StringBuffer sb = new StringBuffer();
	    statAnalyser = new StatAnalyser();
	   addToStats("-------- Average value (mean) test: --------\n", STYLE_HEADER);
	   addToStats("Theoretical mean: " + iRng.getMean() + " \n", STYLE_NORMAL);
	   addToStats("Population mean: " + statAnalyser.getMean(results) + " \n\n", determineStatValidity(STAT_MEAN, iRng.getMean(), statAnalyser.getMean(results), iRng));
		
	   addToStats("-------- Range test: --------\n", STYLE_HEADER);
	   addToStats("Theoretical min range: " + iRng.getRangeMin() + " \n", STYLE_NORMAL);
	   addToStats("Population min range: " + statAnalyser.getRangeMin(results) + " \n\n", determineStatValidity(STAT_MIN_RANGE, iRng.getRangeMin(), statAnalyser.getRangeMin(results), iRng));
		
	   addToStats("Theoretical max range: " + iRng.getRangeMax() + " \n", STYLE_NORMAL);
	   addToStats("Population max range: " + statAnalyser.getRangeMax(results) + " \n\n", determineStatValidity(STAT_MAX_RANGE, iRng.getRangeMax(), statAnalyser.getRangeMax(results), iRng));
	   
	   addToStats("-------- Median test: --------\n", STYLE_HEADER);
	   addToStats("Population median: " + statAnalyser.getMedian(results) + " \n\n", determineStatValidity(STAT_MEDIAN, statAnalyser.getMean(results), statAnalyser.getMedian(results), iRng));
	   
	   addToStats("-------- mode test: --------\n", STYLE_HEADER);
	   addToStats("Population mode value: " + statAnalyser.getModeValue(results) + " \n\n", determineStatValidity(STAT_MODE, statAnalyser.getMean(results), statAnalyser.getModeValue(results), iRng));
	   addToStats("Percent of values that equal to mode: " + (statAnalyser.getModePercent(results) * 100) + "% \n\n", determineStatValidity(STAT_MODE_PERCENT, 0, statAnalyser.getModePercent(results), iRng));
	   
	   addToStats("-------- Standard deviation test: --------\n", STYLE_HEADER);
	   addToStats("Theoretical std dev: " + iRng.getStdDev() + " \n", STYLE_NORMAL);
	   addToStats("Population std dev: " + statAnalyser.getStdDev(results) + " \n\n", determineStatValidity(STAT_STD_DEV, iRng.getStdDev(), statAnalyser.getStdDev(results), iRng));
	   
	   addToStats("-------- Every next greater test: --------\n", STYLE_HEADER);
	   addToStats("Following procent of generated values has next value greater: " + (statAnalyser.getPercentNextGreater(results, 1) * 100) + "% \n\n", determineStatValidity(STAT_NEXT_GREATER, 0, statAnalyser.getPercentNextGreater(results, 1), iRng));
	   
	   addToStats("-------- Every 10 next greater test: --------\n", STYLE_HEADER);
	   addToStats("Following procent of generated values has a value generated 10 values later greater: " + (statAnalyser.getPercentNextGreater(results, 10) * 100) + "% \n\n", determineStatValidity(STAT_10_NEXT_GREATER, 0, statAnalyser.getPercentNextGreater(results, 10), iRng));
	   
		
		if(iRng.getDistribution() == "normal") {
			addToStats("-------- NORMAL DISTRIBUTION mean std dev test: \n", STYLE_HEADER);
			
			addToStats("Theoretical mean std dev: " + statAnalyser.getExpectedNormalStdDevRule() + " \n",  STYLE_NORMAL);
			addToStats("Population mean std dev: " + statAnalyser.testNormalStdDevRule(results) + " \n\n", determineStatValidity(STAT_NORMAL_TEST_1, statAnalyser.getExpectedNormalStdDevRule(), statAnalyser.testNormalStdDevRule(results), iRng));
			//TODO: another normal test
		}
		
		addRemarks();
	}

	@Override
	public void notify(String event, Object data) {
		if(event.equals("generation-progress")) {
			//System.out.println("Has progress:" + ((Integer) data, iRng));
			this.progressBar.setValue((Integer) data);
		}  else if(event.equals("generation-results")) {
			rngOutputTP.setText("");
			rngOutputTP.setText(((StringBuffer) data).toString());
		} else if(event.equals("string-values-generated")) {
			rngOutputTP.setText("");
			rngOutputTP.setText(((StringBuffer) data).toString());
		} else if(event.equals("number-values-generated")) {
			this.getResultsForRng((double[])data, this.lastUsedRng);
			//statResults.setText(this.getResultsForRng((double[])data, this.lastUsedRng, iRng));
			this.progressBar.setValue(100);
			this.occupied = false;
			System.err.println("Time taken:" + (System.currentTimeMillis() - this.startTime));
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		}
	}
	
	private int determineStatValidity(int statType, double comparedValue, double sampleValue, IRng rng) {
		switch (statType) {
		case STAT_MEAN:
			if (comparedValue == sampleValue) {
				addToRemarks("Sample mean value is exactly equal to theoretical mean - this is possible, but very unlikely for large population", Remark.STYLE_IMPORTANT);
				return STYLE_SEVERE;
			} else if (sampleValue > (comparedValue + (0.1 * rng.getStdDev()))){
				addToRemarks("Sample mean value is larger than (theoretical mean + 10% std.dev.) - sample may be too small, or generated values might be incorrect", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue < (comparedValue - (0.1 * rng.getStdDev()))) {
				addToRemarks("Sample mean value is smaller than (theoretical mean - 10% std.dev.) - sample may be too small, or generated values might be incorrect", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			}
			break;
		case STAT_MEDIAN:
			if (sampleValue == comparedValue) {
				addToRemarks("Median value is equal sample mean - std. dev. of the generated values can be too small", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue > (comparedValue + (0.2 * rng.getStdDev()))){
				addToRemarks("Sample median value is larger than (sample mean + 20% theoretical mean) - sample may be too small, or generated values might be incorrect", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue < (comparedValue - (0.2 * rng.getStdDev()))) {
				addToRemarks("Sample median value is smaller than (sample mean + 20% theoretical mean) - sample may be too small, or generated values might be incorrect", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			}
			break;
		case STAT_MODE_PERCENT:
			if (sampleValue > 0.1){
				addToRemarks("More than 10% of values are equal to mode - this may indicate narrow range, or invalid generated values", Remark.STYLE_IMPORTANT);
				return STYLE_SEVERE;
			} else if (sampleValue > 0.01) {
				addToRemarks("More than 1% of values are equal to mode - this may indicate narrow range, or invalid generated values", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			}
			break;
			
		case STAT_NEXT_GREATER:
			if (sampleValue > 0.51){
				addToRemarks("More than 51% of values have next generated value greater than itself - this may indicate that values are distributed unevenly", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue < 0.49) {
				addToRemarks("Less than 49% of values have next generated value greater than itself - this may indicate that values are distributed unevenly", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			}
			break;
		case STAT_10_NEXT_GREATER:
			if (sampleValue > 0.51){
				addToRemarks("More than 51% of values have value generated 10 values later greater than itself - this may indicate that values are distributed unevenly", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue < 0.49) {
				addToRemarks("Less than 49% of values have value generated 10 values later greater than itself - this may indicate that values are distributed unevenly", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			}
			break;
			
			
		case STAT_MIN_RANGE:
			if (comparedValue == sampleValue) {
				addToRemarks("Sample min. value is exactly equal to theoretical min. value - this might be due to incorrect values", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue > (comparedValue + 0.2 * Math.abs(comparedValue - rng.getRangeMax()))) {
				addToRemarks("Sample min. value might be too big (greater than theoretical min. + 20% of range span). This might be due to small distribution range or indicate invalid values.", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue < comparedValue) {
				addToRemarks("Sample min. value is lower than theoretical min. value - generated values are invalid.", Remark.STYLE_IMPORTANT);
				return STYLE_SEVERE;
			}
			break;
		case STAT_MAX_RANGE:
			if (comparedValue == sampleValue) {
				addToRemarks("Sample max. value is exactly equal to theoretical max. value - this might be due to incorrect values", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue < (comparedValue - 0.2 * Math.abs(comparedValue - rng.getRangeMin()))) {
				addToRemarks("Sample max. value might be too low (greater than theoretical max. - 20% of range span). This might be due to small distribution range or indicate invalid values.", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue > comparedValue) {
				addToRemarks("Sample max. value is greater than theoretical max. value - generated values are invalid.", Remark.STYLE_IMPORTANT);
				return STYLE_SEVERE;
			}
			break;
		case STAT_STD_DEV:
			if (sampleValue == 0) {
				addToRemarks("Sample std.dev. is equal to 0 - the generated values are invalid", Remark.STYLE_IMPORTANT);
				return STYLE_SEVERE;
			} else if (sampleValue < (0.5 * comparedValue)) {
				addToRemarks("Sample std.dev. is lesser than 50% of theoretical one - this may indicate invalid values", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue > (2 * comparedValue)) {
				addToRemarks("Sample std.dev. is greter than 2 * theoretical one - the theoretical value migh be very small (" + comparedValue + "), or this may indicate invalid values", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			}
			break;
		case STAT_NORMAL_TEST_1:
			if (sampleValue > 1 || sampleValue < 0) {
				addToRemarks("The ratio of elements for \"Normal distribution mean std. dev. test\" exceeds possible bounds - the generated values are incorrect", Remark.STYLE_IMPORTANT);
				return STYLE_SEVERE;
			} else if (sampleValue > 0.97) {
				addToRemarks("There are more than 97% values that have lesser distance to mean than (2 * std. dev.) - this value should probably be closer to 95%", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			} else if (sampleValue < 0.93) {
				addToRemarks("There are less than 93% values that have lesser distance to mean than (2 * std. dev.) - this value should probably be closer to 95%", Remark.STYLE_NOTIFY);
				return STYLE_NOTIFY;
			}
			break;
		case STAT_NORMAL_TEST_2:
		default:
			return STYLE_NORMAL;
		}
		return STYLE_NORMAL;
	}
	
	private void addToRemarks(String remark, int remarkLevel) {
		remarks.add(new Remark("- " + remark + "\n", remarkLevel));
	}
	

	private void addRemarks() {
		System.out.println("[addRemarks] adding " + remarks.size() + " new remarks");
		
		for (int i = 0; i < remarks.size(); i++) {
			addToStats(remarks.get(i).getText(), remarks.get(i).getRemarkLevel());
		}
	}
	
	
	private void addToStats(String msg, int style) {
		switch (style) {
		case STYLE_HEADER:
			appendToPane(statResults, msg, Color.BLACK, true, false);
			break;
		case STYLE_SEVERE:
			appendToPane(statResults, msg, Color.RED, false, true);
			break;
		case STYLE_NOTIFY:
			appendToPane(statResults, msg, Color.ORANGE, false, false);
			break;
		case STYLE_NORMAL:
			appendToPane(statResults, msg, Color.BLACK, false, false);
			break;
		case Remark.STYLE_IMPORTANT:
			appendToPane(statResults, msg, Color.RED, true, true);
			break;
		case Remark.STYLE_NOTIFY:
			appendToPane(statResults, msg, Color.BLACK, false, false);
			break;
		}
	}
	
	private void appendToPane(JTextPane tp, String msg, Color c, Boolean bold, Boolean underline) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Bold, bold);
		aset = sc.addAttribute(aset, StyleConstants.Underline, underline);
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}
	
	private void clearTextPane(JTextPane tp) {
		tp.setText("");
	}
}
