import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
	private Picture picture;
	private FindSeam findSeam;
	private static final double INFINITY = Double.MAX_VALUE;
	public SeamCarver(Picture picture) {
		// create a seam carver object based on the given picture
		this.picture = new Picture(picture);
		this.findSeam = new FindSeam();
	}
	private int indToX (int ind) {
		ind += 1;
		if (ind % picture.width() == 0) {
			return picture.width() - 1;
		}
		else {
			return (ind % picture.width()) - 1;
		}		
	}
	private int indToY (int ind) {
		ind += 1;
		if (ind % picture.width() == 0) {
			return ind / picture.width() - 1;
		}
		else {
			return (ind / picture.width());
		}	
	}
	private int xyToInd (int x, int y) {
		x += 1;
		y += 1;
		return picture.width() * (y - 1) + x - 1;		
	}
	private class FindSeam {
		double[][] energyTo;
		int[] pathFrom;
		public FindSeam() {
			int indFrom, indTo;
			this.energyTo = new double[height()][width()];
			this.pathFrom = new int[height() * width()];
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
			for (int i = 0; i < energyTo.length; i++) {
				for (int j = 0; j < energyTo[0].length; j++) {
					if (i + 1 < energyTo.length) {
						indFrom = xyToInd(j, i);
						// S
						indTo = xyToInd(j, i + 1);
						relax(indFrom, indTo);
						// WS
						if (j - 1 >= 0) {
							indTo = xyToInd(j - 1, i + 1);
							relax(indFrom, indTo);
						}
						// ES
						if (j + 1 < energyTo[0].length) {
							indTo = xyToInd(j + 1, i + 1);
							relax(indFrom, indTo);
						}
					}
				}
			}
			// output energyTo for debugging
			StdOut.println("energyTO: ");
			for (int row = 0; row < energyTo.length; row++) {
	            for (int col = 0; col < energyTo[0].length; col++)
	                StdOut.printf("%9.0f ", energyTo[row][col]);
	            StdOut.println();
	        }
		}
		private void relax(int indFrom, int indTo) {
//			StdOut.print("energy TO: " + energyTo[indToY(indTo)][indToX(indTo)]);
//			StdOut.print(" | energy From: "  + energyTo[indToY(indFrom)][indToX(indFrom)]);
//			StdOut.print(" | energy: " + energy(indToX(indTo), indToY(indTo)));
			if (energyTo[indToY(indTo)][indToX(indTo)] > 
				energyTo[indToY(indFrom)][indToX(indFrom)] + energy(indToX(indTo), indToY(indTo))) {
				energyTo[indToY(indTo)][indToX(indTo)] = energyTo[indToY(indFrom)][indToX(indFrom)] + energy(indToX(indTo), indToY(indTo));
				pathFrom[indTo] = indFrom;
				StdOut.println(" new energy TO: " + energyTo[indToY(indTo)][indToX(indTo)]);
			}
//			StdOut.println();
		}
		public Stack<Integer> getMinSeam() {
			Stack<Integer> minSeam = new Stack<Integer>();
			double minEnergy = INFINITY;			
			int minEnergyCol = -1;
			for (int i = 0; i < width(); i++) {
				if (energyTo[height() - 1][i] < minEnergy) {
					minEnergy = energyTo[height() - 1][i];
					minEnergyCol = i;
				}
			}
			minSeam.push(xyToInd(minEnergyCol, height() - 1));
			for (int j = pathFrom[xyToInd(minEnergyCol, height() - 1)]; j != 0; j = pathFrom[j]) {
				minSeam.push(j);
			}
			return minSeam;
		}
	}
	public Picture picture() {
		// current picture
		return picture;
	}
	public int width() {
		// width of current picture
		return picture.width();
	}
	public int height() {
		// height of current picture
		return picture.height();
	}
	public double energy(int x, int y) {
		// energy of pixel at column x and row y
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
//	public int[] findHorizontalSeam() {
//		// sequence of indices for horizontal seam
//		
//	}
	public int[] findVerticalSeam() {
		// sequence of indices for vertical seam
		int[] vSeam = new int[height()];
		int i = 0;
		for (int ind : findSeam.getMinSeam()) {
			vSeam[i] = indToX(ind);
			i++;
		}
		return vSeam;
	}
	public void removeHorizontalSeam(int[] seam) {
		// remove horizontal seam from current picture
	}
	public void removeVerticalSeam(int[] seam) {
		// remove vertical seam from current picture
	}

	public static void main(String[] args) {
		Picture pic = new Picture("seamCarving\\6x5.png");
		SeamCarver sc = new SeamCarver(pic);
		
		
		StdOut.print("{");
		int[] vSeam = sc.findVerticalSeam();
		for (int j : vSeam) {
			StdOut.print(j + ", ");
		}
		StdOut.println("}");
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
