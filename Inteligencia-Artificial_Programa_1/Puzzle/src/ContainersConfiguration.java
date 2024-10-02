import java.util.*;

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
     * Construtor de cópia
     */
    public ContainersConfiguration(ContainersConfiguration other) {
        stacks = new ArrayList<>();

        for (Stack<Container> stack : other.stacks) {
            Stack<Container> newStack = new Stack<>();

            for (Container container : stack) {
                newStack.push(new Container(container));
            }

            stacks.add(newStack);
        }
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
    public List<Stack<Container>> getStacks()
    {
        return stacks;
    }

    @SuppressWarnings("unchecked")
    public void setStacks(List<Stack<Container>> ss)
    {
        stacks = new ArrayList<>();
        for(Stack<Container> s: ss)
        {
            stacks.add((Stack<Container>)s.clone());
        }
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    private void removeEmptyStacks() {
        stacks.removeIf(Stack::isEmpty);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getSortedStacks().toArray());
    }


    @Override
    @SuppressWarnings("unchecked")
    public ContainersConfiguration clone() {
        List<Stack<Container>> stacksLocal = getStacks();
        ContainersConfiguration copy = new ContainersConfiguration();
        copy.setStacks(new ArrayList<>(stacksLocal.size()));
        for (Stack<Container> stack : stacksLocal) {
            copy.getStacks().add((Stack<Container>) stack.clone());
        }
        copy.setCost(getK());
        return copy;
    }

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

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();
        List<Stack<Container>> stacksLocal = getStacks();
        for (int i = 0; i < stacksLocal.size(); i++) {
            if (!getStacks().get(i).isEmpty()) {
                ContainersConfiguration newConfiguration;

                newConfiguration = this.clone();
                newConfiguration.moveToGround(i);
                newConfiguration.removeEmptyStacks();
                if (!newConfiguration.equals(this)) {
                    children.add(newConfiguration);
                }

                for (int j = 0; j < stacksLocal.size(); j++) {
                    if (i != j) {
                        newConfiguration = this.clone();
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

    @Override
    public boolean isGoal(Ilayout l) {
        return l.equals(this);
    }

    @Override
    public double getK() {
        return this.cost;
    }

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

    private List<Stack<Container>> getSortedStacks() {
        List<Stack<Container>> sortedStacks = new ArrayList<>(getStacks());
        sortedStacks.sort(ContainersConfiguration.compareChars);
        return sortedStacks;
    }

    private void moveToGround(int fromStack) {
        List<Stack<Container>> stacksLocal = getStacks();
        if (fromStack >= stacksLocal.size()) return;


        Container container = stacksLocal.get(fromStack).pop();

        Stack<Container> newStack = new Stack<>();
        newStack.push(container);
        stacksLocal.add(newStack);

        this.setCost(container.getcost());
    }

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