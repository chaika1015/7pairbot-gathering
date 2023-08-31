package app;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.Font;

public class SimulationPanel extends JPanel implements Runnable, SimulationManageListener{
    private SimulationLog log;
    private SimulationManagement simMG;
    private RuleManagement ruleMG;
    // パネルのサイズ
    private int width = 0;
    private int height = 0;
    // x, y軸上に存在する点の数（奇数で指定）
    private int xLength = 9;
    private int yLength = 9;
    // 点の総数
    private int pointSum = (int)(yLength * (4 * xLength - yLength) + 1) / 4;
    // 点集合
    private TriangularLatticePoint[] points = new TriangularLatticePoint[pointSum];
    // ペアボットの総数
    private int robotCount = 7;
    // ペアボット集合
    private Pairbot[] pairbots = new Pairbot[robotCount];
    // 点が何個のペアボットを描画したのか（描画専用）
    private int[] drawedRobot = new int[pointSum];
    // ペアボットの描画色
    static Color pairbotColor = new Color(248, 180, 248);
    // 点の識別番号を表示するか
    private boolean isVisiblePointId = true;
    // 点の座標を表示するか
    private boolean isVisibleCoordinate = true;
    // ペアボットの初期状態の生成方法の選択
    // 0: ランダム生成, 1: 横一列に生成, 2: デバッグ用1
    private int pairbotInitState = 1;
    // スケジューラーの間隔
    private long breakTime = 300L;
    // 直前のターンでペアボットの移動があったか
    private boolean cannotMoveFlag = false;
    // スレッド
    private Thread t = null;
    // ターン数
    private int turn = 0;

    public SimulationPanel(SimulationManagement simMG, RuleManagement ruleMG, SimulationLog log) {
        super();
        this.log = log;
        this.simMG = simMG;
        this.ruleMG = ruleMG;
        simMG.AddManageListener(this);
        turn = 0;
        pointInit();
        pairbotInit();
        t = new Thread(this);
        System.out.println("Get ready.");
    }

