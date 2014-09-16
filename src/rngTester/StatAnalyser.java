package rngTester;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Zrodla: http://www.johndcook.com/Beautiful_Testing_ch10.pdf,
 * 		   http://www.lhup.edu/~dsimanek/scenario/errorman/distrib.htm
 * 
 * @author s375129
 *
 */
public class StatAnalyser {
	
	public double getMean (double[] values) {
		double sum = 0;
		
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		
		return (sum / values.length);
	}
	
	public double getVariance(double[] values) {
        double mean = this.getMean(values),
               temp = 0;
        
        for (int i = 0; i < values.length; i++) {
			temp += (mean - values[i]) * (mean - values[i]);
		}
        
		return temp / values.length;
    }
	
	public double getStdDev (double[] values) {
		return Math.sqrt(this.getVariance(values));
	}
	
	
	
	public double getRangeMin(double[] values) {
		double minValue = values[0];
		
		for (int i = 1; i < values.length; i++) {
			if (values[i] < minValue) {
				minValue = values[i]; 
			}
		}
		
		return minValue;
	}
	
	public double getRangeMax(double[] values) {
		double maxValue = values[0];
		
		for (int i = 1; i < values.length; i++) {
			if (values[i] > maxValue) {
				maxValue = values[i]; 
			}
		}
		return maxValue;
	}
	
	/**
	 * Dla rozkladu normalnego.
	 * Srednia z rozkladu zmiennych losowych bedzie zmienna losowa o tej samej sredniej, ale odchyleniu std. o wartosci mniejszej 1/sqrt(wielkosc popul)
	 * @param values Tablica wartosci wygenerowanych z rozkladu
	 * @param expectedMean Teoretyczna srednia rozkladu
	 * @return result Tablica wynikow, z ktorych pierwszy to oczekiwane odch std (zgodnie z twierdzeniem powyzej), drugi to rzeczywiste odch std sredniej, a trzeci to wartosc bezwzgledna z ich roznicy
	 */
	public double[] testNormalMeanStdDev(double[] values, double expectedMean) {
		double mean = this.getMean(values),
			   noOfSamples = values.length,
			   populationStdDev = this.getStdDev(values),
			   meanStdDev = Math.abs(mean - expectedMean),
			   expectedStdDev = populationStdDev - (1 / Math.sqrt(noOfSamples));
			   
		double[] result = {expectedStdDev, meanStdDev, Math.abs(expectedStdDev - meanStdDev)};
		
		return result;
	}
	
	/**
	 * Dla rozkladu normalnego.
	 * Regula dotyczaca rozkladu normalnego mowi, ze wartosci z populacji beda odlegle od sredniej o ok. 2 wartosci odch std ok 95% czasu 
	 * @param values
	 * @return result Ile wartosci z populacji roznilo sie od sredniej o nie wiecej niz 2 razy odch std - powinno wynosic okolo 0.95, gdzie 0 oznacza zadna, a 1 oznacza wszystkie
	 */
	public double testNormalStdDevRule(double[] values) {
		double mean = this.getMean(values),
			   stdDev = this.getStdDev(values);
		
	    double successes = 0;
		
		
		for (int i = 1; i < values.length; i++) {
			if (Math.abs(values[i] - mean) <= (2 * stdDev)) {
				successes++;
			}
		}
		return successes / values.length;
	}

	public double getExpectedNormalStdDevRule() {
		return 0.95;
	}

	public double getModeValue(double[] results) {
		double curValue;
		double[] localResults = Arrays.copyOf(results, results.length);
		double[] alreadyTested = new double[results.length];
		int	   curResult = 0,
			   sameCount = 0,
			   alreadyTestedIter = 0;
		Boolean skipLoop = false;
		
		for (int i = 0; i < results.length; i++) {
			curValue = results[i];
			sameCount = 0;
			skipLoop = false;
			
			for (int j = 0; j < alreadyTested.length; j++) {
				if (curValue == alreadyTested[j]) {
					skipLoop = true;
				}
			}
			
			if (skipLoop) {
				continue;
			}
			
			for (int j = 0; j < results.length; j++) {
				if (i != j && results[j] == curValue) {
					sameCount++;
				}
			}
			if (sameCount > curResult) {
				curResult = sameCount;
			}
			alreadyTested[alreadyTestedIter++] = curValue;
			
		}
		
//		while(localResults.length > 0) {
//			//System.out.println(Arrays.toString(localResults));
//			curValue = localResults[0];
//			sameCount = 0;
//			
//			for (int j = 1; j < localResults.length; j++) {
//				if (localResults[j] == curValue) {
//					sameCount++;
//				}
//			}
//			if (sameCount > curResult) {
//				curResult = sameCount;
//			}
//			localResults = removeSingleValue(localResults, curValue);
//		}
		
		if (curResult == 0) {
			return Double.NaN;
		} else {
			return results[curResult];
		}
		
	}

	public double getMedian(double[] results) {
		double[] sortedResults = Arrays.copyOf(results, results.length);
		Arrays.sort(sortedResults);
		
				
		if (sortedResults.length % 2 == 0) {
			return sortedResults[results.length / 2];
		} else {
			return sortedResults[(results.length - 1) / 2];
		}
	}

	public double getModePercent(double[] results) {
		double dominant = this.getModeValue(results),
			   sameCount = 0;
		
		if (dominant == Double.NaN) {
			return Double.NaN;
		}
		
		for (int i = 0; i < results.length; i++) {
			if (results[i] == dominant) {
				sameCount++;
			}
		}
		
		return (sameCount / results.length);
	}
	
	public double getPercentNextGreater(double[] results, int checkEveryN) {
		double result = 0;
		
		for (int i = 0; i < results.length; i++) {
			if (i < (results.length - checkEveryN) && results[i + checkEveryN] > results[i]) {
				result++;
			}
		}
		
		return result / results.length;
	}
	
	public double getPercentNextLesser(double[] results, int checkEveryN) {
		return 1 - getPercentNextGreater(results, checkEveryN);
	}
}
