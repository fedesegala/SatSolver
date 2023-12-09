package SubSolver;

public class ClauseLiteral extends Literal{
    private int assignedValue;  // -1: unassigned, 0: false, 1: true
    public boolean watched;     // used for the watched literal technique
    private Clause justification;
    private boolean decision;

    public ClauseLiteral(int value) {
        super(value);
        this.assignedValue = -1;
    }

    public void setAssignedValue(int assignedValue) {
        this.assignedValue = assignedValue;
    }
    public void setWatched(boolean watched) {
        this.watched = watched;
    }
    public void setJustification(Clause justification) {
        this.justification = justification;
    }

    public int getAssignedValue() {
        return assignedValue;
    }

    @Override
    public String toString() {
        return "ClauseLiteral{" +
                "assignedValue=" + assignedValue +
                ", polarity=" + super.getPolarity() +
                ", index=" + super.getIndex() +
                '}';
    }
}
