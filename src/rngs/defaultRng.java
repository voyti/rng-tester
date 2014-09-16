package rngs;

import java.util.Random;

/**
 * Default java rng with uniform distribution
 */
public class defaultRng implements IRng{
	private Random rnd = new Random();
	
	
	public String distribution = "uniform";
	
	public double getRandom() {
		return rnd.nextDouble();
	}
	
	@Override
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
