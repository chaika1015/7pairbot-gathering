package app;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Font;

public class SimulationLoggerPanel extends JPanel implements SimulationLogChangedListener, ListSelectionListener{
    RuleManagement ruleMG;
    SimulationManagement simMG;
    SimulationLog log;
    private DefaultListModel model = new DefaultListModel();
    JList logger = new JList(model);
    JScrollPane scrollpane = new JScrollPane(logger, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    Font font = new Font(Font.SANS_SERIF, 0, 18);

    public SimulationLoggerPanel(SimulationLog log, RuleManagement ruleMG, SimulationManagement simMG) {
        super();
        this.ruleMG = ruleMG;
        this.simMG = simMG;
        this.log = log;
        log.addLogChangedListener(this);
        this.setLayout(new BorderLayout());
        logger.setFont(font);
        logger.addListSelectionListener(this);
        logger.setFixedCellHeight(20);
        this.add(scrollpane, BorderLayout.CENTER);
        System.out.println("Logger created.");
    }

    @Override
    public void simulationLogChanged(SimulationLogChangedEvent evt) {
        model.addElement(log.getSimulationLog());
        logger.ensureIndexIsVisible(model.getSize() - 1);
    }

    @Override
    public void simulationLogClear(SimulationLogChangedEvent e) {
        System.out.println("Logger reset.");
        model.clear();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object obj = logger.getSelectedValue();
        if (obj != null && obj instanceof Log) {
            Log selectedLog = (Log)obj;
            int parentId = selectedLog.getParentId();
            int childId = selectedLog.getChildId();
            int turn = selectedLog.getTurn();
            if (parentId != -1 && childId != -1) {
                int appliedRuleIndex = ruleMG.getApplyIndex(parentId, childId);
                if (appliedRuleIndex != -1) {
                    ruleMG.selectRuleByLogger(appliedRuleIndex);
                }
            }
            else if (turn != -1) {
                simMG.selectHistoryFromLogger(turn);
            }
        }
    }
}