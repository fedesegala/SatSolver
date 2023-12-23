package SatSolver;

import java.util.*;
import java.util.stream.Collectors;

public class WatchingSolver extends SatSolver {
    private final Map<Clause, List<Literal>> watchedLiterals= new HashMap<>();

    public WatchingSolver(Formula f) {
        super(f);

        this.initWatchedLiterals();

        // debug
        //this.watchedLiterals.keySet().forEach( c -> System.out.println(c + " ---> " + this.watchedLiterals.get(c)));
    }

    /**
     * initializes the two watched literals structure
     */
    private void initWatchedLiterals() {
        for (Clause c : super.formula) {
            List<Literal> watched = new ArrayList<>();

            for (int i = 0; watched.size() < 2 && i < c.size(); i++) {
                if (!super.model.containsKey(c.get(i).getVariable())) watched.add(c.get(i));
                else if (super.model.value(c.get(i))) watched.add(c.get(i));
            }

            this.watchedLiterals.put(c, watched);
        }
    }

    public boolean solve() {
        PropagationResult propagationResult = unitPropagation();
        if (propagationResult.reason.equals("conflict")) return false;

        // after unit-propagation we have to update picker's model
        super.picker.setModel(super.model);

        while (!super.allVariablesAssigned()) {
            Literal decision = picker.decide();
            //System.out.println(decision);

            // increase decision level
            super.model.setDecisionLevel(super.model.getDecisionLevel() + 1);
            super.model.assign(decision.getVariable(), !decision.isNegated(), null);

            super.picker.setModel(super.model);

            // propagate the decision in the two watched literals data structure
            this.fixWatchedLiterals(decision);


            // propagate the decision
            while (true) {
                PropagationResult afterPropagation = unitPropagation();

                if (!afterPropagation.reason.equals("conflict")) break;

                ConflictAnalysisResult analysisResult = super.conflictAnalysis(afterPropagation.clause);

                if (analysisResult.decisionLevel < 0) return false;

                super.backtrack(analysisResult.decisionLevel);
                this.addLearntClause(afterPropagation.clause, analysisResult.learntClause);
                super.model.setDecisionLevel(analysisResult.decisionLevel);

                this.picker.setModel(super.model);
            }
        }

        return true;
    }

    private void addLearntClause(Clause conflict, Clause toLearn) {
        // we need to update watched literals of all clauses: after backtracking we may be able to watch one more literal
        // thus the clause may now be unwatched
        this.formula.add(toLearn);
        this.picker.setFormula(super.formula);

        for (Clause c : this.formula) {
            List<Literal> watched = new ArrayList<>();

            for (int i = 0; i < c.size() && watched.size() < 2; i++) {
                Literal proposal = c.get(i);
                if (!super.model.containsKey(proposal.getVariable())) watched.add(proposal);
                else if (super.model.value(proposal)) watched.add(proposal);
            }

            this.watchedLiterals.put(c, watched);
        }

    }

    private PropagationResult unitPropagation() {
        // find unit clauses
        List<Clause> unitClauses = findUnitClauses();

        boolean proceed = !unitClauses.isEmpty();

        while (proceed) {
            Clause toPropagate = unitClauses.remove(0);
            // unit clause -> we have to propagate the only watched literal
            Literal propagation = this.watchedLiterals.get(toPropagate).get(0);

            this.model.assign(propagation.getVariable(), !propagation.isNegated(), toPropagate);
            this.picker.setModel(this.model);

            // try to fix the two watched literals of the involved clauses
            Clause conflict = this.fixWatchedLiterals(propagation);
            if (conflict!=null) return new PropagationResult("conflict", conflict);

            unitClauses = findUnitClauses();
            proceed = !unitClauses.isEmpty();
        }

        return new PropagationResult("unresolved", null);
    }

    private List<Clause> findUnitClauses() {
        List<Clause> unitClauses = this.watchedLiterals.keySet().stream()
                .filter(clause -> this.watchedLiterals.get(clause).size() == 1)
                .collect(Collectors.toCollection(ArrayList::new));

        List<Clause> output = new ArrayList<>();
        // if a clause is already satisfied, there is no need to propagate
        for (Clause c : unitClauses) {
            boolean sat = false;
            for (Literal l : c) {
                if (super.model.containsKey(l.getVariable()) && super.model.value(l)) {
                    sat = true;
                    break;
                }
            }
            if (!sat) output.add(c);
        }

        return output;
    }

    private Clause fixWatchedLiterals(Literal propagation) {
        List<Clause> toFix = this.watchedLiterals.keySet().stream()
                .filter(clause -> this.watchedLiterals.get(clause).contains(propagation.negate()))
                .collect(Collectors.toCollection(ArrayList::new));

        Clause conflict = null;

        for (Clause c : toFix) {
            List<Literal> newWatched = new ArrayList<Literal>();

            for (int i = 0; i < c.size() && newWatched.size()<=2; i++) {
                Literal proposal = c.get(i);
                if (!this.model.containsKey(proposal.getVariable())) newWatched.add(proposal);
                else if (this.model.value(proposal)) newWatched.add(proposal);
            }

            this.watchedLiterals.put(c, newWatched);
            // if we can't find newly watched literals, the clause is conflictual
            if (newWatched.isEmpty()) conflict = c;
        }

        return conflict;
    }


}
