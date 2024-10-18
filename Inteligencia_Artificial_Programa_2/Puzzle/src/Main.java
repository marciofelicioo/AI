import java.util.Objects;
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
        //out.println(firstConfiguration + " " + secondConfiguration);
        Platform containerConfiguration1 = new Platform(firstConfiguration,true);
        Platform containersConfiguration2 = new Platform(secondConfiguration,false);
        AStar.State result = s.solve(containerConfiguration1,containersConfiguration2);
        out.println(result);
        out.println((int)result.getG());
        out.println(AStar.getGeneratedNodes());
        out.println(AStar.getExpandedNodes());
        sc.close();
    }
}