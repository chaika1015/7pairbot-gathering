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

// import org.json.JSONArray;
// import org.json.JSONObject;

public class readJson {
    public readJson() {
    }

    // JSONからルール群を読み込む
    private static String readRules() {
        String fileString = "";
        try {
            File file = new File("src/main/java/app/SimulationRules.json");
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

    public static List<Algorithm> vanillaDecodeJSON() {
        List<Algorithm> rules = new ArrayList<>();
        String fileString = readRules();
        String trimingString = fileString.replaceAll(" |　|\t|\n|\"", "");
        String[] splitFileString = trimingString.split("\\[\\{|:\\[|:|,|\\],|\\},\\{|\\}\\]", 0);
        int i = 1;
        while (true) {
            int parentId = 0;
            int childId = 0;
            boolean isShort = true;
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
            isShort = splitFileString[i++].equals("true")?true:false;
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
            rules.add(new Algorithm(parentId, childId, isShort, pairPointId, occupy, conditions, nextPointId));
            if (i >= splitFileString.length) {
                break;
            }
        }
        return rules;
    }

    // public static List<Algorithm> decodeJSON() {
    //     String fileString = readRules();
    //     List<Algorithm> rules = new ArrayList<Algorithm>();
    //     JSONArray arr = new JSONArray(fileString);
    //     for (Object str: arr) {
    //         JSONObject obj = new JSONObject((String) str.toString());
    //         int[] occupy = new int[7];
    //         JSONArray arr2 = new JSONArray((String)(obj.get("occupy").toString()));
    //         int i=0;
    //         for (Object str2: arr2) {
    //             occupy[i++] = Integer.parseInt(str2.toString());
    //         }
    //         String[] conditions = new String[7];
    //         JSONArray arr3 = new JSONArray((String)(obj.get("conditions").toString()));
    //         int j=0;
    //         for (Object str3: arr3) {
    //             conditions[j++] = str3.toString();
    //         }
    //         rules.add(new Algorithm((int)obj.get("parentId"), (int)obj.get("childId"), (boolean)obj.get("isShort"), (int)obj.get("pairPointId"), occupy, conditions, (int)obj.get("nextPointId")));
    //     }
    //     return rules;
    // }

    public static void printRules(Algorithm[] rules) {
        System.out.println("[");
        for (int i=0; i<rules.length; i++) {
            System.out.println(rules[i].generateJSONFormat() + (i < rules.length - 1? ",": ""));
        }
        System.out.println("]");
    }

    public static void writeRules(Algorithm[] rules) {
        try {
            File file = new File("src/main/java/app/SimulationRules.json");
            if (!file.exists() || !file.isFile() || !file.canWrite()) {
                System.out.println("file error!\nwe could not write rules.");
                return;
            }
            // FileWriter fileWriter = new FileWriter(file);
            // fileWriter.write("[\n");
            // for (int i=0; i<rules.length; i++) {
            //     fileWriter.write(rules[i].generateJSONFormat() + (i < rules.length - 1? ",\n": ""));
            // }
            // fileWriter.write("\n]");
            // fileWriter.close();

            PrintWriter pw = new PrintWriter(
                             new BufferedWriter(
                             new OutputStreamWriter(
                             new FileOutputStream(file), "UTF-8")));
            pw.print("[\n");
            for (int i=0; i<rules.length; i++) {
                pw.print(rules[i].generateJSONFormat() + (i < rules.length - 1? ",\n": ""));
            }
            pw.print("\n]");
            pw.close();
        }  
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
