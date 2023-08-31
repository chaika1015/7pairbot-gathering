import java.awt.Point;
import java.util.*;

public class Pairbot {
    private Point pointA = new Point(); //Aの座標
    private Point pointB = new Point(); //Bの座標
    private int st = 1; //Shortか
    private int[] viewA = new int[7]; //Aのview
    private int[] viewB = new int[7]; //Bのview
    private int AtoB = 0; //Aから見たBの位置
    private int BtoA = 0; //Bから見たBの位置
    private int destA = 0; //Aの目的地
    private int destB = 0; //Bの目的地
    private ArrayList<Rule> rules = new ArrayList<>(); //ルール

    public Pairbot(int x, int y, ArrayList<Rule> r) {
        pointA = new Point(x,y);
        pointB = new Point(x,y);
        rules = r;
    }

    public void LookA(int[] view) {
        for (int i = 0; i < view.length; i++) {
            viewA[i] = view[i];
        }
        setPairLocation();
        if(AtoB == 0) {
            st = 1;
        }else {
            st = 0;
        }
    }

    public void LookB(int[] view) {
        for (int i = 0; i < view.length; i++) {
            viewB[i] = view[i];
        }
        setPairLocation();
        if(AtoB == 0) {
            st = 1;
        }else {
            st = 0;
        }
    }

    public int computeA() {
        int flag = 0;
        for (int i = 0; i < rules.size(); i++) {
            if(rules.get(i).check(viewA, st, AtoB) != 0) {
                // System.out.println("ルール"+(i+1));
                destA = rules.get(i).check(viewA, st, AtoB);
                flag = 1;
            }
        }

        if(flag == 0) {
            destA = 0;
        }
        return flag;
    }

    public int computeB() {
        int flag = 0;
        for (int i = 0; i < rules.size(); i++) {
            if(rules.get(i).check(viewB, st, BtoA) != 0) {
                destB = rules.get(i).check(viewB, st, BtoA);
                flag = 1;
            }
        }

        if(flag == 0) {
            destB = 0;
        }
        return flag;
    }

    public int moveA() {
        if(destA != 0) {
            pointA.setLocation(move(pointA,destA));
            return 1;
        }
        return 0;
    }

    public int moveB() {
        if(destB != 0) {
            pointB.setLocation(move(pointB,destB));
            return 1;
        }
        return 0;
    }

    public boolean isShort() {
        if(st == 1) {
            return true;
        }
        return false;
    }

    public Point[] aroundPointA() {
        Point[] ap = new Point[7];
        ap[0] = pointA;
        ap[1] = new Point((int)pointA.getX()+1,(int)pointA.getY());
        ap[2] = new Point((int)pointA.getX()+1,(int)pointA.getY()-1);
        ap[3] = new Point((int)pointA.getX(),(int)pointA.getY()-1);
        ap[4] = new Point((int)pointA.getX()-1,(int)pointA.getY());
        ap[5] = new Point((int)pointA.getX()-1,(int)pointA.getY()+1);
        ap[6] = new Point((int)pointA.getX(),(int)pointA.getY()+1);
        return ap;
    }

    public Point[] aroundPointB() {
        Point[] ap = new Point[7];
        ap[0] = pointB;
        ap[1] = new Point((int)pointB.getX()+1,(int)pointB.getY());
        ap[2] = new Point((int)pointB.getX()+1,(int)pointB.getY()-1);
        ap[3] = new Point((int)pointB.getX(),(int)pointB.getY()-1);
        ap[4] = new Point((int)pointB.getX()-1,(int)pointB.getY());
        ap[5] = new Point((int)pointB.getX()-1,(int)pointB.getY()+1);
        ap[6] = new Point((int)pointB.getX(),(int)pointB.getY()+1);
        return ap;
    }

    void setPairLocation() {
        //ペアの位置を計算
        if(pointA.getX()-pointB.getX() == 0) {
            if(pointA.getY()-pointB.getY() == 0) {
                AtoB = 0; BtoA = 0;
            }else if(pointA.getY()-pointB.getY() == 1) {
                AtoB = 3; BtoA = 6;
            }else if(pointA.getY()-pointB.getY() == -1) {
                AtoB = 6; BtoA = 3;
            }else {
                System.out.println("ペアが1以上離れました");
                System.exit(0);
            }
        }else if(pointA.getX()-pointB.getX() == 1){
            if(pointA.getY()-pointB.getY() == 0) {
                AtoB = 4; BtoA = 1;
            }else if(pointA.getY()-pointB.getY() == -1) {
                AtoB = 5; BtoA = 2;
            }else {
                System.out.println("ペアが1以上離れました");
                System.exit(0);
            }
        }else if(pointA.getX()-pointB.getX() == -1) {
            if(pointA.getY()-pointB.getY() == 0) {
                AtoB = 1; BtoA = 4;
            }else if(pointA.getY()-pointB.getY() == 1) {
                AtoB = 2; BtoA = 5;
            }else {
                System.out.println("ペアが1以上離れました");
                System.exit(0);
            }
        }else {
            System.out.println("ペアが1以上離れました");
            System.exit(0);
        }
    }

    Point move(Point p, int d) {
        Point dest = new Point((int)p.getX(),(int)p.getY());
        if(d == 1) {
            dest.setLocation(p.getX()+1, p.getY());
            return dest;
        }
        if(d == 2) {
            dest.setLocation(p.getX()+1, p.getY()-1);
            return dest;
        }
        if(d == 3) {
            dest.setLocation(p.getX(), p.getY()-1);
            return dest;
        }
        if(d == 4) {
            dest.setLocation(p.getX()-1, p.getY());
            return dest;
        }
        if(d == 5) {
            dest.setLocation(p.getX()-1, p.getY()+1);
            return dest;
        }
        if(d == 6) {
            dest.setLocation(p.getX(), p.getY()+1);
            return dest;
        }
        return dest;
    }

    int updatePos(Map<Point, Integer> exist) {
        if(exist.containsKey(pointA)) {
            exist.put(pointA, exist.get(pointA)+1);
        }else {
            exist.put(pointA, 1);
        }

        if(exist.containsKey(pointB)) {
            exist.put(pointB, exist.get(pointB)+1);
        }else {
            exist.put(pointB, 1);
        }

        int a = Math.max(exist.get(pointA), exist.get(pointB));
        return a;
    }
}
