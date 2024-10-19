import java.util.*;
public class AStar {
    protected Queue<State> abertos;
    private Map<Ilayout,State> fechados;
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
        for (Ilayout e : children) {
            if ((n.getFather() == null || !e.equals(n.getFather().getLayout()))) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
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
        Map<Ilayout, State> abertosMap = new HashMap<>();
        Map<Ilayout, Boolean> activeMap = new HashMap<>();
        fechados = new HashMap<>();

        State initialState = new State(initial, null);
        abertos.add(initialState);
        abertosMap.put(initial, initialState);
        activeMap.put(initial, true);

        double currentBestCost = Double.MAX_VALUE;

        while (!abertos.isEmpty()) {
            actual = abertos.poll();

            if (!activeMap.getOrDefault(actual.getLayout(), true)) {
                continue;
            }
            abertosMap.remove(actual.getLayout());
            activeMap.remove(actual.getLayout());
            expandedNodes++;

            if (actual.getLayout().isGoal(objective)) {
                return actual;
            }

            fechados.put(actual.getLayout(), actual);

            List<State> sucs = sucessores(actual);
            generatedNodes += sucs.size();
            System.out.println(generatedNodes);


            for (State successor : sucs) {
                if (successor.getF(this.objective) > currentBestCost * 1.1) { // Allow a small margin
                    continue; // Skip this state if its cost exceeds the current best by 10%
                }

                // Update the current best cost if this state reaches the goal
                if (successor.getLayout().isGoal(this.objective)) {
                    currentBestCost = Math.min(currentBestCost, successor.getG());
                }

                if (!fechados.containsKey(successor.getLayout()) && !abertosMap.containsKey(successor.getLayout())) {
                    if (successor.getLayout().equals(this.objective)) {
                        return successor;
                    }
                    abertos.add(successor);
                    abertosMap.put(successor.getLayout(), successor);
                    activeMap.put(successor.getLayout(), true);
                } else if (abertosMap.containsKey(successor.getLayout())) {
                    State openState = abertosMap.get(successor.getLayout());
                    if (successor.getF(this.objective) < openState.getF(this.objective)) {
                        activeMap.put(openState.getLayout(), false);
                        abertos.add(successor);
                        abertosMap.put(successor.getLayout(), successor);
                        activeMap.put(successor.getLayout(), true);
                    }
//                    if (successor.getG() < openState.getG()) {
//                        // Atualiza o estado na fila de abertos com o novo caminho de menor custo
//                        activeMap.put(openState.getLayout(), false);
//                        abertos.add(successor);
//                        abertosMap.put(successor.getLayout(), successor);
//                        activeMap.put(successor.getLayout(), true);
//                    }
                } else if (fechados.containsKey(successor.getLayout())) {
                    State closedState = fechados.get(successor.getLayout());
                    if (successor.getF(this.objective) < closedState.getF(this.objective) - 1) { // Reabertura seletiva
                        fechados.remove(successor.getLayout());
                        abertos.add(successor);
                        abertosMap.put(successor.getLayout(), successor);
                        activeMap.put(successor.getLayout(), true);
                    }
//                    if (successor.getG() < closedState.getG()) {
//                        // Reabre o estado fechado com o novo caminho mais barato
//                        fechados.remove(successor.getLayout());
//                        abertos.add(successor);
//                        abertosMap.put(successor.getLayout(), successor);
//                        activeMap.put(successor.getLayout(), true);
//                    }
                }
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
