import java.util.*;

public class BestFirst {
    protected Queue<State> abertos;
    private Set<Ilayout> fechados;
    private State actual;
    private Ilayout objective;

    public static class State {
        private Ilayout layout;
        private State father;
        private double g;

        public State() {
            this.layout = null;
            this.father = null;
            this.g = 0.0;
        }

        public State(Ilayout l, State n) {
            this.layout = l;
            this.father = n;
            if (this.father != null)
                this.g = this.father.getG() + l.getK();
            else
                this.g = 0.0;
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

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            State other = (State) obj;
            return layout.equals(other.layout);
        }

        @Override
        public String toString() {
            return this.layout.toString();
        }
    }

    final private List<State> sucessores(State n) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.getLayout().children();
        for (Ilayout e : children) {
            if ((n.getFather() == null || !e.equals(n.getFather().getLayout())) && !fechados.contains(e)) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
        return sucs;
    }


    final public Iterator<State> solve(Ilayout initial, Ilayout goal) {
        this.objective = goal;
        State initialConfiguration = new State(initial, null);
        this.abertos = new PriorityQueue<>(10, (s1, s2) -> (int) Math.signum(s1.getG()-s2.getG()));
        this.fechados = new HashSet<>();
        Set<Ilayout> conjAbertos = new HashSet<>();

        conjAbertos.add(initialConfiguration.getLayout());
        this.abertos.add(initialConfiguration);

        while (!this.abertos.isEmpty()) {
            this.actual = this.abertos.poll();

            if (this.actual.getLayout().isGoal(this.objective)) {
                return reconstructPath(this.actual);
            }

            List<State> sucs = sucessores(this.actual);
            this.fechados.add(this.actual.getLayout());

            for (State sucessor : sucs) {
                if (!fechados.contains(sucessor.getLayout()) && !conjAbertos.contains(sucessor.getLayout())) {
                    if(sucessor.getLayout().isGoal(this.objective))
                    {
                        return reconstructPath(sucessor);
                    }
                    this.abertos.add(sucessor);
                    conjAbertos.add(sucessor.getLayout());
                }
            }
        }
        return null;
    }

    private Iterator<State> reconstructPath(State goalState) {
        List<State> path = new ArrayList<>();
        State current = goalState;
        while (current != null) {
            path.add(current);
            current = current.getFather();
        }
        Collections.reverse(path);
        return path.iterator();
    }
}
