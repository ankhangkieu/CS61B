package hw3.puzzle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Board implements WorldState {
    private int[][] tiles;
    private int size;
    private int rowOfZero;
    private int colOfZero;

    public Board(int[][] tiles) {
        this.size = tiles.length;
        this.tiles = new int[this.size][this.size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] == 0) {
                    this.rowOfZero = i;
                    this.colOfZero = j;
                }
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    public int tileAt(int i, int j) {
        if (i >= size() || j >= size()) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        return this.tiles[i][j];
    }

    public int size() {
        return this.size;
    }

    public int hamming() {
        int wrongPos = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tileAt(i, j) != size() * i + j + 1 && tileAt(i, j) != 0) {
                    wrongPos += 1;
                }
            }
        }
        return wrongPos;
    }

    public int manhattan() {
        int distanceTotal = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                distanceTotal += distanceToRightPlace(i, j);
            }
        }
        return distanceTotal;
    }

    private int distanceToRightPlace(int i, int j) {
        int tile = tileAt(i, j);
        if (tile == 0) {
            return 0;
        }
        int rowDiff = Math.abs((tile - 1) / size() - i);
        int colDiff = Math.abs((tile - 1) % size() - j);
        return rowDiff + colDiff;
    }

    public boolean equals(Object y) {
        if (!(y instanceof Board)) {
            return false;
        }
        if (size() != ((Board) y).size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!Arrays.equals(this.tiles[i], ((Board) y).tiles[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    @Override
    public Iterable<WorldState> neighbors() {
        Set<WorldState> neighbors = new HashSet<>();
        Board newBoard = movedown();
        if (newBoard != null) {
            neighbors.add(newBoard);
        }
        newBoard = moveright();
        if (newBoard != null) {
            neighbors.add(newBoard);
        }
        newBoard = moveup();
        if (newBoard != null) {
            neighbors.add(newBoard);
        }
        newBoard = moveleft();
        if (newBoard != null) {
            neighbors.add(newBoard);
        }
        return neighbors;
    }

    private Board moveup() {
        if (rowOfZero == 0) {
            return null;
        }
        int[][] newTile = getCopyOfTile();
        newTile[rowOfZero][colOfZero] = this.tiles[rowOfZero - 1][colOfZero];
        newTile[rowOfZero - 1][colOfZero] = 0;
        return new Board(newTile);
    }

    private Board movedown() {
        if (rowOfZero == size() - 1) {
            return null;
        }
        int[][] newTile = getCopyOfTile();
        newTile[rowOfZero][colOfZero] = this.tiles[rowOfZero + 1][colOfZero];
        newTile[rowOfZero + 1][colOfZero] = 0;
        return new Board(newTile);
    }

    private Board moveleft() {
        if (colOfZero == 0) {
            return null;
        }
        int[][] newTile = getCopyOfTile();
        newTile[rowOfZero][colOfZero] = this.tiles[rowOfZero][colOfZero - 1];
        newTile[rowOfZero][colOfZero - 1] = 0;
        return new Board(newTile);
    }

    private Board moveright() {
        if (colOfZero == size() - 1) {
            return null;
        }
        int[][] newTile = getCopyOfTile();
        newTile[rowOfZero][colOfZero] = this.tiles[rowOfZero][colOfZero + 1];
        newTile[rowOfZero][colOfZero + 1] = 0;
        return new Board(newTile);
    }

    private int[][] getCopyOfTile() {
        int[][] copy = new int[size()][];
        for (int i = 0; i < size(); i++) {
            copy[i] = new int[size()];
            System.arraycopy(tiles[i], 0, copy[i], 0, size());
        }
        return copy;
    }

    /**
     * Returns the string representation of the board.
     * Uncomment this method.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    @Override
    public int hashCode() {
        int code = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                code += code * 13 + tileAt(i, j) * i * j * 7;
            }
        }
        return code;
    }
}
