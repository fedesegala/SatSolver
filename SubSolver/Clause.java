package SubSolver;

import java.util.HashSet;
import java.util.Set;

public class Clause {
    private Set<Atom> atoms = new HashSet<Atom>();

    public Clause(String input) {
        String [] atoms = input.split("or");

        for (String atom : atoms){
            if (atom.charAt(0) == '!')
                this.atoms.add(new Atom(atom, false));
            else
                this.atoms.add(new Atom(atom, true));
        }
    }

    public Set<Atom> getAtoms() {
        return atoms;
    }


}
