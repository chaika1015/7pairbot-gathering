package app;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InitialStatusPanel extends JPanel implements ActionListener{
    SimulationManagement simMG;
    int robotCount = -1;
    JComboBox[] initialRobotPointIdCombo;
    JLabel[] initialRobotPointIdLabel;
    int pointSum = -1;
    String[] pointIds;
    JButton generateBt = new JButton("生成");
    private Font font = new Font("メイリオ", 0, 18);

    public InitialStatusPanel(SimulationManagement simMG) {
        super();
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        while (pointSum == -1) {
            pointSum = simMG.getPointSum();
        }
        pointIds = new String[pointSum];
        for (int i=0; i<pointSum; i++) {
            pointIds[i] = "" + i;
        }
        this.simMG = simMG;
        while (robotCount == -1) {
            robotCount = simMG.getRobotCount();
        }
        initialRobotPointIdCombo = new JComboBox[robotCount];
        initialRobotPointIdLabel = new JLabel[robotCount];
        for (int i=0; i<robotCount; i++) {
            initialRobotPointIdCombo[i] = new JComboBox(pointIds);
            initialRobotPointIdCombo[i].setFont(font);
            initialRobotPointIdLabel[i] = new JLabel("Pairbot[" + i + "]'s pointId: ");
            initialRobotPointIdLabel[i].setFont(font);
            gbc.gridx = 0;
            gbc.gridy = i;
            layout.setConstraints(initialRobotPointIdLabel[i], gbc);
            this.add(initialRobotPointIdLabel[i]);
            gbc.gridx = 1;
            layout.setConstraints(initialRobotPointIdCombo[i], gbc);
            this.add(initialRobotPointIdCombo[i]);
        }
        generateBt.setFont(font);
        generateBt.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = robotCount;
        layout.setConstraints(generateBt, gbc);
        this.add(generateBt);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateBt) {
            if (simMG.isSimulationRunning()) {
                JOptionPane.showMessageDialog(this, "シミュレーションが動作中です");
                return;
            }
            if (simMG.isSimulationAlived()) {
                String[] selectValues = {"はい", "キャンセル"};
                int select = JOptionPane.showOptionDialog(this, "シミュレーションが初期化されます。\nよろしいですか？", "初期状態の変更", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, selectValues, selectValues[0]);
                if (select == JOptionPane.CLOSED_OPTION || select == 1) {
                    return;
                }
            }
            int[] initialRobotPointId = new int[robotCount];
            for (int i=0; i<robotCount; i++) {
                initialRobotPointId[i] = -1;
            }
            for (int i=0; i<robotCount; i++) {
                int tmp = Integer.parseInt((String)initialRobotPointIdCombo[i].getSelectedItem());
                for (int j=0; j<i; j++) {
                    if (initialRobotPointId[j] == tmp) {
                        JOptionPane.showMessageDialog(this, "一つの点に二つ以上のペアボットは存在できません");
                        return;
                    }
                }
                initialRobotPointId[i] = tmp;
            }
            simMG.changeInitialStatus(initialRobotPointId);
        }
    }
}
