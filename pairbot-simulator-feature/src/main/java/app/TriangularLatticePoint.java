package app;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TriangularLatticePoint extends Point{
    private int pointId;
    // パネル上での座標
    private int panelx = 0;
    private int panely = 0;
    // 接続されている点
    private TriangularLatticePoint nodeUpperLeft = null;
    private TriangularLatticePoint nodeUpperRight = null;
    private TriangularLatticePoint nodeLeft = null;
    private TriangularLatticePoint nodeRight = null;
    private TriangularLatticePoint nodeLowerLeft = null;
    private TriangularLatticePoint nodeLowerRight = null;
    // 点上に存在するロボットの識別番号のリスト
    private List<Integer> haveRobotId = new ArrayList<Integer>();

    // 原点に点を構築して初期化
    public TriangularLatticePoint(int pointId) {
        super();
        this.pointId = pointId;
    }
    // 指定された位置に点を構築して初期化
    public TriangularLatticePoint(int pointId, int x, int y) {
        super(x, y);
        this.pointId = pointId;
    }
    // 指定されたオブジェクトと同じ位置に点を構築して初期化
    public TriangularLatticePoint(TriangularLatticePoint p){
        super((Point)p);
    }

    @Override
    public String toString() {
        String result =  "[" + this.getPointId() + "] (x=" + this.getX() + ", y=" + this.getY() + "), nodes:"
            + " {ul=" + (nodeUpperLeft == null ? "x" : nodeUpperLeft.getPointId())
            + ", ur=" + (nodeUpperRight == null ? "x" : nodeUpperRight.getPointId())
            + ", l=" + (nodeLeft == null ? "x" : nodeLeft.getPointId())
            + ", r=" + (nodeRight == null ? "x" : nodeRight.getPointId())
            + ", ll=" + (nodeLowerLeft == null ? "x" : nodeLowerLeft.getPointId())
            + ", lr=" + (nodeLowerRight == null ? "x" : nodeLowerRight.getPointId()) + "}"
            + ", robots{";
            for (int i=0; i<haveRobotId.size(); i++) {
                if (i != 0) {
                    result += ", ";
                }
                result += haveRobotId.get(i);
            }
            result += "}";
            return result;
    }

    // パネル上での座標を登録
    public void setPanelPoint(int x, int y) {
        this.panelx = x;
        this.panely = y;
    }

    public void setId(int id) {
        this.pointId = id;
    }

    public void setNodeUpperLeft(TriangularLatticePoint p) {
        nodeUpperLeft = p;
    }

    public void setNodeUpperRight(TriangularLatticePoint p) {
        nodeUpperRight = p;
    }

    public void setNodeLeft(TriangularLatticePoint p) {
        nodeLeft = p;
    }

    public void setNodeRight(TriangularLatticePoint p) {
        nodeRight = p;
    }

    public void setNodeLowerLeft(TriangularLatticePoint p) {
        nodeLowerLeft = p;
    }

    public void setNodeLowerRight(TriangularLatticePoint p) {
        nodeLowerRight = p;
    }

    // 点上に存在するロボットのリストの操作
    // 識別番号の追加
    public void addRobotId(int id) {
        haveRobotId.add(id);
    }

    // 識別番号の削除
    public void removeRobotId(int id) {
        if (haveRobotId.contains(id)) {
            haveRobotId.remove(haveRobotId.indexOf(id));
        }
    }

    public void clearRobotId() {
        haveRobotId.clear();
    }

    public int getPointId() {
        return this.pointId;
    }

    public int getIntX() {
        return x;
    }

    public int getIntY() {
        return y;
    }

    public int getPanelx() {
        return panelx;
    }

    public int getPanely() {
        return panely;
    }

    public TriangularLatticePoint getNodeRight() {
        return nodeRight;
    }

    public TriangularLatticePoint getNodeLowerRight() {
        return nodeLowerRight;
    }

    public TriangularLatticePoint getNodeLowerLeft() {
        return nodeLowerLeft;
    }

    public TriangularLatticePoint getNodeLeft() {
        return nodeLeft;
    }

    public TriangularLatticePoint getNodeUpperLeft() {
        return nodeUpperLeft;
    }

    public TriangularLatticePoint getNodeUpperRight() {
        return nodeUpperRight;
    }

    public int getHaveRobot() {
        return haveRobotId.size() >= 2 ? 2 : haveRobotId.size();
    }

    public int[] getAroundOccupy() {
        int[] occupy = new int[7];
        occupy[0] = getHaveRobot();
        occupy[1] = nodeRight != null ? nodeRight.getHaveRobot() : 0;
        occupy[2] = nodeLowerRight != null ? nodeLowerRight.getHaveRobot() : 0;
        occupy[3] = nodeLowerLeft != null ? nodeLowerLeft.getHaveRobot() : 0;
        occupy[4] = nodeLeft != null ? nodeLeft.getHaveRobot() : 0;
        occupy[5] = nodeUpperLeft != null ? nodeUpperLeft.getHaveRobot() : 0;
        occupy[6] = nodeUpperRight != null ? nodeUpperRight.getHaveRobot() : 0;
        return occupy.clone();
    }
}
