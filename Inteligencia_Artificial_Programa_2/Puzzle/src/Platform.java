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
    private Map<Character, int[]> cachedGoalPositions;
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

        getSortedStacks();

        for (Stack<Container> stack : stacks) {
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

        this.getSortedStacks();
        other.getSortedStacks();

        if (this.stacks.size() != other.stacks.size()) return false;

        for (int i = 0; i < this.stacks.size(); i++) {
            Stack<Container> thisStack = this.stacks.get(i);
            Stack<Container> otherStack = other.stacks.get(i);

            if(thisStack.size() != otherStack.size()) return false;

            for (int j = 0; j < thisStack.size(); j++) {
                Container thisContainer = thisStack.get(j);
                Container goalContainer = otherStack.get(j);

                if (!thisContainer.equals(goalContainer)) {
                    return false;
                }
            }
        }
        return true;
    }




    /**
     * Ordena as stacks segundo o id dos contêiners, ignorando as stacks vazias.
     * @return lista de configurações ordenadas
     */
    private void getSortedStacks() {
        getStacks().sort(Comparator.comparing(stack -> stack.firstElement().getId()));  // Ordena diretamente
    }




    public double computeHeuristic(Ilayout goalLayout) {
        Platform current = (Platform) this;
        Platform goal = (Platform) goalLayout;

        double totalEstimatedCost = 0;

        if (cachedGoalPositions == null) {
            cachedGoalPositions = new HashMap<>();
            for (int i = 0; i < goal.stacks.size(); i++) {
                Stack<Container> goalStack = goal.stacks.get(i);
                for (int j = 0; j < goalStack.size(); j++) {
                    cachedGoalPositions.put(goalStack.get(j).getId(), new int[]{i, j});
                }
            }
        }

        for (int i = 0; i < current.stacks.size(); i++) {
            Stack<Container> currentStack = current.stacks.get(i);
            for (int j = 0; j < currentStack.size(); j++) {
                Container currentContainer = currentStack.get(j);
                int[] goalPosition = cachedGoalPositions.get(currentContainer.getId());

                // Ignora se já está na pilha e posição corretas
                if (goalPosition != null && !(j == 0 && goalPosition[1] == 0) && !(j > 0 && goalPosition[1] == j)) {
                    totalEstimatedCost += currentContainer.getCost();
                }
            }
        }

        return totalEstimatedCost;
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
