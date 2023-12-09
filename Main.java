import SubSolver.Literal;
import SubSolver.ClauseLiteral;
import SubSolver.Clause;
import SubSolver.SetOfClauses;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String filePath = "prova.cnf";
        String fileContent = null;
        try {
            // Use Paths.get() to obtain a Path object
            Path path = Paths.get(filePath);

            // Use Files.readAllBytes() to read all bytes from the file
            byte[] fileBytes = Files.readAllBytes(path);

            // Convert the bytes to a string using StandardCharsets.UTF_8
            fileContent = new String(fileBytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileContent != null) {
            String [] clauses = fileContent.split(" 0");

            ArrayList<Clause> c = new ArrayList<>();
            for (String clause : clauses) {
                ArrayList<ClauseLiteral> literals = new ArrayList<ClauseLiteral>();

                for (String literal : clause.split("\\s+")) {
                    if (!literal.isEmpty())
                        literals.add(new ClauseLiteral(Integer.parseInt(literal)));
                }

                c.add(new Clause(literals));
            }

            SetOfClauses S = new SetOfClauses(c);

            for (Clause clause : S.getClauses()) {
                for (ClauseLiteral l : clause.getLiterals()) {
                    if (l.getAssignedValue() != -1) {
                        System.out.println(l);
                    }
                }
            }
        }

    }
}
