package SubSolver;

import java.util.Set;
import java.util.HashSet;

public class SetOfClauses {
    Set<Clause> clauses = new HashSet<>();

    public SetOfClauses(String input) {
        String [] clauses = input.split(" 0");

        for (String clause : clauses){
            this.clauses.add(new Clause(clause));
        }
    }

    public Set<Clause> getClauses() {
        return clauses;
    }

    public void addClause(Clause c) {
        clauses.add(c);
    }
}
