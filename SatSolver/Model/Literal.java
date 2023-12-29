package SatSolver.Model;

import java.util.Objects;

public class Literal implements Comparable<Literal> {
    private int variable;
    private boolean negated;

    public Literal (int variable, boolean negation) {
        this.variable = variable;
        this.negated = negation;
    }

    public int getVariable() {
        return variable;
    }
    public boolean isNegated() {
        return negated;
    }

    public Literal negate() {
        return new Literal(this.variable, !this.negated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal literal)) return false;
        return getVariable() == literal.getVariable() && isNegated() == literal.isNegated();
    }

    @Override
    public int compareTo(Literal o) {
        return Integer.compare(this.variable, o.getVariable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVariable(), isNegated());
    }

    public String toString() {
        if (this.negated)
            return "¬" + this.variable;
        else
            return Integer.toString(this.variable);
    }
}
