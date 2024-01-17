package SatSolver;

import SatSolver.Model.Clause;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Proof extends JFrame{
    public class Node {
        private final Clause clause;
        private Node leftParent;
        private Node rightParent;

        public Node(Clause clause) {
            this.clause = clause;
        }
        public Node(Clause clause, Node left, Node right) {
            this.clause = clause;
            this.leftParent = left;
            this.rightParent = right;
        }
        public Node(Clause clause, Clause left, Clause right) {
            this(clause, new Node(left), new Node(right));
        }

        public void setLeftParent(Node leftParent) {
            this.leftParent = leftParent;
        }
        public void setRightParent(Node rightParent) {
            this.rightParent = rightParent;
        }

        public Clause getClause() {
            return clause;
        }
        public Node getLeftParent() {
            return leftParent;
        }
        public Node getRightParent() {
            return rightParent;
        }
    }

    private Node root;
    private EfficientSolver s;
    private int nLines = 0;

    public Proof (Clause root, EfficientSolver s) {
        this.root = new Node(root);
        this.s = s;


        // clear proof file if previously existing
        try (FileWriter fileWriter = new FileWriter(s.getFilename() + "_proof.txt", false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            // Clear the proof file
            bufferedWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.proofConstruction(this.root);
    }

    private void proofConstruction(Node root) {
        //this.proof.addNode(root.getClause().toString());
        if (s.getGeneratedClauses().containsKey(root.getClause())) {
            Clause left = s.getGeneratedClauses().get(root.getClause()).get(0);
            Clause right = s.getGeneratedClauses().get(root.getClause()).get(1);

            root.setLeftParent(new Node(left));
            root.setRightParent(new Node(right));


            String contentToAppend = root.getClause() + ":" + left + "\n" + root.getClause() + ":" + right + "\n";
            this.nLines += 2;

            try (FileWriter fileWriter = new FileWriter(s.getFilename() + "_proof.txt", true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                // Append the content to the file
                bufferedWriter.write(contentToAppend);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.proofConstruction(new Node(left));
            this.proofConstruction(new Node(right));
        }
    }

    public int getnLines() {
        return nLines;
    }
}
