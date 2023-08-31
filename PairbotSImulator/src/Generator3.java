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


public class Generator3 {
    static Point[][] rotate = {{new Point(1,0), new Point(1,-1), new Point(0,-1), new Point(-1,0), new Point(-1,1), new Point(0,1)},
                            {new Point(2,0), new Point(2,-2), new Point(0,-2), new Point(-2,0), new Point(-2,2), new Point(0,2)},
                            {new Point(3,0), new Point(3,-3), new Point(0,-3), new Point(-3,0), new Point(-3,3), new Point(0,3)},
                            {new Point(2,-1), new Point(1,-2), new Point(-1,-1), new Point(-2,1), new Point(-1,2), new Point(1,1)},
                            {new Point(3,-1), new Point(2,-3), new Point(-1,-2), new Point(-3,1), new Point(-2,3), new Point(1,2)},
                            {new Point(3,-2), new Point(1,-3), new Point(-2,-1), new Point(-3,2), new Point(-1,3), new Point(2,1)}};
    public static void main(String[] args) {
        ArrayList<Point> tGrid = new ArrayList<>();//37点
        ArrayList<Point[]> initState = new ArrayList<>();//初期状況すべて
        //37点をListに保存
        //x=0の座標を生成
        for(int i=-3; i<=3; i++) {
            tGrid.add(new Point(0,i));
        }
        //残りの座標を生成
        for(int i=1; i<=3; i++) {
            for(int j=-3; j<=3-i; j++) {
                tGrid.add(new Point(i,j));
                tGrid.add(new Point(-i,-j));
            }
        }
        tGrid.sort(Comparator.comparing(Point::getX).thenComparing(Point::getY));

        //37C7すべてを探索して連続のものを取得
        Point[] points = new Point[6];
        points[0] = new Point(0,0);
        tGrid.remove(points[0]);
        ArrayList<Point[]> local = new ArrayList<>();
        ArrayList<Point> around1 = aroundP(points, 1, tGrid);
        for(Point i : around1) {
            points[1] = i;
            ArrayList<Point> around2 = aroundP(points, 2, tGrid);
            for(Point j : around2) {
                points[2] = j;
                ArrayList<Point> around3 = aroundP(points, 3, tGrid);
                for(Point k : around3) {
                    points[3] = k;
                    ArrayList<Point>around4 = aroundP(points, 4, tGrid);
                    for(Point l : around4) {
                        points[4] = l;
                        ArrayList<Point> around5 = aroundP(points, 5, tGrid);
                        for(Point m : around5) {
                            points[5] = m;
                                if(!exist(points, local)) {
                                    Point[] lo = localP(points);
                                    local.add(lo);
                                    Point[] p = new Point[6];
                                    for(int o=0; o<6; o++) {
                                        p[o] = new Point((int)points[o].getX(),(int)points[o].getY());
                                    }
                                    initState.add(p);
                                }
                        }
                    }
                }
            }
        }

        System.out.println(initState.size());

        try {
            FileWriter fw = new FileWriter("src/initial3.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            for(Point[] p : initState) {
                for(int i=0; i<6; i++) {
                    pw.print((int)p[i].getX() + "," + (int)p[i].getY());
                    if(i != 5) {
                        pw.print(";");
                    }else {
                        pw.println();
                    }
                }
            }
            pw.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
    
    static ArrayList<Point> aroundP(Point[] points, int index, ArrayList<Point> plane) {
        ArrayList<Point> ap = new ArrayList<>();
        ArrayList<Point> p = new ArrayList<>();
        for(int i=0; i<index; i++) {
                p.add(points[i]);
        }

        //pointsと距離1の点を列挙
        for(int i=0; i<index; i++) {
            for(Point point : plane) {
                if(distance(points[i], point) == 1) {
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
        for (int i = 0; i < points.length; i++) {
            temp.add(points[i]);
        }

        temp.sort(Comparator.comparing(Point::getX).thenComparing(Point::getY));
        Point[] result = new Point[6];
        for(int i=0; i<6; i++) {
            int x = (int)(temp.get(i).getX() - temp.get(0).getX());
            int y = (int)(temp.get(i).getY() - temp.get(0).getY());
            result[i] = new Point(x,y);
        }

        return result;
    }

    static boolean exist(Point[] a, ArrayList<Point[]> list) {
        if(list.size() == 0) { return false;}
        for(int n=0; n<list.size(); n++) {
            //回転を含め同じ形があるかチェック
            Point[] temp = new Point[6];
            for(int r=0; r<6; r++) {
                for(int i=0; i<6; i++) {
                    if(a[i].equals(new Point(0,0))) {
                        temp[i] = new Point(0,0);
                    }else {
                        for(int j=0; j<6; j++) {
                            for(int k=0; k<6; k++) {
                                if(a[i].equals(rotate[j][k])) {
                                    temp[i] = new Point(rotate[j][(k+r)%6]);
                                    break;
                                }
                            }
                        }
                    }
                }
                Point[] lo = localP(temp);
                if(equal(lo, list.get(n))) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean delete(Point[] a, ArrayList<Point[]> list) {
        if(list.size() == 0) { return false;}
        for(int i=0; i<list.size(); i++) {
            if(equal(a, list.get(i))) {
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    static boolean equal(Point[] a, Point[] b) {
        for(int i=0; i<a.length; i++) {
            if(a[i].getX() != b[i].getX()) {
                return false;
            }else if(a[i].getY() != b[i].getY()) {
                return false;
            }
        }
        return true;
    }
}
