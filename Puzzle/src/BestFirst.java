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

        /**
         * Construtor de cópia
         */
        public State(State s){
            this.setLayout(s.getLayout());
            this.setFather(s.getFather());
            this.setG(s.getG());
        }

        /**
         * setters e getters
         */
        public void setLayout(Ilayout l){
            this.layout = l;
        }

        public void setG(double g) {
            this.g = g;
        }

        public void setFather(State father) {
            this.father = father;
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
        /**
         * @param obj serve para verificar se o objeto passado como parametro é ou não igual ao objeto recetor
         * @return true ou false caso seja ou não seja igual
         */

        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            State n = (State) obj;
            return this.layout.equals(n.getLayout());
        }
        /**
         * Este método tem o principal objetivo de retornar um clone de uma instancia para não partilharem
         * os mesmos endereços e assim não correr o risco de caso uma instancia seja alterada a outra também o seja
         */

        public State clone()
        {
            return new State(this);
        }

        /**
         * Este método tem o principal objetivo de retornar a representação textual do objeto State.
         */
        public String toString() {
            return this.layout.toString();
        }
    }

    /**
     * Gera a lista de sucessores do estado atual, isto é, os estados filhos.
     * @param n representa o estado a partir do qual iremos obter os estados filhos
     * @return sucs                                     "I do not think it does"
     */
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

    /**
     *
     * @param initial representa a configuração inicial
     * @param goal representa a configuração objetivo
     * @return o iterador(vista abstrata que permite realizar
     * operações sobre a estrutura de dados lista) caso a configuração objetivo seja encontrada e null cc.
     */
    final public Iterator<State> solve(Ilayout initial, Ilayout goal) {
        this.objective = goal;
        this.abertos = new PriorityQueue<>(10, (s1, s2) -> Double.compare(s1.getG(), s2.getG()));
        this.fechados = new HashMap<>();
        this.abertos.add(new State(initial, null));
        while (!this.abertos.isEmpty()) {
            this.actual = this.abertos.poll();

            if (this.actual.getLayout().isGoal(goal))
                return reconstructPath(this.actual);

            this.fechados.put(this.actual.getLayout(), this.actual);

            List<State> sucs = sucessores(this.actual);

            for (State sucessor : sucs) {
                if (this.fechados.containsKey(sucessor.getLayout())) {
                    continue;
                }
                if (!this.abertos.contains(sucessor)) {
                    this.abertos.add(sucessor);
                }
            }
        }

        return null;
    }

    /**
     * Reconstrói o caminho desde a configuração goal até a configuração inicial
     * @param goalState representa a configuração objetivo
     * @return iterador de lista path (path.iterator());
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
