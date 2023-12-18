package SatSolver;

import java.util.HashMap;

public class Assignment {
    private boolean value;
    private Clause justification;
    private int decisionLevel;

    // used when we propagate, hence we save the propagation of the justification
    public Assignment(boolean value, Clause justification, int decisionLevel) {
        this.value = value;
        this.justification = justification;
        this.decisionLevel = decisionLevel;
    }

    // used when we need to perform a decision, hence we do not save any justification
    public Assignment(boolean value, int decisionLevel) {
        this.value = value;
        this.decisionLevel = decisionLevel;
        this.justification = null;
    }

    public int getDecisionLevel() {
        return this.decisionLevel;
    }
    public Clause getJustification() {
        return this.justification;
    }
    public boolean getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "value=" + value +
                ", justification=" + justification +
                ", decisionLevel=" + decisionLevel +
                '}';
    }
}