    public void pointInit() {
        // 点の初期化
        // 点の識別番号、座標を左上から順に登録していく
        // 集合をひとつずつ初期化していくためインスタンス作成時のみ実行
        // 作成した際にノードとして隣接する点を登録する

        // System.out.println("pointSum: " + pointSum);
        pointSum = (int)(yLength * (4 * xLength - yLength) + 1) / 4;
        simMG.setPointSum(pointSum);

        int pointCount = 0;         // 点の識別番号用
        int upperPointCount = -1;   // ノード登録用上部にある点の識別番号
        for (int i=0; i<yLength; i++) {
            if (i <= (yLength - 1) / 2) {
                int countx = xLength - ((yLength - 1) / 2 - i);
                int lowerx = -((xLength - 1) / 2);
                int y = (yLength - 1) / 2 - i;
                for (int j=0; j<countx; j++) {
                    points[pointCount] = new TriangularLatticePoint(pointCount, lowerx + j, y);
                    // ノードの設定
                    // 点が左端の場合
                    if (j <= 0) {
                        // 二段目以降の場合
                        if (upperPointCount >= 0) {
                            points[pointCount].setNodeUpperRight(points[upperPointCount]);
                            points[upperPointCount].setNodeLowerLeft(points[pointCount]);
                        }
                    }
                    // 点が右端の場合
                    else if (j >= countx - 1) {
                        // 二段目以降の場合
                        if (upperPointCount >= 0) {
                            points[pointCount].setNodeLeft(points[pointCount - 1]);
                            points[pointCount - 1].setNodeRight(points[pointCount]);
                            points[pointCount].setNodeUpperLeft(points[upperPointCount]);
                            points[upperPointCount].setNodeLowerRight(points[pointCount]);
                            upperPointCount++;
                        }
                        // 一段目の場合
                        else {
                            points[pointCount].setNodeLeft(points[pointCount - 1]);
                            points[pointCount - 1].setNodeRight(points[pointCount]);
                            upperPointCount++;
                        }
                    }
                    // 点が端ではない場合
                    else {
                        // 二段目以降の場合
                        if (upperPointCount >= 0) {
                            points[pointCount].setNodeLeft(points[pointCount - 1]);
                            points[pointCount - 1].setNodeRight(points[pointCount]);
                            points[pointCount].setNodeUpperLeft(points[upperPointCount]);
                            points[upperPointCount].setNodeLowerRight(points[pointCount]);
                            points[pointCount].setNodeUpperRight(points[upperPointCount + 1]);
                            points[upperPointCount + 1].setNodeLowerLeft(points[pointCount]);
                            upperPointCount++;
                        }
                        // 一段目の場合
                        else {
                            points[pointCount].setNodeLeft(points[pointCount - 1]);
                            points[pointCount - 1].setNodeRight(points[pointCount]);
                        }
                    }
                    pointCount++;
                }
            } else {
                int countx = xLength - (i - (yLength - 1) / 2);
                int lowerx = -((xLength - 1) / 2) + (i - (yLength + 1) / 2 + 1);
                int y = (yLength - 1) / 2 - i;
                for (int j=0; j<countx; j++) {
                    points[pointCount] = new TriangularLatticePoint(pointCount, lowerx + j, y);
                    // ノードの設定
                    points[pointCount].setNodeUpperLeft(points[upperPointCount]);
                    points[upperPointCount].setNodeLowerRight(points[pointCount]);
                    points[pointCount].setNodeUpperRight(points[upperPointCount + 1]);
                    points[upperPointCount + 1].setNodeLowerLeft(points[pointCount]);
                    // 点が左端の場合
                    if (j <= 0) {
                        upperPointCount++;
                    }
                    // 点が右端の場合
                    else if (j >= countx - 1) {
                        points[pointCount].setNodeLeft(points[pointCount - 1]);
                        points[pointCount - 1].setNodeRight(points[pointCount]);
                        upperPointCount += 2;
                    }
                    // 点が端ではない場合
                    else {
                        points[pointCount].setNodeLeft(points[pointCount - 1]);
                        points[pointCount - 1].setNodeRight(points[pointCount]);
                        upperPointCount++;
                    }
                    pointCount++;
                }
            }
        }

        // for (int i=0; i<pointSum; i++) {
        //     System.out.println(points[i]);
        // }
    }

    public void pairbotInit() {
        // ペアボットの初期化
        // ペアボットインスタンスの作成
        robotCount = 7;
        simMG.setRobotCount(robotCount);
        for (int i=0; i<robotCount; i++) {
            pairbots[i] = new Pairbot(i);
        }
        createInitialState();
    }

    // 初期状態の作成
    public void createInitialState() {
        int[] initialRobotPointId = simMG.getInitialRobotPointId();
        if (initialRobotPointId == null) {
            switch (pairbotInitState) {
                // デバッグ用1 (7x7のときのみ)
                case 2:
                    pairbots[0].setPoint(6);
                    points[6].addRobotId(0);
                    points[6].addRobotId(1);
                    pairbots[1].setPoint(7);
                    points[7].addRobotId(2);
                    points[7].addRobotId(3);
                    pairbots[2].setPoint(13);
                    points[13].addRobotId(4);
                    points[13].addRobotId(5);
                    pairbots[3].setPoint(14);
                    points[14].addRobotId(6);
                    points[14].addRobotId(7);
                    pairbots[4].setPoint(20);
                    points[20].addRobotId(8);
                    points[20].addRobotId(9);
                    pairbots[5].setPoint(22);
                    points[22].addRobotId(10);
                    points[22].addRobotId(11);
                    pairbots[6].setPoint(30);
                    points[30].addRobotId(12);
                    points[30].addRobotId(13);
                    break;
                // 横一列に生成
                case 1:
                    int[] pointList = getYPoints(0);
                    int startIndex = 0;
                    if (pointList.length > robotCount) {
                        startIndex = (pointList.length - robotCount) / 2;
                    }
                    if (robotCount <= pointList.length) {
                        for (int i=0; i<robotCount; i++) {
                            pairbots[i].setPoint(pointList[startIndex + i]);
                            points[pointList[startIndex + i]].addRobotId(pairbots[i].getR1().getId());
                            points[pointList[startIndex + i]].addRobotId(pairbots[i].getR2().getId());
                        }
                    }
                    break;
                // ランダム生成
                case 0:
                default:
                    Random rand = new Random();
                    for (int i=0; i<robotCount; i++) {
                        int targetPointId = rand.nextInt(pointSum);
                        pairbots[i].setPoint(targetPointId);
                        points[targetPointId].addRobotId(pairbots[i].getR1().getId());
                        points[targetPointId].addRobotId(pairbots[i].getR2().getId());
                        // pairbots[i].setR1Point(rand.nextInt(pointSum));
                        // pairbots[i].setR2Point(rand.nextInt(pointSum));
                        // pairbots[i].setLong();
                    }
                    break;
            }
        }
        else {
            for (int i=0; i<robotCount; i++) {
                pairbots[i].setPoint(initialRobotPointId[i]);
                points[initialRobotPointId[i]].addRobotId(pairbots[i].getR1().getId());
                points[initialRobotPointId[i]].addRobotId(pairbots[i].getR2().getId());
            }
        }
    }

