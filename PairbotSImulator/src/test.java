import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.*;
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

public class test {
    public static void main(String[] args) {
        ArrayList<Point> tGrid = new ArrayList<>();//37点
        ArrayList<Point[]> initState = new ArrayList<>();//初期状況すべて
        Map<Point, Integer> exist = new HashMap<>();
        ArrayList<Rule> rules = new ArrayList<>();
        ArrayList<Point[]> initStates = new ArrayList<>();
        try{
            //ルールを読み込み
            File f = new File("src/rules.txt");
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
            System.out.println(line);
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

        Pairbot pairbot = new Pairbot(0, 0, rules);
        int[] view = {0,2,0,0,0,0,0};
        pairbot.LookA(view);
        pairbot.computeA();

        String[] care = {"pass","eq","eq","eq","eq","eq","eq"};

        System.out.println(care[1].equals("eq"));


        Point[] p = new Point[7];
        p[0] = new Point(0,0);
        p[1] = new Point(0,1);
        p[2] = new Point(1,-1);
        p[3] = new Point(1,0);
        p[4] = new Point(1,1);
        p[5] = new Point(2,-1);
        p[6] = new Point(2,0);

        for (int i = 0; i < p.length; i++) {
            exist.put(p[i],2);
        }

        System.out.println(exist.containsKey(new Point(2,1)));
    }

    static ArrayList<Point> aroundP(Point[] points, int index, ArrayList<Point> plane) {
        ArrayList<Point> ap = new ArrayList<>();
        ArrayList<Point> p = new ArrayList<>();
        for(int i=0; i<index; i++) {
                p.add(new Point((int)points[i].getX(),(int)points[i].getY()));
        }

        //pointsと距離1の点を列挙
        for(int i=0; i<index; i++) {
            for(Point point : plane) {
                System.out.println(point.getX() +"," + point.getY());

                if(distance(points[i], point) == 1) {
                    System.out.println(point.getX() +"," + point.getY());
                    if(!ap.contains(point) && !p.contains(point)) { //apに含まれずpointsに含まれなければapに追加
                        ap.add(point);
                    }
                }
            }
        }
        return ap;
    }

    static double distance(Point u, Point v) {
        double d = 0;
        double a = (u.getX()-v.getX())*(u.getY()-v.getY());
        if(a>=0) {
            d = Math.abs(u.getX()-v.getX()) + Math.abs(u.getY()-v.getY());
            return d;
        }else {
            d = Math.abs(u.getX()-v.getX()) + Math.abs(u.getY()-v.getY()) - Math.min(Math.abs(u.getX()-v.getX()), Math.abs(u.getY()-v.getY()));
            return d;
        }
    }

    static Point[] localP(Point[] points) {
        ArrayList<Point> temp = new ArrayList<>();
        for(int i=0; i<7; i++) {
            temp.add(points[i]);
        }

        temp.sort(Comparator.comparing(Point::getX).thenComparing(Point::getY));
        Point[] result = new Point[7];
        for(int i=0; i<7; i++) {
            int x = (int)(temp.get(i).getX() - temp.get(0).getX());
            int y = (int)(temp.get(i).getY() - temp.get(0).getY());
            result[i] = new Point(x,y);
        }

        return result;
    }
}
