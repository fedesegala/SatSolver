package SatSolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Path path;
    private final String fileContent;
    private final Formula formula;


    public Parser(String path) {
        this.fileContent = initFileContent(path);
        this.formula = initFormula();
    }

    private String initFileContent(String path) {
        String output = null;
        try {
            // Use Paths.get() to obtain a Path object
            this.path = Paths.get(path);

            // Use Files.readAllBytes() to read all bytes from the file
            byte[] fileBytes = Files.readAllBytes(this.path);

            // Convert the bytes to a string using StandardCharsets.UTF_8
            output = new String(fileBytes, StandardCharsets.UTF_8);

            return output;

        } catch (IOException e) {
            e.printStackTrace();

            return output;
        }
    }
    private Formula initFormula() {
        if (this.fileContent != null) {
            String [] clauses = fileContent.split(" 0");

            List<Clause> c = new ArrayList<>();

            for (String clause : clauses) {
                List<Literal> l = new ArrayList<>();
                for (String literal : clause.split("\\s+")) {
                    if (!literal.isEmpty()) {
                        int value = Integer.parseInt(literal);
                        boolean negation = value < 0;
                        l.add(new Literal((negation)? -value : value, negation));
                    }
                }
                Clause toAdd = new Clause(l);
                c.add(toAdd);
            }

            Formula output = new Formula(c);
            return output;
        }

        return null;
    }

    public Formula getFormula() {
        return formula;
    }
    public Path getPath() {
        return this.path;
    }
}
