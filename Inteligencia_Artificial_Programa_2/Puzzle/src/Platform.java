import java.util.*;

/**
 * Classe Platform: Esta classe contém construtores que criam objetos ContainerConfiguration
 * e o seu principal objetivo é criar sucessores de uma certa configurção para poder auxiliar
 * o método solve da classe BestFirst (Best First Search)
 * @author Márcio Felício
 * @version 1.0
 * @inv Cada Contêiner tem de ter um custo associado caso seja configuração inicial
 */
public class Platform implements Ilayout, Cloneable {
    /**
     * Estrutura estática da classe ContainerOrganizer
     */
    public static final Comparator<Stack<Container>> compareChars = (s1,s2) -> String.valueOf(
            s1.firstElement().getId()).compareTo(String.valueOf(s2.firstElement().getId()));

    /**
     * Estrutura da classe ContainerOrganizer
     */
    private List<Stack<Container>> stacks;
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
     */
    public Platform(String config, boolean isInitialState) {
        stacks = new ArrayList<>();
        parseInput(config, isInitialState);
        this.cost = 0;
    }

    /**
     * Construtor de cópia para casos de encapsulamento de dados
     */
    public Platform(Platform other) {
        this.stacks = new ArrayList<>(other.stacks.size());

        for (Stack<Container> stack : other.stacks) {
            Stack<Container> newStack = new Stack<>();
            for (Container container : stack) {
                newStack.push(new Container(container));
            }
            this.stacks.add(newStack);
        }

        this.cost = other.getK();
    }

    private void parseInput(String config, boolean isInitialState) {
        String[] stacksConfig = config.split(" ");
        for (String stackStr : stacksConfig) {
            Stack<Container> stack = new Stack<>();
            for (int i = 0; i < stackStr.length(); i++) {
                char containerId = stackStr.charAt(i);

                int movecost = (isInitialState && i + 1 < stackStr.length() && Character.isDigit(stackStr.charAt(i + 1)))
                        ? Character.getNumericValue(stackStr.charAt(i + 1))
                        : 0;

                stack.push(new Container(containerId, movecost));

                if (movecost > 0) {
                    i++;
                }
            }
            stacks.add(stack);
        }
    }

    /**
     * getter para as stacks e setter para o custo de movimento de um contentor
     * @return stacks
     */
    public List<Stack<Container>> getStacks()
    {
        return stacks;
    }


    public void setCost(double cost)
    {
        this.cost = cost;
    }

    /**
     * @return uma chave única para a instância criada
     */
    @Override
    public int hashCode() {
        int hash = 7;

        for (Stack<Container> stack : stacks) {
            hash = 31 * hash + (stack.isEmpty() ? 0 : stack.hashCode());
        }

        return hash;
    }


    /**
     * @return um clone de uma determinada instância com o auxilio do construtor de cópia
     */
    @Override
    public Platform clone() {
        return new Platform(this);
    }

    /**
     * @return representação textual da estrutura da instância
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        List<Stack<Container>> sortedStacks = getSortedStacks();

        for (Stack<Container> stack : sortedStacks) {
            sb.append("[");
            for (int i = 0; i < stack.size(); i++) {
                sb.append(stack.get(i).toString());
                if (i < stack.size() - 1) {
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
        List<Ilayout> children = new ArrayList<>();

        if (!this.stacks.isEmpty()) {
            for (int i = 0; i < this.stacks.size(); i++) {
                Stack<Container> sourceStack = this.stacks.get(i);

                for (int j = 0; j < this.stacks.size(); j++) {
                    if (i == j) continue;

                    Platform child = this.clone();

                    child.moveToStack(i,j);

                    if (child.stacks.get(i).isEmpty()) {
                        child.stacks.remove(i);
                    }

                    children.add(child);
                }

                if (sourceStack.size() >= 2) {
                    Platform child = this.clone();

                    child.moveToGround(i);

                    children.add(child);
                }
            }
        }
        return children;
    }

    /**
     * verifica se a configuração l é a configuração objetivo
     * @param l representa a configuração objetivo que queremos testar
     * @return true se forem iguais e false caso contrário
     */
    @Override
    public boolean isGoal(Ilayout l) {
        return this.equals(l);
    }

    /**
     * retorna o custo de movimento de um certo contentor
     */
    @Override
    public double getK() {
        return this.cost;
    }

