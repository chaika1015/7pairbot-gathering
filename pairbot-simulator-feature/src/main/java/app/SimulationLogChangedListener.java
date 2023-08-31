package app;

import java.util.EventListener;

public interface SimulationLogChangedListener extends EventListener{
    public void simulationLogChanged(SimulationLogChangedEvent e);
    public void simulationLogClear(SimulationLogChangedEvent e);
}
