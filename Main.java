import SatSolver.Formula;
import SatSolver.Parser;
import SatSolver.SatSolver;
import SatSolver.WatchingSolver;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        Parser p = new Parser("prova.cnf");

        System.out.println(p.getPath());

        Formula f = p.getFormula();

        WatchingSolver s = new WatchingSolver(f);
        SatSolver s1 = new SatSolver(f);

        //okay for (int i = 0; i < 1000; i++)
        Instant start = Instant.now();
        System.out.println(s.solve());
        Instant end = Instant.now();
        System.out.println("exec time watching solver: " + Duration.between(start, end));

        start = Instant.now();
        System.out.println(s1.solve());
        end = Instant.now();
        System.out.println("exec time non watching solver: " + Duration.between(start, end));
    }
}
