package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import aif.Measure;

public final class TableMeasure extends JTable {

    private static final long serialVersionUID = 1L;

    static final String[] CONDITIONS = new String[] { "", "<", ">", "<=", ">=", "=", "!=", "[within]", "]within[" };

    public TableMeasure() {
        super(new MeasureModel());

        getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<>(CONDITIONS)));
    }

    @Override
    public TableModel getModel() {
        return super.getModel();
    }

}

final class MeasureModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] ENTETES = new String[] { "Nom voie", "Supprimer?", "Min / Max", "Condition", "Valeur 1", "Valeur 2" };

    private List<Measure> listElements;
    private List<Condition> conditions;

    public MeasureModel() {
        listElements = new ArrayList<Measure>();
        conditions = new ArrayList<Condition>();
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
        case 2:
            return String.class;
        case 4:
            return Float.class;
        case 5:
            return Float.class;
        default:
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;

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
            return listElements.get(row).getWasted();
        case 2:
            return listElements.get(row).getMinValue() + " / " + listElements.get(row).getMaxValue();
        case 3:
            return conditions.get(row).getOperateur();
        case 4:
            return conditions.get(row).getVal1();
        case 5:
            return conditions.get(row).getVal2();
        default:
            return null;
        }

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        Condition condition;

        switch (columnIndex) {
        case 0:
            listElements.get(rowIndex).setName(aValue.toString());
            fireTableCellUpdated(rowIndex, 0);
            break;
        case 1:
            boolean oldValue = listElements.get(rowIndex).getWasted();
            listElements.get(rowIndex).setWasted(!oldValue);
            fireTableCellUpdated(rowIndex, 1);
            break;
        case 3:
            condition = conditions.get(rowIndex);
            condition.setOperateur(aValue.toString());
            break;
        case 4:
            condition = conditions.get(rowIndex);
            condition.setVal1((Float) aValue);
            break;
        case 5:
            condition = conditions.get(rowIndex);
            condition.setVal2((Float) aValue);
            break;
        default:
            break;
        }

    }

    public final List<Condition> getConditions() {
        return conditions;
    }

    public final void clearList() {
        this.listElements.clear();
        this.conditions.clear();
        fireTableDataChanged();
    }

    public final void addElement(Measure element) {
        this.listElements.add(element);
        this.conditions.add(new Condition(element));
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public final void removeElement(int row) {
        this.listElements.remove(row);
        this.conditions.remove(row);
        fireTableRowsDeleted(row, row);
    }

}

final class Condition {

    private final Measure measure;
    private String operateur;
    private Float val1;
    private Float val2;

    public Condition(Measure measure) {
        this.measure = measure;
        this.operateur = "";
        this.val1 = null;
        this.val2 = null;
    }

    public final boolean isEmpty() {
        return "".equals(operateur);
    }

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }

    public Float getVal1() {
        return val1;
    }

    public void setVal1(Float val1) {
        this.val1 = val1;
    }

    public Float getVal2() {
        return val2;
    }

    public void setVal2(Float val2) {
        this.val2 = val2;
    }

    public final void applyCondition() {
        if (isEmpty()) {
            measure.applyCondition(0, measure.getData().size());
            return;
        }

        measure.clearCondition();
        List<Object> data = measure.getData();

        for (int i = 0; i < data.size(); i++) {
            if (isConditionChecked(data.get(i))) {
                measure.applyCondition(i);
            }
        }
    }

    private boolean isConditionChecked(Object objectValue) {
        if ("".equals(operateur) || this.val1 == null) {
            return true;
        }

        if ("NaN".equals(objectValue.toString())) {
            objectValue = Double.NaN;
        }

        if (objectValue instanceof Number) {
            double value = ((Number) objectValue).doubleValue();

            switch (operateur) {
            case "<":
                return value < val1;
            case ">":
                return value > val1;
            case "<=":
                return value <= val1;
            case ">=":
                return value >= val1;
            case "=":
                return Double.compare(value, val1) == 0;
            case "!=":
                if (Double.isNaN(val1)) {
                    return !Double.isNaN(value);
                }
                return Double.compare(value, val1) != 0;
            case "[within]":
                if (this.val2 != null) {
                    return value >= val1 && value <= val2;
                }
                break;
            case "]within[":
                if (this.val2 != null) {
                    return value > val1 && value < val2;
                }
                break;
            default:
                break;
            }
        }

        return true;
    }

}
