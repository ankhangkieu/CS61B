package hw3.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;

public class Solver {
    private ArrayList<WorldState> sol;

    private class WorldStateEntry {
        WorldState state;
        int move;
        int priority;
        WorldStateEntry prev;
        WorldStateEntry(WorldState state, int move, WorldStateEntry prev) {
            this.state = state;
            this.move = move;
            this.priority = move + state.estimatedDistanceToGoal();
            this.prev = prev;
        }
    }

    private class WorldComparator implements Comparator<WorldStateEntry> {
        @Override
        public int compare(WorldStateEntry o1, WorldStateEntry o2) {
            return o1.priority - o2.priority;
        }
    }

    public Solver(WorldState initial) {
        MinPQ<WorldStateEntry> heapState = new MinPQ<>(new WorldComparator());
        HashSet<WorldState> visitedState = new HashSet<>();
        HashSet<WorldState> statesOnControl = new HashSet<>();
        HashMap<WorldState, Integer> distToStart = new HashMap<>();

        statesOnControl.add(initial);
        distToStart.put(initial, 0);
        heapState.insert(new WorldStateEntry(initial, 0, null));
        while (!statesOnControl.isEmpty()) {
            WorldStateEntry current = heapState.delMin();
            if (current.state.isGoal()) {
                this.sol = reconstructPath(current);
                return;
            }

            statesOnControl.remove(current.state);
            visitedState.add(current.state);

            for (WorldState neigh : current.state.neighbors()) {
                if (visitedState.contains(neigh)) {
                    continue;
                }

                int score = distToStart.get(current.state) + 1;
                if (!statesOnControl.contains(neigh)) {
                    statesOnControl.add(neigh);
                } else if (distToStart.containsKey(neigh) && score >= distToStart.get(neigh)) {
                    continue;
                }
                distToStart.put(neigh, score);
                heapState.insert(new WorldStateEntry(neigh, score, current));
            }
        }
    }

    private ArrayList<WorldState> reconstructPath(WorldStateEntry entry) {
        ArrayList<WorldState> path = new ArrayList<>();
        path.add(entry.state);
        entry = entry.prev;
        while (entry != null) {
            path.add(entry.state);
            entry = entry.prev;
        }
        Collections.reverse(path);
        return path;
    }

    public int moves() {
        return sol.size() - 1;
    }

    public Iterable<WorldState> solution() {
        return sol;
    }
}
