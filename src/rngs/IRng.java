package rngs;

public interface IRng {
	public double getRandom();
	double[] getRandomBulk(int count);
	public String getDistribution();
	public double getRangeMin();
	public double getRangeMax();
	public double getMean();
	public double getStdDev();
}
