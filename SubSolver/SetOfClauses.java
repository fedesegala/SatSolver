package SubSolver;

import java.util.Set;
import java.util.HashSet;

public class SetOfClauses {
    Set<Clause> clauses = new HashSet<>();

    public SetOfClauses(String input) {
        input = input.replaceAll("\\s+", "");
        String [] clauses = input.split(";");

        for (String clause : clauses){
            this.clauses.add(new Clause(clause));
        }

    }

    public Set<Clause> getClauses() {
        return clauses;
    }
}
