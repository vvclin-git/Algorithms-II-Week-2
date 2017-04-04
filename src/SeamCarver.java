import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
	private Picture picture, pictureT;
	private FindSeam findSeam, findSeamT;
	private int width, height;
	private static final double INFINITY = Double.MAX_VALUE;
	private boolean lastHorizontal; // indicates if the last operation is horizontal
	public SeamCarver(Picture picture) {
		// create a seam carver object based on the given picture
		if (picture == null) {
			throw new java.lang.NullPointerException();
		}
		this.picture = new Picture(picture);
		this.width = picture.width();
		this.height = picture.height();
		//this.pictureT = picTrans(picture);		
		this.findSeam = new FindSeam(this.picture);
		//this.findSeamT = new FindSeam(this.pictureT);
		this.lastHorizontal = false;
	}
	
	private Picture picTrans (Picture p) {
		Picture pT = new Picture(p.height(), p.width());
		for (int j = 0; j < p.width(); j++) {
			for (int i = 0; i < p.height(); i++) {
				pT.set(i, j, p.get(j, i));
			}
		}
		return pT;
	}
	private class Node {
		int col, row;
		public Node(int col, int row) {
			this.col = col;
			this.row = row;
//			StdOut.println("(" + col + ", " + row + ")");
		}
		public int col() {
			return col;
		}
		public int row() {
			return row;
		}
		public String toString() {
			return "(" + col + ", " + row + ")";
		}
	}
	private class FindSeam {
		double[][] energyTo;		
		Node[][] pathFrom;
		int width, height;
		Picture p;
		public FindSeam(Picture p) {			
			this.p = p;
			this.width = p.width();
			this.height = p.height();
			this.energyTo = new double[p.height()][p.width()];			
			this.pathFrom = new Node[p.height()][p.width()];
			for (int i = 0; i < energyTo.length; i++) {
				for (int j = 0; j < energyTo[0].length; j++) {
					if (i == 0) {
						energyTo[i][j] = 1000;
					}
					else {
						energyTo[i][j] = INFINITY;
					}
				}
			}			
			updateEnergyTo(width, height);
		}
		private void updateEnergyTo(int width, int height) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (i + 1 < height) {
						// S
						relax(j, i, j, i + 1);
						// WS
						if (j >= 2) {
							relax(j, i, j - 1, i + 1);
						}
						// ES
						if (j < width - 2) {
							relax(j, i, j + 1, i + 1);
						}
					}
				}
			}
		}
		private void updateAdjEnergyTo(int row, int col) {
			// update adjacent EnergyTo
			if (row + 1 < height) {
				// S
				relax(col, row, col, row + 1);
				// WS
				if (col >= 2) {
					relax(col, row, col - 1, row + 1);
				}
				// ES
				if (col < width - 2) {
					relax(col, row, col + 1, row + 1);
				}
			}			
		}
		
		private void relax(int fromX, int fromY, int toX, int toY) {
//			StdOut.print("energy TO " + "(" + toX + ", " + toY + "): " + energyTo[indToY(indTo, p)][indToX(indTo, p)]);
//			StdOut.print(" | energy From" + "(" + fromX + ", " + fromY + "): "  + energyTo[indToY(indFrom, p)][indToX(indFrom, p)]);
//			StdOut.print(" | energy: " + energy(indToX(indTo, p), indToY(indTo, p)));
			if (energyTo[toY][toX] > 
				energyTo[fromY][fromX] + energy(toX, toY)) {
				energyTo[toY][toX] = energyTo[fromY][fromX] + energy(toX, toY);				
				pathFrom[toY][toX] = new Node(fromX, fromY);				
//				StdOut.println(" new energy TO: " + energyTo[toY][toX] + "| new path from ind: " + indFrom);
//				for (int j : pathFrom) {
//					StdOut.print(j + ", ");
//				}
				
			}
//			StdOut.println();
		}
		private double energy(int x, int y) {
			// energy of pixel at column x and row y
			double energyX, energyY;			
			if ((x > 0 & x < width - 1) & 
					(y > 0 & y < height - 1)){
				energyX = colorDistSq(p.get(x - 1, y), p.get(x + 1, y));
				energyY = colorDistSq(p.get(x, y - 1), p.get(x, y + 1));
				return Math.sqrt(energyX + energyY);
			}
			else {
				return 1000;
			}
			
		}
		private void dataDump() {
//			boolean debug = false;
			boolean debug = true;
			if (!debug) {
				return;
			}
			StdOut.println("energyTO: ");
			for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++)
	                StdOut.printf("%9.2f ", energyTo[row][col]);
	            StdOut.println();
	        }
			StdOut.println("energy: ");
			for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++)
	                StdOut.printf("%9.2f ", energy(col, row));
	            StdOut.println();
	        }
			StdOut.println("picture: ");
			for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++)
	                StdOut.printf(p.get(col, row).toString() + " ");
	            StdOut.println();
			}
		}
		public Stack<Node> getMinSeam() {
			Stack<Node> minSeam = new Stack<Node>();
			double minEnergy = INFINITY;			
			int minEnergyCol = -1;
			int col, row;
			Node nextNode;
			for (int i = 0; i < width; i++) {
				if (energyTo[height - 1][i] < minEnergy) {
					minEnergy = energyTo[height - 1][i];
					minEnergyCol = i;
				}
			}
//			minSeam.push(xyToInd(minEnergyCol, height - 1, width)); // push the last grid
			minSeam.push(new Node(minEnergyCol, height - 1)); // push the last grid
			col = minEnergyCol;
			row = height - 1;
			while (pathFrom[row][col] != null) {
				nextNode = pathFrom[row][col];
				minSeam.push(nextNode);
				row = nextNode.row();				
				col = nextNode.col();				
			}						
			return minSeam;
		}
		public void vCut(int[] seam) {
//			StdOut.println(width + " x " + height + " before v cut: ");
//			dataDump();
			for (int j = 0; j < height; j++) {
				for (int i = seam[j]; i < width - 1; i++) {
					//if (i < width - 1) {
						p.set(i, j, p.get(i + 1, j));
						if (j == 0) {
							energyTo[j][i] = 1000;
						}
						else {
							energyTo[j][i] = INFINITY;
						}						
//						updateAdjEnergyTo(j, i);						
					//}					
				}				
			}
			width -= 1;
			updateEnergyTo(width, height);			
//			StdOut.println(width + " x " + height + " after v cut: ");
//			dataDump();
			
		}
