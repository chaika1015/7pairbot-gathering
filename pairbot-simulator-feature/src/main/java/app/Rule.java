package app;

import java.util.Arrays;
import java.awt.Point;

public class Rule {
    public int[] view = new int[7];
    public String[] care = new String[7];
    public int st;
    public int pairPos;
    public int dest;

    public Rule(int[] v, String[] c, int s, int p, int d) {
        view = v.clone();
        care = c.clone();
        st = s;
        pairPos = p;
        dest = d;
    }

    public int check(int[] robotView, int s, int p) {
        if(st != s) { return 0;}
        if(pairPos != p) { return 0;}
        for(int i=0; i<7; i++) {
            if(care[i].equals("eq")) {
                if(robotView[i] != view[i]) {
                    return 0;
                }
            }
        }
        return dest;
    }
}
