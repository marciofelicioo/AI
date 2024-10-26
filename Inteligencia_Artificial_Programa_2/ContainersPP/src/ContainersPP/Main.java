package ContainersPP;

import java.util.Scanner;

/**
 * Classe Cliente, para testar as outras classes e casos possiveis caso um cliente queira usar.
 * @author Márcio Felício, Maria Anjos, Miguel Rosa
 * @version 1.0 19/10/2024
 */

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        AStar s = new AStar();

        String firstConfiguration = sc.nextLine();
        String secondConfiguration = sc.nextLine();

        Platform containerConfiguration1 = new Platform(firstConfiguration, true);
        Platform containersConfiguration2 = new Platform(secondConfiguration, false);
//        HeuristicStrategy heuristic = new HeuristicStrategy(containerConfiguration1.getStacks());
//        double heuristicCost = heuristic.computeHeuristic(containersConfiguration2);
//        System.out.println("Estimated Heuristic Cost: " + heuristicCost + "\n");

//        // Obter memória antes da execução do algoritmo
//        Runtime runtime = Runtime.getRuntime();
//        runtime.gc(); // Sugestão para que o GC libere a memória antes da medição inicial
//        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

//        long startTime = System.nanoTime();
        AStar.State solution = s.solve(containerConfiguration1, containersConfiguration2);
//        long endTime = System.nanoTime();

//        // Obter memória após a execução do algoritmo
//        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
//        long memoryUsed = memoryAfter - memoryBefore;

//        double totalTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
//        double pathLength = solution.getPathLength();
//        double penetrance = pathLength / AStar.getExpandedNodes();

        System.out.println(solution);
//        System.out.println("Results: ");
        System.out.println((int) solution.getG());
//        System.out.println("Total Cost (G): " + (int) solution.getG());
//        System.out.println("Path Length (L): " + pathLength);
//        System.out.println("Generated Nodes (G): " + AStar.getGeneratedNodes());
//        System.out.println("Expanded Nodes (E): " + AStar.getExpandedNodes());
//        System.out.println("Penetrance (P = L / E): " + penetrance);
//        System.out.printf("Total Time: %.6f seconds%n", totalTimeInSeconds);
//        System.out.println("Memory Used (bytes): " + memoryUsed);

        sc.close();
    }
}