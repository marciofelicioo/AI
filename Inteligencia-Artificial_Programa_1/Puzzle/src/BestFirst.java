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
            return layout.hashCode();
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

    /**
     * método que implementa o Best First search
     * @param initial representa a configuração inicial que irá ser processada pelo agente
     * @param goal representa a configuração objetivo que o agente quer alcançar
     * @return Iterator<State> que corresponde a uma vista abstrata sobre uma estrutura de dados
     */
    final public Iterator<State> solve(Ilayout initial, Ilayout goal) {
        this.objective = goal;
        State initialConfiguration = new State(initial, null);
        this.abertos = new PriorityQueue<>(10, (s1, s2) -> Double.compare(s1.getG(), s2.getG()));
        this.fechados = new HashSet<>();
        Set<Ilayout> abertosSet = new HashSet<>();

        this.abertos.add(initialConfiguration);
        abertosSet.add(initialConfiguration.getLayout());

        while (!this.abertos.isEmpty()) {
            this.actual = this.abertos.poll();

            if (this.actual.getLayout().isGoal(this.objective)) {
                return reconstructPath(this.actual);
            }

            List<State> sucs = sucessores(this.actual);
            this.fechados.add(this.actual.getLayout());

            for (State sucessor : sucs) {
                if (!fechados.contains(sucessor.getLayout()) && !abertosSet.contains(sucessor.getLayout())) {
                    if (sucessor.getLayout().isGoal(this.objective)) {
                        return reconstructPath(sucessor);
                    }
                    this.abertos.add(sucessor);
                    abertosSet.add(sucessor.getLayout());
                }
            }
        }
        return null;
    }

    /**
     * método que irá gerar o path desde a configuração objetivo até a configuração inicial
     * @param goalState representa o estado objetivo encontrado
     * @return Iterator<State> que corresponde a uma vista abstrata sobre uma estrutura de dados
     */
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
