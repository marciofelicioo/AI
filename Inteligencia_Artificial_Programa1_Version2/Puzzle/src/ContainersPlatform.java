    import java.util.*;
    import java.util.stream.Collectors;

    public class ContainersPlatform implements Ilayout, Cloneable {

        /**
         * Estrutura da classe ContainerOrganizer
         */
        private List<Stack<Container>> stacks;
        private double cost;


        /**
         * Construtor por omissão
         */
        public ContainersPlatform() {
            stacks = null;
            this.cost = 0;
        }

        /**
         * Construtor de inicialização
         */
        public ContainersPlatform(String config) {
            stacks = new ArrayList<>();
            InputConfiguration(config);
            this.cost = 0;
        }

        private void InputConfiguration(String config) {
            String[] stacksConfig = config.split(" ");
            for (String stackStr : stacksConfig) {
                Stack<Container> stack = new Stack<>();
                for (int i = 0; i < stackStr.length(); i++) {
                    char containerId = stackStr.charAt(i);
                    int movecost = 0;
                    if(i + 1 < stackStr.length() && Character.isDigit(stackStr.charAt(i + 1)))
                    {
                        movecost = Character.getNumericValue(stackStr.charAt(i + 1));
                        i++;
                    }
                    stack.push(new Container(containerId, movecost));
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
                stacks.add((Stack<Container>)s.clone());
            }
        }

        public void setCost(double cost)
        {
            this.cost = cost;
        }


        @Override
        public int hashCode() {
            return Arrays.hashCode(this.stacks.stream().sorted((s1,s2) -> String.valueOf(
                    s1.firstElement().getId()).compareTo(String.valueOf(s2.firstElement().getId()))).toArray());
        }


        @Override
        @SuppressWarnings("unchecked")
        public ContainersPlatform clone() {
            List<Stack<Container>> stacksLocal = stacks;
            ContainersPlatform copy = new ContainersPlatform();
            copy.setStacks(new ArrayList<>(stacksLocal.size()));
            for (Stack<Container> stack : stacksLocal) {
                copy.stacks.add((Stack<Container>) stack.clone());
            }
            copy.setCost(getK());
            return copy;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            List<Stack<Container>> sortedStacks = this.stacks.stream().sorted((s1,s2) -> String.valueOf(
                    s1.firstElement().getId()).compareTo(String.valueOf(s2.firstElement().getId()))).toList();;

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
            List<Stack<Container>> stacksLocal = stacks;
            for (int i = 0; i < stacksLocal.size(); i++) {
                if (!stacks.get(i).isEmpty()) {
                    ContainersPlatform newConfiguration;

                    newConfiguration = this.clone();
                    newConfiguration.ToGround(i);
                    newConfiguration.stacks.removeIf(Stack::isEmpty);
                    if (!newConfiguration.equals(this)) {
                        children.add(newConfiguration);
                    }

                    for (int j = 0; j < stacksLocal.size(); j++) {
                        if (i != j) {
                            newConfiguration = this.clone();
                            newConfiguration.ToStack(i,j);
                            newConfiguration.stacks.removeIf(Stack::isEmpty);;
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

            ContainersPlatform other = (ContainersPlatform) o;

            List<Stack<Container>> sortedThisStacks = this.stacks.stream().sorted((s1,s2) -> String.valueOf(
                    s1.firstElement().getId()).compareTo(String.valueOf(s2.firstElement().getId()))).toList();

            List<Stack<Container>> sortedOtherStacks = other.stacks.stream().sorted((s1,s2) -> String.valueOf(
                    s1.firstElement().getId()).compareTo(String.valueOf(s2.firstElement().getId()))).toList();

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

        private void ToGround(int fromStack) {
            List<Stack<Container>> stacksLocal = stacks;
            Stack<Container> newStack = new Stack<>();
            newStack.push(stacksLocal.get(fromStack).pop());
            stacksLocal.add(newStack);
    
            this.setCost(stacksLocal.get(fromStack).pop().getcost());
        }

        private void ToStack(int fromStack, int toStack) {
            List<Stack<Container>> stacksLocal = stacks;
            stacksLocal.get(toStack).push(stacksLocal.get(fromStack).pop());
            this.setCost(stacksLocal.get(fromStack).pop().getcost());
        }
    }