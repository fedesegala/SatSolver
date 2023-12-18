import SatSolver.Clause;
import SatSolver.Formula;
import SatSolver.Parser;
import SatSolver.Solver;
import SatSolver.SatSolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Parser p = new Parser("prova.cnf");

        System.out.println(p.getPath());

        Formula f = p.getFormula();

        SatSolver s = new SatSolver(f);

        System.out.println(s.solve());
    }
}
