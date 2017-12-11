package tetris;

import java.util.concurrent.CountDownLatch;
import java.util.*;

public class TestFile {

	public static void main(String[] args) { 

	// overriding the game itsself in the totial 
		
	for (int i =0 ; i < 2 ; i ++) {	
		
		CountDownLatch latch  = new CountDownLatch(1); 
		TetrisAuto tetris = new TetrisAuto(latch); 
		String [] s = new String [4]; 
		s[0] = "-0.5"; 
		s[1] = "0.76"; 
		s[2] = "-0.36"; 
		s[3] = "-0.2"; 
		tetris.main(s);

	
		try {
			latch.await(); 

		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		latch = new CountDownLatch(1); 
	
		System.out.println("Tetris core each game " + tetris.finalscore);
	} 
	
	} 
	
}
