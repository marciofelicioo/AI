import java.util.*;
public class AStar {
    protected Queue<State> abertos;
    private Set<Ilayout> fechados = new HashSet<>();
    private State actual;
    private Ilayout objective;

    static int generatedNodes = 0;
    static int expandedNodes = 0;

    public static class State {
        private Ilayout layout;
        private State father;
        private double g;

        public State(Ilayout l, State n) {
            this.layout = l;
            this.father = n;
            if (this.father != null)
                this.g = this.father.getG() + l.getK();
            else
                this.g = 0.0;
        }

        // F(n) = G(n) + H(n)
        public double getF(Ilayout objective) {
            return this.g + this.layout.computeHeuristic(objective);
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

        // Adiciona apenas layouts únicos ao HashSet e cria um novo estado para cada um
        for (Ilayout e : children) {
            if ((n.getFather() == null || !e.equals(n.getFather().getLayout()))) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }

        // Ordena os sucessores com base na heurística (h(n)) para expandir primeiro os mais promissores
//        sucs.sort(Comparator.comparingDouble(s -> s.getLayout().computeHeuristic(this.objective)));
//
        return sucs;
    }



    /**
     * Método que implementa o A*.
     *
     * @param initial representa a configuração inicial
     * @param goal    representa a configuração objetivo
     * @return O estado objetivo alcançado
     */
    public State solve(Ilayout initial, Ilayout goal) {
        this.objective = goal;

        abertos = new PriorityQueue<>(10000, Comparator.comparingDouble(s -> s.getF(this.objective)));
        Map<Ilayout, State> abertosMap = new HashMap<>();  // Novo HashMap para estados abertos
        fechados = new HashSet<>();

        State initialState = new State(initial, null);
        abertos.add(initialState);
        abertosMap.put(initial, initialState);

        while (!abertos.isEmpty()) {
            actual = abertos.poll();
            abertosMap.remove(actual.getLayout());

            expandedNodes++;
            System.out.println(expandedNodes);

            if (actual.getLayout().isGoal(objective)) {
                return actual;
            }

            fechados.add(actual.getLayout());

            List<State> sucs = sucessores(actual);
            generatedNodes += sucs.size();

            for (State successor : sucs) {
                if (!fechados.contains(successor.getLayout()) && !abertosMap.containsKey(successor.getLayout())) {
                    //System.out.println(successor);
                    if (successor.getLayout().equals(this.objective)) {
                        return successor;
                    }
                    abertos.add(successor);
                    abertosMap.put(successor.getLayout(), successor);
                }

//                else if (abertosMap.containsKey(successor.getLayout())) {
//                    State openState = abertosMap.get(successor.getLayout());
//                    if (successor.getF(this.objective) < openState.getF(this.objective)) {
//                        abertos.remove(openState);
//                        abertos.add(successor);
//                        abertosMap.put(successor.getLayout(), successor);
//                    }
//                }
//
//
//                else if (fechados.containsKey(successor.getLayout())) {
////                    System.out.println("ok");
//                    State closedState = fechados.get(successor.getLayout());
//                    if (successor.getF(this.objective) < closedState.getF(this.objective)) {
//                        fechados.remove(successor.getLayout());
//                        abertos.add(successor);
//                        abertosMap.put(successor.getLayout(), successor);
//                    }
//                }
            }
        }

        return null;
    }





    public static int getGeneratedNodes() {
        return generatedNodes;
    }

    public static int getExpandedNodes() {
        return expandedNodes;
    }
}
