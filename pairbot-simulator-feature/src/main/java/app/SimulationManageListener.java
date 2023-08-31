package app;

import java.util.EventListener;

public interface SimulationManageListener  extends EventListener{
    public void simulationStarted(SimulationManageEvent e);
    public void simulationStopped(SimulationManageEvent e);
    public void simulationReset(SimulationManageEvent e);
    public void historySelected(SimulationManageEvent e);
    public void initialStatusChanged(SimulationManageEvent e);
}
