package SatSolver;

public class ConflictAnalysisResult {
    private int decisionLevel;
    private Clause learntClause;

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
