import SubSolver.Clause;
import SubSolver.Atom;
import SubSolver.SetOfClauses;
import SubSolver.TransitionSystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

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
            SetOfClauses clauses = new SetOfClauses(fileContent);
            TransitionSystem t = new TransitionSystem(clauses);

            clauses = t.getClauses();

            for (Clause c : clauses.getClauses()) {
                System.out.println(c.getAtoms().toString());
                for (Atom a : c.getAtoms()) {
                    if (a.isWatched()) {
                        System.out.println(a);
                    }
                }
            }
        }
    }
}
