package SubSolver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class SetOfClauses {
    private ArrayList<Clause> clauses;

    public SetOfClauses(ArrayList<Clause> clauses) {
        this.clauses = new ArrayList<>();
        this.clauses.addAll(clauses);
    }

    public ArrayList<Clause> getClauses() {
        return clauses;
    }
}
