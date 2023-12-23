package SatSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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

    public Literal decide1() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Che letterale vuoi decidere?");

        int value = sc.nextInt();
        if (value < 0) return new Literal(Math.abs(value), true);
        return new Literal(value, false);
    }
    public Literal decide2() {
        Integer [] unassignedVariables = this.formula.getVariables().stream()
                .filter(variable -> !model.containsKey(variable)).toArray(Integer[]::new);
        int variable = unassignedVariables[randomGenerator.nextInt(unassignedVariables.length)];
        boolean value = (randomGenerator.nextBoolean());

        return new Literal(variable, !value);
    }

    public Literal decide() {
        List<Literal> unassignedLiterals = new ArrayList<>();

        for (Clause c : this.formula) {
            for (Literal l : c) {
                if (!this.model.containsKey(l.getVariable()))unassignedLiterals.add(l);
            }
        }

        return unassignedLiterals.get(randomGenerator.nextInt(unassignedLiterals.size()));
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }
}
