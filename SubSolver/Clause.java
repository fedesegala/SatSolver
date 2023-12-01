package SubSolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Clause {
    private Set<Atom> atoms = new HashSet<Atom>();
    private ArrayList<Atom> watchedLiterals = new ArrayList<Atom>();
    private int assignedValue = -1; // -1: to be defined, 0: false (conflict clause), 1: true
    
    public Clause(String input) {
        String [] atoms = input.split("\\s+");

        for (String atom : atoms){
            if (!atom.isEmpty()) {
                this.atoms.add(new Atom(Integer.parseInt(atom)));
            }
        }
    }

    // getter
    public Set<Atom> getAtoms() {
        return atoms;
    }
    public ArrayList<Atom> getWatchedLiterals() {
        return watchedLiterals;
    }

    public void addWatchedLiteral(Atom a, int index) {
        this.watchedLiterals.add(index, a);
    }

    public void modifyAtom(Atom old, Atom atom) {
        atoms.remove(old);
        atoms.add(atom);
    }



}
