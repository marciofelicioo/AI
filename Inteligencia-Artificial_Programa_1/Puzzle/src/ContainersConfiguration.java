import java.util.*;

public class ContainersConfiguration implements Ilayout, Cloneable {
    /**
     * Estrutura estática da classe ContainerOrganizer
     */
    public static final Comparator<Deque<Container>> compareChars = (s1, s2) ->
            Character.compare(s1.peek().getId(), s2.peek().getId());

    /**
     * Estrutura da classe ContainerOrganizer
     */
    private List<Deque<Container>> stacks;
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

        for (Deque<Container> stack : other.stacks) {
            Deque<Container> newStack = new ArrayDeque<>();

            for (Container container : stack) {
                newStack.push(new Container(container));
            }

            stacks.add(newStack);
        }
    }
    private void parseInput(String config, boolean isInitialState) {
        String[] stacksConfig = config.split(" ");
        for (String stackStr : stacksConfig) {
            Deque<Container> stack = new ArrayDeque<>();
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


    @SuppressWarnings("unchecked")
    public void setStacks(List<Stack<Container>> ss)
    {
        stacks = new ArrayList<>();
        for(Stack<Container> s: ss)
        {
            stacks.add((Deque<Container>)s.clone());
        }
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    private void removeEmptyStacks() {
        stacks.removeIf(Deque::isEmpty);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getSortedStacks().toArray());
    }


    @Override
    @SuppressWarnings("unchecked")
    public ContainersConfiguration clone() {
        ContainersConfiguration copy = new ContainersConfiguration();
        copy.setStacks(new ArrayList<>(stacks.size()));
        for (Deque<Container> stack : stacks) {
            copy.stacks.add(new ArrayDeque<>(stack));
        }
        copy.setCost(getK());
        return copy;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Deque<Container>> sortedStacks = getSortedStacks();
        for (Deque<Container> stack : sortedStacks) {
            sb.append("[");
            for (Iterator<Container> it = stack.iterator(); it.hasNext(); ) {
                sb.append(it.next());
                if (it.hasNext()) {
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

        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                ContainersConfiguration newConfiguration;

                newConfiguration = this.clone();
                newConfiguration.moveToGround(i);
                newConfiguration.removeEmptyStacks();
                if (!newConfiguration.equals(this)) {
                    children.add(newConfiguration);
                }

                for (int j = 0; j < stacks.size(); j++) {
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

        List<Deque<Container>> sortedThisStacks = this.getSortedStacks();
        List<Deque<Container>> sortedOtherStacks = other.getSortedStacks();

        if (sortedThisStacks.size() != sortedOtherStacks.size()) return false;

        for (int i = 0; i < sortedThisStacks.size(); i++) {
            Deque<Container> thisStack = sortedThisStacks.get(i);
            Deque<Container> otherStack = sortedOtherStacks.get(i);

            if (thisStack.size() != otherStack.size()) return false;

            Iterator<Container> thisIterator = thisStack.iterator();
            Iterator<Container> otherIterator = otherStack.iterator();

            while (thisIterator.hasNext() && otherIterator.hasNext()) {
                Container thisContainer = thisIterator.next();
                Container goalContainer = otherIterator.next();

                if (!thisContainer.equals(goalContainer)) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<Deque<Container>> getSortedStacks() {
        List<Deque<Container>> sortedStacks = new ArrayList<>(stacks);
        sortedStacks.sort(ContainersConfiguration.compareChars);
        return sortedStacks;
    }

    private void moveToGround(int fromStack) {
        if (fromStack >= stacks.size()) return;

        if (stacks.get(fromStack).isEmpty()) return;

        Container container = stacks.get(fromStack).pop();

        Deque<Container> newStack = new ArrayDeque<>();
        newStack.push(container);
        stacks.add(newStack);

        this.setCost(container.getcost());
    }

    private void moveToStack(int fromStack, int toStack) {
        if (fromStack >= stacks.size() || toStack >= stacks.size()) {
            return;
        }

        if (stacks.get(fromStack).isEmpty()) return;

        Container container = stacks.get(fromStack).pop();
        stacks.get(toStack).push(container);

        this.setCost(container.getcost());
    }
}