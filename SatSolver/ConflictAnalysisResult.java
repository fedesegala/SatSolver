package SatSolver;

import SatSolver.Model.Clause;

public class ConflictAnalysisResult {
    protected int decisionLevel;
    protected Clause learntClause;

    public ConflictAnalysisResult(int decisionLevel, Clause learntClause) {
        this.learntClause = learntClause;
        this.decisionLevel = decisionLevel;
    }

    public int getDecisionLevel() {
        return decisionLevel;
    }
    public Clause getLearntClause() {
        return learntClause;
    }

    public void setDecisionLevel(int decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

}
