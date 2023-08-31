import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.*;

public class Generator2 {
    public static void main(String[] args) throws IOException {
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
                                Point[] lo = localP(points);
                                if(!exist(lo, local)) {
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




        // for(int i=0; i<37; i++) {
        //     System.out.println(i);
        //     //Point[] points = new Point[7];
        //     points[0] = tGrid.get(i);
        //     for(int j=0; j<37; j++) {
        //         if(j != i) {
        //             points[1] = tGrid.get(j);
        //             for(int k=0; k<37; k++) {
        //                 if(k != i && k != j) {
        //                     points[2] = tGrid.get(k);
        //                     for(int l=0; l<37; l++) {
        //                         if(l != i && l != j && l != k) {
        //                             points[3] = tGrid.get(l);
        //                             for(int m=0; m<37; m++) {
        //                                 if(m != i && m != j && m != k && m != l) {
        //                                     points[4] = tGrid.get(m);
        //                                     for(int n=0; n<37; n++) {
        //                                         if(n != i && n != j && n != k && n != l && n != m) {
        //                                             points[5] = tGrid.get(n);
        //                                             for(int o=0; o<37; o++) {
        //                                                 if(o != i && o != j && o != k && o != l && o != m && o != n) {
        //                                                     points[6] = tGrid.get(0);
        //                                                     if(renzoku(points)) {
        //                                                         initState.add(points);
        //                                                         //System.out.println(initState.size());
        //                                                     }
        //                                                 }
        //                                             }
        //                                         }
        //                                     }
        //                                 }
        //                             }
        //                         }
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }
        System.out.println(initState.size());
        // System.err.println(initState.get(1000).length);
        // for(int i = 0; i<10; i++) {
        //     System.out.print("[");
        //     for(int j=0; j<7; j++) {
        //         System.out.print("(" + initState.get(i)[j].getX() + "," + initState.get(i)[j].getY() +")");;
        //     }
        //     System.out.println("]");
        // }
        try {
            FileWriter fw = new FileWriter("src/initial2.txt");
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

    public static boolean renzoku(Point[] points) {
        boolean c = false;
        for(int i=0; i<6; i++) {
            Point p = points[i];
            for(int j=0; j<6; j++) {
                Point q = points[j];
                if(distance(p, q) > 1){
                    return false;
                } 
            }
        }
        return true;
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
        for(int i=0; i<list.size(); i++) {
            if(equal(a, list.get(i))) {
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
