import java.util.*;
import java.util.stream.Collectors;

public class ContainersPlatform implements Ilayout, Cloneable {

    
    private List<Stack<Container>> stacks;
    private double cost;

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


    @Override
    public int hashCode() {
        return Arrays.hashCode(this.stacks.stream().sorted((s1,s2) -> String.valueOf(
                s1.firstElement().getId()).compareTo(String.valueOf(s2.firstElement().getId()))).toArray());
    }

    @Override
    @SuppressWarnings("unchecked")
    public ContainersPlatform clone() {
        ContainersPlatform copy = new ContainersPlatform();
        copy.stacks = new ArrayList<>(stacks.size());
        for (Stack<Container> stack : stacks) {
            copy.stacks.add((Stack<Container>) stack.clone());
        }
        copy.cost = getK();
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

    private void toGround(int fromStack) {
        if (fromStack >= stacks.size()) return;
        Stack<Container> newStack = new Stack<>();
        Container container = stacks.get(fromStack).pop();
        newStack.push(container);
        stacks.add(newStack);
        this.cost = container.getcost();
    }

    private void toStack(int fromStack, int toStack) {
        if (fromStack >= stacks.size() || toStack >= stacks.size()) return;
        Container container = stacks.get(fromStack).pop();
        stacks.get(toStack).push(container);
        this.cost =  container.getcost();
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

}