package SubSolver;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Clause {
    private ArrayList<ClauseLiteral> literals;

    public Clause(ArrayList<ClauseLiteral> literals) {
        this.literals = new ArrayList<>();

        this.literals.addAll(literals);

        // if we have only one literal we have to immediately propogate it
        if (this.literals.size() == 1) {
            ClauseLiteral l = this.literals.remove(0);
            l.setAssignedValue(1);
            this.literals.add(l);
        }
    }

    public ArrayList<ClauseLiteral> getLiterals() {
        return literals;
    }
}
