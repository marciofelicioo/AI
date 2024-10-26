package ContainersPP;
import java.util.*;
/**
 * Classe AStar: Implementa o algoritmo de busca A* (A* Search Algorithm) para encontrar o caminho
 * com menor custo acumulado em um espaço de estados definido. A classe armazena os estados gerados
 * e expandidos, além de calcular a função F(n) = G(n) + H(n) para o processo de busca.
 *
 * @author Márcio Felício, Maria Anjos, Miguel Rosa
 * @version 1.0 21/10/2024
 * @inv
 * - Cada estado gerado (State) deve ter um custo g(n) acumulado correto baseado nos custos
 *   dos contêineres movidos até esse estado.(PlatformException)
 * - Cada estado deve calcular corretamente a heurística admissível h(n), garantindo que
 *   h(n) nunca superestima o custo real do caminho para o objetivo (admissibilidade).
 * - O número de nós gerados (generatedNodes) e expandidos (expandedNodes) deve ser sempre
 *   atualizado corretamente para refletir o número real de estados processados.
 * - A estrutura de dados de estados abertos (PriorityQueue<State> abertos) deve estar ordenada
 *   por F(n), garantindo que o estado com menor custo acumulado F(n) seja sempre expandido primeiro.
 * - A heurística deve ser corretamente armazenada em cache para evitar cálculos redundantes.
 */
public class AStar {
    /**
     * Estrutra da classe AStar
     */
    protected Queue<State> abertos;
    private Map<Ilayout, State> abertosMap;
    private Set<Ilayout> fechados;
    private State actual;
    private Ilayout objective;
    public static int generatedNodes = 0;
    public static int expandedNodes = 0;

    /**
     * Classe estática que representa os nós da classe AStar
     * Atributos:
     * - Ilayout: representa a configuração de contentores
     * - State: representa o estado pai
     * - g: representa o custo acumulado desde o estado inicial até o estado currente
     * - heuristicCache: representa a cache que irá guardar valores de h(n) para evitar cálculos repetitivos
     */
    public static class State {
        /**
         * Estrutura da classe State
         */
        private Ilayout layout;
        private State father;
        private double g;
        Map<Ilayout, Double> heuristicCache = new HashMap<>();

        /**
         * Construtor de inicialização da classe State
         *
         * @param l representa a configuração usada pra criar um estado
         * @param n representa o estado pai
         */
        public State(Ilayout l, State n) {
            this.layout = l;
            this.father = n;
            if (this.father != null)
                this.g = this.father.getG() + l.getK();
            else
                this.g = 0.0;
        }

        /**
         * getters da classe State
         */
        public double getF(Ilayout objective) {
            return this.g + computeHeuristic(this.layout, objective);
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

        /**
         * Este método calcula o comprimento do caminho percorrido até o estado atual,
         * contando o número de estados desde o estado inicial até o atual.
         */
        public int getPathLength() {
            int length = 0;
            State current = this;
            while (current.getFather() != null) {
                length++;
                current = current.getFather();
            }
            return length;
        }

        /**
         * Este método calcula a heurística H(n) para o estado atual em relação ao objetivo.
         * Usa cache para evitar recalcular a heurística para o mesmo estado.
         */
        public double computeHeuristic(Ilayout layout, Ilayout goal) {
            Platform current = (Platform)  layout;
            HeuristicStrategy heuristic = new HeuristicStrategy(current.getStacks());
            if (heuristicCache.containsKey(layout)) {
                return heuristicCache.get(layout);
            }
            double heuristicValue = heuristic.computeHeuristic(goal);
            heuristicCache.put(layout, heuristicValue);
            return heuristicValue;
        }

        /**
         * Código único atribuído a cada instância de State criada
         */
        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        /**
         * método que compara se duas instâncias de State são iguais
         *
         * @param obj representa o objeto que será comparado com o recetor
         * @return true caso sejam, false caso contrário
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            State other = (State) obj;
            return layout.equals(other.layout);
        }

        /**
         * @return representação textual da classe State
         */
        @Override
        public String toString() {
            return this.layout.toString();
        }
    }

    /**
     * Método para gerar os sucessores de um estado dado. Para cada filho gerado a partir
     * do layout atual, verifica se não é o estado pai (evitando ciclos).
     *
     * @param n representa o estado que será utilizado para gerar novas configurações
     * @return List<State> sucessores que representa as configurações que foram geradas
     */
    final private List<State> sucessores(State n) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.getLayout().children();
        for (Ilayout e : children) {
            generatedNodes++;
            if ((n.getFather() == null || !e.equals(n.getFather().getLayout()))) {
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
        return sucs;
    }

    /**
     * getters da classe AStar
     */
    public static int getGeneratedNodes() {
        return generatedNodes;
    }

    public static int getExpandedNodes() {
        return expandedNodes;
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
        abertos = new PriorityQueue<>(10, Comparator.comparingDouble((State s) -> s.getF(this.objective))
                .thenComparing(s -> s.computeHeuristic(s.getLayout(), objective)));
        fechados = new HashSet<>();

        State initialState = new State(initial, null);

        abertos.add(initialState);

        abertosMap = new HashMap<>();
        abertosMap.put(initial, initialState);

        List<State> sucs;

        while (!abertos.isEmpty()) {
            actual = abertos.poll();
            expandedNodes++;

            if (actual.getLayout().isGoal(objective)) {
                return actual;
            }

            abertosMap.remove(actual.getLayout());
            fechados.add(actual.getLayout());

            sucs = sucessores(actual);

            for (State successor : sucs) {
                if (!fechados.contains(successor.getLayout()) && !abertosMap.containsKey(successor.getLayout())) {
                    abertos.add(successor);
                    abertosMap.put(successor.getLayout(), successor);
                }
                /**
                 * Eficiência reduzida ao usar estas condições
                 */
//                else if (abertosMap.containsKey(successor.getLayout())) {
//                    State existingNode = abertosMap.get(successor.getLayout());
//                    if (successor.getF(objective) < existingNode.getF(objective)) {
//                        abertos.remove(existingNode);
//                        abertos.add(successor);
//                        abertosMap.put(successor.getLayout(), successor);
//                    }
//                } else if (fechados.contains(successor.getLayout())) {
//                    if (successor.getF(objective) < abertosMap.get(successor.getLayout()).getF(objective)) {
//                        fechados.remove(successor.getLayout());
//                        abertos.add(successor);
//                        abertosMap.put(successor.getLayout(), successor);
//                    }
//                }
            }
        }
        return null;
    }
}
