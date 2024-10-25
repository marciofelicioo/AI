import java.util.*;
import java.util.stream.Collectors;

public class ContainersPlatform implements Ilayout, Cloneable {

    private List<Stack<Contentor>> stacks;
    private double cost;

    private class Contentor {

        private char id;
        private int ContentorCost;

        public Contentor(char id, int cost) {
            this.id = id;
            this.ContentorCost = cost;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }
    public ContainersPlatform() {
        stacks = null;
        this.cost = 0;
    }

    public ContainersPlatform(String config) {
        stacks = new ArrayList<>();
        InputConfiguration(config);
        this.cost = 0;
    }

    private void InputConfiguration(String config) {
        String[] stacksConfig = config.split(" ");
        for (String stackStr : stacksConfig) {
            Stack<Contentor> stack = new Stack<>();
            for (int i = 0; i < stackStr.length(); i++) {
                char ContentorId = stackStr.charAt(i);
                int movementCost = 0;
                if(i + 1 < stackStr.length() && Character.isDigit(stackStr.charAt(i + 1)))
                {
                    movementCost = Character.getNumericValue(stackStr.charAt(i + 1));
                    i++;
                }
                stack.push(new Contentor(ContentorId,movementCost));
            }
            stacks.add(stack);
        }
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(this.stacks.stream().sorted((s1,s2) -> String.valueOf(
                s1.firstElement().id).compareTo(String.valueOf(s2.firstElement().id))).toArray());
    }

    @Override
    @SuppressWarnings("unchecked")
    public ContainersPlatform clone() {
        ContainersPlatform copy = new   ContainersPlatform();
        copy.stacks = new ArrayList<>(stacks.size());
        for (Stack<Contentor> stack : stacks) {
            copy.stacks.add((Stack<Contentor>) stack.clone());
        }
        copy.cost = getK();
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        List<Stack<Contentor>> sortedStacks = this.stacks.stream().sorted((s1,s2) -> String.valueOf(
                s1.firstElement().id).compareTo(String.valueOf(s2.firstElement().id))).toList();

        for (Stack<Contentor> stack : sortedStacks) {
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

    private void toGround(int fromStack) {
        if (fromStack >= stacks.size()) return;
        Stack<Contentor> newStack = new Stack<>();
        Contentor Contentor = stacks.get(fromStack).pop();
        newStack.push(Contentor);
        stacks.add(newStack);
        this.cost = Contentor.ContentorCost;
    }

    private void toStack(int fromStack, int toStack) {
        if (fromStack >= stacks.size() || toStack >= stacks.size()) return;
        Contentor Contentor = stacks.get(fromStack).pop();
        stacks.get(toStack).push(Contentor);
        this.cost =  Contentor.ContentorCost;
    }

    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();
        ContainersPlatform newPlatform;
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                for (int j = 0; j < stacks.size(); j++) {
                    if (i != j) {
                        newPlatform = this.clone();
                        newPlatform.toStack(i,j);
                        newPlatform.stacks.removeIf(Stack::isEmpty);;
                        if (!newPlatform.equals(this)) {
                            children.add(newPlatform);
                        }
                    }
                }

                newPlatform = this.clone();
                newPlatform.toGround(i);
                newPlatform.stacks.removeIf(Stack::isEmpty);
                if (!newPlatform.equals(this)) {
                    children.add(newPlatform);
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

        List<Stack<Contentor>> sortedThisStacks = this.stacks.stream().sorted((s1,s2) -> String.valueOf(
                s1.firstElement().id).compareTo(String.valueOf(s2.firstElement().id))).toList();

        List<Stack<Contentor>> sortedOtherStacks = other.stacks.stream().sorted((s1,s2) -> String.valueOf(
                s1.firstElement().id).compareTo(String.valueOf(s2.firstElement().id))).toList();

        if (sortedThisStacks.size() != sortedOtherStacks.size()) return false;

        for (int i = 0; i < sortedThisStacks.size(); i++) {
            Stack<Contentor> thisStack = sortedThisStacks.get(i);
            Stack<Contentor> otherStack = sortedOtherStacks.get(i);

            if(thisStack.size() != otherStack.size()) return false;

            for (int j = 0; j < thisStack.size(); j++) {
                Contentor thisContentor = thisStack.get(j);
                Contentor goalContentor = otherStack.get(j);

                if (!(thisContentor.id == goalContentor.id)) {
                    return false;
                }
            }
        }
        return true;
    }

}