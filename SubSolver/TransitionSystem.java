package SubSolver;

import java.util.Set;
import java.util.HashSet;
import java.util.Stack;

public class TransitionSystem {
    private Stack<HashSet<Atom>> model;
    private SetOfClauses clauses;
    private Clause conflictClause;
    private int level;

    public TransitionSystem(SetOfClauses clauses) {
        this.clauses = clauses;
        initWatchedLiterals();
        this.model = new Stack<HashSet<Atom>>();
        this.model.push(new HashSet<Atom>());
        conflictClause = null;

        this.level = 0;
    }

    public Clause getConflictClause() {
        return conflictClause;
    }
    public SetOfClauses getClauses() {
        return clauses;
    }
    public Stack<HashSet<Atom>> getModel() {
        return model;
    }
    public int getLevel() {
        return level;
    }
    public HashSet<Atom> getCurrentLevel() {
        return model.pop();
    }

    // sets the 2 watched literals for all clauses
    public void initWatchedLiterals() {
        for (Clause c : clauses.getClauses()) {
            int watchedCount = 0;
            for (Atom a : c.getAtoms()) {
                a.setWatched();
                c.addWatchedLiteral(a, watchedCount);
                watchedCount ++;
                if (watchedCount == 2) break;
            }
        }
    }

    public boolean solve() {
        boolean satisfiable;
        boolean termination = false;

        while (true) {
            // propagate everything that's possible for the current level
            propagate();
            System.out.println(model.pop().toString());
            System.exit(0);
            // fix watched literals
            // if nothing was propagated decide and fix watched literals
        }

    }

    public void propagate() {
        HashSet<Atom> currentLevel = model.pop();
        for (Clause c : clauses.getClauses()) {
            if (c.getWatchedLiterals().size() == 1) {
                currentLevel.add(c.getWatchedLiterals().get(0));
            }
        }
        model.push(currentLevel);
    }
}
