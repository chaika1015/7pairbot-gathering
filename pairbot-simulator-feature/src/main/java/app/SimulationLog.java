package app;

import java.util.ArrayList;

public class SimulationLog{
    // ログを保存するリスト
    private ArrayList<Log> logs = new ArrayList<Log>();
    // ログが変更したことを取得するイベントリスナー
    private SimulationLogChangedListener listener = null;

    public SimulationLog() {
    }

    // ログの追加
    public void addSimulationLog(String log_string) {
        if (log_string.length() == 0) {
            return;
        }
        if (listener == null) {
            return;
        }
        logs.add(0, new Log(log_string));
        listener.simulationLogChanged(new SimulationLogChangedEvent(this));
    }
    public void addSimulationLog(int parentId, int childId, String log_string) {
        if (log_string.length() == 0) {
            return;
        }
        if (listener == null) {
            return;
        }
        logs.add(0, new Log(parentId, childId, log_string));
        listener.simulationLogChanged(new SimulationLogChangedEvent(this));
    }
    public void addSimulationLog(int turn, String log_string) {
        if (log_string.length() == 0) {
            return;
        }
        if (listener == null) {
            return;
        }
        logs.add(0, new Log(turn, log_string));
        listener.simulationLogChanged(new SimulationLogChangedEvent(this));
    }

    // ログの取得
    public Log getSimulationLog() {
        return logs.get(0);
    }

    // ログのクリア
    public void clearLog() {
        logs = new ArrayList<Log>();
        listener.simulationLogClear(new SimulationLogChangedEvent(this));
    }

    // リスナーの設定メソッド
    public void addLogChangedListener (SimulationLogChangedListener listener) {
        this.listener = listener;
    }
    public void removeLogChangedListener () {
        listener = null;
    }
}

class Log {
    private int parentId = -1;
    private int childId = -1;
    private int turn = -1;
    private String logString;

    public Log(int parentId, int childId, String logString) {
        this.parentId = parentId;
        this.childId = childId;
        this.logString = logString;
    }

    public Log(int turn, String logString) {
        this.turn = turn;
        this.logString = logString;
    }

    public Log(String logString) {
        this.logString = logString;
    }

    public int getParentId() {
        return parentId;
    }

    public int getChildId() {
        return childId;
    }

    public int getTurn() {
        return this.turn;
    }

    public String getLogString() {
        return logString;
    }

    public String toString() {
        return logString;
    }
}
