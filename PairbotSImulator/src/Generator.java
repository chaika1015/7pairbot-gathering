import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.*;

public class Generator {
    static ArrayList<Point> tGrid = new ArrayList<>();//37点
    static ArrayList<Point[]> initState = new ArrayList<>();//初期状況すべて
    static ArrayList<Point[]> local = new ArrayList<>();
    static int robot = 6;//ロボット数
    static int range = 3;
    static Point[][] rotate = {{new Point(1,0), new Point(1,-1), new Point(0,-1), new Point(-1,0), new Point(-1,1), new Point(0,1)},
                            {new Point(2,0), new Point(2,-2), new Point(0,-2), new Point(-2,0), new Point(-2,2), new Point(0,2)},
                            {new Point(3,0), new Point(3,-3), new Point(0,-3), new Point(-3,0), new Point(-3,3), new Point(0,3)},
                            {new Point(2,-1), new Point(1,-2), new Point(-1,-1), new Point(-2,1), new Point(-1,2), new Point(1,1)},
                            {new Point(3,-1), new Point(2,-3), new Point(-1,-2), new Point(-3,1), new Point(-2,3), new Point(1,2)},
                            {new Point(3,-2), new Point(1,-3), new Point(-2,-1), new Point(-3,2), new Point(-1,3), new Point(2,1)}};
    public static void main(String[] args) throws IOException {
        //37点をListに保存
        //x=0の座標を生成
        for(int i=-range; i<=range; i++) {
            tGrid.add(new Point(0,i));
        }
        //残りの座標を生成
        for(int i=1; i<=range; i++) {
            for(int j=-range; j<=range-i; j++) {
                tGrid.add(new Point(i,j));
                tGrid.add(new Point(-i,-j));
            }
        }
        tGrid.sort(Comparator.comparing(Point::getX).thenComparing(Point::getY));

        //探索
        Point[] points = new Point[robot]; //7点を保存
        points[0] = new Point(0,0);  //1点を(0,0)に固定
        tGrid.remove(points[0]);  //(0,0)を平面から削除
        System.out.println("探索開始");
        ArrayList<Point> around = aroundP(points, 1, tGrid);
        saiki(around, points, 1);
        System.out.println("探索終了");


        try {
            FileWriter fw = new FileWriter("src/initest6.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            for(Point[] p : initState) {
                for(int i=0; i<robot; i++) {
                    pw.print((int)p[i].getX() + "," + (int)p[i].getY());
                    if(i != robot-1) {
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

    public static void saiki(ArrayList<Point> around, Point[] points, int index) {
        if(index == robot-1) { //7点目の場合
            for(Point n : around) {
                points[robot-1] = n; //7点目を追加
                Point[] lo = localP(points); //7点をソートして先頭を原点としたローカル座標に変換
                if(!exist2(points, local)) { //ローカル座標の集合に同じものがなければ
                    local.add(lo);
                    Point[] p = new Point[robot];
                    for(int o=0; o<robot; o++) {
                        p[o] = new Point((int)points[o].getX(),(int)points[o].getY());
                    }
                    initState.add(p); //座標をソートして初期状況の集合に追加
                    System.out.println(initState.size());
                }
            }
        }else { //7点目以外の場合
            for (Point n : around) {
                points[index] = n;
                saiki(aroundP(points, index+1, tGrid), points, index+1);
            }
        }
    }

    static ArrayList<Point> aroundP(Point[] points, int index, ArrayList<Point> plane) {
        ArrayList<Point> ap = new ArrayList<>();  //周囲の点を保存
        ArrayList<Point> p = new ArrayList<>();  //引数で受けとる値(探索中の7点)を保存
        for(int i=0; i<index; i++) {
                p.add(points[i]);
        }

        //pointsと距離1の点を列挙
        for(int i=0; i<index; i++) {
            for(Point point : plane) {  //平面37点すべてを探索
                if(distance(points[i], point) == 1) {  //現在決まっている初期状況の点と距離が1かどうか
                    if(!ap.contains(point) && !p.contains(point)) { //ap(周囲の点)に含まれずp(すでに決まっている点)に含まれなければapに追加
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

    static Point[] localP(Point[] points) {  //ローカル座標生成
        ArrayList<Point> temp = new ArrayList<>();  //引数の座標を一時保存
        for (int i = 0; i < points.length; i++) {
            temp.add(points[i]);
        }

        temp.sort(Comparator.comparing(Point::getX).thenComparing(Point::getY));  //7点をソート
        Point[] result = new Point[robot];
        for(int i=0; i<robot; i++) {  //ソートした7点を先頭の座標からの相対座標に変換
            int x = (int)(temp.get(i).getX() - temp.get(0).getX());
            int y = (int)(temp.get(i).getY() - temp.get(0).getY());
            result[i] = new Point(x,y);
        }

        return result;
    }

    static boolean exist(Point[] a, ArrayList<Point[]> list) {
        if(list.size() == 0) { return false;}
        for(int i=0; i<list.size(); i++) {
            if(equal(a, list.get(i))) {
                return true;
            }
        }
        return false;
    }

    static boolean exist2(Point[] a, ArrayList<Point[]> list) {
        if(list.size() == 0) { return false;}
        //回転を含め同じ形があるかチェック
        Point[] temp = new Point[robot]; //回転した座標を入れるやつ
        for(int r=0; r<robot; r++) { //6回転分調査する
            for(int i=0; i<robot; i++) {
                if(a[i].equals(new Point(0,0))) { //原点を入れる
                    temp[i] = new Point(0,0);
                }else {
                    for(int j=0; j<6; j++) {
                        for(int k=0; k<6; k++) {
                            if(a[i].equals(rotate[j][k])) { //回転リストのどれと一致するか調べる
                                temp[i] = new Point(rotate[j][(k+r)%6]); //一致したら回転数rに対応するものをtempに入れる
                                break;
                            }
                        }
                    }
                }
            }
            Point[] lo = localP(temp); //生成した回転座標をソートして先頭を原点とした局所座標化
            for(int n=0; n<list.size(); n++) { //listに一致するものがあるか調べる
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
