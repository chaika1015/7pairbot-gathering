package app;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Font;

public class HistoryPanel extends JPanel implements HistoryManageListener, ListSelectionListener{
    private SimulationManagement simMG;
    private String[] turnList;
    private JList historyList;
    private Font font = new Font(Font.SANS_SERIF, 0, 18);

    public HistoryPanel(SimulationManagement simMG) {
        super();
        this.simMG = simMG;
        this.simMG.AddHistoryManageListener(this);
        turnList = simMG.getHistoryStrings();
        historyList = new JList(turnList);
        historyList.setFont(font);
        historyList.setFixedCellWidth(240);
        historyList.addListSelectionListener(this);
        JScrollPane sp = new JScrollPane(historyList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.setLayout(new BorderLayout());
        this.add(sp, BorderLayout.CENTER);
    }

    @Override
    public void historyChanged(HistoryManageEvent e) {
        turnList = simMG.getHistoryStrings();
        historyList.setListData(turnList);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int selectedIndex = historyList.getSelectedIndex();
        simMG.setPresentIndex(selectedIndex);
        simMG.selectHistory();
    }

    @Override
    public void historyCleared(HistoryManageEvent e) {
        turnList = new String[0];
        historyList.setListData(turnList);
    }

    @Override
    public void historySelectedByLogger(HistoryManageEvent e) {
        historyList.setSelectedIndex(simMG.getPresentIndex());
    }
}
