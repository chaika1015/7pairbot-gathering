package app;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationToolPanel extends JPanel implements ActionListener{
    private JToolBar toolBar = new JToolBar();
    private SimulationManagement simMG = null;
    private JButton startBt = null;
    private JButton stopBt = null;
    private JButton resetBt = null;

    public SimulationToolPanel(SimulationManagement simMG) {
        super();
        this.simMG = simMG;
        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.CENTER);
        toolBar.setFloatable(false);
        
        startBt = new JButton(new ImageIcon("src/main/java/app/img/playbutton.png"));
        startBt.addActionListener(this);
        startBt.setBorderPainted(false);
        toolBar.add(startBt);
        stopBt = new JButton(new ImageIcon("src/main/java/app/img/pausebutton.png"));
        stopBt.addActionListener(this);
        stopBt.setBorderPainted(false);
        toolBar.add(stopBt);
        resetBt = new JButton(new ImageIcon("src/main/java/app/img/stopbutton.png"));
        resetBt.addActionListener(this);
        resetBt.setBorderPainted(false);
        toolBar.add(resetBt);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBt) {
            System.out.println("startButton pressed.");
            simMG.startSimulate();
        }
        else if (e.getSource() == stopBt) {
            System.out.println("stopButton pressed.");
            simMG.stopSimulate();
        }
        else if (e.getSource() == resetBt) {
            System.out.println("resetButton pressed.");
            simMG.resetSimulate();
        }
    }
}
