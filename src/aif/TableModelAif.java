package aif;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class TableModelAif extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final String[] COLUMN = {"NOM", "NOMBRE DE POINTS", "NOMBRE DE VOIES"};

	private List<Aif> listAif = new ArrayList<Aif>();

	public TableModelAif() {
		super();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN[column];
	}

	@Override
	public int getColumnCount() {
		return COLUMN.length;
	}

	@Override
	public int getRowCount() {
		return listAif.size();
	}

	public final void addAif(Aif aif)
	{
		if(!listAif.contains(aif))
		{
			listAif.add(aif);
			fireTableDataChanged();
		}
	}

	public final void addAif(Aif[] tabAif)
	{
		for(Aif aif : tabAif)
		{
			if(!listAif.contains(aif))
			{
				listAif.add(aif);
			}
		}
		fireTableDataChanged();
	}
	
	public final void removeAif(int[] rows)
	{
		List<Aif> selectedAif = new ArrayList<Aif>(rows.length);
		for(int row : rows)
		{
			selectedAif.add(listAif.get(row));
		}
		listAif.removeAll(selectedAif);
		fireTableDataChanged();
	}

	public final void clearTable()
	{
		if(listAif.size() > 0)
		{
			listAif.clear();
			fireTableDataChanged();
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		return listAif.get(row).getInfos()[col];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public final List<Aif> getListAif() {
		return listAif;
	}

}
