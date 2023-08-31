import java.util.*;
import java.util.stream.*;
import java.io.BufferedReader;
import java.nio.file.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;

public class Kirarity {
    public static void main(String[] args) {
        ArrayList<Rule> rules = new ArrayList<>();
        try{
            //ルールを読み込み
            File f = new File("src/rules_test.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while(line != null) {
                String[] parts = line.split(";");
                String[] v = parts[0].split(",");
                int[] view = Stream.of(v).mapToInt(Integer::parseInt).toArray();
                String[] care = parts[1].split(",");
                int st = Integer.parseInt(parts[2]);
                int pair = Integer.parseInt(parts[3]);
                int dest = Integer.parseInt(parts[4]);
                Rule rule = new Rule(view,care,st,pair,dest);
                rules.add(rule);
                line = br.readLine();
            }
            br.close();
        }catch(Exception e) {
            System.out.println(e);
        }

        ArrayList<Rule> newRules = new ArrayList<>();

        for(int i=0; i<rules.size(); i++) {
            newRules.add(rules.get(i));
            for(int j=0; j<5; j++) {
                newRules.add(rules.get(i).rotate(j+1));
            }
        }

        try {
            FileWriter fw = new FileWriter("src/rules4.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            for (int i=0; i<newRules.size(); i++) {
                for (int j = 0; j < 7; j++) {
                    pw.print(newRules.get(i).getView()[j]);
                    if(j<6) {
                        pw.print(",");
                    }else {
                        pw.print(";");
                    }
                }
                for (int j = 0; j < 7; j++) {
                    pw.print(newRules.get(i).getCare()[j]);
                    if(j<6) {
                        pw.print(",");
                    }else {
                        pw.print(";");
                    }
                }
                pw.print(newRules.get(i).GetSt() + ";");
                pw.print(newRules.get(i).getPairPos() + ";");
                pw.println(newRules.get(i).getDest());
            }
            pw.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
        System.out.println("complete");
    }
}
