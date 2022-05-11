package gui.main_frame;

import javax.swing.table.DefaultTableModel;

class MyTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 3851037583321935448L;

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}