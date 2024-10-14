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

        // A = {} (Inicializa a fila de espera de nós abertos)
        this.abertos = new PriorityQueue<>(10, (s1, s2) -> Double.compare(s1.getG(), s2.getG()));

        // F = {} (Inicializa a lista de nós fechados)
        this.fechados = new HashSet<>();
        Set<Ilayout> abertosSet = new HashSet<>(); // Para verificar se um nó já está na lista aberta

        // f^(inicial) (Calcula o custo para o nó inicial)
        this.abertos.add(initialConfiguration);  // A <- nó(inicial)
        abertosSet.add(initialConfiguration.getLayout());

        // Repetir indefinidamente até encontrar a solução ou fracassar
        while (!this.abertos.isEmpty()) {
            // 51. Se A estiver vazia, fracasso e sair
            if (this.abertos.isEmpty()) {
                return null;  // Retorna fracasso se não houver mais nós a serem avaliados
            }

            // 52. actual = primeiro(A)
            this.actual = this.abertos.poll();  // Pega o nó com menor f(n)

            // 53. Se actual é objetivo, retorna o caminho(actual) e sair
            if (this.actual.getLayout().isGoal(this.objective)) {
                return reconstructPath(this.actual);  // Retorna o caminho até o objetivo
            }

            // 54. Remover actual de A
            abertosSet.remove(this.actual.getLayout());

            // 55. Adiciona actual a F (Lista de nós fechados)
            this.fechados.add(this.actual.getLayout());

            // 56. Sucs <- expande (actual) (Obter sucessores do nó atual)
            List<State> sucs = sucessores(this.actual);

            // 57. f^(Sucs) (Calcula o valor f dos sucessores)

            // 58. Para cada S em Sucs
            for (State sucessor : sucs) {
                // Se S não existe nem em A nem em F, então inserir S em A
                if (!fechados.contains(sucessor.getLayout()) && !abertosSet.contains(sucessor.getLayout())) {
                    // Verifica se o sucessor já atingiu o objetivo
                    if (sucessor.getLayout().isGoal(this.objective)) {
                        return reconstructPath(sucessor);
                    }

                    // Inserir o sucessor na fila de espera (A)
                    this.abertos.add(sucessor);
                    abertosSet.add(sucessor.getLayout());
                }
                // Se S existe em A e f^(S) < f^(nó n de A), então atualizar n em A
                else if (abertosSet.contains(sucessor.getLayout())) {
                    for (State existingNode : this.abertos) {
                        if (existingNode.getLayout().equals(sucessor.getLayout()) &&
                                sucessor.getG() < existingNode.getG()) {
                            this.abertos.remove(existingNode);  // Remove o nó antigo com maior custo
                            this.abertos.add(sucessor);         // Adiciona o novo nó com menor custo
                            break;
                        }
                    }
                }
                // Se S existe em F e f^(S) < f^(nó n de F), então atualizar n em F
                else if (fechados.contains(sucessor.getLayout())) {
                    for (Ilayout closedNode : this.fechados) {
                        if (closedNode.equals(sucessor) && sucessor.getG() < closedNode.getK()) {
                            fechados.remove(closedNode);  // Remove o nó com maior custo dos fechados
                            this.abertos.add(sucessor);   // Reinsere o nó com menor custo em A
                            abertosSet.add(sucessor.getLayout());
                            break;
                        }
                    }
                }
            }
        }

        // Se a fila de espera esvaziar sem encontrar o objetivo, retorna fracasso
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
