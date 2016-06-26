import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
	private Picture picture, pictureT;
	private FindSeam findSeam, findSeamT;
	private int width, height;
	private static final double INFINITY = Double.MAX_VALUE;
	public SeamCarver(Picture picture) {
		// create a seam carver object based on the given picture
		if (picture == null) {
			throw new java.lang.NullPointerException();
		}
		this.picture = new Picture(picture);
		this.width = picture.width();
		this.height = picture.height();
		this.pictureT = picTrans(picture);		
		this.findSeam = new FindSeam(this.picture);
		this.findSeamT = new FindSeam(this.pictureT);
		
	}
	private int indToX (int ind, Picture picture) {
		ind += 1;
		if (ind % picture.width() == 0) {
			return picture.width() - 1;
		}
		else {
			return (ind % picture.width()) - 1;
		}		
	}
	private int indToY (int ind, Picture picture) {
		ind += 1;
		if (ind % picture.width() == 0) {
			return ind / picture.width() - 1;
		}
		else {
			return (ind / picture.width());
		}	
	}
	private int xyToInd (int x, int y, Picture picture) {
		x += 1;
		y += 1;
		return picture.width() * (y - 1) + x - 1;		
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
	private class FindSeam {
		double[][] energyTo;		
		int[] pathFrom;
		int width, height;
		Picture p;
		public FindSeam(Picture p) {			
			this.p = p;
			this.width = p.width();
			this.height = p.height();
			this.energyTo = new double[p.height()][p.width()];			
			this.pathFrom = new int[p.height() * p.width()];
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
			for (int ind = 0; ind < pathFrom.length; ind++) {
				pathFrom[ind] = -1;
			}
			updateEnergyTo(width, height);

		}
		private void updateEnergyTo(int width, int height) {
			int indFrom, indTo;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (i + 1 < height) {
						indFrom = xyToInd(j, i, p);
						// S
						indTo = xyToInd(j, i + 1, p);
						relax(indFrom, indTo);
						// WS
						if (j - 1 >= 0) {
							indTo = xyToInd(j - 1, i + 1, p);
							relax(indFrom, indTo);
						}
						// ES
						if (j + 1 < width) {
							indTo = xyToInd(j + 1, i + 1, p);
							relax(indFrom, indTo);
						}
					}
				}
			}
		}
		private void updateAdjEnergyTo(int row, int col) {
			// update adjacent EnergyTo
			int indFrom, indTo;
			indFrom = xyToInd(col, row, p);
			if (row + 1 < height) {
				// S
				indTo = xyToInd(col, row + 1, p);
				relax(indFrom, indTo);
				// WS
				if (col - 1 >= 0) {
					indTo = xyToInd(col - 1, row + 1, p);
					relax(indFrom, indTo);
				}
				// ES
				if (col + 1 < width) {
					indTo = xyToInd(col + 1, row + 1, p);
					relax(indFrom, indTo);
				}
			}			
		}
		private void relax(int indFrom, int indTo) {			
			int fromY, fromX, toY, toX;
			fromY = indToY(indFrom, p);
			fromX = indToX(indFrom, p);
			toY = indToY(indTo, p);
			toX = indToX(indTo, p);
//			StdOut.print("energy TO " + "(" + toX + ", " + toY + "): " + energyTo[indToY(indTo, p)][indToX(indTo, p)]);
//			StdOut.print(" | energy From" + "(" + fromX + ", " + fromY + "): "  + energyTo[indToY(indFrom, p)][indToX(indFrom, p)]);
//			StdOut.print(" | energy: " + energy(indToX(indTo, p), indToY(indTo, p)));
			if (energyTo[toY][toX] > 
				energyTo[fromY][fromX] + energy(toX, toY)) {
				energyTo[toY][toX] = energyTo[fromY][fromX] + energy(toX, toY);
				pathFrom[indTo] = indFrom;
//				StdOut.println(" new energy TO: " + energyTo[toY][toX] + "| new path from ind: " + indFrom);
//				for (int j : pathFrom) {
//					StdOut.print(j + ", ");
//				}
				
			}
//			StdOut.println();
		}
		private void relax1(int fromX, int fromY, int toX, int toY) {			
			int indTo, indFrom;
			indFrom = xyToInd(fromX, fromY, p);
			indTo = xyToInd(toX, toY, p); 
//			StdOut.print("energy TO " + "(" + toX + ", " + toY + "): " + energyTo[indToY(indTo, p)][indToX(indTo, p)]);
//			StdOut.print(" | energy From" + "(" + fromX + ", " + fromY + "): "  + energyTo[indToY(indFrom, p)][indToX(indFrom, p)]);
//			StdOut.print(" | energy: " + energy(indToX(indTo, p), indToY(indTo, p)));
			if (energyTo[toY][toX] > 
				energyTo[fromY][fromX] + energy(toX, toY)) {
				energyTo[toY][toX] = energyTo[fromY][fromX] + energy(toX, toY);
				pathFrom[indTo] = indFrom;
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
//				StdOut.println(picture.get(x-1, y));
//				StdOut.println(picture.get(x+1, y));
//				StdOut.println(energyX);
				return Math.sqrt(energyX + energyY);
			}
			else {
				return 1000;
			}
			
		}
		private void dataDump() {
			boolean debug = false;
//			boolean debug = true;
			if (!debug) {
				return;
			}
			StdOut.println("energyTO: ");
			for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++)
	                StdOut.printf("%9.0f ", energyTo[row][col]);
	            StdOut.println();
	        }
			StdOut.println("energy: ");
			for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++)
	                StdOut.printf("%9.0f ", energy(col, row));
	            StdOut.println();
	        }
			StdOut.println("picture: ");
			for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++)
	                StdOut.printf(p.get(col, row).toString() + " ");
	            StdOut.println();
			}
		}
		public Stack<Integer> getMinSeam() {
			Stack<Integer> minSeam = new Stack<Integer>();
			double minEnergy = INFINITY;			
			int minEnergyCol = -1;
			for (int i = 0; i < width; i++) {
				if (energyTo[height - 1][i] < minEnergy) {
					minEnergy = energyTo[height - 1][i];
					minEnergyCol = i;
				}
			}
			minSeam.push(xyToInd(minEnergyCol, height - 1, p)); // push the last grid
			for (int j = pathFrom[xyToInd(minEnergyCol, height - 1, p)]; j != -1; j = pathFrom[j]) {
				minSeam.push(j);
			}			
			return minSeam;
		}
		public void vCut(int[] seam) {
//			StdOut.println(width + " x " + height + " before v cut: ");
			dataDump();
			for (int j = 0; j < height; j++) {
				for (int i = seam[j]; i < width; i++) {
					if (i < width - 1) {
						p.set(i, j, p.get(i + 1, j));
						updateAdjEnergyTo(j, i);						
					}					
				}				
			}
			width -= 1;
//			StdOut.println(width + " x " + height + " after v cut: ");
			dataDump();
			
		}
		public void hCut(int[] seam) {
//			StdOut.println(width + " x " + height + " before h cut: ");
			dataDump();
			for (int i = 0; i < width; i++) {
				for (int j = seam[i]; j < height; j++){
					if (j < height - 1) {
						p.set(i, j, p.get(i, j + 1));
						updateAdjEnergyTo(j, i);					
					}
				}
			}
			height -= 1;
//			StdOut.println(width + " x " + height + " after h cut: ");
			dataDump();
		}
	}
	public Picture picture() {
		// current picture
		Picture output = new Picture(width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				output.set(i, j, findSeam.p.get(i, j));
			}
		}
		return output;
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
//			StdOut.println(picture.get(x-1, y));
//			StdOut.println(picture.get(x+1, y));
//			StdOut.println(energyX);
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
				throw new java.lang.IllegalArgumentException();
			}
		}
	}
	
	public int[] findHorizontalSeam() {
		// sequence of indices for horizontal seam
		int[] hSeam = new int[width()];
		int i = 0;
//		StdOut.println();
//		StdOut.print("min h seam: ");
		for (int ind : findSeamT.getMinSeam()) {
//			StdOut.print(ind + ", ");
			hSeam[i] = indToX(ind, pictureT);
			i++;
		}
//		StdOut.println();
		return hSeam;
	}
	
	public int[] findVerticalSeam() {
		// sequence of indices for vertical seam
		int[] vSeam = new int[height()];
		int i = 0;
//		StdOut.println("v seam size : " + findSeam.getMinSeam().size());
		for (int ind : findSeam.getMinSeam()) {
//			StdOut.println(ind);
			vSeam[i] = indToX(ind, picture);
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
		findSeam.hCut(seam);
		findSeamT.vCut(seam);
		height -= 1;
	}
	public void removeVerticalSeam(int[] seam) {
		// remove vertical seam from current picture
		if (seam == null) {
			throw new java.lang.NullPointerException();
		}
		checkSeam(seam, height, width);
		findSeam.vCut(seam);
		findSeamT.hCut(seam);
		width -= 1;
	}

	public static void main(String[] args) {
		Picture pic = new Picture("seamCarving\\6x5.png");
		SeamCarver sc = new SeamCarver(pic);
		sc.removeHorizontalSeam(sc.findHorizontalSeam());
		sc.removeVerticalSeam(sc.findVerticalSeam());
//		Picture picCarved = sc.picture();
//		picCarved.save("seamCarving\\6x5c.png");
//		StdOut.print("{");
//		int[] vSeam = sc.findVerticalSeam();
//		for (int j : vSeam) {
//			StdOut.print(j + ", ");
//		}
//		StdOut.println("}");
//		StdOut.print("{");
//		int[] hSeam = sc.findHorizontalSeam();
//		for (int j : hSeam) {
//			StdOut.print(j + ", ");
//		}
//		StdOut.println("}");
//		StdOut.println("}");
//		for (int i = 0; i < 12; i++) {
//			StdOut.println("(" + sc.indToX(i) + ", " + sc.indToY(i) + ")");
//		}
//		for (int j = 0; j < sc.height(); j++) {
//			for (int i =0; i < sc.width(); i++) {
//				StdOut.println("(" + i + ", " + j + ") |" + sc.xyToInd(i, j));
//			}
//		}
	}

}
