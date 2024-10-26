package ContainersPP;

import java.util.*;
/**
 * Classe ContainersPP.HeuristicStrategy: Esta classe contém um construtor que cria objetos HeuristicStrategy
 * e o seu principal objetivo é calcular a heurística para poder auxiliar o método solve da classe AStar (A* algorithm).
 *
 * @author Márcio Felício, Maria Anjos, Miguel Rosa
 * @version 1.0 19/10/2024
 *
 * @inv
 * - O cache `cacheGoalPositions` deve armazenar corretamente as posições de cada contêiner para as
 *      configurações objetivo processadas, evitando recalcular o mesmo layout várias vezes.
 * - O valor retornado pelo método `computeHeuristic` nunca deve superestimar o custo real necessário para
 *      transformar a configuração atual na configuração objetivo, garantindo a admissibilidade da heurística no contexto do A*.
 */

public class HeuristicStrategy implements Ilayout{
    /**
     * Cache estático para armazenar cacheGoalPoisitons para diferentes goalLayouts.
     */
    private static final Map<Ilayout, HashMap<Character, List<Container>>> cacheGoalPositions = new HashMap<>();
    private final List<Deque<Container>> stacks;
    public HeuristicStrategy(List<Deque<Container>> stacks)
    {
        this.stacks = stacks;
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
    @Override
    public double computeHeuristic(Ilayout goalLayout) {
        double h = 0;

        HashMap<Character, List<Container>> goalBaseMap = cacheGoalPositions(goalLayout);

        for (Deque<Container> currentStack : this.stacks) {
            if (currentStack.isEmpty()) continue;

            List<Container> currentStackList = new ArrayList<>(currentStack);
            char currentBaseId = currentStackList.getFirst().getId();
            List<Container> goalStack = goalBaseMap.get(currentBaseId);

            if (goalStack != null) {
                int minHeight = Math.min(currentStackList.size(), goalStack.size());
                boolean mismatchFound = false;
                int firstMismatchIndex = -1;

                for (int j = 0; j < minHeight; j++) {
                    if (currentStackList.get(j).getId() != goalStack.get(j).getId()) {
                        mismatchFound = true;
                        firstMismatchIndex = j;
                        break;
                    }
                }
                if (mismatchFound) {
                    for (int k = firstMismatchIndex; k < currentStackList.size(); k++) {
                        h += currentStackList.get(k).getCost();
                    }
                }
                if (currentStackList.size() > goalStack.size()) {
                    for (int k = goalStack.size(); k < currentStackList.size(); k++) {
                        h += currentStackList.get(k).getCost();
                    }
                }
            } else {
                for (Container container : currentStack) {
                    h += container.getCost();
                }
            }
        }

        return h;
    }

    /**
     * Recupera o cacheGoalPositions para um dado goalLayout utilizando cache.
     * Se o cacheGoalPositions já foi calculado para este goalLayout, retorna do cache.(melhora a eficiência evitando recalcular)
     * Caso contrário, calcula, armazena no cache e retorna.
     *
     * @param goalLayout O layout objetivo para o qual calcular o goalBaseMap.
     * @return HashMap<Character, List<Container>> mapeamento das bases para suas respectivas pilhas.
     */
    private HashMap<Character, List<Container>> cacheGoalPositions(Ilayout goalLayout) {

        if (cacheGoalPositions.containsKey(goalLayout)) {
            return cacheGoalPositions.get(goalLayout);
        }

        HashMap<Character, List<Container>> goalBaseMap = new HashMap<>();
        for (Deque<Container> goalStack : ((Platform) goalLayout).getStacks()) {
            if (!goalStack.isEmpty()) {
                List<Container> goalStackList = new ArrayList<>(goalStack);
                char goalBaseId = goalStackList.getFirst().getId();
                goalBaseMap.put(goalBaseId, goalStackList);
            }
        }

        cacheGoalPositions.put(goalLayout, goalBaseMap);
        return goalBaseMap;
    }

    /**
     * Primeira heurística usada para calcular o custo estimado até o objetivo.
     * @deprecated Esta heurística foi substituída por uma nova abordagem mais eficiente e precisa.
     * @see #computeHeuristic(Ilayout) above
     */
    @Deprecated
    /*public double computeHeuristic(Ilayout goalLayout) {
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
                if (goalPosition.base != currentBase) {
                    h += container.getCost();
                } else if (goalPosition.height != j) {
                    h += container.getCost();
                }
                j++;
            }
        }
        return h;
    }*/

    @Override
    public List<Ilayout> children() {
        return List.of();
    }

    @Override
    public boolean isGoal(Ilayout layout) {
        return false;
    }

    @Override
    public double getK() {
        return 0;
    }
}
