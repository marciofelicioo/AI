import java.util.*;
public class AStar {
    protected Queue<State> abertos;
    private Map<Ilayout, State> abertosMap;
    private Map<Ilayout, Boolean> activeMap;
    private Map<Ilayout, State> fechados;
    private State actual;
    private Ilayout objective;
    static int generatedNodes = 0;
    static int expandedNodes = 0;

    public static class State {
        private Ilayout layout;
        private State father;
        private double g;
        Map<Ilayout, Double> heuristicCache = new HashMap<>();

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
            return this.g + computeHeuristic(this.layout,objective);
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

        public double computeHeuristic(Ilayout layout,Ilayout goal) {
            if (heuristicCache.containsKey(layout)) {
                return heuristicCache.get(layout);
            }
            double heuristicValue = layout.computeHeuristic(goal);
            heuristicCache.put(layout, heuristicValue);
            return heuristicValue;
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

    final private List<State> sucessores(State n, int maxSucessores) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.getLayout().children();
        for (Ilayout e : children) {
            if ((n.getFather() == null || !e.equals(n.getFather().getLayout()))) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
            if (sucs.size() >= maxSucessores) break;
        }
//        sucs.sort(Comparator.comparingDouble(s -> s.getF(this.objective)));
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
        abertos = new PriorityQueue<>(100000, Comparator.comparingDouble((State s) -> s.getF(this.objective))
//                .thenComparing(s -> -(s.getG()))
//                .thenComparing(s -> -(s.getLayout().children().size()))
        );

        abertosMap = new HashMap<>();
        activeMap = new HashMap<>();
        fechados = new HashMap<>();
        State initialState = new State(initial, null);
        abertos.add(initialState);
        abertosMap.put(initial, initialState);
        activeMap.put(initial, true);

        while (!abertos.isEmpty()) {
            actual = abertos.poll();
            if (!activeMap.getOrDefault(actual.getLayout(), true)) {
                continue;
            }
            abertosMap.remove(actual.getLayout());
            activeMap.remove(actual.getLayout());
            expandedNodes++;
            System.out.println("valor de custo acumulado do atual - " + actual.getG());
            System.out.println("valor heuristico do atual - " + actual.getLayout().computeHeuristic(this.objective));
            System.out.println("valor de f(n) do atual - " + actual.getF(this.objective));
            System.out.println("estado correspondente ao valor heuristico do atual" + actual);

            if (actual.getLayout().isGoal(objective)) {
                return actual;
            }

            fechados.put(actual.getLayout(), actual);

            List<State> sucs = sucessores(actual, 100000);
            generatedNodes += sucs.size();
            for (State successor : sucs) {

                if (!fechados.containsKey(successor.getLayout()) && !abertosMap.containsKey(successor.getLayout())) {
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

                } else if (fechados.containsKey(successor.getLayout())) {
                    State closedState = fechados.get(successor.getLayout());
                    if (successor.getF(this.objective) < closedState.getF(this.objective) - 0.5) { // Valor reduzido para reabertura seletiva mais restrita
                        fechados.remove(successor.getLayout());
                        abertos.add(successor);
                        abertosMap.put(successor.getLayout(), successor);
                        activeMap.put(successor.getLayout(), true);
                    }
                }
            }
        }
        return null;  // Retorna null se não encontrar o objetivo
    }
    private void cleanUpClosedSet(Map<Ilayout, State> fechados) {
        List<Map.Entry<Ilayout, State>> entries = new ArrayList<>(fechados.entrySet());
        entries.sort(Comparator.comparing(e -> e.getValue().getF(this.objective)));
        int toRemove = (int) (fechados.size() * 0.2);  // Remove apenas os 20% piores
        for (int i = 0; i < toRemove; i++) {
            fechados.remove(entries.get(i).getKey());
        }
    }

    private void cleanUpOpenSet(Queue<State> abertos) {
        // Remove estados cuja heurística esteja significativamente acima da média
        double averageF = abertos.stream().mapToDouble(s -> s.getF(this.objective)).average().orElse(Double.MAX_VALUE);
        abertos.removeIf(state -> state.getF(this.objective) > averageF * 1.2);  // Remove os piores 50%
    }
    public static int getGeneratedNodes() {
        return generatedNodes;
    }

    public static int getExpandedNodes() {
        return expandedNodes;
    }

}
