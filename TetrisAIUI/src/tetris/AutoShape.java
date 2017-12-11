package tetris;
 
enum AutoShape { 
	ZShape(new int[][]{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}), 
	Z1Shape(new int[][]{{-1, 0}, {0, 0}, {0, 1}, {1, 1}}),
	TShape(new int[][]{{-1, 0}, {0, 0}, {1, 0}, {0, 1}}), 
	T1Shape(new int[][]{{-1, 0}, {0, 0}, {1, 0}, {0, -1}}), 
	T2Shape(new int[][]{{-1, 0}, {0, 0}, {0, 1}, {0, -1}}), 
	T3Shape(new int[][]{{1, 0}, {0, 0}, {0, 1}, {0, -1}}),  
	SShape(new int[][]{{0, -1}, {0, 0}, {1, 0}, {1, 1}}), 
	S1Shape(new int[][]{{0, 1}, {0, 0}, {1, 0}, {-1, 1}}),  
	LShape(new int[][]{{-1, -1}, {0, -1}, {0, 0}, {0, 1}}),
	L1Shape(new int[][]{{-1, 0}, {1, 0}, {0, 0}, {1, -1}}),
	L2Shape(new int[][]{{1, 1}, {0, -1}, {0, 0}, {0, 1}}),
	L3Shape(new int[][]{{-1, 1}, {-1, 0}, {0, 0}, {1, 0}}), 
	JShape(new int[][]{{1, -1}, {0, -1}, {0, 0}, {0, 1}}), 
	J1Shape(new int[][]{{-1, 0}, {1, 0}, {0, 0}, {1, 1}}),  
	J2Shape(new int[][]{{-1, 0}, {0, -1}, {0, 0}, {0, 1}}),   
	J3Shape(new int[][]{{-1, -1}, {-1, 0}, {0, 0}, {1, 0}}),
	IShape(new int[][]{{0, -1}, {0, 0}, {0, 1}, {0, 2}}),
	I1Shape(new int[][]{{-1, 0}, {0, 0},{1, 0}, {2,0}}),    
	Square(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}});  
	private AutoShape(int[][] shape) {
		this.shape = shape;
		pos = new int[4][2];
		 
		reset();
	}

	void reset() {
		for (int i = 0; i < pos.length; i++) {
			pos[i] = shape[i].clone();
		}
	}

	final int[][] pos, shape;
	
	int rotate;
	int left;
	int right;
}
