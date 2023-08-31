package app;

import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class Menubar extends JMenuBar{
    public Menubar() {
        JMenu[] menus = {
            new JMenu("File"),
            new JMenu("Edit")};
        
            for (JMenu menu : menus) {
                add(menu);
            }
    }
}
