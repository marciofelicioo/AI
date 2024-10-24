package ContainersPP;

import Exceptions.ContainersException;
import Exceptions.PlatformException;

import java.util.*;

/**
 * Classe ContainersPP.Platform: Esta classe contém construtores que criam objetos Platform
 * e o seu principal objetivo é criar sucessores de uma certa configuração e
 * calcular a heuristica para poder auxiliar o método solve da classe AStar (A* algorithm)
 * @author Márcio Felício, Maria Anjos, Miguel Rosa
 * @version 1.0 19/10/2024
 * @inv Cada Contentor tem de ter um custo associado caso seja configuração inicial
 */
public class Platform implements Ilayout, Cloneable {
    /**
     * Estrutura estática da classe Platform
     */
    public static final Comparator<Deque<Container>> compareChars = (s1, s2) -> {
        if (s1.isEmpty() && s2.isEmpty()) return 0;
        if (s1.isEmpty()) return -1;
        if (s2.isEmpty()) return 1;
        return String.valueOf(s1.peekFirst().getId()).compareTo(String.valueOf(s2.peekFirst().getId()));
    };

    private static Map<Character, Position> goalPositions = null;

    private static class Position {
        char base;
        int height;

        Position(char base, int height) {
            this.base = base;
            this.height = height;
        }
    }

    /**
     * Estrutura da classe Platform
     */
    private final List<Deque<Container>> stacks;
    private double cost;

    /**
     * Construtor por omissão
     */
    public Platform() {
        stacks = null;
        this.cost = 0;
    }

    /**
     * Construtor de inicialização
     * @throws PlatformException Se algum contentor na configuração inicial não tiver um custo associado.
     */
    public Platform(String config, boolean isInitial) throws PlatformException, ContainersException {
        stacks = new ArrayList<>();
        parseInput(config, isInitial);
        this.cost = 0;
    }

    /**
     * Construtor de cópia para casos de encapsulamento de dados
     */
    public Platform(Platform other) {
        this.stacks = new ArrayList<>(other.stacks.size());

        for (Deque<Container> stack : other.stacks) {
            Deque<Container> newStack = new ArrayDeque<>(stack);
            this.stacks.add(newStack);
        }
        this.cost = other.getK();
    }

    /**
     * Método recebe uma string que representa os contentores(respetivos caracteres e custos)
     * coloca os respetivos valores primitivos em cada instância de contentor e de seguida
     * guarda na estrutura de dados List<Deque<Container>>
     *
     * @param config    A string de entrada que contém a configuração das pilhas de contentores.
     * @param isInitial Indica se a configuração é inicial (true) ou final (false).
     * @throws PlatformException Se algum contentor na configuração inicial não tiver um custo associado.
     *
     * @inv Cada Contentor tem de ter um custo associado caso seja configuração inicial.
     *      Se não houver um custo associado a um contentor em uma configuração inicial, deve lançar PlatformException.
     */
    private void parseInput(String config, Boolean isInitial) throws PlatformException, ContainersException {
        String[] stacksConfig = config.split(" ");
        for (String stackStr : stacksConfig) {
            Deque<Container> stack = new ArrayDeque<>();
            int length = stackStr.length();
            for (int i = 0; i < length; i++) {
                char containerId = stackStr.charAt(i);
                int movecost = 0;
                if (isInitial) {
                    if (i + 1 < length && Character.isDigit(stackStr.charAt(i + 1))) {
                        movecost = Character.getNumericValue(stackStr.charAt(++i));
                    } else {
                        throw new PlatformException("Cada contentor na configuração inicial deve ter um custo associado.");
                    }
                }
                stack.addLast(new Container(containerId, movecost));
            }
            stacks.add(stack);
        }
        getSortedStacks();
    }

