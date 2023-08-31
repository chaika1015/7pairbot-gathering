package app;

import java.util.EventListener;

public interface RuleManageListener extends EventListener{
    public void ruleSelectedByLogger(RuleManageEvent e);
    public void ruleChanged(RuleManageEvent e);
}
