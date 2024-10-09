import java.util.Iterator;
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
        BestFirst s = new BestFirst();
        String firstConfiguration = sc.nextLine();
        String secondConfiguration = sc.nextLine();
        //out.println(firstConfiguration + " " + secondConfiguration);
        ContainersConfiguration containerConfiguration1 = new ContainersConfiguration(firstConfiguration,true);
        ContainersConfiguration containersConfiguration2 = new ContainersConfiguration(secondConfiguration,false);
        Iterator<BestFirst.State> it = s.solve(containerConfiguration1, containersConfiguration2);
        if (it==null) out.println("no solution found");
        else {
            while(it.hasNext()) {
                BestFirst.State i = it.next();
                out.println(i);
                if (!it.hasNext()) out.println((int)i.getG());
            }
        }
        sc.close();
    }
}