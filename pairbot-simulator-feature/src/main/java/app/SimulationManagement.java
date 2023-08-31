package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimulationManagement {
    // シミュレーションの操作を取得するイベントリスナー
    private SimulationManageListener listener = null;
    // シュミレーションが動いているか
    private boolean simulationRunning = false;
    // シミュレーションスレッドが一度でも開始されたか
    private boolean simulationAlived = false;
    // ペアボットの履歴
    private List<Pairbot[]> history = new ArrayList<Pairbot[]>();
    // ペアボットの履歴の操作を取得するリスナー
    private HistoryManageListener listener_his = null;
    // 現在の状態が履歴のどこを指しているか
    private int presentIndex = 0;
    // ペアボットの台数
    private int robotCount = -1;
    // ポイントの数
    private int pointSum = -1;
    // 初期状態のポイントの識別番号
    private int[] initialRobotPointId = null;

    public SimulationManagement() {
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }
    public void setSimulationRunning(boolean f) {
        simulationRunning = f;
    }

    public boolean isSimulationAlived() {
        return simulationAlived;
    }
    public void setSimulationAlived(boolean f) {
        this.simulationAlived = f;
    }

    public String[] getHistoryStrings() {
        String[] result = new String[history.size()];
        for (int i=0; i<result.length; i++) {
            result[i] = "turnCount: " + i;
        }
        return result;
    }

    public int[] getInitialRobotPointId() {
        return initialRobotPointId;
    }

    // リスナーの設定メソッド
    public void AddManageListener (SimulationManageListener listener) {
        this.listener = listener;
    }
    public void removeManageListener () {
        this.listener = null;
    }
    // ヒストリーリスナーの設定メソッド
    public void AddHistoryManageListener (HistoryManageListener listener) {
        this.listener_his = listener;
    }
    public void removeHistoryManageListener () {
        this.listener_his = null;
    }

    // シミュレーションを操作するメソッド
    public void startSimulate() {
        if (listener != null) {
            listener.simulationStarted(new SimulationManageEvent(this));
        }
    }
    public void stopSimulate() {
        if (listener != null) {
            listener.simulationStopped(new SimulationManageEvent(this));
        }
    }
    public void resetSimulate() {
        if (listener != null) {
            simulationAlived = false;
            listener.simulationReset(new SimulationManageEvent(this));
            clearHistory();
        }
    }
    
    // ヒストリーを操作するメソッド
    public void addHistory(Pairbot[] turn) {
        if (listener_his != null) {
            Pairbot[] pairbots = new Pairbot[robotCount];
            for (int i=0; i<robotCount; i++) {
                pairbots[i] = turn[i].clone();
            }
            history.add(pairbots);
            presentIndex = history.size() - 1;
            listener_his.historyChanged(new HistoryManageEvent(this));
        }
    }

    public void removeHistory() {
        if (listener_his != null) {
            for (int i=history.size()-1; i>presentIndex; i--) {
                history.remove(i);
            }
            listener_his.historyChanged(new HistoryManageEvent(this));
        }
    }

    public void clearHistory() {
        if (listener_his != null) {
            history.clear();
            presentIndex = 0;
            listener_his.historyCleared(new HistoryManageEvent(this));
        }
    }

    public Pairbot[] getHistory() {
        return history.get(presentIndex);
    }

    public int getPresentIndex() {
        return presentIndex;
    }

    public void setPresentIndex(int presentIndex){
        this.presentIndex = presentIndex;
    }

    public int getRobotCount() {
        return robotCount;
    }
    public void setRobotCount(int robotCount) {
        this.robotCount = robotCount;
    }

    public int getPointSum() {
        return pointSum;
    }
    public void setPointSum(int pointSum) {
        this.pointSum = pointSum;
    }

    // ヒストリーを選択するメソッド
    public void selectHistory() {
        if ( listener != null) {
            listener.historySelected(new SimulationManageEvent(this));
        }
    }

    public void selectHistoryFromLogger(int turn) {
        if (listener_his != null) {
            presentIndex = turn;
            listener_his.historySelectedByLogger(new HistoryManageEvent(this));
        }
    }

    public void changeInitialStatus(int[] initialRobotPointId) {
        if (listener != null) {
            this.initialRobotPointId = initialRobotPointId.clone();
            listener.initialStatusChanged(new SimulationManageEvent(this));
        }
    }
}
