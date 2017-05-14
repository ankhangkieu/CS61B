package hw3.puzzle;

import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.introcs.In;

public class BoardPuzzleSolver {
    public static void main(String[] args) {
        for (int k = 0; k < 51; k++) {
            String fileName = String.format("input/puzzle4x4-%02d.txt", k);
            In in = new In(fileName);
            int size = in.readInt();
            int[][] tiles = new int[size][];
            for (int i = 0; i < size; i++) {
                tiles[i] = new int[size];
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    tiles[i][j] = in.readInt();
                }
            }
            Board startState = new Board(tiles);
            Stopwatch stopwatch = new Stopwatch();
            Solver solver = new Solver(startState);
            if (solver.moves() != k) {
                System.out.println("ERROR: " + k + " !=  " + solver.moves()
                        + ", Time: " + stopwatch.elapsedTime());
            } else {
                System.out.println("DONE: " + k + ", Time: " + stopwatch.elapsedTime());
            }
        }
    }
}
