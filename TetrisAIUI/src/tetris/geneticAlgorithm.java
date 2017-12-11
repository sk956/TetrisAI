package tetris;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import tetris.*;
import java.util.*; 

public class geneticAlgorithm {
	// create the weight function 
	static HashSet<ArrayList<Double>> weights = new HashSet<ArrayList<Double>>();
	//static HashSet<ArrayList <AutoShape>> games = new HashSet<ArrayList <AutoShape>>();
	static ConcurrentHashMap<ArrayList<Double>, Integer> maps = new ConcurrentHashMap<ArrayList<Double>, Integer> (); 

	public static void weightCreate() {

		while(weights.size()< 200) { 	
			ArrayList <Double> weightarray = new ArrayList<Double>(); 
			for (int i =0 ; i < 4 ; i++) {
				Random r = new Random(); 
				double randomvalue = r.nextDouble();  	
				if (i == 0 || i == 2 || i == 3) {
					randomvalue  = randomvalue* (-1); 
				}
				weightarray.add(randomvalue); 

			}
			weights.add(weightarray); 

		} 
	}


	public static void runGame(ArrayList<Double> w) {

		CountDownLatch latch  = new CountDownLatch(1); 
		TetrisAuto tetris = new TetrisAuto(latch); 
		String [] s = new String [4];  
		s[0] = w.get(0).toString(); 
		s[1] =  w.get(1).toString();  
		s[2] =  w.get(2).toString(); 
		s[3] =  w.get(3).toString(); 
		tetris.main(s);
		try {
			latch.await(); 

		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		//createing the average of the score
		//System.out.println("Final score" + tetris.finalscore);
		maps.put(w, tetris.finalscore); 
		//System.out.println(maps);
	}



	public static int returnScore(ArrayList<Double> w) {

		CountDownLatch latch  = new CountDownLatch(1); 
		TetrisAuto tetris = new TetrisAuto(latch ); 
		String [] s = new String [4];  
		s[0] = w.get(0).toString(); 
		s[1] =  w.get(1).toString();  
		s[2] =  w.get(2).toString(); 
		s[3] =  w.get(3).toString(); 
		tetris.main(s);
		try {
			latch.await(); 

		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		//createing the average of the score
		return tetris.finalscore;
	}
	
	private static ArrayList<Double> cloneWeights(ArrayList<Double> toCopy) {
		ArrayList<Double> toChange = new ArrayList<Double>();
		for (int i = 0; i < toCopy.size(); i++) {
			toChange.add(toCopy.get(i));
		}
		
		return toChange;
	}


	public static void main(String[] args) {
		int counter = 0; 
		weightCreate();  
		for (ArrayList<Double> w : weights) {
			runGame(w);	
		} 

		while (counter < 100) {
			Set keyset = maps.keySet();
			Collection <Integer> valueList = maps.values();  
			List<Integer > oglist = new ArrayList<Integer>( valueList );
			List<Integer> list = new ArrayList<Integer>(oglist);
			Collections.sort(list); 
			System.out.println("list: " + list);
	
			int maxscore = list.get(list.size()-1); 
			System.out.println("Counter: " + counter);
			maps.forEach((k,v) -> {if (v == maxscore) {System.out.println("middle max score: " + maxscore + " middle max weight value" +k);}}); 
			
			int thirtyscore;
			if (list.size() > 51) {
				thirtyscore = list.get(list.size() - 51);
			} else {
				thirtyscore = list.get(0);
			}
	
			final int minimumscore = list.get(0); 
			ArrayList<ArrayList<Double>> keyArray = new ArrayList<ArrayList<Double>>(); 
			ArrayList<ArrayList<Double>> tenRandomArray = new ArrayList<ArrayList<Double>>(); 
			keyArray.addAll(keyset); 
			ArrayList<ArrayList<Double>> keyArrayClone = new ArrayList<ArrayList<Double>>(keyArray);
			
			
			for (int i = 0; i < 10; i++) {
				Random r = new Random(); 
			
				int index = r.nextInt(keyArrayClone.size()-1); 
				if (!tenRandomArray.contains(keyArrayClone.get(index))) {
					tenRandomArray.add(keyArrayClone.get(index));	
				} 
			}

			int score1  =0 ; 
			ArrayList<Double> weight1 = new ArrayList<Double>();
			weight1.add(0.1) ; 
			weight1.add(0.2); 
			weight1.add(0.3);
			weight1.add(0.4);
			//second highest score
			int score2 =0; 
			ArrayList<Double> weight2 = new ArrayList<Double>(); 
			weight2.add(0.1) ; 
			weight2.add(0.2); 
			weight2.add(0.3);
			weight2.add(0.4);
			for (ArrayList<Double> w : tenRandomArray ) {
				int score = maps.get(w); 
				if (score > score2) {
					if (score > score1) {
						score2 = score1;
						score1 =score;
						weight2 = cloneWeights(weight1);
						weight1  = cloneWeights(w); 

					} else {
						score2 = score;
						weight2 = cloneWeights(w);
					} 
				}
			}
			ArrayList<Double> child = new ArrayList<Double>();  
			child.add(weight1.get(0)); 
			child.add(weight1.get(1)); 
			child.add(weight2.get(2)); 
			child.add(weight2.get(3));

			Random r = new Random(); 
			double randomvalue = r.nextDouble();

			if (randomvalue <= 0.05) {
				int ind = r.nextInt(4);
				double original = child.get(ind);
				double plusminus = r.nextDouble();
				if (plusminus < 0.5) {
					child.set(ind, original - (double)r.nextInt(20)/100);
				} else {
					child.set(ind, original + (double)r.nextInt(20)/100);
				}

			}

			int childscore = returnScore(child); 
			if (thirtyscore < childscore) { 
				
				maps.forEach((k,v) -> {
					if (maps.size() == 40) {

						boolean removed1 = maps.remove(k, minimumscore) ;
						//System.out.println(removed);
						if (removed1) {
							System.out.println("values try 2" + maps.values());
							
						}
					}
				});
				System.out.println("after removal: " + maps.size()); 	
				System.out.println("add");
				maps.put(child, childscore);
				System.out.println("after add: " + maps.size());
		
			}
			
			counter++;
		} 

		Collection <Integer> valueList = maps.values();  
		List<Integer > oglist1 = new ArrayList<Integer>( valueList );
		List<Integer> list1 = new ArrayList<Integer>(oglist1);
		Collections.sort(list1); 
		int maxscore1 = list1.get(list1.size()-1); 
		System.out.println("final size: " + maps.size());
		maps.forEach((k,v) -> {if (v == maxscore1) {System.out.println("final score" + maxscore1 + "final weight value" +k);   }}); 



	}
}

