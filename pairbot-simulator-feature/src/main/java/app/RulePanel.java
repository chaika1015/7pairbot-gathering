package app;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RulePanel extends JPanel implements ListSelectionListener, RuleManageListener, ActionListener{
    private RuleManagement ruleMG;
    private SimulationManagement simMG;
    private Algorithm[] rules;
    private Font font = new Font(Font.SANS_SERIF, 0, 18);
    private JList<Algorithm> ruleList;
    private JTextArea ruleView;
    private JToolBar ruleToolBar = new JToolBar();
    private JButton addBt = null;
    private JButton editBt = null;
    private JButton deleteBt = null;
    private JButton saveBt = null;

    public RulePanel(RuleManagement ruleMG, SimulationManagement simMG) {
        super();
        this.ruleMG = ruleMG;
        this.simMG = simMG;
        ruleMG.AddManageListener(this);
        rules = ruleMG.getRules().toArray(new Algorithm[ruleMG.getRulesSize()]);
        ruleList = new JList<Algorithm>(rules);
        ruleList.setFont(font);
        ruleList.addListSelectionListener(this);
        JScrollPane sp = new JScrollPane(ruleList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        ruleView = new JTextArea(5, 25);
        ruleView.setEditable(false);
        ruleView.setFont(font);
        ruleView.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

        ruleToolBar.setFloatable(false);
        addBt = new JButton(new ImageIcon("src/main/java/app/img/ruleaddbutton.png"));
        addBt.setBorderPainted(false);
        addBt.addActionListener(this);
        ruleToolBar.add(addBt);
        editBt = new JButton(new ImageIcon("src/main/java/app/img/ruleeditbutton.png"));
        editBt.setBorderPainted(false);
        editBt.addActionListener(this);
        ruleToolBar.add(editBt);
        deleteBt = new JButton(new ImageIcon("src/main/java/app/img/ruledeletebutton.png"));
        deleteBt.setBorderPainted(false);
        deleteBt.addActionListener(this);
        ruleToolBar.add(deleteBt);
        saveBt = new JButton(new ImageIcon("src/main/java/app/img/rulesavebutton.png"));
        saveBt.setBorderPainted(false);
        saveBt.addActionListener(this);
        ruleToolBar.add(saveBt);

        this.setLayout(new BorderLayout());
        this.add(sp, BorderLayout.CENTER);
        this.add(ruleView, BorderLayout.NORTH);
        this.add(ruleToolBar, BorderLayout.SOUTH);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int selectedIndex = ruleList.getSelectedIndex();
        if (selectedIndex != -1) {
            Algorithm rule = rules[selectedIndex];
            ruleView.setText(rule.showRule());
        }
        else {
            ruleView.setText("");
        }
    }

    @Override
    public void ruleSelectedByLogger(RuleManageEvent e) {
        int selectedIndexByLogger = ruleMG.getSelectedIndex();
        ruleList.setSelectedIndex(selectedIndexByLogger);
        ruleList.ensureIndexIsVisible(selectedIndexByLogger);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBt) {
            if (simMG.isSimulationRunning()) {
                JOptionPane.showMessageDialog(this, "シミュレーションが動作中です");
                return;
            }
            while (true) {
                String[] selectValues = {"追加", "キャンセル"};
                String[] pointValues = {"0", "1", "2", "3", "4", "5", "6"};
                String[] conditionValues = {"=", ">=", "<=", "pass"};
                String[] occupyValues = {"0", "1", "2"};
                JPanel ruleAddPanel = new JPanel();
                GridBagLayout layout = new GridBagLayout();
                ruleAddPanel.setLayout(layout);
                GridBagConstraints gbc = new GridBagConstraints();
                JLabel idLabel = new JLabel("識別番号");
                JTextField parentIdField = new JTextField(4);
                JLabel idSpanLabel = new JLabel(" - ");
                JTextField childIdField = new JTextField(4);
                gbc.gridx = 0;
                gbc.gridy = 0;
                layout.setConstraints(idLabel, gbc);
                ruleAddPanel.add(idLabel);
                gbc.gridx = 1;
                layout.setConstraints(parentIdField, gbc);
                ruleAddPanel.add(parentIdField);
                gbc.gridx = 2;
                layout.setConstraints(idSpanLabel, gbc);
                ruleAddPanel.add(idSpanLabel);
                gbc.gridx = 3;
                layout.setConstraints(childIdField, gbc);
                ruleAddPanel.add(childIdField);
                JLabel robotstatusLabel = new JLabel("ペアボットの状態");
                JRadioButton radioShort = new JRadioButton("Short", true);
                radioShort.setActionCommand("short");
                JRadioButton radioLong = new JRadioButton("Long");
                radioLong.setActionCommand("long");
                ButtonGroup gbGroup = new ButtonGroup();
                gbGroup.add(radioShort);
                gbGroup.add(radioLong);
                gbc.gridx = 0;
                gbc.gridy = 1;
                layout.setConstraints(robotstatusLabel, gbc);
                ruleAddPanel.add(robotstatusLabel);
                gbc.gridx = 1;
                layout.setConstraints(radioShort, gbc);
                ruleAddPanel.add(radioShort);
                gbc.gridx = 2;
                layout.setConstraints(radioLong, gbc);
                ruleAddPanel.add(radioLong);
                JLabel pairPointLabel = new JLabel("ペアの位置(Long状態の時)");
                JComboBox pairPointCombo = new JComboBox(pointValues);
                gbc.gridx = 0;
                gbc.gridy = 2;
                layout.setConstraints(pairPointLabel, gbc);
                ruleAddPanel.add(pairPointLabel);
                gbc.gridx = 1;
                layout.setConstraints(pairPointCombo, gbc);
                ruleAddPanel.add(pairPointCombo);
                JLabel[] occupyLabel = new JLabel[7];
                JComboBox[] occupyCombo = new JComboBox[7];
                JComboBox[] conditionCombo = new JComboBox[7];
                for (int i=0; i<7; i++) {
                    occupyLabel[i] = new JLabel("Occupy(r_i." + i + ") ");
                    occupyCombo[i] = new JComboBox(occupyValues);
                    conditionCombo[i] = new JComboBox(conditionValues);
                    gbc.gridx = 0;
                    gbc.gridy = 3 + i;
                    layout.setConstraints(occupyLabel[i], gbc);
                    ruleAddPanel.add(occupyLabel[i]);
                    gbc.gridx = 1;
                    layout.setConstraints(conditionCombo[i], gbc);
                    ruleAddPanel.add(conditionCombo[i]);
                    gbc.gridx = 2;
                    layout.setConstraints(occupyCombo[i], gbc);
                    ruleAddPanel.add(occupyCombo[i]);
                }
                JLabel nextPointLabel = new JLabel("移動後の位置");
                JComboBox nextPointCombo = new JComboBox(pointValues);
                gbc.gridx = 0;
                gbc.gridy = 10;
                layout.setConstraints(nextPointLabel, gbc);
                ruleAddPanel.add(nextPointLabel);
                gbc.gridx = 1;
                layout.setConstraints(nextPointCombo, gbc);
                ruleAddPanel.add(nextPointCombo);
                int select = JOptionPane.showOptionDialog(this, ruleAddPanel, "ルールの追加", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, selectValues, selectValues[0]);
        
                if (select == JOptionPane.CLOSED_OPTION || select == 1) {
                    break;
                }
                else if (select == 0) {
                    try {
                        int parentId = Integer.parseInt(parentIdField.getText());
                        int childId = Integer.parseInt(childIdField.getText());
                        if (parentId < 0 || childId < 0) {
                            JOptionPane.showMessageDialog(this, "識別番号の欄には正の数値を入力してください");
                            continue;
                        }
                        if (parentId >= 10000 || childId >= 10000) {
                            JOptionPane.showMessageDialog(this, "識別番号の欄には10000以下の数値を入力してください");
                            continue;
                        }
                        if (ruleMG.getApplyIndex(parentId, childId) != -1) {
                            JOptionPane.showMessageDialog(this, "既に存在する識別番号です");
                            continue;
                        }
                        boolean isShort = true;
                        int pairPointId = 0;
                        if (gbGroup.getSelection().getActionCommand().equals("long")) {
                            isShort = false;
                            pairPointId = Integer.parseInt((String)pairPointCombo.getSelectedItem());
                        }
                        int[] occupy = new int[7];
                        String[] conditions = new String[7];
                        for (int i=0; i<7; i++) {
                            occupy[i] = Integer.parseInt((String)occupyCombo[i].getSelectedItem());
                            switch (conditionCombo[i].getSelectedIndex()) {
                                case 1:
                                    conditions[i] = "me";
                                    break;
                                case 2:
                                    conditions[i] = "le";
                                    break;
                                case 3:
                                    conditions[i] = "pass";
                                    break;
                                case 0:
                                default:
                                    conditions[i] = "eq";
                            }
                        }
                        int nextPointId = Integer.parseInt((String)nextPointCombo.getSelectedItem());
                        Algorithm result = new Algorithm(parentId, childId, isShort, pairPointId, occupy, conditions, nextPointId);
                        ruleMG.addRule(result);
                        break;
                    }
                    catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(this, "識別番号の欄に正しい数値を入力してください");
                    }
                }
            }
        }
        else if (e.getSource() == editBt) {
            if (simMG.isSimulationRunning()) {
                JOptionPane.showMessageDialog(this, "シミュレーションが動作中です");
                return;
            }
            int selectedIndex = ruleList.getSelectedIndex();
            if (selectedIndex == -1){
                JOptionPane.showMessageDialog(this, "ルールが選択されていません");
                return;
            }
            while (true) {
                String[] selectValues = {"更新", "キャンセル"};
                String[] pointValues = {"0", "1", "2", "3", "4", "5", "6"};
                String[] conditionValues = {"=", ">=", "<=", "pass"};
                String[] occupyValues = {"0", "1", "2"};
                JPanel ruleAddPanel = new JPanel();
                GridBagLayout layout = new GridBagLayout();
                ruleAddPanel.setLayout(layout);
                GridBagConstraints gbc = new GridBagConstraints();
                JLabel idLabel = new JLabel("識別番号");
                JTextField parentIdField = new JTextField(4);
                parentIdField.setText("" + rules[selectedIndex].getParentId());
                parentIdField.setEditable(false);
                JLabel idSpanLabel = new JLabel(" - ");
                JTextField childIdField = new JTextField(4);
                childIdField.setText("" + rules[selectedIndex].getChildId());
                childIdField.setEditable(false);
                gbc.gridx = 0;
                gbc.gridy = 0;
                layout.setConstraints(idLabel, gbc);
                ruleAddPanel.add(idLabel);
                gbc.gridx = 1;
                layout.setConstraints(parentIdField, gbc);
                ruleAddPanel.add(parentIdField);
                gbc.gridx = 2;
                layout.setConstraints(idSpanLabel, gbc);
                ruleAddPanel.add(idSpanLabel);
                gbc.gridx = 3;
                layout.setConstraints(childIdField, gbc);
                ruleAddPanel.add(childIdField);
                JLabel robotstatusLabel = new JLabel("ペアボットの状態");
                JRadioButton radioShort = new JRadioButton("Short", true);
                radioShort.setActionCommand("short");
                JRadioButton radioLong = new JRadioButton("Long");
                radioLong.setActionCommand("long");
                ButtonGroup gbGroup = new ButtonGroup();
                gbGroup.add(radioShort);
                gbGroup.add(radioLong);
                gbc.gridx = 0;
                gbc.gridy = 1;
                layout.setConstraints(robotstatusLabel, gbc);
                ruleAddPanel.add(robotstatusLabel);
                gbc.gridx = 1;
                layout.setConstraints(radioShort, gbc);
                ruleAddPanel.add(radioShort);
                gbc.gridx = 2;
                layout.setConstraints(radioLong, gbc);
                ruleAddPanel.add(radioLong);
                JLabel pairPointLabel = new JLabel("ペアの位置(Long状態の時)");
                JComboBox pairPointCombo = new JComboBox(pointValues);
                if (!rules[selectedIndex].getIsShort()) {
                    radioLong.setSelected(true);
                    pairPointCombo.setSelectedIndex(rules[selectedIndex].getPairPointId());
                }
                gbc.gridx = 0;
                gbc.gridy = 2;
                layout.setConstraints(pairPointLabel, gbc);
                ruleAddPanel.add(pairPointLabel);
                gbc.gridx = 1;
                layout.setConstraints(pairPointCombo, gbc);
                ruleAddPanel.add(pairPointCombo);
                int[] selectedOccupy = rules[selectedIndex].getOccupy();
                String[] selectedConditions = rules[selectedIndex].getConditions();
                JLabel[] occupyLabel = new JLabel[7];
                JComboBox[] occupyCombo = new JComboBox[7];
                JComboBox[] conditionCombo = new JComboBox[7];
                for (int i=0; i<7; i++) {
                    occupyLabel[i] = new JLabel("Occupy(r_i." + i + ") ");
                    occupyCombo[i] = new JComboBox(occupyValues);
                    occupyCombo[i].setSelectedIndex(selectedOccupy[i]);
                    conditionCombo[i] = new JComboBox(conditionValues);
                    switch (selectedConditions[i]) {
                        case "me":
                            conditionCombo[i].setSelectedIndex(1);
                            break;
                        case "le":
                            conditionCombo[i].setSelectedIndex(2);
                            break;
                        case "pass":
                            conditionCombo[i].setSelectedIndex(3);
                            break;
                        case "eq":
                        default:
                            break;
                    }
                    gbc.gridx = 0;
                    gbc.gridy = 3 + i;
                    layout.setConstraints(occupyLabel[i], gbc);
                    ruleAddPanel.add(occupyLabel[i]);
                    gbc.gridx = 1;
                    layout.setConstraints(conditionCombo[i], gbc);
                    ruleAddPanel.add(conditionCombo[i]);
                    gbc.gridx = 2;
                    layout.setConstraints(occupyCombo[i], gbc);
                    ruleAddPanel.add(occupyCombo[i]);
                }
                JLabel nextPointLabel = new JLabel("移動後の位置");
                JComboBox nextPointCombo = new JComboBox(pointValues);
                nextPointCombo.setSelectedIndex(rules[selectedIndex].getNextPointId());
                gbc.gridx = 0;
                gbc.gridy = 10;
                layout.setConstraints(nextPointLabel, gbc);
                ruleAddPanel.add(nextPointLabel);
                gbc.gridx = 1;
                layout.setConstraints(nextPointCombo, gbc);
                ruleAddPanel.add(nextPointCombo);
                int select = JOptionPane.showOptionDialog(this, ruleAddPanel, "ルールの編集", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, selectValues, selectValues[0]);
        
                if (select == JOptionPane.CLOSED_OPTION || select == 1) {
                    break;
                }
                else if (select == 0) {
                    try {
                        int parentId = Integer.parseInt(parentIdField.getText());
                        int childId = Integer.parseInt(childIdField.getText());
                        boolean isShort = true;
                        int pairPointId = 0;
                        if (gbGroup.getSelection().getActionCommand().equals("long")) {
                            isShort = false;
                            pairPointId = Integer.parseInt((String)pairPointCombo.getSelectedItem());
                        }
                        int[] occupy = new int[7];
                        String[] conditions = new String[7];
                        for (int i=0; i<7; i++) {
                            occupy[i] = Integer.parseInt((String)occupyCombo[i].getSelectedItem());
                            switch (conditionCombo[i].getSelectedIndex()) {
                                case 1:
                                    conditions[i] = "me";
                                    break;
                                case 2:
                                    conditions[i] = "le";
                                    break;
                                case 3:
                                    conditions[i] = "pass";
                                    break;
                                case 0:
                                default:
                                    conditions[i] = "eq";
                            }
                        }
                        int nextPointId = Integer.parseInt((String)nextPointCombo.getSelectedItem());
                        Algorithm result = new Algorithm(parentId, childId, isShort, pairPointId, occupy, conditions, nextPointId);
                        ruleMG.updateRule(selectedIndex, result);
                        ruleList.setSelectedIndex(selectedIndex);
                        break;
                    }
                    catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(this, "識別番号の欄に正しい数値を入力してください");
                    }
                }
            }
        }
        else if (e.getSource() == deleteBt) {
            if (simMG.isSimulationRunning()) {
                JOptionPane.showMessageDialog(this, "シミュレーションが動作中です");
                return;
            }
            int selectedIndex = ruleList.getSelectedIndex();
            if (selectedIndex == -1){
                JOptionPane.showMessageDialog(this, "ルールが選択されていません");
            }
            String[] selectValues = {"削除", "キャンセル"};
            int select = JOptionPane.showOptionDialog(this, "Rule" + rules[selectedIndex].getParentId() + "-" + rules[selectedIndex].getChildId() + "を削除しますか？", "ルールの削除", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, selectValues, selectValues[0]);
            if (select == JOptionPane.CLOSED_OPTION || select == 1) {
                return;
            }
            else if (select == 0) {
                ruleMG.deleteRule(selectedIndex);
            }
        }
        else if (e.getSource() == saveBt) {
            if (simMG.isSimulationRunning()) {
                JOptionPane.showMessageDialog(this, "シミュレーションが動作中です");
                return;
            }
            if (!ruleMG.isEdited()) {
                JOptionPane.showMessageDialog(this, "ルールは編集されていません");
                return;
            }
            String[] selectValues = {"上書き保存", "キャンセル"};
            int select = JOptionPane.showOptionDialog(this, "ルールを上書き保存しますか？", "ルールの上書き保存", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, selectValues, selectValues[0]);
            if (select == JOptionPane.CLOSED_OPTION || select == 1) {
                return;
            }
            else if (select == 0) {
                ruleMG.saveRule();
            }
        }
    }

    @Override
    public void ruleChanged(RuleManageEvent e) {
        rules = ruleMG.getRules().toArray(new Algorithm[ruleMG.getRulesSize()]);
        ruleList.setListData(rules);
    }
}
