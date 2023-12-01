package SubSolver;

import java.util.Objects;

public class Atom {
    private final int name;
    private int assignedValue;  // -1 : not assigned, 0: false, 1: true
    private boolean watched;

    public Atom(int name) {
        this.name = name;
        this.assignedValue = -1;
        this.watched = false;
    }
    public Atom(int name, int value, boolean watched) {
        this.name = name;
        this.assignedValue = value;
        this.watched = watched;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Atom atom)) return false;
        return getName() == atom.getName();
    }

    public String toString() {
        return String.valueOf(this.name);
    }


}
