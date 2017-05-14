package hw2;                       

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int size;
    private int sizeOfOpens;
    private WeightedQuickUnionUF connectedBlocks;
    private WeightedQuickUnionUF fullBlocks;
    private boolean[][] openGrid;

    public Percolation(int N) {
        if (N <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        openGrid = new boolean[N][];
        connectedBlocks = new WeightedQuickUnionUF(N * N + 1);
        fullBlocks = new WeightedQuickUnionUF(N * N + 2);
        sizeOfOpens = 0;
        size = N;
        for (int i = 0; i < N; i++) {
            openGrid[i] = new boolean[N];
        }
    }

    public void open(int row, int col) {
        if (row >= size || col >= size) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        if (isOpen(row, col)) {
            return;
        }
        openGrid[row][col] = true;
        if (row == size - 1) {
            fullBlocks.union(row * size + col + 1, size * size + 1);
        }
        checkFullBlock(row, col);
        sizeOfOpens += 1;
    }

    private void checkFullBlock(int row, int col) {
        int placeInGrid = row * size + col + 1;
        if (row == 0) {
            connectedBlocks.union(0, placeInGrid);
            fullBlocks.union(0, placeInGrid);
        }
        if (isOpen(Math.max(row - 1, 0), col)) {
            connectedBlocks.union(placeInGrid, Math.max(row - 1, 0) * size + col + 1);
            fullBlocks.union(placeInGrid, Math.max(row - 1, 0) * size + col + 1);
        }
        if (isOpen(Math.min(row + 1, size - 1), col)) {
            connectedBlocks.union(placeInGrid, Math.min(row + 1, size - 1) * size + col + 1);
            fullBlocks.union(placeInGrid, Math.min(row + 1, size - 1) * size + col + 1);
        }
        if (isOpen(row, Math.min(col + 1, size - 1))) {
            connectedBlocks.union(placeInGrid, row * size + Math.min(col + 1, size - 1) + 1);
            fullBlocks.union(placeInGrid, row * size + Math.min(col + 1, size - 1) + 1);
        }
        if (isOpen(row, Math.max(col - 1, 0))) {
            connectedBlocks.union(placeInGrid, row * size + Math.max(col - 1, 0) + 1);
            fullBlocks.union(placeInGrid, row * size + Math.max(col - 1, 0) + 1);
        }
    }

    public boolean isOpen(int row, int col) {
        if (row >= size || col >= size) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        return openGrid[row][col];
    }

    public boolean isFull(int row, int col) {
        if (row >= size || col >= size) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        return connectedBlocks.connected(0, row * size + col + 1)
                && fullBlocks.connected(0, row * size + col + 1);
    }

    public int numberOfOpenSites() {
        return sizeOfOpens;
    }

    public boolean percolates() {
        return fullBlocks.connected(size * size + 1, 0);
    }
}
