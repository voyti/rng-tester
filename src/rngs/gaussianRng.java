package rngs;

import java.util.Random;

	/**
	 * Default java rng with uniform distribution with mean of 0 and std dev of 1
	 */
public class gaussianRng implements IRng{
	
	
	private Random rnd = new Random();
	
	public double getRandom() {
		return rnd.nextGaussian();
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
		return "normal";
	}

	@Override
	public double getRangeMin() {
		return Double.NaN;
	}

	@Override
	public double getRangeMax() {
		return Double.NaN;
	}
	
	@Override
	public double getMean() {
		return 0;
	}

	@Override
	public double getStdDev() {
		return 1;
	}
}
