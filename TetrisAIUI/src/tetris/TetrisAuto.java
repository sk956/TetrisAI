package tetris;

import java.awt.*;
import java.awt.event.*;
import static java.lang.Math.*;
import static java.lang.String.format;
import java.util.*;
import javax.swing.*;
import static tetris.Config.*;
import java.util.concurrent.*;
import java.awt.event.ActionListener;
/*modifications from https://rosettacode.org/wiki/Tetris/Java */
public class TetrisAuto extends JPanel implements Runnable {
	enum Dir {
		right(1, 0), down(0, 1), left(-1, 0);

		Dir(int x, int y) {
			this.x = x;
			this.y = y;
		}

		final int x, y;
	};

	public static final int EMPTY = -1;
	public static final int BORDER = -2;
	boolean firstShape = false;
	AutoShape fallingShape;
	AutoShape nextShape;
	static int finalscore =0 ; 


	// position of falling shape
	int fallingShapeRow;
	int fallingShapeCol;
	int nextShapeRow = 1;
	int nextShapeCol = 5;

	final int[][] grid = new int[nRows][nCols];
	int[][] copygrid = new int[nRows][nCols];

	Thread fallingThread;
	final Scoreboard scoreboard = new Scoreboard();
	static final Random rand = new Random();

	boolean manualYN = false;
	private static CountDownLatch latch ;
	int counterStart = 1; 

	static ArrayList<AutoShape> autoshapes = new ArrayList<AutoShape>();
	static ArrayList<Double> weights = new ArrayList<Double>();
	

