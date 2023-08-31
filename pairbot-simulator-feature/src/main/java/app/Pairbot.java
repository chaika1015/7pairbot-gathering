package app;

public class Pairbot implements Cloneable{
    // ペアボットの識別番号
    private int id;
    // ペアボットを構成する二つのロボット
    private Robot r2;       // Low
    private Robot r1;       // High
    // Short状態かLong状態か[true: Short状態, false: Long状態]
    private boolean isShort;
    // 周りにどれだけペアボットが存在するか
    private int[] occupy = new int[7];
    private int[] occupyR2 = new int[7];
    // 移動先の点の識別番号
    private int nextPointId = 0;
    // ペア(R1から見た)が相対的にどこに位置するか
    private int pairPointIdR1 = 0;
    // ペア(R2から見た)が相対的にどこに位置するか
    private int pairPointIdR2 = 0;
    // どちらのロボットが動くか
    private int which = 0;

    public Pairbot(int id) {
        this.id = id;
        // ロボットを作成
        r1 = new Robot(2 * id);
        r2 = new Robot(2 * id + 1);
    }

    // ロボットの座標を設定
    public void setPoint(int pointId) {
        r1.setPoint(pointId);
        r2.setPoint(pointId);
        isShort = true;
    }

    public void setR1Point(int pointId) {
        r1.setPoint(pointId);
    }

    public void setR2Point(int pointId) {
        r2.setPoint(pointId);
    }

    public void setShort() {
        isShort = true;
    }

    public void setLong() {
        isShort = false;
    }

    public void setNextPointId(int nextPointId) {
        this.nextPointId = nextPointId;
    }

    public void setOccupy(int[] occupy) {
        if (occupy.length == 7) {
            this.occupy = occupy.clone();
        }
    }

    public void setOccupyR2(int[] occupyR2) {
        if (occupyR2.length == 7) {
            this.occupyR2 = occupyR2.clone();
        }
    }

    public void setPairPointIdR1(int pairPointId) {
        this.pairPointIdR1 = pairPointId;
    }
    public void setPairPointIdR2(int pairPointId) {
        this.pairPointIdR2 = pairPointId;
    }

    public void setWhich(int which) {
        this.which = which;
    }


    public int getId() {
        return id;
    }

    public Robot getR1() {
        return r1;
    }

    public Robot getR2() {
        return r2;
    }

    public boolean getIsShort() {
        return isShort;
    }

    public int getNextPointId() {
        return nextPointId;
    }

    public int getPairPointIdR1() {
        return pairPointIdR1;
    }
    public int getPairPointIdR2() {
        return pairPointIdR2;
    }


    public int[] getOccupy() {
        return occupy.clone();
    }

    public int[] getOccupyR2() {
        return occupyR2.clone();
    }

    public int getWhich() {
        return which;
    }

    public void showOccupy() {
        System.out.print("id: " + id + (isShort?", Short": ", Long ") + ", occupy: {");
        for (int i=0; i<occupy.length; i++) {
            System.out.print(occupy[i] + (i!=occupy.length-1?", ":""));
        }
        System.out.println("}, pair: " + pairPointIdR1);
    }

    public void showOccupyR2() {
        System.out.print("id: " + id + (isShort?", Short": ", Long ") + ", occupy: {");
        for (int i=0; i<occupyR2.length; i++) {
            System.out.print(occupyR2[i] + (i!=occupyR2.length-1?", ":""));
        }
        System.out.println("}, pair: " + pairPointIdR2);
    }


    public Pairbot clone() {
        Pairbot result = new Pairbot(id);
        result.setR1Point(r1.getPointId());
        result.setR2Point(r2.getPointId());
        if (isShort) result.setShort();
        else result.setLong();
        result.setNextPointId(nextPointId);
        result.setOccupy(occupy);
        result.setOccupyR2(occupyR2);
        result.setPairPointIdR1(pairPointIdR1);
        result.setPairPointIdR2(pairPointIdR2);
        return result;
    }
}

class Robot {
    // ロボットの識別番号
    private int id;
    // ロボットが存在する点の識別番号
    private int pointId;

    public Robot(int id) {
        this.id = id;
    }

    public void setPoint(int pointId) {
        this.pointId = pointId;
    }

    public int getPointId() {
        return this.pointId;
    }

    public int getId() {
        return this.id;
    }
}
