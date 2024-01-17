import SatSolver.EfficientSolver;
import SatSolver.Model.Formula;
import SatSolver.Parser;
import SatSolver.SatSolver;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        fun2();
    }

    private static void fun2() {
        String fileName = "unsat3"  ;
        Parser p = new Parser(fileName);

        System.out.println(p.getPath());

        Formula f = p.getFormula();

        EfficientSolver s = new EfficientSolver(f, p.getFilename());

        System.out.println(new Date());

        s.cdclSolve();

        System.out.println(new Date());

        System.out.println("The proof has been exported so it can be converted to image");
        System.out.println("\tIt is " + s.getProof().getnLines() + " lines long. Are you sure you want the conversion" +
                " to happen? [NOTE: it may take long] \n y/n");
        Scanner sc = new Scanner(System.in);
        String choice = sc.nextLine();

        if (choice.equals("y") || choice.equals("Y")) {
            try {
                String command = "python textToProof.py " + fileName + "_proof";
                ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));

                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("The proof has been saved to " + fileName + "_proof.png");
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Fine! You can take a look at the file " + fileName + "_proof.txt if you wish!");
        }
    }
}
