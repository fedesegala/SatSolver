package SatSolver;

import SatSolver.Model.Clause;

public class PropagationResult {
    protected String reason;
    protected Clause clause;

    public PropagationResult(String reason, Clause clause) {
        this.reason = reason;
        this.clause = clause;
    }

}
