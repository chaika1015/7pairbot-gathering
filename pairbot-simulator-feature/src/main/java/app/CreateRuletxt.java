package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateRuletxt {
    public static void main(String[] args) {
        List<Rule> rules = vanillaDecodeJSON();
        try {
            FileWriter fw = new FileWriter("C:/Users/taguy/OneDrive - 名古屋工業大学/研究室/pairbot-simulator-feature/src/main/java/app/rules3.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            for (int i=0; i<rules.size(); i++) {
                for (int j = 0; j < 7; j++) {
                    pw.print(rules.get(i).view[j]);
                    if(j<6) {
                        pw.print(",");
                    }else {
                        pw.print(";");
                    }
                }
                for (int j = 0; j < 7; j++) {
                    pw.print(rules.get(i).care[j]);
                    if(j<6) {
                        pw.print(",");
                    }else {
                        pw.print(";");
                    }
                }
                pw.print(rules.get(i).st + ";");
                pw.print(rules.get(i).pairPos + ";");
                pw.println(rules.get(i).dest);
            }
            pw.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
        System.out.println("complete");

    }

    private static String readRules() {
        String fileString = "";
        try {
            File file = new File("C:/Users/taguy/OneDrive - 名古屋工業大学/研究室/pairbot-simulator-feature/src/main/java/app/SimulationRules.json");
            FileReader filereader = new FileReader(file);

            fileString = "";
            int ch;
            while ((ch = filereader.read()) != -1) {
                fileString += (char)ch;
            }
            // System.out.println(fileString);
            filereader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return fileString;
    }

    public static List<Rule> vanillaDecodeJSON() {
        List<Rule> rules = new ArrayList<>();
        String fileString = readRules();
        String trimingString = fileString.replaceAll(" |　|\t|\n|\"", "");
        String[] splitFileString = trimingString.split("\\[\\{|:\\[|:|,|\\],|\\},\\{|\\}\\]", 0);
        int i = 1;
        while (true) {
            int parentId = 0;
            int childId = 0;
            int isShort = 0;
            int pairPointId = 0;
            int[] occupy = new int[7];
            String[] conditions = new String[7];
            int nextPointId = 0;
            i++;    // parentId
            // System.out.println(splitFileString[i]);
            try {
                parentId = Integer.parseInt(splitFileString[i++]);
            }
            catch (NumberFormatException e) {
                parentId = 0;
            }
            i++;    // childId
            // System.out.println(splitFileString[i]);
            try {
                childId = Integer.parseInt(splitFileString[i++]);
            }
            catch (NumberFormatException e) {
                childId = 0;
            }
            i++;    // isShort
            // System.out.println(splitFileString[i]);
            if(splitFileString[i++].equals("true")) {
                isShort = 1;
            }else {
                isShort = 0;
            }
            i++;    // pairPointId
            // System.out.println(splitFileString[i]);
            try {
                pairPointId = Integer.parseInt(splitFileString[i++]);
            }
            catch (NumberFormatException e) {
                pairPointId = 0;
            }
            i++;    // occupy
            for (int j=0;j<7;j++) {
                // System.out.println(splitFileString[i]);
                try {
                    occupy[j] = Integer.parseInt(splitFileString[i++]);
                }
                catch (NumberFormatException e) {
                    occupy[j] = 0;
                }
            }
            i++;    // conditions
            for (int j=0;j<7;j++) {
                // System.out.println(splitFileString[i]);
                conditions[j] = splitFileString[i++];
            }
            i++;    // nextPointId
            // System.out.println(">" + splitFileString[i] + "<");
            try {
                nextPointId = Integer.parseInt(splitFileString[i++]);
            }
            catch (NumberFormatException e) {
                nextPointId = 0;
            }
            rules.add(new Rule(occupy, conditions, isShort, pairPointId, nextPointId));
            if (i >= splitFileString.length) {
                break;
            }
        }
        return rules;
    }
}