    /**
     * Verifica se as configurações são iguais
     * @param o representa uma configuração que será testada com o this (instância receptora)
     * @return true caso sejam iguais e false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Platform other = (Platform) o;

        return this.toString().equals(other.toString());
    }


    /**
     * Ordena as stacks segundo o id dos contêiners, ignorando as stacks vazias.
     * @return lista de configurações ordenadas
     */
    private List<Stack<Container>> getSortedStacks() {
        List<Stack<Container>> sortedStacks = new ArrayList<>(getStacks());


        sortedStacks.removeIf(Stack::isEmpty);

        sortedStacks.sort((s1, s2) -> {
                if (!s1.isEmpty() && !s2.isEmpty()) {
                return String.valueOf(s1.firstElement().getId())
                        .compareTo(String.valueOf(s2.firstElement().getId()));
            }
            return 0;
        });

        return sortedStacks;
    }


    public double computeHeuristic(Ilayout goalLayout) {
        Platform current = (Platform) this;
        Platform goal = (Platform) goalLayout;

        double totalEstimatedCost = 0;
        List<Stack<Container>> currentStacks = current.getStacks();
        List<Stack<Container>> goalStacks = goal.getStacks();

        // Mapa para armazenar a posição alvo de cada contentor no estado objetivo
        Map<Character, int[]> goalPositions = new HashMap<>();

        // Preencher o mapa com a posição objetivo de cada contentor no estado objetivo
        for (int i = 0; i < goalStacks.size(); i++) {
            Stack<Container> goalStack = goalStacks.get(i);
            for (int j = 0; j < goalStack.size(); j++) {
                Container goalContainer = goalStack.get(j);
                goalPositions.put(goalContainer.getId(), new int[]{i, j});  // Posição [pilha, índice na pilha]
            }
        }

        // Percorrer a configuração atual e verificar se o contentor está na pilha correta e na posição correta
        for (int i = 0; i < currentStacks.size(); i++) {
            Stack<Container> currentStack = currentStacks.get(i);
            for (int j = 0; j < currentStack.size(); j++) {
                Container currentContainer = currentStack.get(j);

                // Obter a posição objetivo do contentor
                int[] goalPosition = goalPositions.get(currentContainer.getId());

                if (goalPosition == null) {
                    continue;  // O contentor não existe no estado objetivo
                }

                int goalStackIndex = goalPosition[0];
                int goalPositionIndex = goalPosition[1];

                // Verifica se o contentor está na pilha errada
                if (goalStackIndex != i) {
                    // Se o contentor deveria estar no chão e está no chão, ele está correto
                    if (goalPositionIndex == 0 && j == 0) {
                        continue;  // Se o contentor está na posição correta (chão), não adicionar custo
                    }
                    // Caso contrário, ele está fora do lugar
                    totalEstimatedCost += 1;
                }
                // Verifica se o contentor está na pilha correta, mas fora da posição final correta
                else if (goalPositionIndex != j) {
                    totalEstimatedCost += 1;
                }
            }
        }

        return totalEstimatedCost;  // Retorna o custo estimado
    }





    /**
     * move o container de uma stack e coloca noutra stack completamente nova
     * @param fromStack representa a stack de onde será removido o container
     */
    private void moveToGround(int fromStack) {
        List<Stack<Container>> stacksLocal = getStacks();
        if (fromStack >= stacksLocal.size() || stacksLocal.get(fromStack).isEmpty()) return;

        Container container = stacksLocal.get(fromStack).pop();

        Stack<Container> newStack = new Stack<>();
        newStack.push(container);
        stacksLocal.add(newStack);

        this.setCost(container.getCost());
    }

    /**
     *
     * @param fromStack representa a stack de onde será removido o contêiner
     * @param toStack representa a stack para onde será enviado o contêiner
     */
    private void moveToStack(int fromStack, int toStack) {
        List<Stack<Container>> stacksLocal = getStacks();
        if (fromStack >= stacksLocal.size() || toStack >= stacksLocal.size() || stacksLocal.get(fromStack).isEmpty()) {
            return;
        }

        Container container = stacksLocal.get(fromStack).pop();
        stacksLocal.get(toStack).push(container);

        this.setCost(container.getCost());
    }
}