//		public void hCut(int[] seam) {			
//			StdOut.println(width + " x " + height + " before h cut: ");
//			dataDump();
//			for (int i = 0; i < width; i++) {
//				for (int j = seam[i]; j < height; j++){
//					if (j < height - 1) {
//						p.set(i, j, p.get(i, j + 1));
//						updateAdjEnergyTo(j, i);					
//					}
//				}
//			}
//			height -= 1;
//			StdOut.println(width + " x " + height + " after h cut: ");
//			dataDump();
//		}
		public Picture picture() {
			// create a new picture object according to trimmed dimensions
			Picture output = new Picture(width, height);
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					output.set(i, j, p.get(i, j));
				}
			}
			return output;
		}
	}
	public Picture picture() {
		// return picture object according to last operation
		if (lastHorizontal) {
			return picTrans(findSeamT.picture());					
		}
		else {
			return findSeam.picture();
		}
		// current picture
//		Picture output = new Picture(width, height);
//		for (int i = 0; i < width; i++) {
//			for (int j = 0; j < height; j++) {
//				output.set(i, j, findSeam.p.get(i, j));
//			}
//		}		
//		return output;
	}
	public int width() {
		// width of current picture
		return width;
	}
	public int height() {
		// height of current picture
		return height;
	}
	public double energy(int x, int y) {
		// energy of pixel at column x and row y
		if ((x < 0 | x >= picture.width()) | (y < 0 | y >= picture.height())) {
			throw new java.lang.IndexOutOfBoundsException();
		}
		double energyX, energyY;		
		if ((x > 0 & x < width() - 1) & 
				(y > 0 & y < height() - 1)){
			energyX = colorDistSq(picture.get(x - 1, y), picture.get(x + 1, y));
			energyY = colorDistSq(picture.get(x, y - 1), picture.get(x, y + 1));
			return Math.sqrt(energyX + energyY);
		}
		else {
			return 1000;
		}
		
	}
	private double colorDistSq(Color p1, Color p2) {
		double dR, dG, dB;
		dR = p2.getRed() - p1.getRed();
		dG = p2.getGreen() - p1.getGreen();
		dB = p2.getBlue() - p1.getBlue();
		return dR * dR + dG * dG + dB * dB;		
	}
	private void checkSeam(int[] seam, int dim1, int dim2) {
		if (seam.length != dim1) {
			throw new java.lang.IllegalArgumentException();
		}
		for (int i = 0; i < seam.length - 1; i++) {
			if (Math.abs(seam[i] - seam[i + 1]) > 1) {
				throw new java.lang.IllegalArgumentException();
			}
		}
		for (int i = 0; i < seam.length; i++) {
			if (seam[i] < 0 | seam[i] >= dim2) {
				StdOut.print("dimension: " + dim2 + ", illegal seam " + seam[i] + "\n");
				throw new java.lang.IllegalArgumentException();
			}
		}
	}
	
	public int[] findHorizontalSeam() {
		// sequence of indices for horizontal seam
		int[] hSeam = new int[width()];
		int i = 0;
		// check last operation, update the findseam object if the last operation is not horizontal
		if (!lastHorizontal) {
			findSeamT = new FindSeam(picTrans(findSeam.picture()));
		}		
//		StdOut.println();
//		StdOut.print("min h seam: ");
		for (Node n : findSeamT.getMinSeam()) {
			hSeam[i] = n.col();
			i++;
		}
		
//		StdOut.println();
		return hSeam;
	}
	
	public int[] findVerticalSeam() {
		// sequence of indices for vertical seam
		int[] vSeam = new int[height()];
		int i = 0;
		// check last operation, update the findseam object if the last operation is horizontal
		if (lastHorizontal) {
			findSeam = new FindSeam(picTrans(findSeamT.picture()));
		}
//		StdOut.println("v seam size : " + findSeam.getMinSeam().size());
		for (Node n : findSeam.getMinSeam()) {
			vSeam[i] = n.col();
			i++;
		}
		return vSeam;
	}
	public void removeHorizontalSeam(int[] seam) {
		// remove horizontal seam from current picture
		if (seam == null) {
			throw new java.lang.NullPointerException();
		}
		checkSeam(seam, width, height);
//		findSeam.hCut(seam);
		// check last operation, update the findseam object if the last operation is not horizontal
		if (!lastHorizontal) {
			findSeamT = new FindSeam(picTrans(findSeam.picture()));
		}		
		findSeamT.vCut(seam);
		height -= 1;
		lastHorizontal = true;
	}
	public void removeVerticalSeam(int[] seam) {
//		StdOut.print("before removing: " + width + "\n");		
		// remove vertical seam from current picture
		if (seam == null) {
			throw new java.lang.NullPointerException();
		}
		checkSeam(seam, height, width);
		// check last operation, update the findseam object if the last operation is horizontal
		if (lastHorizontal) {
			findSeam = new FindSeam(picTrans(findSeamT.picture()));
		}
		
		findSeam.vCut(seam);
		
//		findSeamT.hCut(seam);
		width -= 1;
//		StdOut.print("after removing: " + width + "\n");
		lastHorizontal = false;
	}

	public static void main(String[] args) {
//		Picture pic = new Picture("seamCarving\\6x5.png");
		Picture pic = new Picture("seamCarving\\6x5_after_cut.png");
//		Picture pic = new Picture("seamCarving\\HJocean.png");
		SeamCarver sc = new SeamCarver(pic);
//		for (int i = 1; i < 34; i++) {
//			sc.removeVerticalSeam(sc.findVerticalSeam());
//		}		
//		Picture picture = SCUtility.toEnergyPicture(sc);
        int[] verticalSeam = sc.findVerticalSeam();
//        Picture overlay = SCUtility.seamOverlay(picture, false, verticalSeam);
//        overlay.save("6x5_after_cut_overlay_1.png");
		StdOut.println("remove v seam");
		StdOut.print("{");
		int[] vSeam = sc.findVerticalSeam();
		for (int j : vSeam) {
			StdOut.print(j + ", ");
		}
		StdOut.println("}");
        sc.removeVerticalSeam(verticalSeam);
//        pic = sc.picture();
//        pic.save("6x5_after_cut.png");
//        verticalSeam = sc.findVerticalSeam();
//        picture = SCUtility.toEnergyPicture(sc);
//        overlay = SCUtility.seamOverlay(picture, false, verticalSeam);
//        overlay.save("6x5_overlay_2.png");
		
////		Picture picT = sc.picTrans(pic);
////		SeamCarver scT = new SeamCarver(picT);
//		StdOut.println("remove v seam");
//		StdOut.print("{");
//		int[] vSeam = sc.findVerticalSeam();
//		for (int j : vSeam) {
//			StdOut.print(j + ", ");
//		}
//		StdOut.println("}");		
//		//sc.removeVerticalSeam(vSeam);
//		StdOut.println("remove h seam");
//		StdOut.print("{");
//		int[] hSeam = sc.findHorizontalSeam();
//		for (int j : hSeam) {
//			StdOut.print(j + ", ");
//		}
//		StdOut.println("}");		
//		//sc.removeHorizontalSeam(sc.findHorizontalSeam());

	}

}
