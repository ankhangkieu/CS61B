package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] result;
    private int numberOfExp;

    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        result = new double[T];
        numberOfExp = T;
        double gridSize = N * N;
        for (int i = 0; i < T; i++) {
            Percolation per = new Percolation(N);
            while (!per.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                per.open(row, col);
            }
            result[i] = ((double) per.numberOfOpenSites()) / gridSize;
        }
    }

    public double mean() {
        return StdStats.mean(result);
    }

    public double stddev() {
        return StdStats.stddev(result);
    }

    public double confidenceLow() {
        return mean() - 1.96 * stddev() / Math.sqrt(numberOfExp);

    }

    public double confidenceHigh() {
        return mean() + 1.96 * stddev() / Math.sqrt(numberOfExp);
    }
}
