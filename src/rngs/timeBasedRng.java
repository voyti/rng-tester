package rngs;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Prosty, nieprzemyslany i zly generator generujacy liczby "losowe" z przedzialu 0-1 na podstawie obecnego czasu
 * @author s375129
 */
public class timeBasedRng implements IRng{
	private double counter;
	private double sum;
	
	public timeBasedRng () {
		this.counter = 0.2;
	}
	
	public double getRandom() {
    	sum = counter * (System.currentTimeMillis() % 10);
    	
		while (sum > 1.0) {
			try {
	    	    Thread.sleep(3);
	    	} catch(InterruptedException ex) {
	    	    Thread.currentThread().interrupt();
	    	}
			sum = sum - (double) ((double)(System.currentTimeMillis() % 10) / 10);
		}
		try {
    	    Thread.sleep(3);
    	} catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
    	}
		return sum;
	}
	
	public double[] getRandomBulk(int count) {
		double[] result = new double[count];
		for (int i = 0; i < count; i++) {
			result[i] = getRandom();
		}
		return result;
	}
	
	@Override
	public String getDistribution() {
		return "uniform";
	}

	@Override
	public double getRangeMin() {
		return 0;
	}

	@Override
	public double getRangeMax() {
		return 1;
	}
	
	@Override
	public double getMean() {
		return 0.5;
	}
	
	@Override
	public double getStdDev() {
		return ((this.getRangeMax() - this.getRangeMin()) / Math.sqrt(12));
	}
}
