package gui.addition;

import javax.swing.*;
import java.awt.*;

public class IndentedRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus)
    {
        JLabel lbl = (JLabel)super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 5,0,5));
        return lbl;
    }
}
