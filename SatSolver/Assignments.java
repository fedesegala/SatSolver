package SatSolver;

import java.util.HashMap;

public class Assignments extends HashMap<Integer, Assignment> {
    private int decisionLevel;

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
        this.put(variable, new Assignment(value, this.decisionLevel));
    }
    public void assign(int variable, boolean value, Clause justification) {
        this.put(variable, new Assignment(value, justification, this.decisionLevel));
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

}
