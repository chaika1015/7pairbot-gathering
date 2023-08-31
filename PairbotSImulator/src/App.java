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



public class App {
    public static void main(String[] args) throws Exception {
        ArrayList<Rule> rules = new ArrayList<>();
        ArrayList<Point[]> initStates = new ArrayList<>();
        try{
            //ルールを読み込み
            File f = new File("src/completerules.txt");
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

            //初期状況を読み込み
            f = new File("src/initial.txt");
            br = new BufferedReader(new FileReader(f));
            line = br.readLine();
            while(line != null) {
                String[] points = line.split(";");
                Point[] initPoint = new Point[7];
                for(int i=0; i<7; i++) {
                    String[] point = points[i].split(",");
                    int x = Integer.parseInt(point[0]);
                    int y = Integer.parseInt(point[1]);
                    initPoint[i] = new Point(x,y);
                }
                initStates.add(initPoint);
                line = br.readLine();
            }
            br.close();
                
        }catch(Exception e) {
            System.out.println(e);
        }

        //読み込んだ初期状況すべてを検証
        long startTime = System.currentTimeMillis();
        System.out.println(initStates.size());
        int icount = 1;
        int scount = 0;
        ArrayList<Integer> fstate = new ArrayList<>();
        for(Point[] init : initStates) {
            //pairbot7ペアを生成
            ArrayList<Pairbot> pairbots = new ArrayList<>();
            Map<Point, Integer> exist = new HashMap<>();//ある座標にいるロボットの台数
            for(int i=0; i<7; i++) {
                pairbots.add(new Pairbot((int)init[i].getX(), (int)init[i].getY(), rules));
                exist.put(init[i], 2);
            }

            int finish = 1;
            int step = 0;
            int triple = 0;
            int sim = 0;
            while(finish != 0 && step<200) {
                finish = 0;
                //Look
                for(int i=0; i<7; i++) {
                    int[] view = new int[7];
                    int checkA = 0;
                    int checkB = 1;
                    //AのロボットがLookする
                    Point[] around = pairbots.get(i).aroundPointA();
                    for(int j=0; j<7; j++) {
                        if(exist.containsKey(around[j])) {
                            view[j] = exist.get(around[j]);
                            checkA++;
                        }else {
                            view[j] = 0;
                        }
                    }
                    pairbots.get(i).LookA(view);
                    //Longの場合はBもLookする
                    if(!pairbots.get(i).isShort()) {
                        around = pairbots.get(i).aroundPointB();
                        checkB = 0;
                        for(int j=0; j<7; j++) {
                            if(exist.containsKey(around[j])) {
                                view[j] = exist.get(around[j]);
                                checkB++;
                            }else {
                                view[j] = 0;                            }
                        }
                    }
                    pairbots.get(i).LookB(view);
                        if(checkA == 1 && checkB ==1) {
                        System.out.println("孤立 " + i + " " + step);
                    }
                }

                //Compute
                for(int i=0; i<7; i++) {
                    // System.out.println("ロボット"+(i+1));
                    int flagA = 0;
                    int flagB = 0;
                    //AのロボットがComputeする
                    flagA = pairbots.get(i).computeA();
                    //Longの場合はBもComputeする
                    if(!pairbots.get(i).isShort()) {
                        flagB = pairbots.get(i).computeB();
                    }

                    if(flagA + flagB > 1) {
                        sim = 1;
                        System.out.println("2台同時に動きました");
                        break;
                    }
                }

                if(sim == 1) {
                    break;
                }

                //Move
                for(int i=0; i<7; i++) {
                    //AのロボットがMoveする
                    finish += pairbots.get(i).moveA();  //Moveしたらfinishがインクリメントされる,0のままなら終了
                    //Longの場合はBもMoveする
                    if(!pairbots.get(i).isShort()) {
                        finish += pairbots.get(i).moveB();
                    }
                }
                //existを更新
                exist = new HashMap<>();
                for (int i=0; i<7; i++) {
                    if(pairbots.get(i).updatePos(exist) > 2) {
                        triple = 1;
                        System.out.println("3台重なりました");
                        break;
                    };
                }
                step++;
                if(triple == 1) {
                    System.out.println("ループを抜けます");
                    break;
                }

                // System.out.println("ステップ:" +step);
                // for(Map.Entry<Point, Integer> entry : exist.entrySet()){
		        //     System.out.println(entry.getKey() + ":" + entry.getValue());
		        // }
            }

            //成功判定
            boolean success = true;
            for(int i=0; i<7; i++) { //全員Shortか判定
                if(!pairbots.get(i).isShort()) {//Longがあれば失敗
                    //System.out.println("集合失敗");
                    success = false;
                }
            }

            if(success) {
                for(int i=0; i<7; i++) {
                    Point[] around = pairbots.get(i).aroundPointA();
                    int count = 0;
                    for(int j=0; j<7; j++) {  //ペアの周囲7点にすべてロボットが7台存在していたら終了
                        if(exist.containsKey(around[j])) {
                            if(exist.get(around[j]) == 2) {
                                count++;
                            }
                        }
                    }
                    if(count == 7) {
                        success = true;
                        break;
                    }else {
                        success = false;
                    }
                } 
            }

            if(triple == 1) {
                success = false;
            }
            if(success) {
                //System.out.println(icount + ":集合成功　" + step);
                scount++;
            }else {
                //System.out.println(icount + ":集合失敗　" + step);
                fstate.add(icount);
            }
            icount++;
        }
        icount--;
        System.out.println(scount + "/" + icount);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime + "ms");
        // for (int i = 0; i < 10; i++) {
        //     System.out.println(fstate.get(i));

        // }
        try {
            FileWriter fw = new FileWriter("PairbotSImulator/src/result.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            for (int i=0; i<fstate.size(); i++) {
                pw.print(fstate.get(i) + ": ");
                Point[] p = initStates.get(fstate.get(i)-1);
                for(int j=0; j<7; j++) {
                    pw.print("(" + (int)p[j].getX() + "," + (int)p[j].getY() + ") ");
                }
                pw.println();
            }
            pw.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
}
