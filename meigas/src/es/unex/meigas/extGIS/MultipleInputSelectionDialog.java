package es.unex.meigas.extGIS;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import es.unex.meigas.dataObjects.IVectorLayer;

public class MultipleInputSelectionDialog extends JDialog {

	private JButton jButtonOK;
	private JButton jButtonCancel;
	private JTable jTable;
	private JScrollPane jScrollPane;
	private LayerAndIDField[] m_Selection;

	public MultipleInputSelectionDialog(Frame window, LayerAndIDField[] layers) {

		super(window, "", true);

		initGUI(layers);

	}

	private void initGUI(LayerAndIDField[] layers) {

		TableLayout thisLayout = new TableLayout(new double[][] {
				{3.0, TableLayout.FILL, TableLayout.FILL, 7.0, TableLayout.FILL, 3.0},
				{3.0, TableLayout.FILL, TableLayout.FILL, 3.0, TableLayout.MINIMUM, 3.0}});
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		getContentPane().setLayout(thisLayout);
		this.setResizable(false);
		{
			jButtonOK = new JButton();
			getContentPane().add(jButtonOK, "2, 4");
			jButtonOK.setText("Aceptar");
			jButtonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					saveAndClose();
				}
			});
		}
		{
			jButtonCancel = new JButton();
			getContentPane().add(jButtonCancel, "4, 4");
			jButtonCancel.setText("Cancelar");
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					cancel();
				}
			});
		}
		{
			jScrollPane = new JScrollPane();
			getContentPane().add(jScrollPane, "1, 1, 4, 2");
			{
				TableModel jTableModel =
					new LayersAndIDFieldsTableModel(layers);
				jTable = new JTable();
				jScrollPane.setViewportView(jTable);
				jTable.setModel(jTableModel);
				TableColumn col = jTable.getColumnModel().getColumn(2);
				col.setCellEditor(new IDFieldCellEditor());
			}
		}
		this.setSize(301, 239);

	}

	protected void saveAndClose() {

		ArrayList<LayerAndIDField> list = new ArrayList<LayerAndIDField>();
		TableModel model = jTable.getModel();
		int iCount = model.getRowCount();
		for (int i = 0; i < iCount; i++) {
			boolean bSelected = ((Boolean)model.getValueAt(i, 0)).booleanValue();
			if (bSelected){
				LayerAndIDField layer = new LayerAndIDField(
							(IVectorLayer) model.getValueAt(i, 1),
							(String) model.getValueAt(i, 2));
				list.add(layer);
			}
		}

		m_Selection = (LayerAndIDField[]) list.toArray(new LayerAndIDField[0]);

		this.dispose();
		this.setVisible(false);


	}

	protected void cancel() {

		m_Selection = null;

		this.dispose();
		this.setVisible(false);

	}

	public LayerAndIDField[] getSelectedLayerAndIDFields() {

		return m_Selection;

	}

}
