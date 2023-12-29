package SatSolver;

import SatSolver.Model.Clause;
import SatSolver.Model.Formula;
import SatSolver.Model.Literal;
import SatSolver.SatSolver;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

public class EfficientSolver extends SatSolver {
    private final Set<Literal> literals = new HashSet<>();
    private final HashMap<Literal, List<Clause>> lit2clauses = new HashMap<>();
    private final HashMap<Clause, List<Literal>> clause2lits = new HashMap<>();
    private final Map<Literal, Clause> toPropagate = new HashMap<>();

    public EfficientSolver(Formula f) {
        super(f);

        this.initWatches();
        this.initPropagation();

        System.out.println(clause2lits);
        System.out.println(lit2clauses);
        System.out.println(literals);

        System.out.println(toPropagate);
    }

    private void initWatches() {
        for (Clause c : super.formula) {
            List<Literal> watched = new ArrayList<>();

            for (Literal l : c) {
                this.literals.add(l);

                if (!this.lit2clauses.containsKey(l)) this.lit2clauses.put(l, new ArrayList<>());
                if (!this.lit2clauses.containsKey(l.negate())) this.lit2clauses.put(l.negate(), new ArrayList<>());
                if (watched.size() < 2)
                    watched.add(l);
            }

            this.clause2lits.put(c, watched);
        }

        for (Clause c : super.formula) {
            for (Literal l : this.clause2lits.get(c))
                this.lit2clauses.get(l).add(c);
        }
    }

    private void initPropagation() {
        for (Clause c : formula) {
            if (this.clause2lits.get(c).size() == 1) {
                Literal propagation = this.clause2lits.get(c).get(0);
                //super.model.assign(propagation.getVariable(), !propagation.isNegated(), c);
                toPropagate.put(propagation, c);
            }
        }
    }

    public boolean solve() {
        PropagationResult propagation = this.unitPropagation();

        if (propagation.reason.equals("conflict")) return false;

        // after unit-propagation we have to update picker's model
        super.picker.setModel(super.model);

        while (!super.allVariablesAssigned()) {
            // decide a literal
            Literal decision = picker.decide();
            super.model.setDecisionLevel(super.model.getDecisionLevel() + 1);
            super.model.assign(decision.getVariable(), !decision.isNegated());

            // fix watched literals according to decision
            this.fixWatchedLiterals(decision);

            // propagate decision
            while (true) {
                propagation = unitPropagation();
                //System.out.println("conflict occurred: " + propagation.clause);

                //System.out.println("current model: ");
                //this.model.forEach((integer, assignment) -> System.out.println("\t\t " + integer + ": " + assignment ));

                if (!propagation.reason.equals("conflict")) break;

                ConflictAnalysisResult analysis = super.conflictAnalysis(propagation.clause);

                if (analysis.decisionLevel < 0) return false;

                //System.out.println("\t Should backtrack at level " + analysis.decisionLevel);
                super.backtrack(analysis.decisionLevel);
                super.model.setDecisionLevel(analysis.decisionLevel);
                this.addLearntClause(analysis.learntClause);

                //System.out.println("\tlearnt clause: " + analysis.learntClause + ", watched literals: " + this.clause2lits.get(analysis.learntClause));

                //System.out.println("\t After backtracking the model is");
                //this.model.forEach((integer, assignment) -> System.out.println("\t\t" + integer + ": " + assignment ));

                super.picker.setModel(super.model);
            }

        }

        return true;
    }

    private Clause fixWatchedLiterals(Literal literal) {
        literal = literal.negate();
        Clause conflict = null;

        List<Clause> involvedClauses = this.lit2clauses.get(literal);

        for (Clause c : new ArrayList<>(involvedClauses)) {
            List<Literal> watched = this.clause2lits.get(c);

            for (Literal l : new ArrayList<>(watched)) {
                if (!this.availableLiteral(l)) {
                    this.clause2lits.get(c).remove(l);
                    this.lit2clauses.get(l).remove(c);
                }
            }

            for (int i = 0; i < c.size() && watched.size() < 2; i++) {
                if (this.availableLiteral(c.get(i)) && !watched.contains(c.get(i))) {
                    this.clause2lits.get(c).add(c.get(i));
                    this.lit2clauses.get(c.get(i)).add(c);
                }
            }

            if (this.clause2lits.get(c).isEmpty()) conflict = c;
            if (this.clause2lits.get(c).size() == 1 && !this.satisfiedClause(c)) {
                toPropagate.put(this.clause2lits.get(c).get(0), c);
            }
        }

        return conflict;

    }

    private boolean availableLiteral (Literal l) {
        return !super.model.containsKey(l.getVariable()) || super.model.value(l);
    }

    private void addLearntClause (Clause clause) {
        super.formula.add(clause);
        super.picker.setFormula(super.formula);

        this.clause2lits.put(clause, new ArrayList<>());

        List<Clause> clauses = this.formula.stream()
                .filter(c -> this.clause2lits.get(c).size() < 2)
                .collect(Collectors.toCollection(ArrayList::new));

        for (Clause c : clauses) {
            List<Literal> watched = this.clause2lits.get(c);

            for (Literal l : new ArrayList<>(watched)) {
                if (!this.availableLiteral(l)) {
                    this.clause2lits.get(c).remove(l);
                    this.lit2clauses.get(l).remove(c);
                }
            }

            for (int i = 0; i < c.size() && watched.size() < 2; i++) {
                if (this.availableLiteral(c.get(i)) && !watched.contains(c.get(i))) {
                    this.clause2lits.get(c).add(c.get(i));
                    this.lit2clauses.get(c.get(i)).add(c);
                }
            }
            if (this.clause2lits.get(c).size() == 1 && !this.satisfiedClause(c)) {
                toPropagate.put(this.clause2lits.get(c).get(0), c);
            }
        }
    }

    private PropagationResult unitPropagation() {
        Clause output = null;
        while (!toPropagate.isEmpty()) {
            Literal propagation = new ArrayList<>(toPropagate.keySet()).get(0);
            Clause justification = toPropagate.remove(propagation);

            this.model.assign(propagation.getVariable(), !propagation.isNegated(), justification);

            //System.err.println("\t\tPropagated Literal " + propagation);

            Clause conflict = this.fixWatchedLiterals(propagation);

            if (conflict != null) output = conflict;
        }

        if (output != null) return new PropagationResult("conflict", output);
        return new PropagationResult("unresolved", null);
    }

    public boolean satisfiedClause(Clause c) {
        List<Literal> watched = this.clause2lits.get(c);

        boolean found = false;

        for (Literal l : watched) {
            if (this.model.containsKey(l.getVariable()) && this.model.value(l)) {
                found = true;
                break;
            }
        }

        return found;
    }

}
