import java.util.*;
import java.util.concurrent.ForkJoinPool;
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

public class RuletoTex {
    public static void main(String[] args) throws Exception {
    ArrayList<Rule> rules = new ArrayList<>();
        try{
            //ルールを読み込み
            File f = new File("src/completerules.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));
            FileWriter fw = new FileWriter("src/rulestex.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            String line = br.readLine();
            int counter = 1;
            while(line != null) {
                String[] parts = line.split(";");
                String[] v = parts[0].split(",");
                int[] view = Stream.of(v).mapToInt(Integer::parseInt).toArray();
                String[] care = parts[1].split(",");
                int st = Integer.parseInt(parts[2]);
                int pair = Integer.parseInt(parts[3]);
                int dest = Integer.parseInt(parts[4]);
                
                //tex用テキストを出力
                String output = "\\STATE Rule" + counter + "  :: $pair=l_" + pair + "\\land ";
                //条件式の部分をまとめてラベルが小さい順に並び替え
                int[][] num = new int[3][7];
                for(int i=0; i<view.length; i++) {
                    if(care[i].equals("eq")) {
                        num[view[i]][i] = 1;
                    }
                }

                for(int i=0; i<3; i++) {
                    int flag=0;
                    for(int j=0; j<7; j++) {
                        if(num[i][j] == 1) {
                            output += "occupy(l_" + j +")=";
                            flag++;
                        }
                    }
                    if(flag > 0) {
                        output += i + " \\land ";
                    }
                }

                output = output.substring(0, output.length()-6);

                output += "\\longrightarrow move(l_" + dest + ")$\\\\";
                //System.out.println(output);
                pw.println(output);
                line = br.readLine();
                counter++;
            }
            br.close();
            pw.close();
        }catch(Exception e) {
            System.out.println(e);
        }
    }
}
