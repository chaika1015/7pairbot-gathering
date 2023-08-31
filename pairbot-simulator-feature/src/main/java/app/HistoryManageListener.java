package app;

import java.util.EventListener;

public interface HistoryManageListener extends EventListener{
    public void historyChanged(HistoryManageEvent e);
    public void historyCleared(HistoryManageEvent e);
    public void historySelectedByLogger(HistoryManageEvent e);
}
