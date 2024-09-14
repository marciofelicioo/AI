import java.util.Iterator;
import java.util.Scanner;

import static java.lang.System.out;

public class Main {
    public static void main (String [] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new Board(sc.next()),
                new Board(sc.next()));
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