package app;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Font;
import java.awt.BorderLayout;
import java.util.Enumeration;

public class Simulator extends JFrame implements TabManageListener{
    private SimulationLog log = new SimulationLog();
    private SimulationManagement simMG = new SimulationManagement();
    private RuleManagement ruleMG = new RuleManagement();
    private SimulationPanel simPanel = new SimulationPanel(simMG, ruleMG, log);
    private SimulationLoggerPanel simLogPanel = new SimulationLoggerPanel(log, ruleMG, simMG);
    private SimulationToolPanel toolPanel = new SimulationToolPanel(simMG);
    private RulePanel rulePanel = new RulePanel(ruleMG, simMG);
    private HistoryPanel hisPanel = new HistoryPanel(simMG);
    private InitialStatusPanel iniPanel = new InitialStatusPanel(simMG);
    private JPanel leftPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JTabbedPane tabbedpane = new JTabbedPane();
    private Font font = new Font("メイリオ", 0, 16);

    public Simulator() {
        this.setTitle("Pairbot-simulator");
        this.setBounds(0, 0, 1280, 720);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Enumeration<Object> enumeration = UIManager.getDefaults().keys();
        while(enumeration.hasMoreElements()){
            Object key = enumeration.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof java.awt.Font){
                UIManager.put(key.toString(), font);
            }
        }

        ruleMG.AddTabManageListener(this);

        // setJMenuBar(new Menubar());
        this.setLayout(new BorderLayout());
        leftPanel.setLayout(new BorderLayout());
        centerPanel.setLayout(new BorderLayout());
        this.add(leftPanel, BorderLayout.WEST);
        this.add(centerPanel, BorderLayout.CENTER);

        tabbedpane.add("ルール", rulePanel);
        tabbedpane.add("初期状況", iniPanel);
        tabbedpane.setFont(font);

        centerPanel.add(simPanel, BorderLayout.CENTER);
        simPanel.setFocusable(true);
        centerPanel.add(simLogPanel, BorderLayout.SOUTH);
        centerPanel.add(toolPanel, BorderLayout.NORTH);
        leftPanel.add(tabbedpane, BorderLayout.CENTER);
        centerPanel.add(hisPanel, BorderLayout.EAST);

        this.setVisible(true);
        simMG.startSimulate();
    }

    @Override
    public void selectRuleTab(TabManageEvent e) {
        if (tabbedpane.getSelectedIndex() != 0) {
            tabbedpane.setSelectedIndex(0);
        }
    }
}
