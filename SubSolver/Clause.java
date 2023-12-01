package SubSolver;

import java.util.HashSet;
import java.util.Set;

public class Clause {
    private Set<Atom> atoms = new HashSet<Atom>();
    private Atom[] watchedLiterals = new Atom[2];
    
    public Clause(String input) {
        String [] atoms = input.split("\\s+");

        for (String atom : atoms){
            if (!atom.isEmpty()) {
                this.atoms.add(new Atom(Integer.parseInt(atom)));
            }
        }
    }

    public Set<Atom> getAtoms() {
        return atoms;
    }


}
