package SatSolver;

import java.util.ArrayList;
import java.util.Random;
import java.util.random.*;

public class Picker {
    private Formula formula;
    private Assignments model;
    private final Random randomGenerator = new Random();

    public Picker(Formula formula, Assignments model) {
        this.formula = formula;
        this.model = model;
    }

    public void setModel(Assignments model) {
        this.model = model;
    }

    public Literal decide() {
        Integer [] unassignedVariables = this.formula.getVariables().stream()
                .filter(variable -> !model.containsKey(variable)).toArray(Integer[]::new);
        int variable = unassignedVariables[randomGenerator.nextInt(unassignedVariables.length)];
        boolean value = (randomGenerator.nextBoolean());

        return new Literal(variable, !value);
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }
}
