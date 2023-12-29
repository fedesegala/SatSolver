package SatSolver.Model;

import java.util.ArrayList;
import java.util.List;

public class Formula extends ArrayList<Clause>{
    private final List<Integer> variables = new ArrayList<>();

    public Formula (List<Clause> clauses) {
        super();
        this.addAll(clauses);
        for (Clause clause : this) {
            for (Literal l : clause) {
                int variable = l.getVariable();
                if (!variables.contains(variable)) this.variables.add(variable);
            }
        }
    }

    public List<Integer> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        String [] clauseRepresentations = new String[this.size()];

        for(int i = 0; i < this.size(); i++) {
            clauseRepresentations[i] = this.get(i).toString();
        }

        return String.join(" & ", clauseRepresentations);
    }
}
