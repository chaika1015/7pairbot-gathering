package app;

import java.util.EventObject;

// ルールの操作時に発生するイベント
public class RuleManageEvent extends EventObject{
    public RuleManageEvent(Object source) {
        super(source);
    }
}