	public TetrisAuto(CountDownLatch l ) {
		this.latch = l;
	

		nextShape = AutoShape.Square;
		setPreferredSize(dim);
		setBackground(bgColor);
		setFocusable(true);
		initGrid();
		selectShape();

		if (scoreboard.isGameOver()) {
			startNewGame();
			repaint();
		}
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (scoreboard.isGameOver()) {
					startNewGame();
					repaint();
				}
			}
		});
	}

	public void automaticPlay() {
		if (autoshapes.size() == 0) {
		//	System.out.println("autoshape size =0");
			scoreboard.setGameOver();
	
			
		}
	
		if (scoreboard.isGameOver()) {
			//startNewGame();
			
			stop(); 
			return;
		}

		AutoShape s = autoshapes.get(0);
		AutoShape s2 = autoshapes.get(0);
		if (autoshapes.size() > 1) {
			s2 = autoshapes.get(1);
			// nextShape = s2;
		}

		// System.out.print("shapes: " + autoshapes);
		autoshapes.remove(0);
		// Possible rotations for the current block
		ArrayList<AutoShape> Rotations = new ArrayList<AutoShape>();
		// System.out.println("s: " + s);
		if (s == AutoShape.ZShape) {
			Rotations.add(AutoShape.ZShape);
			Rotations.add(AutoShape.Z1Shape);

		} else if (s == AutoShape.TShape) {
			Rotations.add(AutoShape.TShape);
			Rotations.add(AutoShape.T1Shape);
			Rotations.add(AutoShape.T2Shape);
			Rotations.add(AutoShape.T3Shape);
		} else if (s == AutoShape.SShape) {
			Rotations.add(AutoShape.SShape);
			Rotations.add(AutoShape.S1Shape);

		} else if (s == AutoShape.LShape) {
			Rotations.add(AutoShape.LShape);
			Rotations.add(AutoShape.L1Shape);
			Rotations.add(AutoShape.L2Shape);
			Rotations.add(AutoShape.L3Shape);
		} else if (s == AutoShape.JShape) {
			Rotations.add(AutoShape.JShape);
			Rotations.add(AutoShape.J1Shape);
			Rotations.add(AutoShape.J2Shape);
			Rotations.add(AutoShape.J3Shape);
		} else if (s == AutoShape.IShape) {
			Rotations.add(AutoShape.IShape);
			Rotations.add(AutoShape.I1Shape);

		} else {
			Rotations.add(AutoShape.Square);
		}
		// The Possible rotations for the next block
		ArrayList<AutoShape> Rotations2 = new ArrayList<AutoShape>();
		if (s2 == AutoShape.ZShape) {
			Rotations2.add(AutoShape.ZShape);
			Rotations2.add(AutoShape.Z1Shape);

		} else if (s2 == AutoShape.TShape) {
			Rotations2.add(AutoShape.TShape);
			Rotations2.add(AutoShape.T1Shape);
			Rotations2.add(AutoShape.T2Shape);
			Rotations2.add(AutoShape.T3Shape);
		} else if (s2 == AutoShape.SShape) {
			Rotations2.add(AutoShape.SShape);
			Rotations2.add(AutoShape.S1Shape);

		} else if (s2 == AutoShape.LShape) {
			Rotations2.add(AutoShape.LShape);
			Rotations2.add(AutoShape.L1Shape);
			Rotations2.add(AutoShape.L2Shape);
			Rotations2.add(AutoShape.L3Shape);
		} else if (s2 == AutoShape.JShape) {
			Rotations2.add(AutoShape.JShape);
			Rotations2.add(AutoShape.J1Shape);
			Rotations2.add(AutoShape.J2Shape);
			Rotations2.add(AutoShape.J3Shape);
		} else if (s2 == AutoShape.IShape) {
			Rotations2.add(AutoShape.IShape);
			Rotations2.add(AutoShape.I1Shape);

		} else {
			Rotations2.add(AutoShape.Square);
		}

		ArrayList<Double> maxScore = new ArrayList<Double>(); // {current position, max score, next position, current
		// rotation}

		updatecopyGrid();

		while (canMove(Rotations.get(0), Dir.down)) {
			move(Dir.down);
		}
		copyaddShape(Rotations.get(0));

		while (nextcanMove(Rotations2.get(0), Dir.down)) {
			nextmove(Dir.down);
		}
		copyaddShape(Rotations2.get(0));
		// System.out.println("hi");
		double score = calculate();
		//why is there a maxscore calculation here at this point ?_? 
		maxScore.add(5.0);
		maxScore.add(score);
		maxScore.add(0.0);
		maxScore.add(0.0);
		// System.out.println(score);
		fallingShapeCol = 5;
		fallingShapeRow = 1;
		nextShapeCol = 5;
		nextShapeRow = 1;

		for (int i = 0; i < Rotations.size(); i++) {
			for (int j = 0; j < Rotations2.size(); j++) {
				AutoShape curr = Rotations.get(i);
				AutoShape next = Rotations2.get(j);

				for (int k = 5; k < 11; k++) {
					nextShapeCol = k;

					boolean inBounds = true;
					for (int[] p : next.pos) {
						int newCol = nextShapeCol + p[0] + Dir.down.x;
						if (newCol > 11)
							inBounds = false;
						j++;
					}

					if (inBounds) {
						maxScore = testLoc(curr, next, maxScore, k, (double) i);
					}
				}
				nextShapeCol = 5;
				for (int k = 0; k < 4; k++) {
					nextShapeCol = k;

					boolean inBounds = true;
					for (int[] p : next.pos) {
						int newCol = nextShapeCol + p[0] + Dir.down.x;
						if (newCol < 0)
							inBounds = false;
					}

					if (inBounds) {
						maxScore = testLoc(curr, next, maxScore, k, (double) i);
					}

				}
			}

		}
		int rot = (int) Math.round(maxScore.get(3));
		//		System.out.println("rotations: " + Rotations);
		//		System.out.println("first fall: " + fallingShape);
		//		System.out.println("first next: " + nextShape);
		fallingShape = Rotations.get(rot);
		//		System.out.println("second fall: " + fallingShape);
		//		System.out.println("second next: " + nextShape);

		double loc = maxScore.get(0);
		//System.out.println(loc + "location");
		if (loc == 0) {
			for (int i = 0; i < 5; i++) {
				if (canMove(fallingShape, Dir.left)) {
					fallingShapeCol -= 1;
				}
				// move(Dir.left);
			}
		} else if (loc == 1.0) {

			for (int i = 0; i < 4; i++) {
				if (canMove(fallingShape, Dir.left)) {
					fallingShapeCol -= 1;
				}
				// move(Dir.left);
			}
		} else if (loc == 2.0) {
			for (int i = 0; i < 3; i++) {
				if (canMove(fallingShape, Dir.left)) {
					fallingShapeCol -= 1;
					//System.out.println("falling shape"  + fallingShapeCol);
				}
			}
		} else if (loc == 3.0) {
			for (int i = 0; i < 2; i++) {
				if (canMove(fallingShape, Dir.left)) {
					fallingShapeCol -= 1;
				}
			}
		} else if (loc == 4.0) {
			for (int i = 0; i < 1; i++) {
				if (canMove(fallingShape, Dir.left)) {
					fallingShapeCol -= 1;
				}
			}
		} else if (loc == 6.0) {
			for (int i = 0; i < 1; i++) {
				if (canMove(fallingShape, Dir.right)) {
					fallingShapeCol += 1;
				}
			}
		} else if (loc == 7.0) {
			for (int i = 0; i < 2; i++) {
				if (canMove(fallingShape, Dir.right)) {
					fallingShapeCol += 1;
				}
			}
		} else if (loc == 8.0) {
			for (int i = 0; i < 3; i++) {
				if (canMove(fallingShape, Dir.right)) {
					fallingShapeCol += 1;
				}
			}
		} else if (loc == 9.0) {
			//System.out.println("goesin 9.0");
			for (int i = 0; i < 4; i++) {
				if (canMove(fallingShape, Dir.right)) {
					//System.out.println("goesin 9.0");
					fallingShapeCol += 1;
					//System.out.println("after falling shape 9.0 " + fallingShapeCol);
				}
			}
		} else if (loc == 10.0) {
			for (int i = 0; i < 5; i++) {
				if (canMove(fallingShape, Dir.right)) {
					fallingShapeCol += 1;
				}
			}
		}
	}

	public ArrayList<Integer> height() {
		ArrayList<Integer> array = new ArrayList<Integer>();
		for (int i = 1; i < nCols-1; i++) {
			int j = 0;
			while (j < nRows-1 && (copygrid[j][i] == EMPTY || copygrid[j][i] == BORDER)) {
				j++;
			}

			if (j == nRows-2 && (copygrid[j][i] == EMPTY || copygrid[j][i] == BORDER)) {
				array.add(0);
			
			} else {
				array.add(nRows - j-1);
			}
		}
		//System.out.println(array);
		return array;
	}

	public int numHoles(ArrayList<Integer> heightarray) {
		int count = 0;
		//System.out.println("heightarray" +heightarray.size());

		for (int i = 0; i < heightarray.size(); i++) {
			for (int j = nRows - heightarray.get(i); j < nRows - 1; j++) {
				if (copygrid[j][i+1] == EMPTY) {
					count++;
				} else {
					continue;
				}
			}
		}

		return count;
	}

	public double calculate() {
		ArrayList<Integer> heightarray = new ArrayList<Integer>();
		heightarray = height();
		int maxheight = Collections.max(heightarray);
		int minheight = Collections.min(heightarray);
		int bumpiness = maxheight - minheight;
		int holes = numHoles(heightarray);
		int rowsremoved = simremoveLines();

		//System.out.println("rows: " + rowsremoved);
		double total = weights.get(0) * maxheight + weights.get(1) * rowsremoved + weights.get(2) * holes
				+ weights.get(3) * bumpiness;
		//System.out.println("calculate function " + total);
		return weights.get(0) * maxheight + weights.get(1) * rowsremoved + weights.get(2) * holes
				+ weights.get(3) * bumpiness;
	}

	public ArrayList<Double> testLoc(AutoShape curr, AutoShape next, ArrayList<Double> maxScore, int nextPos,
			double rotNum) {
		int origCol = fallingShapeCol;
		int origRow = fallingShapeRow;
		updatecopyGrid();
		// First get score when curr is in original position
		while (canMove(curr, Dir.down)) {
			move(Dir.down);
		}
		copyaddShape(curr);
		while (nextcanMove(next, Dir.down)) {
			nextmove(Dir.down);
		}
		copyaddShape(next);
		// System.out.println("hey");

		double score = calculate();
		// System.out.println(score);
		if (score > maxScore.get(1)) {
			maxScore.set(0, 5.0);
			maxScore.set(1, score);
			maxScore.set(2, (double) nextPos);
			maxScore.set(3, rotNum);
		}

		// Reset position
		fallingShapeCol = origCol;
		fallingShapeRow = origRow;
		nextShapeCol = 5;
		nextShapeRow = 1;

		// Test all left positions for curr against same next
		while (canMove(curr, Dir.left)) {
			fallingShapeCol -= 1;
			updatecopyGrid();
			while (canMove(curr, Dir.down)) {
				move(Dir.down);
			}
			copyaddShape(curr);
			while (nextcanMove(next, Dir.down)) {
				nextmove(Dir.down);
			}

			copyaddShape(next);
			// System.out.println("copy add Shape");

			double score1 = calculate();
			// System.out.println(score1);
			if (score1 > maxScore.get(1)) {
				maxScore.set(0, (double) fallingShapeCol);
				maxScore.set(1, score1);
				maxScore.set(2, (double) nextPos);
				maxScore.set(3, rotNum);
			}
		}

		// Reset curr position
		fallingShapeCol = origCol;
		fallingShapeRow = origRow;
		nextShapeCol = 5;
		nextShapeRow = 1;

		// Test all right positions for curr against same next
		while (canMove(curr, Dir.right)) {
			fallingShapeCol += 1;
			updatecopyGrid();
			while (canMove(curr, Dir.down)) {
				move(Dir.down);
			}
			copyaddShape(curr);
			while (nextcanMove(next, Dir.down)) {
				nextmove(Dir.down);
			}
			copyaddShape(next);
			// System.out.println("hello");

			double score1 = calculate();
			//System.out.println("score for weight" + score1);
			if (score1 > maxScore.get(1)) {
				maxScore.set(0, (double) fallingShapeCol);
				maxScore.set(1, score1);
				maxScore.set(2, (double) nextPos);
				maxScore.set(3, rotNum);
			}
		}

		// Reset curr position
		fallingShapeCol = origCol;
		fallingShapeRow = origRow;
		nextShapeCol = 5;
		nextShapeRow = 1;

		return maxScore;
	}

	void selectShape() {
		fallingShapeRow = 1;
		fallingShapeCol = 5;
		// System.out.println("initial next step" + nextShape);

		fallingShape = nextShape;
		if (!manualYN && fallingShape != null) {
			// fallingshape.reset();
			// fallingShape.rotate = new Random().nextInt(3);
			fallingShape.left = new Random().nextInt(10);
			fallingShape.right = new Random().nextInt(10);
			// System.out.println("rotate: " + fallingShape.rotate + "l: " +
			// fallingShape.left + "r: " + fallingShape.right);
			automaticPlay();
		}

		AutoShape[] shapes = AutoShape.values();
		// System.out.println("autoshapes" +autoshapes);

		if (autoshapes.size() > 1) {
			if (firstShape == true) {
				// System.out.println("Getting the firstshape booleab");
				nextShape = autoshapes.get(0);

			}
			firstShape = true;
		}
//		} else {
//			nextShape = shapes[rand.nextInt(shapes.length)];
//		}
		if (fallingShape != null)
			fallingShape.reset();
	}

	void startNewGame() {
	
		stop();
		initGrid();
		selectShape();
		scoreboard.reset();
		(fallingThread = new Thread(this)).start();
	}

	void stop() {
	
		if (fallingThread != null) {
			Thread tmp = fallingThread;
			fallingThread = null;
			tmp.interrupt();
			//System.out.println("score stop" + scoreboard.getScore());
		}
	}

	void initGrid() {
		for (int r = 0; r < nRows; r++) {
			Arrays.fill(grid[r], EMPTY);
			for (int c = 0; c < nCols; c++) {
				if (c == 0 || c == nCols - 1 || r == nRows - 1)
					grid[r][c] = BORDER;
			}
		}
	}

	@Override
	public void run() {
	
		autoshapes.add(AutoShape.Square);
		autoshapes.add(AutoShape.IShape);
		autoshapes.add(AutoShape.JShape) ; 
		autoshapes.add(AutoShape.TShape); 
		autoshapes.add(AutoShape.ZShape); 
		autoshapes.add(AutoShape.ZShape); 
		autoshapes.add(AutoShape.LShape);
		autoshapes.add(AutoShape.SShape);
		autoshapes.add(AutoShape.JShape);
	
		while (Thread.currentThread() == fallingThread) {
			//waiter.waitFor();
			try {
				Thread.sleep(scoreboard.getSpeed());
			} catch (InterruptedException e) {
				return;
			}

			if (!scoreboard.isGameOver()) {
				if (canMove(fallingShape, Dir.down)) {
					move(Dir.down);
				} else {
					shapeHasLanded();
				}
				repaint();
				// System.out.println("repaint the game over");
			} else { 
				//Thread.sleep(scoreboard.getScore());
	
			}
			
		}
	
		finalscore = scoreboard.getScore(); 
		latch.countDown();
//		System.out.println("Check if tis inside startnewgame");
//		if (counterStart >0 ) {
//			System.out.println("counterstart" + counterStart);
//			startNewGame(); 
//			repaint(); 
//			counterStart--; 
//		}
		
//		System.out.println("Check if tis going out startnewgame");
		//repaint(); 
		
	}

	void drawStartScreen(Graphics2D g) {
		g.setFont(mainFont);

		g.setColor(titlebgColor);
		g.fill(titleRect);
		g.fill(clickRect);

		g.setColor(textColor);
		g.drawString("Tetris", titleX, titleY);

		g.setFont(smallFont);
		g.drawString("click to start", clickX, clickY);
	}

	void drawSquare(Graphics2D g, int colorIndex, int r, int c) {
		g.setColor(colors[colorIndex]);
		g.fillRect(leftMargin + c * blockSize, topMargin + r * blockSize, blockSize, blockSize);

		g.setStroke(smallStroke);
		g.setColor(squareBorder);
		g.drawRect(leftMargin + c * blockSize, topMargin + r * blockSize, blockSize, blockSize);
	}

	void drawUI(Graphics2D g) {
		// grid background
		g.setColor(gridColor);
		g.fill(gridRect);

		// the blocks dropped in the grid
		for (int r = 0; r < nRows; r++) {
			for (int c = 0; c < nCols; c++) {
				int idx = grid[r][c];
				if (idx > EMPTY)
					drawSquare(g, idx, r, c);
			}
		}

		// the borders of grid and preview panel
		g.setStroke(largeStroke);
		g.setColor(gridBorderColor);
		g.draw(gridRect);
		g.draw(previewRect);

		// scoreboard
		int x = scoreX;
		int y = scoreY;
		g.setColor(textColor);
		g.setFont(smallFont);
		g.drawString(format("hiscore  %6d", scoreboard.getTopscore()), x, y);
		g.drawString(format("level    %6d", scoreboard.getLevel()), x, y + 30);
		g.drawString(format("lines    %6d", scoreboard.getLines()), x, y + 60);
		g.drawString(format("score    %6d", scoreboard.getScore()), x, y + 90);

		// preview
		int minX = 5, minY = 5, maxX = 0, maxY = 0;
		for (int[] p : nextShape.pos) {
			minX = min(minX, p[0]);
			minY = min(minY, p[1]);
			maxX = max(maxX, p[0]);
			maxY = max(maxY, p[1]);
		}
		double cx = previewCenterX - ((minX + maxX + 1) / 2.0 * blockSize);
		double cy = previewCenterY - ((minY + maxY + 1) / 2.0 * blockSize);

		g.translate(cx, cy);
		for (int[] p : nextShape.shape)
			drawSquare(g, nextShape.ordinal(), p[1], p[0]);
		g.translate(-cx, -cy);
		// System.out.println("autoshapesdraw" + autoshapes);
	}
	// System.out.println("autoshapesdraw" + autoshapes);

	void drawFallingShape(Graphics2D g) {
		int idx = fallingShape.ordinal();
		for (int[] p : fallingShape.pos)
			drawSquare(g, idx, fallingShapeRow + p[1], fallingShapeCol + p[0]);
	}

	@Override
	public void paintComponent(Graphics gg) {
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D) gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawUI(g);

		if (scoreboard.isGameOver()) {
			drawStartScreen(g);
		} else {
			drawFallingShape(g);
		}
	}

	void move(Dir dir) {
		fallingShapeRow += dir.y;
		fallingShapeCol += dir.x;
	}

	void nextmove(Dir dir) {
		nextShapeRow += dir.y;
		nextShapeCol += dir.x;
	}

	boolean canMove(AutoShape s, Dir dir) {
		for (int[] p : s.pos) {
			int newCol = fallingShapeCol + dir.x + p[0];
			int newRow = fallingShapeRow + dir.y + p[1];
			if (grid[newRow][newCol] != EMPTY)
				return false;
		}
		return true;
	}

	boolean nextcanMove(AutoShape s, Dir dir) {
		for (int[] p : s.pos) {
			int newCol = nextShapeCol + dir.x + p[0];
			int newRow = nextShapeRow + dir.y + p[1];
			// System.out.println("newCol" + newCol);
			// System.out.println("newRow" + newRow);
			if (grid[newRow][newCol] != EMPTY)
				return false;
		}
		return true;
	}

	void shapeHasLanded() {
		addShape(fallingShape);
		if (fallingShapeRow < 2) {
			scoreboard.setGameOver();
			scoreboard.setTopscore();
			stop();
		} else {
			scoreboard.addLines(removeLines());
		}
		selectShape();
	}

	int simremoveLines() {
		int ct = 0;
		for (int r = 0; r < nRows - 1; r++) {
			for (int c = 1; c < nCols - 1; c++) {
				if (copygrid[r][c] == EMPTY)
					break;
				if (c == nCols-2) {
					ct++;
				}
			}
		}
		return ct;
	}

	int removeLines() {
		int count = 0;
		for (int r = 0; r < nRows - 1; r++) {
			for (int c = 1; c < nCols - 1; c++) {
				if (grid[r][c] == EMPTY)
					break;
				if (c == nCols-2) {
					count++;
					removeLine(r);
				}
			}
		}
		return count;
	}

	void removeLine(int line) {
		for (int c = 0; c < nCols; c++)
			grid[line][c] = EMPTY;

		for (int c = 0; c < nCols; c++) {
			for (int r = line; r > 0; r--)
				grid[r][c] = grid[r - 1][c];
		}
	}

	void addShape(AutoShape s) {
		for (int[] p : s.pos)
			grid[fallingShapeRow + p[1]][fallingShapeCol + p[0]] = s.ordinal();
	}

	void copyaddShape(AutoShape s) {
		for (int[] p : s.pos)
			copygrid[fallingShapeRow + p[1]][fallingShapeCol + p[0]] = s.ordinal();
	}

	void updatecopyGrid() {
		for (int i = 0; i < nRows; i++) {
			copygrid[i] = grid[i].clone();
		}
	}

	public static void main(String[] args) {
	
		
		weights.add(-0.8162609014165183); 
		weights.add (0.2598748239478459); 
		weights.add( -0.5472997964881219); 
		weights.add(-0.08184266202802037);

		TetrisAuto tetris = new TetrisAuto(latch);
	
		
		SwingUtilities.invokeLater(() -> { 
				
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setTitle("Tetrisauto");
				f.setResizable(false);				
				f.add(tetris, BorderLayout.CENTER);
				f.pack();
				f.setLocationRelativeTo(null);
				f.setVisible(true);

				//System.out.println(tetris.scoreboard.getScore());
			});
		try {
		
			latch.await();
			
		} catch (InterruptedException e) {
	
					
			e.printStackTrace();	
		} 
		
			
		//thread.sleep(4000);
		//thread.start();

	}
}