    private int[] getXPoints(int x) {
        List<Integer> tmp = new ArrayList<Integer>();
        if (((-(xLength - 1) / 2) <= x)||(x <= (xLength - 1) / 2)) {
            for (int i=0; i<pointSum; i++) {
                if(points[i].getIntX() == x) {
                    tmp.add(i);
                }
            }
        }
        int[] result = new int[tmp.size()];
        for (int i=0; i<tmp.size(); i++) {
            result[i] = tmp.get(i);
        }
        return result.clone();
    }

    private int[] getYPoints(int y) {
        List<Integer> tmp = new ArrayList<Integer>();
        if (((-(yLength - 1) / 2) <= y)||(y <= (yLength - 1) / 2)) {
            for (int i=0; i<pointSum; i++) {
                if(points[i].getIntY() == y) {
                    tmp.add(i);
                }
            }
        }
        int[] result = new int[tmp.size()];
        for (int i=0; i<tmp.size(); i++) {
            result[i] = tmp.get(i);
        }
        return result.clone();
    }

    // シュミレーションの実行内容
    public void run() {
        System.out.println("Thread started.");
        simMG.addHistory(pairbots);
        turn++;
        while(true) {
            try {
                Thread.sleep(breakTime);
            }
            catch (InterruptedException e1) {
                simMG.setSimulationRunning(false);
                log.addSimulationLog("[System log] Simulation stop.");
                try {
                    while (true) {
                        Thread.sleep(100000000000L);
                    }
                }
                catch (InterruptedException e2) {
                    simMG.setSimulationRunning(true);
                    log.addSimulationLog("[System log] Simulation restart.");
                }
            }
            if (!cannotMoveFlag) {
                log.addSimulationLog(turn, "* turn_count: " + turn++);
            }
            else {
                log.addSimulationLog(turn, "* turn_count: " + turn);
            }
            look();
            compute();
            move();
            if (!cannotMoveFlag) {
                simMG.addHistory(pairbots.clone());
            }
            repaint();
        }
    }

    // lookメソッド
    public void look() {
        System.out.println("look");
        for (int i=0; i<robotCount; i++) {
            // Short状態の場合はHighの周りを観察する
            pairbots[i].setOccupy(points[pairbots[i].getR1().getPointId()].getAroundOccupy());
            pairbots[i].setOccupyR2(points[pairbots[i].getR2().getPointId()].getAroundOccupy());
            pairbots[i].showOccupy();
            pairbots[i].showOccupyR2();
            System.out.println(i+":look");
        }
    }

