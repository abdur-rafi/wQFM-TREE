package src.ConsensusTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConsensusTree {

    static String filepath="./input/ConsensusTrees/tree1.txt.raxml.consensustreemre";

    public static String readAndPrintFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                return line;
            }
        }

        return "";
    }

    public static void main(String[] args) {

        String consensustree;

        try {
            consensustree=readAndPrintFile(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        NewickTree newickTree = NewickTree.readNewickFormat(consensustree);
        System.out.println(newickTree.root.children.size());
        System.out.println(newickTree.toString());
    }




}
