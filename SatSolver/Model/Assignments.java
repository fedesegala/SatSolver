package SatSolver.Model;

import java.util.HashMap;

public class Assignments extends HashMap<Integer, Assignment> {
    private int decisionLevel;
    private int arrivalOrder = 0;   // used for explain

    public Assignments() {
        super();
        this.decisionLevel = 0;
    }

    public int getDecisionLevel() {
        return decisionLevel;
    }

    public void setDecisionLevel(int decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    /**
     * Given a literal l, it returns the value of the literal evaluated under the current assignment
     *
     * @param l: a Literal l
     * @return the value of l under the current assignment
     */
    public boolean value(Literal l) {
        if (l.isNegated()) {
            return ! this.get(l.getVariable()).getValue();
        } else {
            return this.get(l.getVariable()).getValue();
        }
    }

    public void assign(int variable, boolean value) {
        this.arrivalOrder = 0;
        this.put(variable, new Assignment(value, null, this.decisionLevel, this.arrivalOrder));
    }
    public void assign(int variable, boolean value, Clause justification) {
        //if (!this.containsKey(variable))
            this.arrivalOrder ++;
        this.put(variable, new Assignment(value, justification, this.decisionLevel, this.arrivalOrder));
    }

    public void unassign(int variable) {
        this.remove(variable);
    }

    public boolean satisfy(Formula f) {
        for (Clause clause : f) {
            boolean found = false;
            for (Literal l : clause) {
                if (this.value(l)) found = true;
            }
            if (!found) return false;
        }

        return true;
    }

    public void setArrivalOrder(int arrivalOrder) {
        this.arrivalOrder = arrivalOrder;
    }
}
