package SubSolver;
import java.math.*;
import java.util.Objects;

public class Literal {
    private final int index;            // atom
    private final boolean polarity;     // polarity of the atom

    public Literal (int value) {
        this.polarity = value >= 0;
        this.index = Math.abs(value);
    }

    public int getIndex() {
        return index;
    }
    public boolean getPolarity() {
        return this.polarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal literal)) return false;
        return getIndex() == literal.getIndex() && getPolarity() == literal.getPolarity();
    }

    @Override
    public String toString() {
        return "Literal{" +
                "index=" + index +
                ", polarity=" + polarity +
                '}';
    }
}