    // computeメソッド
    public void compute() {
        System.out.println("compute");
        int noMoveCount = 0;
        Outer:
        for (Pairbot pairbot: pairbots) {
            for (Algorithm rule: ruleMG.getRules()) {
                if (rule.isApply(pairbot.getIsShort(), pairbot.getOccupy(), pairbot.getPairPointIdR1())) {
                    pairbot.setWhich(1);
                    pairbot.setNextPointId(rule.getNextPointId());
                    log.addSimulationLog(rule.getParentId(), rule.getChildId(), "[" + pairbot.getId() + "] -> Rule" + rule.getParentId() + "-" + rule.getChildId());
                    System.out.println("[" + pairbot.getId() + "] -> Rule" + rule.getParentId() + "-" + rule.getChildId());
                    continue Outer;
                }
                if (rule.isApply(pairbot.getIsShort(), pairbot.getOccupyR2(), pairbot.getPairPointIdR2())) {
                    pairbot.setWhich(2);
                    pairbot.setNextPointId(rule.getNextPointId());
                    log.addSimulationLog(rule.getParentId(), rule.getChildId(), "[" + pairbot.getId() + "] -> Rule" + rule.getParentId() + "-" + rule.getChildId());
                    System.out.println("[" + pairbot.getId() + "] -> Rule" + rule.getParentId() + "-" + rule.getChildId());
                    continue Outer;
                }
            }
            pairbot.setNextPointId(0);
            noMoveCount++;
            System.out.println(noMoveCount);
        }
        if (noMoveCount == robotCount) {
            log.addSimulationLog("cannot move.");
            t.interrupt();
            cannotMoveFlag = true;
        }
        else {
            cannotMoveFlag = false;
        }
    }

