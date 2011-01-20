package es.unex.meigas.extImportFromLayer;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import info.clearthought.layout.TableLayout;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extImportFromLayer.ImportFromLayerWizard;

/**
 * Panel to select what coordinates of Trees
 */
public class SelectTreeCoordinatesPanel extends BaseWizardPanel implements ActionListener{

	private JRadioButton coordGeomRadio = null;
	private JRadioButton coordFieldsRadio = null;
	protected JComboBox coordXComboBox = null;
	protected JComboBox coordYComboBox = null;
	JLabel coorXLabel = null;
	JLabel coorYLabel = null;

	public SelectTreeCoordinatesPanel(ImportFromLayerWizard wizard) {

		super(wizard);

	}

	public void initGUI(){
		removeAll();
		// border
		double b = 10;
		// vertical space between label and element
		double vs = 5;
		// vertical gap
		double vg = 10;
		// horizontal gap
		double hg = 10;
		// prerrefered
		double p = TableLayout.PREFERRED;
		// fill
		double f = TableLayout.FILL;

		double[] cols = {f,p,vs,p,vs,p,f};
		double[] rows = {f,p,hg,p,hg,p,hg,p,f};
		TableLayout thisLayout = new TableLayout(cols, rows);

		ImportFromLayerWizard wizard = (ImportFromLayerWizard)m_ParentPanel;

		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(550, 220));

		ButtonGroup group = new ButtonGroup();
		coordGeomRadio = new JRadioButton("Usar coordenadas de las geometrías");
		coordGeomRadio.addActionListener(this);
		this.add(coordGeomRadio, "1, 1, 3, 1");
		coordFieldsRadio = new JRadioButton("Coger coordenadas de atributos:");
		coordFieldsRadio.addActionListener(this);
		this.add(coordFieldsRadio, "1, 3, 3, 1");
		group.add(coordGeomRadio);
		group.add(coordFieldsRadio);

		coorXLabel = new JLabel("X: ");
		coorXLabel.setEnabled(false);
		this.add(coorXLabel, "1, 5");
		String[] attributes = wizard.getAttributes();
		coordXComboBox = new JComboBox(attributes);
		coordXComboBox.setEnabled(false);
		wizard.selectOneOfThis(coordXComboBox,
				new String[] {"Coor_x", "Coord_x", "X"});
		this.add(coordXComboBox, "3, 5");
		coorYLabel = new JLabel("Y: ");
		coorYLabel.setEnabled(false);
		this.add(coorYLabel, "1, 7");
		coordYComboBox = new JComboBox(attributes);
		coordYComboBox.setEnabled(false);
		wizard.selectOneOfThis(coordYComboBox,
				new String[]{"Coor_y", "Coord_y", "Y"});
		this.add(coordYComboBox, "3, 7");
		//setVisible(true);

		coordGeomRadio.setSelected(true);
		//refreshGUI();

	}

	public boolean useGeometry(){

		if (coordGeomRadio.isSelected()){
			return true;
		} else {
			return false;
		}

	}

	public boolean hasEnoughInformation() {

		if (coordGeomRadio == null){
			// Check if GUI is initialized.
			// TODO Maybe it must be in other place.
			initGUI();
		}
		if (coordGeomRadio.isSelected()){
			return true;
		} else {
			if (coordXComboBox.getSelectedItem().toString().compareTo("") != 0){
				if (coordYComboBox.getSelectedItem().toString().compareTo("") != 0){
					return true;
				}
			}
		}
		return false;
	}

	private void refreshGUI(){
		boolean coords = coordFieldsRadio.isSelected();
		coorXLabel.setEnabled(coords);
		coorYLabel.setEnabled(coords);
		coordXComboBox.setEnabled(coords);
		coordYComboBox.setEnabled(coords);
	}

	public void actionPerformed(ActionEvent e) {
		refreshGUI();
		m_ParentPanel.updateButtons();
	}

	public boolean isFinish() {

		return false;

	}

	public int getTreeCoordXIdx() {

		return coordXComboBox.getSelectedIndex();

	}

	public int getTreeCoordYIdx() {

		return coordXComboBox.getSelectedIndex();

	}

}
