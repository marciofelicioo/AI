import java.util.Iterator;
import java.util.Scanner;

import static java.lang.System.out;

public class Main {
    public static void main (String [] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        BestFirst s = new BestFirst();
        String firstConfiguration = sc.nextLine();
        String secondConfiguration = sc.nextLine();
        //out.println(firstConfiguration + " " + secondConfiguration);
        ContainersPlatform containerConfiguration1 = new ContainersPlatform(firstConfiguration);
        ContainersPlatform containersPlatform2 = new ContainersPlatform(secondConfiguration);
        Iterator<BestFirst.State> it = s.solve(containerConfiguration1, containersPlatform2);
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