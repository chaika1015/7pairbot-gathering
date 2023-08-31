package app;

import java.util.EventObject;

// シミュレーションの操作が行われたときに発生するイベント
public class SimulationManageEvent extends EventObject{
    public SimulationManageEvent(Object source) {
        super(source);
    }
    
}
