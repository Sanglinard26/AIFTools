package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import aif.Measure;

public final class TableMeasure extends JTable {

    private static final long serialVersionUID = 1L;

    public TableMeasure() {
        super(new MeasureModel());
    }

    @Override
    public TableModel getModel() {
        return super.getModel();
    }

}

final class MeasureModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] ENTETES = new String[] { "Nom voie", "Cocher pour supprimer?" };

    private List<Measure> listElements;
    private List<Boolean> listDrawn;

    public MeasureModel() {
        listElements = new ArrayList<Measure>();
        listDrawn = new ArrayList<Boolean>();
    }

    @Override
    public String getColumnName(int column) {
        return ENTETES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return String.class;
        case 1:
            return Boolean.class;
        default:
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 1) ? true : false;

    }

    @Override
    public int getColumnCount() {
        return ENTETES.length;
    }

    @Override
    public int getRowCount() {
        return listElements.size();
    }

    @Override
    public Object getValueAt(int row, int col) {

        switch (col) {
        case 0:
            return listElements.get(row);
        case 1:
            return listDrawn.get(row);
        default:
            return null;
        }

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        boolean oldValue = this.listDrawn.get(rowIndex);
        this.listDrawn.set(rowIndex, !oldValue);
        fireTableCellUpdated(rowIndex, 1);
    }

    public final void clearList() {
        this.listElements.clear();
        this.listDrawn.clear();
        fireTableDataChanged();
    }

    public final void addElement(Measure element) {
        this.listElements.add(element);
        this.listDrawn.add(false);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public final void removeElement(int row) {
        this.listElements.remove(row);
        fireTableRowsDeleted(row, row);
    }

}