    // moveメソッド
    public void move() {
        System.out.println("move");
        for (Pairbot pairbot: pairbots) {
            if (pairbot.getIsShort()) {
                int nextPointId = 0;
                switch (pairbot.getNextPointId()) {
                    case 1:
                        points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                        pairbot.setLong();
                        pairbot.setPairPointIdR1(4);
                        pairbot.setPairPointIdR2(1);
                        nextPointId = points[pairbot.getR1().getPointId()].getNodeRight().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR1Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR1().getId());
                        break;
                    case 2:
                        points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                        pairbot.setLong();
                        pairbot.setPairPointIdR1(5);
                        pairbot.setPairPointIdR2(2);
                        nextPointId = points[pairbot.getR1().getPointId()].getNodeLowerRight().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR1Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR1().getId());
                        break;
                    case 3:
                        points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                        pairbot.setLong();
                        pairbot.setPairPointIdR1(6);
                        pairbot.setPairPointIdR2(3);
                        nextPointId = points[pairbot.getR1().getPointId()].getNodeLowerLeft().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR1Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR1().getId());
                        break;
                    case 4:
                        points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                        pairbot.setLong();
                        pairbot.setPairPointIdR1(1);
                        pairbot.setPairPointIdR2(4);
                        nextPointId = points[pairbot.getR1().getPointId()].getNodeLeft().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR1Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR1().getId());
                        break;
                    case 5:
                        points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                        pairbot.setLong();
                        pairbot.setPairPointIdR1(2);
                        pairbot.setPairPointIdR2(5);
                        nextPointId = points[pairbot.getR1().getPointId()].getNodeUpperLeft().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR1Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR1().getId());
                        break;
                    case 6:
                        points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                        pairbot.setLong();
                        pairbot.setPairPointIdR1(3);
                        pairbot.setPairPointIdR2(6);
                        nextPointId = points[pairbot.getR1().getPointId()].getNodeUpperRight().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR1Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR1().getId());
                        break;
                    case 0:
                    default:
                        break;
                }
            }
            else {
                if(pairbot.getWhich() ==1) {
                    int nextPointId = 0;
                    switch (pairbot.getNextPointId()) {
                        case 1:
                            points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                            pairbot.setShort();
                            pairbot.setPairPointIdR1(0);
                            pairbot.setPairPointIdR2(0);
                            nextPointId = points[pairbot.getR1().getPointId()].getNodeRight().getPointId();
                            System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                            pairbot.setR1Point(nextPointId);
                            points[nextPointId].addRobotId(pairbot.getR1().getId());
                            break;
                        case 2:
                            points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                            pairbot.setShort();
                            pairbot.setPairPointIdR1(0);
                            pairbot.setPairPointIdR2(0);
                            nextPointId = points[pairbot.getR1().getPointId()].getNodeLowerRight().getPointId();
                            System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                            pairbot.setR1Point(nextPointId);
                            points[nextPointId].addRobotId(pairbot.getR1().getId());
                            break;
                        case 3:
                            points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                            pairbot.setShort();
                            pairbot.setPairPointIdR1(0);
                            pairbot.setPairPointIdR2(0);
                            nextPointId = points[pairbot.getR1().getPointId()].getNodeLowerLeft().getPointId();
                            System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                            pairbot.setR1Point(nextPointId);
                            points[nextPointId].addRobotId(pairbot.getR1().getId());
                            break;
                        case 4:
                            points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                            pairbot.setShort();
                            pairbot.setPairPointIdR1(0);
                            pairbot.setPairPointIdR2(0);
                            nextPointId = points[pairbot.getR1().getPointId()].getNodeLeft().getPointId();
                            System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                            pairbot.setR1Point(nextPointId);
                            points[nextPointId].addRobotId(pairbot.getR1().getId());
                            break;
                        case 5:
                            points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                            pairbot.setShort();
                            pairbot.setPairPointIdR1(0);
                            pairbot.setPairPointIdR2(0);
                            nextPointId = points[pairbot.getR1().getPointId()].getNodeUpperLeft().getPointId();
                            System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                            pairbot.setR1Point(nextPointId);
                            points[nextPointId].addRobotId(pairbot.getR1().getId());
                            break;
                        case 6:
                            points[pairbot.getR1().getPointId()].removeRobotId(pairbot.getR1().getId());
                            pairbot.setShort();
                            pairbot.setPairPointIdR1(0);
                            pairbot.setPairPointIdR2(0);
                            nextPointId = points[pairbot.getR1().getPointId()].getNodeUpperRight().getPointId();
                            System.out.println("pairbot[" + pairbot.getId() + "].R1: move[" + pairbot.getR1().getPointId() + "](" + points[pairbot.getR1().getPointId()].getIntX() + ", " + points[pairbot.getR1().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                            pairbot.setR1Point(nextPointId);
                            points[nextPointId].addRobotId(pairbot.getR1().getId());
                            break;
                        case 0:
                        default:
                            break;
                    }
                }else if(pairbot.getWhich() ==2) {
                    int nextPointId = 0;
                switch (pairbot.getNextPointId()) {
                    case 1:
                        points[pairbot.getR2().getPointId()].removeRobotId(pairbot.getR2().getId());
                        pairbot.setShort();
                        pairbot.setPairPointIdR1(0);
                        pairbot.setPairPointIdR2(0);
                        nextPointId = points[pairbot.getR2().getPointId()].getNodeRight().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R2: move[" + pairbot.getR2().getPointId() + "](" + points[pairbot.getR2().getPointId()].getIntX() + ", " + points[pairbot.getR2().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR2Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR2().getId());
                        break;
                    case 2:
                        points[pairbot.getR2().getPointId()].removeRobotId(pairbot.getR2().getId());
                        pairbot.setShort();
                        pairbot.setPairPointIdR1(0);
                        pairbot.setPairPointIdR2(0);
                        nextPointId = points[pairbot.getR2().getPointId()].getNodeLowerRight().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R2: move[" + pairbot.getR2().getPointId() + "](" + points[pairbot.getR2().getPointId()].getIntX() + ", " + points[pairbot.getR2().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR2Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR2().getId());
                        break;
                    case 3:
                        points[pairbot.getR2().getPointId()].removeRobotId(pairbot.getR2().getId());
                        pairbot.setShort();
                        pairbot.setPairPointIdR1(0);
                        pairbot.setPairPointIdR2(0);
                        nextPointId = points[pairbot.getR2().getPointId()].getNodeLowerLeft().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R2: move[" + pairbot.getR2().getPointId() + "](" + points[pairbot.getR2().getPointId()].getIntX() + ", " + points[pairbot.getR2().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR2Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR2().getId());
                        break;
                    case 4:
                        points[pairbot.getR2().getPointId()].removeRobotId(pairbot.getR2().getId());
                        pairbot.setShort();
                        pairbot.setPairPointIdR1(0);
                        pairbot.setPairPointIdR2(0);
                        nextPointId = points[pairbot.getR2().getPointId()].getNodeLeft().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R2: move[" + pairbot.getR2().getPointId() + "](" + points[pairbot.getR2().getPointId()].getIntX() + ", " + points[pairbot.getR2().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR2Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR2().getId());
                        break;
                    case 5:
                        points[pairbot.getR2().getPointId()].removeRobotId(pairbot.getR2().getId());
                        pairbot.setShort();
                        pairbot.setPairPointIdR1(0);
                        pairbot.setPairPointIdR2(0);
                        nextPointId = points[pairbot.getR2().getPointId()].getNodeUpperLeft().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R2: move[" + pairbot.getR2().getPointId() + "](" + points[pairbot.getR2().getPointId()].getIntX() + ", " + points[pairbot.getR2().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR2Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR2().getId());
                        break;
                    case 6:
                        points[pairbot.getR2().getPointId()].removeRobotId(pairbot.getR2().getId());
                        pairbot.setShort();
                        pairbot.setPairPointIdR1(0);
                        pairbot.setPairPointIdR2(0);
                        nextPointId = points[pairbot.getR2().getPointId()].getNodeUpperRight().getPointId();
                        System.out.println("pairbot[" + pairbot.getId() + "].R2: move[" + pairbot.getR2().getPointId() + "](" + points[pairbot.getR2().getPointId()].getIntX() + ", " + points[pairbot.getR2().getPointId()].getIntY() + ") -> [" + nextPointId + "](" + points[nextPointId].getIntX() + ", " + points[nextPointId].getIntY() + ")");
                        pairbot.setR2Point(nextPointId);
                        points[nextPointId].addRobotId(pairbot.getR2().getId());
                        break;
                    case 0:
                    default:
                        break;
                }
                
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        System.out.println("repaint");
        // パネルのグラフィックの描画
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.setBackground(Color.WHITE);
        drawPlane(g2d);
    }

    // 三角格子平面を描画するメソッド
    private void drawPlane(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        // パネルのサイズを取得
        width = this.getWidth();
        height = this.getHeight();
        // 一辺の長さを計算し、短い方を採用する。
        int boxWidth = width / (xLength + 1);
        int boxHeight = height / (yLength + 1);
        boxHeight = Math.min(boxWidth, boxHeight);
        // 横の線を描画
        int starty = height / 2 - boxHeight * ((yLength - 1) / 2);
        for (int i=0; i<yLength; i++) {
            int tmpy = boxHeight * i;
            g2d.drawLine(0, starty + tmpy, width, starty + tmpy);
        }
        // 斜めの線を描画
        boxWidth = (int)(boxHeight / 0.865);
        int startx = width / 2 - boxWidth * ((xLength - 1) / 2);
        int tmpWidth = (int)(height / 3.46);
        for (int i=0; i<xLength; i++) {
            int tmpxLeft = boxWidth * i - tmpWidth;
            int tmpxRight = boxWidth * i + tmpWidth;
            g2d.drawLine(startx + tmpxLeft, height, startx + tmpxRight, 0);
            g2d.drawLine(startx + tmpxLeft, 0, startx + tmpxRight, height);
        }
        // x, y軸を描画
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, height / 2, width, height / 2);
        g2d.drawLine(width / 2 + tmpWidth, 0, width / 2 - tmpWidth, height);

        // 点を描画
        // 描画方法を左上から順に変更
        int pointCount = 0;     // 点の識別番号用
        for (int i=0; i<yLength; i++) {
            if (i <= (yLength - 1) / 2) {
                int countx = xLength - ((yLength - 1) / 2 - i);
                startx = width / 2 - (boxWidth * (countx - 1)) / 2;
                int ovalHeight = height / 2 - boxHeight * ((yLength - 1) / 2 - i);
                for (int j=0; j<countx; j++) {
                    g2d.fillOval(startx + (boxWidth * j) - 4, ovalHeight - 4, 8, 8);
                    if(isVisiblePointId && isVisibleCoordinate) {
                        g2d.drawString("[" + pointCount + "] (" + points[pointCount].getIntX() + ", " + points[pointCount].getIntY() + ")", startx + (boxWidth * j) + 10, ovalHeight - 4);
                    }
                    else if(isVisibleCoordinate) {
                        g2d.drawString("(" + points[pointCount].getIntX() + ", " + points[pointCount].getIntY() + ")", startx + (boxWidth * j) + 10, ovalHeight - 4);
                    }
                    else if (isVisiblePointId) {
                        g2d.drawString("[" + pointCount + "]", startx + (boxWidth * j) + 10, ovalHeight - 4);
                    }
                    points[pointCount].setPanelPoint(startx + (boxWidth * j), ovalHeight);
                    pointCount++;
                }
            } else {
                int countx = xLength - (i - (yLength - 1) / 2);
                startx = width / 2 - (boxWidth * (countx - 1)) / 2;
                int ovalHeight = height / 2 + boxHeight * (i - (yLength - 1) / 2);
                for (int j=0; j<countx; j++) {
                    g2d.fillOval(startx + (boxWidth * j) - 4, ovalHeight - 4, 8, 8);
                    if(isVisiblePointId && isVisibleCoordinate) {
                        g2d.drawString("[" + pointCount + "] (" + points[pointCount].getIntX() + ", " + points[pointCount].getIntY() + ")", startx + (boxWidth * j) + 10, ovalHeight - 4);
                    }
                    else if(isVisibleCoordinate) {
                        g2d.drawString("(" + points[pointCount].getIntX() + ", " + points[pointCount].getIntY() + ")", startx + (boxWidth * j) + 10, ovalHeight - 4);
                    }
                    else if (isVisiblePointId) {
                        g2d.drawString("[" + pointCount + "]", startx + (boxWidth * j) + 10, ovalHeight - 4);
                    }
                    points[pointCount].setPanelPoint(startx + (boxWidth * j), ovalHeight);
                    pointCount++;
                }
            }
        }

        for (int i=0; i<pointCount; i++) {
            drawedRobot[i] = 0;
        }

        // ペアボットを描画
        g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 24));
        for (int i=0; i<robotCount; i++) {
            // ペアボットがShort状態の場合
            if (pairbots[i].getIsShort()) {
                g2d.setStroke(new BasicStroke(2));
                int pairbotPointId = pairbots[i].getR1().getPointId();
                int pairbotx = points[pairbotPointId].getPanelx();
                int pairboty = points[pairbotPointId].getPanely() - drawedRobot[pairbotPointId] * 8;
                g2d.setColor(pairbotColor);
                g2d.fillOval(pairbotx - 16, pairboty - 16, 32, 32);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawOval(pairbotx - 16, pairboty - 16, 32, 32);
                g2d.setColor(pairbotColor);
                g2d.fillOval(pairbotx - 16, pairboty - 24, 32, 32);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawOval(pairbotx - 16, pairboty - 24, 32, 32);
                g2d.drawString("" + i, pairbotx - 6, pairboty);
                drawedRobot[pairbotPointId] += 3;
            }
            // ペアボットがLong状態の場合
            else {
                int r1PointId = pairbots[i].getR1().getPointId();
                int r1x = points[r1PointId].getPanelx();
                int r1y = points[r1PointId].getPanely() - drawedRobot[r1PointId] * 8;
                int r2PointId = pairbots[i].getR2().getPointId();
                int r2x = points[r2PointId].getPanelx();
                int r2y = points[r2PointId].getPanely() - drawedRobot[r2PointId] * 8;
                g2d.setStroke(new BasicStroke(7));
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawLine(r1x, r1y, r2x, r2y);
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(pairbotColor);
                g2d.drawLine(r1x, r1y, r2x, r2y);
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(pairbotColor);
                g2d.fillOval(r1x - 16, r1y - 16, 32, 32);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawOval(r1x - 16, r1y - 16, 32, 32);
                g2d.drawString("" + i, r1x - 6, r1y + 8);
                g2d.setColor(pairbotColor);
                g2d.fillOval(r2x - 16, r2y - 16, 32, 32);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawOval(r2x - 16, r2y - 16, 32, 32);
                g2d.drawString("" + i, r2x - 6, r2y + 8);
                drawedRobot[r1PointId] += 2;
                drawedRobot[r2PointId] += 2;
            }
        }
    }

    @Override
    public void simulationStarted(SimulationManageEvent e) {
        if (!t.isAlive()) {
            log.addSimulationLog("[System log] Simulation start.");
            t.start();
            simMG.setSimulationRunning(true);
            simMG.setSimulationAlived(true);
        }
        else {
            if (!simMG.isSimulationRunning()) {
                t.interrupt();
                // ヒストリーを選択されたターンから現在のターンまで削除して現在のターンを選択されたターンに変更
                if (turn != simMG.getPresentIndex()) {
                    turn = simMG.getPresentIndex();
                    simMG.removeHistory();
                }
            }
        }
    }

    @Override
    public void simulationStopped(SimulationManageEvent e) {
        if (t.isAlive() && simMG.isSimulationRunning()) {
            t.interrupt();
        }
    }

    @Override
    public void simulationReset(SimulationManageEvent e) {
        if (t.isAlive()) {
            log.clearLog();
            t.stop();
            t = new Thread(this);
            turn = 0;
            cannotMoveFlag = false;
            simMG.setSimulationRunning(false);
            pointInit();
            pairbotInit();
            repaint();
            log.addSimulationLog("[System log] Simulation reset.");
        }
    }

    @Override
    public void historySelected(SimulationManageEvent e) {
        // ヒストリーパネルで選択された時の挙動
        if (!simMG.isSimulationRunning()) {
            System.out.println("history is selected.");
            int selectedTurn = simMG.getPresentIndex();
            if (selectedTurn > -1) {
                Pairbot[] selectedHistory = simMG.getHistory();
                for (int i=0; i<robotCount; i++) {
                    pairbots[i] = selectedHistory[i].clone();
                }
                for (int i=0; i<pointSum; i++) {
                    if (points[i].getHaveRobot() > 0) {
                        points[i].clearRobotId();
                    }
                }
                System.out.print("[");
                for (int i=0; i<robotCount; i++) {
                    System.out.print(String.format("%02d, ", pairbots[i].getR1().getPointId()));
                    points[pairbots[i].getR1().getPointId()].addRobotId(pairbots[i].getR1().getId());
                    System.out.print(String.format("%02d", pairbots[i].getR2().getPointId()));
                    points[pairbots[i].getR2().getPointId()].addRobotId(pairbots[i].getR2().getId());
                    if (i != robotCount - 1) System.out.print(", ");
                }
                System.out.println("]");
            }
            repaint();
        }
    }

    @Override
    public void initialStatusChanged(SimulationManageEvent e) {
        if (t.isAlive()) {
            log.clearLog();
            t.stop();
            t = new Thread(this);
            turn = 0;
            cannotMoveFlag = false;
            simMG.setSimulationRunning(false);
            simMG.setSimulationAlived(false);
            pointInit();
            pairbotInit();
            repaint();
            log.addSimulationLog("[System log] Simulation reset.");
            log.addSimulationLog("[System log] Initial status changed.");
        }
        else {
            for (int i=0; i<pointSum; i++) {
                if (points[i].getHaveRobot() > 0) {
                    points[i].clearRobotId();
                }
            }
            createInitialState();
            repaint();
            log.addSimulationLog("[System log] Initial status changed.");
        }
    }
}
