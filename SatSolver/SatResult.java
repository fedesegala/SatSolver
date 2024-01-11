package SatSolver;

import SatSolver.Model.Clause;

public class SatResult {
    protected Clause lastConflict;
    protected boolean sat;

    public SatResult(Clause c, boolean b) {
        this.lastConflict = c;
        this.sat = b;
    }
}
