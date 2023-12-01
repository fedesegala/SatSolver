package SubSolver;

public class Atom {
    private final int name;
    private int assignedValue;  // -1 : not assigned, 0: false, 1: true
    private boolean watched;

    public Atom(int name) {
        this.name = name;
        this.assignedValue = -1;
        this.watched = false;
    }

    // getter
    public int getName() {
        return this.name;
    }
    public int getAssignedValue() { return this.assignedValue; }
    public boolean isWatched() {
        return watched;
    }

    // setter
    public void setAssignedValue(int assignedValue) {
        this.assignedValue = assignedValue;
    }
    public void setWatched() {
        this.watched = true;
    }
    public void setUnwatched() {
        this.watched = false;
    }

    public String toString() {
        return String.valueOf(this.name);
    }

}