    /**
     * Getter para as stacks e setter para o custo de movimento de um contentor
     * @return stacks
     */
    public List<Deque<Container>> getStacks() {
        return stacks;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Métodos que definem um código único para todas as instâncias criadas
     * @return uma chave única para a instância criada
     */
    @Override
    public int hashCode() {
        int result = 1;
        for (Deque<Container> stack : stacks) {
            result = 31 * result + dequeHashCode(stack);
        }
        return result;
    }

    /**
     * Gera um código hash para uma Deque de contêineres.
     * @param deque a Deque de contêineres para a qual o código hash será calculado.
     * @return um valor de hash único baseado no conteúdo da Deque.
     */
    private int dequeHashCode(Deque<Container> deque) {
        int h = 1;
        for (Container c : deque) {
            h = 31 * h + (c == null ? 0 : c.hashCode());
        }
        return h;
    }
    /**
     * Verifica se as configurações são iguais
     * @param o representa uma configuração que será testada com o this (instância receptora)
     * @return true caso sejam iguais e false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;
        Platform other = (Platform) o;
        if (this.stacks.size() != other.stacks.size()) {
            return false;
        }
        for (int i = 0; i < this.stacks.size(); i++) {
            Deque<Container> thisStack = this.stacks.get(i);
            Deque<Container> otherStack = other.stacks.get(i);
            if (!dequesAreEqual(thisStack, otherStack)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se duas Deques de contentores são iguais.
     * A igualdade é determinada comparando os elementos em cada posição das duas filas.
     *
     * @param d1 a primeira Deque de contentores a ser comparada.
     * @param d2 a segunda Deque de contentores a ser comparada.
     * @return true se as duas Deques tiverem o mesmo tamanho, false caso contrário.
     */
    private boolean dequesAreEqual(Deque<Container> d1, Deque<Container> d2) {
        if (d1.size() != d2.size()) {
            return false;
        }
        Iterator<Container> it1 = d1.iterator();
        Iterator<Container> it2 = d2.iterator();
        while (it1.hasNext()) {
            Container c1 = it1.next();
            Container c2 = it2.next();
            if (!c1.equals(c2)) {
                return false;
            }
        }
        return true;
    }
    /**
     * @return um clone de uma determinada instância Platform com o auxílio do construtor de cópia
     */
    @Override
    public Platform clone() {
        return new Platform(this);
    }

    /**
     * @return representação textual da estrutura de uma instância Platform
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Deque<Container> stack : stacks) {
            sb.append("[");
            int count = 0;
            int size = stack.size();
            for (Container container : stack) {
                sb.append(container.toString());
                if (++count < size) {
                    sb.append(", ");
                }
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    /**
     * @return as configurações feitas através de uma configuração pai
     */
    @Override
    public List<Ilayout> children() {
        List<Ilayout> littleOnes = new ArrayList<>();
        int numStacks = stacks.size();
        for (int i = 0; i < numStacks; i++) {
            Deque<Container> fromStack = stacks.get(i);
            if (!fromStack.isEmpty()) {
                for (int j = 0; j < numStacks; j++) {
                    if (i != j) {
                        Platform newState = new Platform(this);  // Clone current state
                        Deque<Container> newFromStack = newState.stacks.get(i);
                        Deque<Container> newToStack = newState.stacks.get(j);

                        Container movedContainer = newFromStack.removeLast();
                        newToStack.addLast(movedContainer);

                        if (newFromStack.isEmpty()) {
                            newState.stacks.remove(i);
                            if (i < j) {
                                // Ajustar índice após remoção
                                // Nenhuma ação necessária aqui porque estamos trabalhando com uma cópia
                            }
                        }

                        newState.setCost(movedContainer.getCost());
                        littleOnes.add(newState);
                    }
                }

                Platform newState = new Platform(this);
                Deque<Container> newFromStack = newState.stacks.get(i);

                Container movedContainer = newFromStack.removeLast();

                Deque<Container> newStack = new ArrayDeque<>();
                newStack.addLast(movedContainer);
                newState.stacks.add(newStack);

                if (newFromStack.isEmpty()) {
                    newState.stacks.remove(i);
                }

                newState.setCost(movedContainer.getCost());
                newState.getSortedStacks();
                littleOnes.add(newState);
            }
        }
        return littleOnes;
    }

    /**
     * Verifica se a configuração l é a configuração objetivo
     * @param l representa a configuração objetivo que queremos testar
     * @return true se forem iguais e false caso contrário
     */
    @Override
    public boolean isGoal(Ilayout l) {
        return this.equals(l);
    }

    /**
     * Retorna o custo de movimento de um certo contentor
     */
    @Override
    public double getK() {
        return this.cost;
    }
    /**
     *
     * Este método calcula o valor da heurística (h(n)) para a configuração atual em relação à configuração
     * objetivo, representada por `goalLayout`. A heurística serve para estimar o custo mínimo restante
     * para alcançar a solução final no algoritmo A*.
     *
     * @param goalLayout Representa a configuração final desejada.
     * @return O valor estimado da heurística, que reflete o custo estimado necessário para transformar a
     *         configuração atual na configuração objetivo.
     */
    public double computeHeuristic(Ilayout goalLayout) {
        Platform goalPlatform = (Platform) goalLayout;

        if (goalPositions == null) {
            cacheGoalPositions(goalPlatform);
        }

        double h = 0;

        for (int i = 0; i < this.stacks.size(); i++) {
            Deque<Container> stack = this.stacks.get(i);
            char currentBase = stack.isEmpty() ? '\0' : stack.peekFirst().getId();

            int j = 0;
            for (Container container : stack) {
                Position goalPosition = goalPositions.get(container.getId());
                if (goalPosition == null) {
                    j++;
                    continue;
                }
//                if (!(j == 0 && goalPosition.height == 0) && !(j > 0 && goalPosition.height == j)) {
//                    h += container.getCost();
//                }

                if (goalPosition.base != currentBase) {
                    h += container.getCost();
                } else if (goalPosition.height != j) {
                    h += 2 * container.getCost();
                }
                j++;
            }
        }
        return h;
    }


    /**
     * Este método armazena em cache as posições objetivo (base e altura) de cada contentor na
     * configuração final (goal). Esse cache é usado para otimizar o cálculo da heurística,
     * permitindo que o algoritmo A* acesse rapidamente a posição final esperada de cada contentor.
     *
     * @param goal A configuração final (Platform) que contém as posições desejadas dos contentores.
     */
    private void cacheGoalPositions(Platform goal) {
        goalPositions = new HashMap<>();
        for (Deque<Container> stack : goal.stacks) {
            char base = stack.isEmpty() ? '\0' : stack.peekFirst().getId();
            int height = 0;
            for (Container container : stack) {
                goalPositions.put(container.getId(), new Position(base, height));
                height++;
            }
        }
    }

    /**
     * Ordena as pilhas segundo o primeiro character do primeiro contentor
     */
    private void getSortedStacks() {
        stacks.sort(Platform.compareChars);
    }
}