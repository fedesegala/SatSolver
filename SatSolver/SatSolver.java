package SatSolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class SatSolver {
    protected Assignments model;
    protected Formula formula;
    protected Picker picker;

    public SatSolver(Formula formula) {
        this.formula = formula;
        this.model = new Assignments();
        picker = new Picker(this.formula, this.model);
    }

    public boolean solve() {
        // propagate unit clauses at level 0
        PropagationResult afterPropagation = unitPropagation();

        if (afterPropagation.reason.equals("conflict")) return false;

        while (!allVariablesAssigned()) {
            // decide a literal with some heuristic
            Literal decision = picker.decide();

            // all unit propagations are done at level 0, first decision will be assigned at level 1
            this.model.setDecisionLevel(this.model.getDecisionLevel() + 1);
            this.model.assign(decision.getVariable(), !decision.isNegated());

            // update picker model
            picker.setModel(this.model);

            // propagate the decision
            while (true) {
                afterPropagation = unitPropagation();

                if (!afterPropagation.reason.equals("conflict")) break;

                ConflictAnalysisResult analysisResult = conflictAnalysis(afterPropagation.clause);

                if (analysisResult.getDecisionLevel() == -1) return false;

                addLearntClause(analysisResult.getLearntClause());
                backtrack(analysisResult.getDecisionLevel());

                this.model.setDecisionLevel(analysisResult.getDecisionLevel());
            }

        }
        return true;
    }

    protected void backtrack(int level) {
        ArrayList<Integer> toRemove = new ArrayList<>();

        ArrayList<Integer> keys = new ArrayList<>(this.model.keySet());
        ArrayList<Assignment> assignments = new ArrayList<>(this.model.values());

        // search for the assignments that have to be removed in the current trail
        for (Assignment assignment : assignments) {
            if (assignment.getDecisionLevel() > level) {
                int index = assignments.indexOf(assignment);
                toRemove.add(keys.get(index));
            }
        }

        for (int var : toRemove) {
            this.model.remove(var);
        }

        this.picker.setModel(this.model);
    }

    private void addLearntClause(Clause c) {
        this.formula.add(c);
    }

    private PropagationResult unitPropagation() {
        boolean finish = false;

        while (!finish) {
            finish = true;
            for (Clause c : formula) {
                String status = clauseStatus(c);
                if (status.equals("unresolved") || status.equals("satisfied")) continue;
                if (status.equals("unsatisfied")) return new PropagationResult("conflict", c);

                // unit clause case

                // select literal to propagate
                Literal literal = c.stream()
                        .filter(l -> !this.model.containsKey(l.getVariable()))
                        .iterator().next();

                // assign that literal in the trail at the current decision level
                this.model.assign(literal.getVariable(), !literal.isNegated(), c);
                // update the picker model
                picker.setModel(this.model);
                finish = false;
            }
        }

        return new PropagationResult("unresolved", null);
    }

    private String clauseStatus(Clause c) {
        ArrayList<Integer> values = new ArrayList<>();

        for (Literal l : c) {
            if (!this.model.containsKey(l.getVariable())) {
                values.add(-1);
            } else {
                values.add(this.model.value(l)? 1 : 0);
            }
        }

        if (values.contains(1)){
            return "satisfied";
        }
        if (values.stream().filter(value -> value.equals(0)).count() == values.size()) {
            return "unsatisfied";
        }

        if (values.stream().filter(value -> value.equals(0)).count() == values.size() - 1) {
            return "unit";
        }

        return "unresolved";
    }

    protected boolean allVariablesAssigned() {
        return formula.getVariables().size() == this.model.keySet().size();
    }

    private Clause resolve(Clause a, Clause b, int x) {
        Set<Literal> resultSet = new HashSet<>(a);
        resultSet.addAll(b);

        resultSet.remove(new Literal(x, true));
        resultSet.remove(new Literal(x, false));

        return new Clause(resultSet.stream().toList());
    }

    protected ConflictAnalysisResult conflictAnalysis(Clause conflict) {
        if (this.model.getDecisionLevel() == 0) return new ConflictAnalysisResult(-1, null);

        // literals with current decision level
        ArrayList<Literal> currentLevelLiterals = conflict
                .stream()
                .filter(literal -> this.model.getDecisionLevel() == this.model.get(literal.getVariable()).getDecisionLevel())
                .collect(Collectors.toCollection(ArrayList::new));

        while (currentLevelLiterals.size() != 1) {
            // implied literals
            ArrayList<Literal> impliedLiterals = currentLevelLiterals
                    .stream()
                    .filter(literal -> this.model.get(literal.getVariable()).getJustification() != null)
                    .collect(Collectors.toCollection(ArrayList::new));

            Literal lit = impliedLiterals.iterator().next();
            Clause justification = this.model.get(lit.getVariable()).getJustification();

            conflict = resolve(conflict, justification, lit.getVariable());

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

        if (sortedDecisionLevels.size() <= 1) return new ConflictAnalysisResult(0, conflict);

        return new ConflictAnalysisResult(sortedDecisionLevels.get(sortedDecisionLevels.size()-2), conflict);

    }
}
