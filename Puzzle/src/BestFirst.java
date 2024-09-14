import javax.swing.text.html.HTMLDocument;
import java.util.*;
import static java.lang.System.out;

public class BestFirst {
    protected Queue<State> abertos;
    private Map<Ilayout, State> fechados;
    private State actual;
    private Ilayout objective;

    public static class State {
        private Ilayout layout;
        private State father;
        private double g;

        public State(Ilayout l, State n) {
            this.layout = l;
            this.father = n;
            if (this.father != null)
                this.g = this.father.getG() + l.getK();
            else this.g = 0.0;
        }

        public String toString() {
            return this.layout.toString();
        }

        public double getG() {
            return this.g;
        }

        public Ilayout getLayout() {
            return this.layout;
        }

        public State getFather() {
            return this.father;
        }

        public int hashCode() {
            return toString().hashCode();
        }

        public boolean equals(Object o) {
            if (o == null) return false;
            if (this.getClass() != o.getClass()) return false;
            State n = (State) o;
            return this.layout.equals(n.getLayout());
        }
    }

    final private List<State> sucessores(State n) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.getLayout().children();
        for (Ilayout e : children) {
            if (n.getFather() == null || !e.equals(n.getFather().getLayout())) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
        return sucs;
    }

    final public Iterator<State> solve(Ilayout initial, Ilayout goal) {
        this.objective = goal;
        // Create the priority queue 'abertos' with a comparator based on state cost (g)
        this.abertos = new PriorityQueue<>(10, (s1, s2) -> Double.compare(s1.getG(), s2.getG()));
        this.fechados = new HashMap<>();

        // Add the initial state to the open list (abertos)
        this.abertos.add(new State(initial, null));

        while (!this.abertos.isEmpty()) {
            // Let 'actual' be the state with the lowest cost (remove from abertos)
            this.actual = this.abertos.poll();

            // If 'actual' state matches the goal state, return the solution
            if (this.actual.getLayout().equals(goal)) {
                return reconstructPath(this.actual); // Returns the path from initial to goal
            }

            // Insert the 'actual' state into the closed list (fechados)
            this.fechados.put(this.actual.getLayout(), this.actual);

            // Get all the successors of the 'actual' state
            List<State> sucs = sucessores(this.actual);

            for (State sucessor : sucs) {
                // If the successor is already in the closed list, skip it
                if (this.fechados.containsKey(sucessor.getLayout())) {
                    continue;
                }

                // If the successor is not in 'abertos', add it to the priority queue
                if (!this.abertos.contains(sucessor)) {
                    this.abertos.add(sucessor);
                }
            }
        }

        // If 'abertos' becomes empty, return null (failure to find a solution)
        return null;
    }
    private Iterator<State> reconstructPath(State goalState) {
        List<State> path = new ArrayList<>();
        State current = goalState;

        // Traverse backwards from the goal to the initial state using the parent reference
        while (current != null) {
            path.add(current);
            current = current.getFather();
        }

        // Reverse the path to get the sequence from the initial state to the goal
        Collections.reverse(path);

        // Return the path as an iterator
        return path.iterator();
    }
}
