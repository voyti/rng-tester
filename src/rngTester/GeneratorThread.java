package rngTester;

import java.util.Arrays;

import rngs.IRng;

public class GeneratorThread extends Thread {

	private ICallbackReceiver receiver;
	private int populationSize;
	private IRng rng;

	public GeneratorThread(ICallbackReceiver receiver) {
		this.receiver = receiver;
		this.setPriority(Thread.MAX_PRIORITY);
	}

	public synchronized void run() {
	    StringBuffer sb = new StringBuffer();
		double result;
		double [] results = new double[this.populationSize];

		for (int i = 0; i < populationSize; i++) {
			if (i > 0) {
				sb.append(",       ");
			}
			result = this.rng.getRandom();
			results[i] = result;
			sb.append(String.valueOf(result));
			
			double progress = ((double) ((double)i / (double)populationSize) * 100);
			receiver.notify("generation-progress", (int) progress);
			receiver.notify("generation-results", sb);
		}
		
		receiver.notify("string-values-generated", sb);
		receiver.notify("number-values-generated", results);
	}

	public void generate(int populationSize, IRng iRng) {
		this.populationSize = populationSize;
		this.rng = iRng;
		this.start();
	}
}
