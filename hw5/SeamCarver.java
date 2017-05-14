import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.HashMap;

public class SeamCarver {
    private Picture picture;
    private double[][] energy;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        calculateEnergy();
    }

    private void calculateEnergy() {
        energy = new double[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                energy[i][j] = xEnergyAt(i, j) + yEnergyAt(i, j);
            }
        }
    }

    private double xEnergyAt(int row, int col) {
        if (width() <= 2) {
            return 0;
        }
        double redDiff, greenDiff, blueDiff;
        int leftCol = col - 1, rightCol = col + 1;
        if (col == 0) {
            leftCol = width() - 1;
        } else if (col == width() - 1) {
            rightCol = 0;
        }
        Color leftPixel = picture.get(leftCol, row);
        Color rightPixel = picture.get(rightCol, row);
        redDiff = Math.abs(leftPixel.getRed() - rightPixel.getRed());
        greenDiff = Math.abs(leftPixel.getGreen() - rightPixel.getGreen());
        blueDiff = Math.abs(leftPixel.getBlue() - rightPixel.getBlue());
        return Math.pow(redDiff, 2) + Math.pow(greenDiff, 2) + Math.pow(blueDiff, 2);
    }

    private double yEnergyAt(int row, int col) {
        if (height() <= 2) {
            return 0;
        }
        double redDiff, greenDiff, blueDiff;
        int topRow = row - 1, bottomRow = row + 1;
        if (row == 0) {
            topRow = height() - 1;
        } else if (row == height() - 1) {
            bottomRow = 0;
        }
        Color topPixel = picture.get(col, topRow);
        Color bottomPixel = picture.get(col, bottomRow);
        redDiff = Math.abs(topPixel.getRed() - bottomPixel.getRed());
        greenDiff = Math.abs(topPixel.getGreen() - bottomPixel.getGreen());
        blueDiff = Math.abs(topPixel.getBlue() - bottomPixel.getBlue());
        return Math.pow(redDiff, 2) + Math.pow(greenDiff, 2) + Math.pow(blueDiff, 2);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return energy[y][x];
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findVerticalSeamHelper(width(), height());
    }

    private int[] findVerticalSeamHelper(int width, int height) {
        int[] vSeam = new int[height];
        HashMap<Integer, Double> cost = new HashMap<>();
        HashMap<Integer, Integer> comeFrom = new HashMap<>();
        int row = 0;
        for (; row < height; row++) {
            if (row == 0) {
                for (int i = 0; i < width; i++) {
                    comeFrom.put(i, -1);
                    cost.put(i, energy(i, row));
                }
            } else {
                for (int i = 0; i < width; i++) {
                    int cur = width * row + i;
                    int upleft = width * (row - 1) + i - 1;
                    int up = width * (row - 1) + i;
                    int upright = width * (row - 1) + i + 1;
                    int from;
                    if (width == 1) {
                        from = up;
                    } else if (i == 0) {
                        from = cost.get(up) < cost.get(upright) ? up : upright;
                    } else if (i == width - 1) {
                        from = cost.get(up) < cost.get(upleft) ? up : upleft;
                    } else {
                        if (cost.get(up) < cost.get(upleft)) {
                            from = cost.get(up) < cost.get(upright) ? up : upright;
                        } else {
                            from = cost.get(upleft) < cost.get(upright) ? upleft : upright;
                        }
                    }
                    comeFrom.put(cur, from);
                    cost.put(cur, cost.get(from) + energy(i, row));
                }
            }
        }
        row -= 1;
        int minPos = width * row;
        for (int i = 0; i < width; i++) {
            if (cost.get(minPos) > cost.get(width * row + i)) {
                minPos = width * row + i;
            }
        }
        for (int i = height - 1; i >= 0; i--) {
            vSeam[i] = minPos % width;
            minPos = comeFrom.get(minPos);
        }
        return vSeam;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] oldEnergy = energy;
        energy = new double[oldEnergy[0].length][oldEnergy.length];
        for (int i = 0; i < oldEnergy.length; i++) {
            for (int j = 0; j < oldEnergy[0].length; j++) {
                energy[j][i] = oldEnergy[i][j];
            }
        }
        int[] hSeam = findVerticalSeamHelper(height(), width());
        energy = oldEnergy;
        return hSeam;
    }


    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        Picture oldPicture = picture;
        picture = new Picture(width() - 1, height());
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (j != seam[i]) {
                    picture.set(i, j, oldPicture.get(i, j));
                }
            }
        }
        calculateEnergy();
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        Picture oldPicture = picture;
        picture = new Picture(width(), height() - 1);
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (i != seam[j]) {
                    picture.set(i, j, oldPicture.get(i, j));
                }
            }
        }
        calculateEnergy();
    }
}
