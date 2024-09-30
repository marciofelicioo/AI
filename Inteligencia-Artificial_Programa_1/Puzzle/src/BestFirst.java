import java.util.*;

public class BestFirst {
    protected Queue<State> abertos;
    private Map<Ilayout, State> fechados;
    private State actual;
    private Ilayout objective;

    /**
     * Classe interna que representa um estado no espaço de busca.
     */

    public static class State {
        private Ilayout layout;
        private State father;
        private double g;
        /**
         * Construtor por omissão
         */
        public State(){
            this.layout = null;
            this.father = null;
            this.g = 0.0;
        }
        /**
         * Construtor de inicialização do estado
         */
        public State(Ilayout l, State n) {
            this.layout = l;
            this.father = n;
            if (this.father != null)
                this.g = this.father.getG() + l.getK();
            else this.g = 0.0;
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
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            State n = (State) obj;
            return this.layout.equals(n.getLayout());
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
            if (n.getFather() == null || !e.equals(n.getFather().getLayout())) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
        return sucs;
    }


    final public Iterator<State> solve(Ilayout initial, Ilayout goal) {
        this.objective = goal;
        this.abertos = new PriorityQueue<>(10, Comparator.comparingDouble(State::getG));
        this.fechados = new HashMap<>();
        Set<Ilayout> visited = new HashSet<>();


        this.abertos.add(new State(initial, null));
        visited.add(initial);

        while (!this.abertos.isEmpty()) {
            this.actual = this.abertos.poll();


            if (this.actual.getLayout().isGoal(goal)) {
                return reconstructPath(this.actual);
            }


            this.fechados.put(this.actual.getLayout(), this.actual);


            List<State> sucs = sucessores(this.actual);
            for (State sucessor : sucs) {

                if (!fechados.containsKey(sucessor.getLayout()) && visited.add(sucessor.getLayout())) {


                    if (sucessor.getLayout().isGoal(this.objective)) {
                        return reconstructPath(sucessor);
                    }

                    this.abertos.add(sucessor);
                } else {
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
