package app;

import java.util.EventObject;

// シミュレーションのログが変更されたときに発生するイベント
public class SimulationLogChangedEvent extends EventObject{
    public SimulationLogChangedEvent(Object source) {
        super(source);
    }
}
