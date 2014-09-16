package rngs;

public class badRngTwo implements IRng{
	private double counter;
	
	public badRngTwo () {
		this.counter = 0;
	}
	
	public double getRandom() {
		if (counter >= 1.0) {
			counter = 0;
		}
		counter += 0.1;
		return counter + 0.1;
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
