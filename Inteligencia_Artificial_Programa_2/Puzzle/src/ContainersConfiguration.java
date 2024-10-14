import java.util.*;
/**
 * Classe ContainersConfiguration: Esta classe contém construtores que criam objetos ContainerConfiguration
 * e o seu principal objetivo é criar sucessores de uma certa configurção para poder auxiliar
 * o método solve da classe BestFirst (Best First Search)
 * @author Márcio Felício
 * @version 1.0
 * @inv Cada Contêiner tem de ter um custo associado caso seja configuração inicial
 */
public class ContainersConfiguration implements Ilayout, Cloneable {
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
    public ContainersConfiguration() {
        stacks = null;
        this.cost = 0;
    }

    /**
     * Construtor de inicialização
     */
    public ContainersConfiguration(String config, boolean isInitialState) {
        stacks = new ArrayList<>();
        parseInput(config, isInitialState);
        this.cost = 0;
    }

    /**
     * Construtor de cópia para casos de encapsulamento de dados
     */
    public ContainersConfiguration(ContainersConfiguration other) {
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
     * método que remove da lista de stacks stacks que estejam vazias através da operação
     * removeIf da java Collection FrameWork com a respetiva interface funcional
     * Predicate associada
     */
    private void removeEmptyStacks() {
        stacks.removeIf(Stack::isEmpty);
    }

    /**
     * @return uma chave única para a instância criada
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getSortedStacks().toArray());
    }

    /**
     * @return um clone de uma determinada instância com o auxilio do construtor de cópia
     */
    @Override
    public ContainersConfiguration clone() {
        return new ContainersConfiguration(this);
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
        List<Stack<Container>> stacksLocal = getStacks();
        for (int i = 0; i < stacksLocal.size(); i++) {
            if (!stacksLocal.get(i).isEmpty()) {
                ContainersConfiguration newConfiguration = new ContainersConfiguration(this);
                newConfiguration.moveToGround(i);
                newConfiguration.removeEmptyStacks();
                if (!newConfiguration.equals(this)) {
                    children.add(newConfiguration);
                }

                for (int j = 0; j < stacksLocal.size(); j++) {
                    if (i != j) {
                        newConfiguration = new ContainersConfiguration(this);
                        newConfiguration.moveToStack(i, j);
                        newConfiguration.removeEmptyStacks();
                        if (!newConfiguration.equals(this)) {
                            children.add(newConfiguration);
                        }
                    }
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
        return l.equals(this);
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

        ContainersConfiguration other = (ContainersConfiguration) o;

        List<Stack<Container>> sortedThisStacks = this.getSortedStacks();
        List<Stack<Container>> sortedOtherStacks = other.getSortedStacks();

        if (sortedThisStacks.size() != sortedOtherStacks.size()) return false;

        for (int i = 0; i < sortedThisStacks.size(); i++) {
            Stack<Container> thisStack = sortedThisStacks.get(i);
            Stack<Container> otherStack = sortedOtherStacks.get(i);

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
     * ordena as stacks segundo o id dos contêiners
     * @return lista de configurações ordenadas
     */
    private List<Stack<Container>> getSortedStacks() {
        List<Stack<Container>> sortedStacks = new ArrayList<>(getStacks());
        sortedStacks.sort(ContainersConfiguration.compareChars);
        return sortedStacks;
    }
    /**
     * move o container de uma stack e coloca noutra stack completamente nova
     * @param fromStack representa a stack de onde será removido o container
     */
    private void moveToGround(int fromStack) {
        List<Stack<Container>> stacksLocal = getStacks();
        if (fromStack >= stacksLocal.size()) return;


        Container container = stacksLocal.get(fromStack).pop();

        Stack<Container> newStack = new Stack<>();
        newStack.push(container);
        stacksLocal.add(newStack);

        this.setCost(container.getcost());
    }

    /**
     *
     * @param fromStack representa a stack de onde será removido o contêiner
     * @param toStack representa a stack para onde será enviado o contêiner
     */
    private void moveToStack(int fromStack, int toStack) {
        List<Stack<Container>> stacksLocal = getStacks();
        if (fromStack >= stacksLocal.size() || toStack >= stacksLocal.size()) {
            return;
        }

        Container container = stacksLocal.get(fromStack).pop();
        stacksLocal.get(toStack).push(container);

        this.setCost(container.getcost());
    }
}