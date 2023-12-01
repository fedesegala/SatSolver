package SubSolver;

public class Atom {
    private final String name;
    private boolean polarity;
    private int assignedValue;  // -1 : not assigned, 0: false, 1: true

    public Atom(String name, boolean polarity) {
        this.name = name;
        this.polarity = polarity;
        this.assignedValue = -1;
    }

    public String getName() {
        return this.name;
    }
    public boolean getPolarity() {
        return this.polarity;
    }
    public int getAssignedValue() { return this.assignedValue; }

    public void setAssignedValue(int assignedValue) {
        this.assignedValue = assignedValue;
    }

    public String toString() {
        return this.name;
    }

}
