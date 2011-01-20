package es.unex.meigas.extGIS;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import es.unex.meigas.dataObjects.IVectorLayer;

public class IDFieldCellEditor extends AbstractCellEditor implements TableCellEditor {

	private JComboBox m_Combo;

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int col) {

		final LayersAndIDFieldsTableModel model = (LayersAndIDFieldsTableModel) table.getModel();
		IVectorLayer layer = (IVectorLayer) model.getValueAt(row, 1);
		String[] fields = layer.getFieldNames();
		m_Combo =  new JComboBox(fields);
		final int iCol = col;
		final int iRow = row;
		m_Combo.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				model.setValueAt(m_Combo.getSelectedItem(), iRow, iCol);
			}
		});

		return m_Combo;

	}

	public Object getCellEditorValue() {

		if (m_Combo != null){
			return m_Combo.getSelectedItem();
		}
		else{
			return null;
		}

	}


}
