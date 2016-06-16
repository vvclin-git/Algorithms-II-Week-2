import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
	Picture picture;
	double[][] energyTo;
	int[][] pathFrom;
	private static final double INFINITY = Double.MAX_VALUE;
	public SeamCarver(Picture picture) {
		// create a seam carver object based on the given picture
		this.picture = new Picture(picture);
		this.energyTo = new double[picture.height()][picture.width()];
		this.pathFrom = new int[picture.height()][picture.width()];
		for (int i = 0; i < energyTo.length; i++) {
			for (int j =0; j < energyTo[0].length; j++) {
				if (i == 0) {
					energyTo[i][j] = 0;
				}
				else {
					energyTo[i][j] = INFINITY;
				}
			}
		}
	}
	private int indToX (int ind) {
		
	}
	private int indToY (int ind) {
		
	}
	private int xyToInd (int x, int y) {
		
	}
	private class FindSeam {
		public FindSeam() {
			int indFrom, indTo;
			for (int i = 0; i < energyTo.length; i++) {
				for (int j =0; j < energyTo[0].length; j++) {
					indFrom = xyToInd(j, i);
				}
			}
		}
		private void relax(int indFrom, int indTo) {
			if (energyTo[indToY(indTo)][indToX(indTo)] > 
				energyTo[indToY(indFrom)][indToX(indFrom)] + energy(indToX(indTo), indToY(indTo))) {
				energyTo[indToY(indTo)][indToX(indTo)] = energyTo[indToY(indFrom)][indToX(indFrom)] + energy(indToY(indTo), indToX(indTo));
				pathFrom[indToY(indTo)][indToX(indTo)] = indFrom;
			}
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
	public  double energy(int x, int y) {
		// energy of pixel at column x and row y
		double energyX, energyY;
		if ((x > 0 & x < picture.width() - 1) & 
				(y > 0 & y < picture.height() - 1)){
			energyX = colorDistSq(picture.get(x - 1, y), picture.get(x + 1, y));
			energyY = colorDistSq(picture.get(x, y - 1), picture.get(x, y + 1));
			StdOut.println(picture.get(x-1, y));
			StdOut.println(picture.get(x+1, y));
			StdOut.println(energyX);
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
//	public   int[] findHorizontalSeam() {
//		// sequence of indices for horizontal seam
//	}
//	public   int[] findVerticalSeam() {
//		// sequence of indices for vertical seam
//	}
	public void removeHorizontalSeam(int[] seam) {
		// remove horizontal seam from current picture
	}
	public void removeVerticalSeam(int[] seam) {
		// remove vertical seam from current picture
	}

	public static void main(String[] args) {
		Picture pic = new Picture("seamCarving\\3x4.png");
		SeamCarver sc = new SeamCarver(pic);
		StdOut.println(sc.width());
		
	}

}
