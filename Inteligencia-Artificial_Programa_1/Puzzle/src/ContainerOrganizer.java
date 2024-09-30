import java.util.*;

public class ContainerOrganizer implements Ilayout, Cloneable {
    /**
     * Estrutura da classe ContainerOrganizer
     */
    private List<Stack<Container>> stacks;
    private int cost;

    /**
     * Classe container interna para armazenar o identificador e o custo de movimentação
     */
    private class Container {
        /**
         * Estrutura da classe interna Container
         */
        private char id;
        private int containerCost;

        /**
         * Construtor de inicialização da classe Container
         */
        public Container(char id, int cost) {
            this.id = id;
            this.containerCost = cost;
        }

        /**
         * Construtor de cópia para a classe interna Container
         */
        public Container(Container c) {
            this.id = c.getId();
            this.containerCost = c.getcost();
        }

        public int getcost() {
            return this.containerCost;
        }

        public char getId() {
            return this.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, containerCost);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Container other = (Container) obj;
            return this.id == other.id && this.containerCost == other.containerCost;
        }


        /**
         * Retorna a representação textual da classe interna container
         */
        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }

    /**
     * Construtor por omissão
     */
    public ContainerOrganizer() {
        this.stacks = null;
        this.cost = 0;
    }

    /**
     * Construtor de inicialização
     */
    public ContainerOrganizer(String config, boolean isInitialState) {
        this.stacks = new ArrayList<>();
        parseInput(config, isInitialState);
        this.cost = 0;
    }

    /**
     * Construtor de cópia
     */
    public ContainerOrganizer(ContainerOrganizer other) {
        this.stacks = new ArrayList<>();

        for (Stack<Container> stack : other.stacks) {
            Stack<Container> newStack = new Stack<>();

            for (Container container : stack) {
                newStack.push(new Container(container));
            }

            this.stacks.add(newStack);
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
            this.stacks.add(stack);
        }
    }


    public static final Comparator<Stack<Container>> compareChars = (s1,s2) -> String.valueOf(
            s1.firstElement().getId()).compareTo(String.valueOf(s2.firstElement().getId()));

    private List<Stack<Container>> getSortedStacks() {
        List<Stack<Container>> sortedStacks = new ArrayList<>(this.stacks);
        sortedStacks.sort(ContainerOrganizer.compareChars);
        return sortedStacks;
    }


    private void removeEmptyStacks() {
        stacks.removeIf(Stack::isEmpty);
    }


    private void moveToGround(int fromStack) {
        if (fromStack >= stacks.size()) return;

        if (stacks.get(fromStack).isEmpty()) return;

        Container container = stacks.get(fromStack).pop();

        if (stacks.get(fromStack).isEmpty()) {
            stacks.remove(fromStack);
        }

        Stack<Container> newStack = new Stack<>();
        newStack.push(container);
        stacks.add(newStack);

        cost = container.getcost();

    }


    private void moveToStack(int fromStack, int toStack) {
        if (fromStack >= stacks.size() || toStack >= stacks.size()) {
            return;
        }

        if (stacks.get(fromStack).isEmpty()) return;

        Container container = stacks.get(fromStack).pop();
        stacks.get(toStack).push(container);

        cost = container.getcost();

    }

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();

        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                ContainerOrganizer newOrganizer;

                newOrganizer = this.clone();
                newOrganizer.moveToGround(i);
                newOrganizer.removeEmptyStacks();
                if (!newOrganizer.equals(this)) {
                    children.add(newOrganizer);
                }

                for (int j = 0; j < stacks.size(); j++) {
                    if (i != j) {
                        newOrganizer = this.clone();
                        newOrganizer.moveToStack(i, j);
                        newOrganizer.removeEmptyStacks();
                        if (!newOrganizer.equals(this)) {
                            children.add(newOrganizer);
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

        ContainerOrganizer other = (ContainerOrganizer) o;

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

                if (thisContainer.getId() != goalContainer.getId()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getSortedStacks().toArray());
    }



    @Override
    public ContainerOrganizer clone() {
        ContainerOrganizer copy = new ContainerOrganizer();
        copy.stacks = new ArrayList<>(this.stacks.size());
        for (Stack<Container> stack : this.stacks) {
            copy.stacks.add((Stack<Container>) stack.clone());
        }
        copy.cost = this.cost;
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        List<Stack<Container>> sortedStacks = getSortedStacks();

        for (Stack<Container> stack : sortedStacks) {
            sb.append("[");
            for (int i = 0; i < stack.size(); i++) {
                sb.append(stack.get(i).id);
                if (i < stack.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}