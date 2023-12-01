import SubSolver.Clause;
import SubSolver.Atom;
import SubSolver.SetOfClauses;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String input = "A or B or !C; B or D or !B; A or !C";
        SetOfClauses clauses = new SetOfClauses(input);


        Set<Clause> output = clauses.getClauses();

        for (Clause c : output) {
            for (Atom a : c.getAtoms()) {
                System.out.println(a);
            }
        }
    }
}
