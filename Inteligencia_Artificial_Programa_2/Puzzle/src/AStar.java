import java.util.*;
public class AStar {
    protected Queue<State> abertos;
    private Map<Ilayout, State> fechados;
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
            State nn = new State(e, n);
            if ((n.getFather() == null || !e.equals(n.getFather().getLayout()))) {
                sucs.add(nn);
            }
        }
        return sucs;
    }

    /**
     * Método que implementa o A*.
     * @param initial representa a configuração inicial
     * @param goal representa a configuração objetivo
     * @return O estado objetivo alcançado
     */
    public State solve(Ilayout initial, Ilayout goal) {
        this.objective = goal;
        abertos = new PriorityQueue<>(10, Comparator.comparingDouble(s -> s.getF(this.objective)));
        fechados = new HashMap<>();

        State initialState = new State(initial, null);
        abertos.add(initialState);

        while (!abertos.isEmpty()) {
            actual = abertos.poll();

            expandedNodes++;
            System.out.println(expandedNodes);

            if (actual.getLayout().isGoal(objective)) {
                return actual;
            }


            fechados.put(actual.getLayout(), actual);


            List<State> sucs = sucessores(actual);
            generatedNodes += sucs.size();

            for (State successor : sucs) {
                double successorF = successor.getF(this.objective);


                if (!fechados.containsKey(successor.getLayout()) && !abertos.contains(successor)) {
                    if(successor.getLayout().equals(this.objective))
                    {
                        return successor;
                    }
                    abertos.add(successor);
                }

                else if (abertos.contains(successor)) {
                    for (State openState : abertos) {
                        if (openState.equals(successor) && successorF < openState.getF(this.objective)) {
                            abertos.remove(openState);
                            abertos.add(successor);
                            break;
                        }
                    }
                }
                else if (fechados.containsKey(successor.getLayout()) && successorF < fechados.get(successor.getLayout()).getF(this.objective)) {
                    fechados.remove(successor.getLayout());
                    abertos.add(successor);
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
