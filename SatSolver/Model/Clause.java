package SatSolver.Model;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class Clause extends ArrayList<Literal>{

    public Clause(List<Literal> literals) {
        super();
        this.addAll(literals);
        super.sort(Comparator.naturalOrder());
    }


    @Override
    public int hashCode() {
        int x = 0;
        for (Literal lit : this) {
            x ^= lit.hashCode();
        }

        return x;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) return "☐";

        String [] literalRepresentations = new String[this.size()];

        for(int i = 0; i < this.size(); i++) {
            literalRepresentations[i] = this.get(i).toString();
        }

        return String.join(" || ", literalRepresentations);
    }
}
