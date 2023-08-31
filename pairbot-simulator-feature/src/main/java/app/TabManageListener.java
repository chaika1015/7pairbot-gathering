package app;

import java.util.EventListener;

public interface TabManageListener extends EventListener{
    public void selectRuleTab(TabManageEvent e);
}
