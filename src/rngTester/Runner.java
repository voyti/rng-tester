package rngTester;

import java.util.ArrayList;

import javax.swing.JFrame;

import rngs.IRng;
import rngs.badRngOne;
import rngs.badRngTwo;
import rngs.defaultRng;
import rngs.fakeGaussianRng;
import rngs.gaussianRng;
import rngs.timeBasedRng;

public class Runner {

	public static void main(String[] args) {
		ArrayList<IRng> rngs = new ArrayList<IRng>();
		
		rngs.add(new badRngOne());
		rngs.add(new badRngTwo());
		rngs.add(new timeBasedRng());
		rngs.add(new defaultRng());
		rngs.add(new gaussianRng());
		rngs.add(new fakeGaussianRng());
		
		TesterFrame tf = new TesterFrame(rngs);
		tf.pack();
		tf.setLocationRelativeTo(null);
		tf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tf.setVisible(true);
		
	}

}
