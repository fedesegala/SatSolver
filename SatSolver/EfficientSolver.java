package SatSolver;

import SatSolver.Model.Clause;
import SatSolver.Model.Formula;
import SatSolver.Model.Literal;

import java.util.*;
import java.util.stream.Collectors;

public class EfficientSolver extends SatSolver {
    private final Set<Literal> literals = new HashSet<>();
    private final Map<Clause, List<Clause>> generatedClauses = new HashMap<>();
    private final HashMap<Literal, List<Clause>> lit2clauses = new HashMap<>();
    private final HashMap<Clause, List<Literal>> clause2lits = new HashMap<>();
    private final Map<Literal, Clause> toPropagate = new HashMap<>();
    private String filename;
    private Proof proof;

    public EfficientSolver(Formula f, String filename) {
        super(f);
        this.filename = filename;

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

    public void cdclSolve() {
        SatResult result = this.satisfy();

        if (result.sat) {
            System.out.println("The formula is SAT");
            this.model.forEach((i, assignment) -> System.out.println(i + "-> " + assignment.getValue()));
        } else {
            System.out.println("\n\nThe formula is UNSAT, now generating the proof..\n\n");
            //this.conflicts.forEach(System.out::println);
            Clause conflict = result.lastConflict;

            while(conflict.size() != 1) {
                System.out.println("Resolving from conflict: " + conflict);
                ConflictAnalysisResult analysis = this.conflictAnalysis(conflict);
                conflict = analysis.learntClause;

                this.model.forEach((var, as) -> System.out.println(var + "-> " + this.model.get(var)));
            }

            Clause justification = this.model.get(conflict.get(0).getVariable()).getJustification();

            Clause empty = this.resolve(conflict, justification, conflict.get(0).getVariable());

            System.out.println("\n\nPrinting Generated Clauses");

            this.generatedClauses.forEach((clause, list) -> System.out.println(clause + "-> " + list));

            System.out.println("\n\n");

            this.proof = new Proof(empty, this);
        }
    }

    private SatResult satisfy() {
        PropagationResult propagation = this.unitPropagation();

        if (propagation.reason.equals("conflict")) return new SatResult(propagation.clause, false);

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

                //System.out.println("\u001B[32m" + "------------PROPAGATION-----------");
                //this.model.forEach((var, as) -> System.out.println("\u001B[0m"+ "\t" + var + "-> " + as));
                //System.out.println("Current conflict: " + propagation.clause);

                if (!propagation.reason.equals("conflict")) break;
                if (super.model.getDecisionLevel() == 0) {
                    //super.backtrack(0);
                    //super.model.setDecisionLevel(0);
                    return new SatResult(propagation.clause, false);
                }

                ConflictAnalysisResult analysis = this.conflictAnalysis(propagation.clause);
                //System.out.println("Current Conflict: " + analysis.learntClause + "\n\tBacktrack at level: " + analysis.decisionLevel);

                if (analysis.decisionLevel < 0) {
                    //super.backtrack(0);
                    //super.model.setDecisionLevel(0);
                    //this.addLearntClause(analysis.learntClause);
                    //System.err.println(analysis.learntClause);
                    return new SatResult(analysis.learntClause, false);
                }

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

        return new SatResult(null, true);
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
            if (this.clause2lits.get(c).size() == 1 && this.toBeSatisfied(c)) {
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
            if (this.clause2lits.get(c).size() == 1 && this.toBeSatisfied(c)) {
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

    public boolean toBeSatisfied(Clause c) {
        List<Literal> watched = this.clause2lits.get(c);

        boolean found = false;

        for (Literal l : watched) {
            if (this.model.containsKey(l.getVariable()) && this.model.value(l)) {
                found = true;
                break;
            }
        }

        return !found;
    }

    protected ConflictAnalysisResult conflictAnalysis(Clause conflict) {
        // literals with current decision level
        ArrayList<Literal> currentLevelLiterals = conflict
                .stream()
                .filter(literal -> this.model.getDecisionLevel() == this.model.get(literal.getVariable()).getDecisionLevel())
                .collect(Collectors.toCollection(ArrayList::new));

        while (currentLevelLiterals.size() != 1) {
            //System.out.println("\t\tSubConflict: " + conflict);

            // implied literals
            ArrayList<Literal> impliedLiterals = currentLevelLiterals
                    .stream()
                    .filter(literal -> this.model.get(literal.getVariable()).getJustification() != null)
                    .collect(Collectors.toCollection(ArrayList::new));

            //this.model.forEach((i, assignment) -> System.out.println(i + "-> " + assignment));


            Literal lit = impliedLiterals.stream()
                    .max(Comparator.comparingInt(literal -> this.model.get(literal.getVariable()).getArrivalOrder()))
                    .orElse(null);

            assert lit != null;
            Clause justification = this.model.get(lit.getVariable()).getJustification();

            //System.out.println("\t\t\tResolving with " + justification);

            conflict = this.resolve(conflict, justification, lit.getVariable());

            currentLevelLiterals = conflict
                    .stream()
                    .filter(literal -> this.model.getDecisionLevel() == this.model.get(literal.getVariable()).getDecisionLevel())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        HashSet<Integer> decisionLevels = conflict
                .stream()
                .map(literal -> model.get(literal.getVariable()).getDecisionLevel())
                .collect(Collectors.toCollection(HashSet<Integer>::new));

        ArrayList<Integer> sortedDecisionLevels = decisionLevels
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(ArrayList<Integer>::new));

        if (sortedDecisionLevels.size() <= 1) {
            return new ConflictAnalysisResult(0, conflict);
        }

        return new ConflictAnalysisResult(sortedDecisionLevels.get(sortedDecisionLevels.size()-2), conflict);
    }

    protected Clause resolve(Clause a, Clause b, int x) {
        Set<Literal> resultSet = new HashSet<>(a);
        resultSet.addAll(b);

        resultSet.remove(new Literal(x, true));
        resultSet.remove(new Literal(x, false));

        Clause result = new Clause(resultSet.stream().toList());

        if (!this.generatedClauses.containsKey(result)) {
            this.generatedClauses.put(result, new ArrayList<Clause>());
        }

        this.generatedClauses.get(result).add(a);
        this.generatedClauses.get(result).add(b);

        return new Clause(resultSet.stream().toList());
    }

    public Map<Clause, List<Clause>> getGeneratedClauses() {
        return generatedClauses;
    }

    public String getFilename() {
        return filename;
    }

    public Proof getProof() {
        return proof;
    }
}
