import SatSolver.EfficientSolver;
import SatSolver.Model.Formula;
import SatSolver.Model.Literal;
import SatSolver.Parser;
import SatSolver.WatchingSolver;
import SatSolver.SatSolver;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        fun2();
    }

    private static void fun2() {
        Parser p = new Parser("prova.cnf");

        System.out.println(p.getPath());

        Formula f = p.getFormula();

        //WatchingSolver s1 = new WatchingSolver(f);
        EfficientSolver s = new EfficientSolver(f);

        System.out.println(new Date());

        //System.out.println(s1.solve());
        System.out.println(s.solve());

        System.out.println(new Date());
    }

    /*
    public static void fun1() {
        Parser p = new Parser("unsat1.cnf");

        System.out.println(p.getPath());

        Formula f = p.getFormula();

        SatSolver s = new SatSolver(f);
        WatchingSolver s1 = new WatchingSolver(f);
        EfficientSolver s2 = new EfficientSolver(f);


        Instant start = Instant.now();
        System.out.println(s.solve());
        Instant end = Instant.now();
        System.out.println("exec time base solver: " + Duration.between(start, end));

        start = Instant.now();
        System.out.println(s1.solve());
        end = Instant.now();
        System.out.println("exec time watching solver: " + Duration.between(start, end));

        start = Instant.now();
        System.out.println(s2.cdclSolve());
        end = Instant.now();
        System.out.println("exec time efficient solver: " + Duration.between(start, end));
    }
    */
}
