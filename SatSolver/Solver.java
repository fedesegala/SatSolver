package SatSolver;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    private Assignments model;
    private Formula formula;
    private Picker picker;
    private HashMap<Clause, ArrayList<Literal>> watchedLiterals = new HashMap<>();
    private HashMap<Literal, ArrayList<Clause>> watchingClauses = new HashMap<>();

    public Solver(Formula formula) {
        this.formula = formula;
        this.model = new Assignments();
        picker = new Picker(this.formula, this.model);
    }

    public boolean solve() {
        /*
         * if we find here a conflict clause, since the decision level is 0, that means the formula is not
         * satisfiable, hence we return false (UNSAT). GENERATE PROOF IN THIS CASE
         */
        if (this.unitPropagation() != null) return false;

        while (!allVariablesAssigned()) {
            // decision phase
            Literal temp = picker.decide();

            int var = temp.getVariable();
            boolean value = !temp.isNegated();

            model.setDecisionLevel(model.getDecisionLevel() + 1);
            model.assign(var, value);


            while (true) {
                Clause conflictClause = unitPropagation();

                if (conflictClause == null) break;

                ConflictAnalysisResult analysis = conflictAnalysis(conflictClause);

                // UNSAT
                if (analysis.getDecisionLevel() == -1) {
                    return false;
                }

                // if we can continue
                this.addLearntClause(conflictClause);
                this.backtrack(analysis.getDecisionLevel());
                this.model.setDecisionLevel(analysis.getDecisionLevel());
                picker.setModel(this.model);
                picker.setFormula(this.formula);
            }
        }

        return true;
    }

    private Clause unitPropagation() {
        // finish gets true if no unit clauses nor conflict clauses are found during one iteration
        boolean finish = false;
        while (!finish) {
            finish = true;

            for (Clause clause : this.formula) {
                String status = clauseStatus(clause);

                // if the current clause is satisfied or its value is still undefined, proceed with the next one
                if (status.equals("unresolved") || status.equals("satisfied")) continue;
                // if we find a unit clause propagate the literal
                else if (status.equals("unit")) {
                    // select the literal to propagate
                    Literal lit = clause.stream()
                            .filter(literal -> !model.containsKey(literal.getVariable()))
                            .iterator().next();

                    int variable = lit.getVariable();
                    boolean value = !lit.isNegated();

                    model.assign(variable, value, clause);
                    finish = false;
                } else {    // we found a conflict clause
                    return clause;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param clause: a clause for which we want to evaluate the status
     * @return the status of the clause, i.e.,
     * "unit" : all but one literal are assigned false
     * "unsatisfied" : all literals are assigned false
     * "satisfied" : one of the literals is assigned true
     * "unresolved": neither unit, unsatisfied nor satisfied
     */
    private String clauseStatus(Clause clause) {
        // -1: variable unassigned, 0: false, 1: true
        ArrayList<Integer> values = new ArrayList<>();

        for (Literal lit : clause) {
            // variable not yet assigned in the trail
            if (!model.containsKey(lit.getVariable())) values.add(-1);
                // if already assigned evaluate the variable under the current interpretation
            else values.add(model.value(lit) ? 1 : 0);
        }

        if (values.contains(1)) return "satisfied";
        else {
            long count = values.stream().filter(value -> value == 0).count();
            if (count == values.size()) return "unsatisfied";
            if (count == values.size() - 1) return "unit";

        }

        return "unresolved";
    }

    private boolean allVariablesAssigned() {
        return formula.getVariables().size() == model.keySet().size();
    }

    private Clause resolve(Clause a, Clause b, int x) {
        Set<Literal> resultSet = new HashSet<>(a);
        resultSet.addAll(b);

        resultSet.remove(new Literal(x, true));
        resultSet.remove(new Literal(x, false));

        return new Clause(resultSet.stream().toList());
    }

    private ConflictAnalysisResult conflictAnalysis(Clause conflictClause) {
        //System.out.println("La conflict clause corrente: " + conflictClause);
        if (this.model.getDecisionLevel() == 0)     // if the conflict is at level 0, return unsat
            return new ConflictAnalysisResult(-1, null);

        // literals that are falsified at the current decision level
        ArrayList<Literal> literals = conflictClause
                .stream()
                .filter(literal -> model.get(literal.getVariable()).getDecisionLevel() == model.getDecisionLevel())
                .collect(Collectors.toCollection(ArrayList<Literal>::new));


        // explain until we are left with one falsified literal
        while (literals.size() != 1) {
            //System.out.println("\tExplaining " + conflictClause);

            // find implied literals between the literals
            literals = (ArrayList<Literal>) literals
                    .stream()
                    .filter(lit -> model.get(lit.getVariable()).getJustification() != null)
                    .collect(Collectors.toCollection(ArrayList<Literal>::new));

            //System.out.println("\tNow printing the list of implied literals for the conflict clause");
            //System.out.print("\t");
            literals.forEach(l -> System.out.print(l + " "));
            System.out.println();

            Literal l = literals.iterator().next();
            Clause justification = model.get(l.getVariable()).getJustification();
            //System.out.println("\tLiteral chosen: " + l + " justification: " + justification);
            //System.out.println("\n");

            // obtain another conflict clause from the previous
            conflictClause = resolve(conflictClause, justification, l.getVariable());

            // search for the literals that are initialized at the current decision level in the conflict clause
            literals = conflictClause
                    .stream()
                    .filter(literal -> model.get(literal.getVariable()).getDecisionLevel() == model.getDecisionLevel())
                    .collect(Collectors.toCollection(ArrayList<Literal>::new));

            //System.out.println("\t\tAfter explain, the conflict clause is: " + conflictClause + " these are the literals");
            //System.out.print("\t\t");
            //literals.forEach(literal -> System.out.print(" " + literal));
            //System.out.println();

        }

        HashSet<Integer> decisionLevels = conflictClause
                .stream()
                .map(literal -> model.get(literal.getVariable()).getDecisionLevel())
                .collect(Collectors.toCollection(HashSet<Integer>::new));

        ArrayList<Integer> sortedDecisionLevels = decisionLevels
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(ArrayList<Integer>::new));

        if (sortedDecisionLevels.size() <= 1) return new ConflictAnalysisResult(0, conflictClause);

        return new ConflictAnalysisResult(sortedDecisionLevels.get(sortedDecisionLevels.size()-2), conflictClause);
    }

    private void addLearntClause (Clause c) {
        this.formula.add(c);
    }

    private void backtrack(int level) {
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
    }
}
