import java.util.Scanner;

import static java.lang.System.out;

/**
 * Classe Cliente, para testar as outras classes e casos possiveis caso um cliente queira usar.
 * @author Márcio Felício
 * @version 1.0 09/10/2024
 */

public class Main {
    public static void main (String [] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        AStar s = new AStar();

        String firstConfiguration = sc.nextLine();
        String secondConfiguration = sc.nextLine();

        Platform containerConfiguration1 = new Platform(firstConfiguration, true);
        Platform containersConfiguration2 = new Platform(secondConfiguration, false);
        double heuristicCost = containerConfiguration1.computeHeuristic(containersConfiguration2);
        System.out.println("Custo heurístico estimado: " + heuristicCost);

        long startTime = System.nanoTime();

        AStar.State result = s.solve(containerConfiguration1, containersConfiguration2);

        long endTime = System.nanoTime();

        double totalTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;

        out.println(result);
        out.println((int)result.getG());
        out.println("Generated Nodes: " + AStar.getGeneratedNodes());
        out.println("Expanded Nodes: " + AStar.getExpandedNodes());

        out.printf("Tempo total: %.6f segundos%n", totalTimeInSeconds);

        sc.close();
    }
}
