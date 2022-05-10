package gui.main_frame;

import javax.swing.table.DefaultTableModel;

class MyDefaultTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 3851037583321935448L;

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int col) {
        if (col == 2 || col ==5|| col ==6|| col ==7)
            return Integer.class;
        else return String.class;
    }
}