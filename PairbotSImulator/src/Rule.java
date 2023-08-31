public class Rule {
    private int[] view = new int[7];
    private String[] care = new String[7];
    private int st;
    private int pairPos;
    private int dest;

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

    public Rule rotate(int n) {//nは回転数
        int[] v = new int[7];
        String[] c = new String[7];
        int s = st;
        int p = 0;
        if(s != 1) {
            p = pairPos+n;  //n回回転
            if(p>6) {       //1~6におさめる
                p = p%6;
            }
        }
        int d = dest+n;
        if(d > 6) {
            d = d%6;
        }

        v[0] = view[0];
        c[0] = care[0];
        
        for(int i=1; i<7; i++) {
            int num = i+n;
            if(num > 6) {
                num = num%6;
            }
            v[num] = view[i];
            c[num] = care[i];
        }

        Rule newRule = new Rule(v, c, s, p, d);
        return newRule;
    }


    public int[] getView() {
        return view;
    }

    public String[] getCare() {
        return care;
    }

    public int GetSt() {
        return st;
    }

    public int getPairPos() {
        return pairPos;
    }

    public int getDest() {
        return dest;
    }
}
