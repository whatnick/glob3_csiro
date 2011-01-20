package es.unex.meigas.extImportFromLayer;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.extBase.BaseWizardPanel;

/**
 * Panel to select the attributes of the Tree Shapefile
 */
public class SelectTreeParametersFieldsPanel extends BaseWizardPanel{

	private String[] attributes = null;

	private JLabel jLabelName;
	private JLabel jLabelDBH;
	private JLabel jLabelVolume;
	private JLabel jLabelBark;
	private JLabel jLabelPosition;
	private JLabel jLabelLogHeight;
	private JLabel jLabelCrownDiameter;
	private JLabel jLabelShapeFactor;
	private JLabel jLabelHeightGrowth;
	private JLabel jLabelRadialGrowth;
	private JLabel jLabelAge;
	private JLabel jLabelSpecie;
	private JLabel jLabelHeight;
	private JLabel jLabelNoBarkVolume;

	private JComboBox comboBoxName;
	private JComboBox comboBoxDBH;
	private JComboBox comboBoxVolume;
	private JComboBox comboBoxBark;
	private JComboBox comboBoxPosition;
	private JComboBox comboBoxLogHeight;
	private JComboBox comboBoxCrownDiameter;
	private JComboBox comboBoxShapeFactor;
	private JComboBox comboBoxHeightGrowth;
	private JComboBox comboBoxRadialGrowth;
	private JComboBox comboBoxAge;
	private JComboBox comboBoxSpecie;
	private JComboBox comboBoxHeight;
	private JComboBox comboBoxNoBarkVolume;

	public SelectTreeParametersFieldsPanel(ImportFromLayerWizard wizard) {

		super(wizard);

	}

	public void initGUI(){
		removeAll();
		// border
		double b = 10;
		// vertical space between label and element
		double vs = 5;
		// vertical gap
		double vg = 15;
		// horizontal gap
		double hg = 10;
		// prerrefered
		double p = TableLayout.PREFERRED;
		// fill
		double f = TableLayout.FILL;

		double[] cols = {f, p, vs, p, vg, p, vs, p, f};
		double[] rows = {f, p, hg, p, hg, p, hg, p, hg, p, hg, p, hg, p, hg, p, f};
		TableLayout thisLayout = new TableLayout(cols, rows);

		ImportFromLayerWizard wizard = (ImportFromLayerWizard)m_ParentPanel;

		JPanel jPanel = new JPanel();
		jPanel.setPreferredSize(new java.awt.Dimension(550, 320));
		jPanel.setLayout(thisLayout);
		{
			jLabelName = new JLabel();
			jPanel.add(jLabelName, "1, 1");
			jLabelName.setText("Nombre");
		}
		{
			jLabelDBH = new JLabel();
			jPanel.add(jLabelDBH, "1, 3");
			jLabelDBH.setText("Diámetro normal (cm)");
		}
		{
			jLabelCrownDiameter = new JLabel();
			jPanel.add(jLabelCrownDiameter, "1, 5");
			jLabelCrownDiameter.setText("Diámetro de copa (m)");
		}
		{
			jLabelHeight = new JLabel();
			jPanel.add(jLabelHeight, "1, 7");
			jLabelHeight.setText("Altura total (m)");
		}
		{
			jLabelLogHeight = new JLabel();
			jPanel.add(jLabelLogHeight, "1, 9");
			jLabelLogHeight.setText("Altura de fuste (m)");
		}
		{
			jLabelVolume = new JLabel();
			jPanel.add(jLabelVolume, "1, 11");
			jLabelVolume.setText("Volumen c/c (m3)");
		}
		{
			jLabelNoBarkVolume = new JLabel();
			jPanel.add(jLabelNoBarkVolume, "1, 13");
			jLabelNoBarkVolume.setText("Volumen s/c (m3)");
		}

		{
			jLabelAge = new JLabel();
			jPanel.add(jLabelAge, "5, 1");
			jLabelAge.setText("Edad (años)");
		}
		{
			jLabelRadialGrowth = new JLabel();
			jPanel.add(jLabelRadialGrowth, "5, 3");
			jLabelRadialGrowth.setText("Crecimiento radial (cm/año)");
		}
		{
			jLabelHeightGrowth = new JLabel();
			jPanel.add(jLabelHeightGrowth, "5, 5");
			jLabelHeightGrowth.setText("Crecimiento en altura (cm/año)");
		}
		{
			jLabelBark = new JLabel();
			jPanel.add(jLabelBark, "5, 7");
			jLabelBark.setText("Espesor de corteza (mm)");
		}
		{
			jLabelShapeFactor = new JLabel();
			jPanel.add(jLabelShapeFactor, "5, 9");
			jLabelShapeFactor.setText("Parámetro de forma");
		}
		{
			jLabelSpecie = new JLabel();
			jPanel.add(jLabelSpecie, "5, 11");
			jLabelSpecie.setText("Especie");
		}
		{
			jLabelPosition = new JLabel();
			jPanel.add(jLabelPosition, "5, 13");
			jLabelPosition.setText("Posición");
		}

		attributes = wizard.getAttributes();
		// JComboBoxes
		{
			comboBoxName = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxName,
								   new String[]{"Nombre", "Name"});
			jPanel.add(comboBoxName, "3, 1");
		}
		{
			comboBoxDBH = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxDBH,
								   new String[]{"Dia_normal", "dbh", "Diam"});
			jPanel.add(comboBoxDBH, "3, 3");
		}
		{
			comboBoxCrownDiameter = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxCrownDiameter,
						new String[]{"Dia_copa", "Crown"});
			jPanel.add(comboBoxCrownDiameter, "3, 5");
		}
		{
			comboBoxHeight = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxHeight,
					new String[]{"Altura_Tot", "Height"});
			jPanel.add(comboBoxHeight, "3, 7");
		}
		{
			comboBoxLogHeight = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxLogHeight,
					new String[]{"Altura_Fus", "LogHeight"});
			jPanel.add(comboBoxLogHeight, "3, 9");
		}
		{
			comboBoxVolume = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxVolume,
					new String[]{"Vol_cc_m3", ""});
			jPanel.add(comboBoxVolume, "3, 11");
		}
		{
			comboBoxNoBarkVolume = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxNoBarkVolume,
					new String[]{"Vol_sc_m3", ""});
			jPanel.add(comboBoxNoBarkVolume, "3, 13");
		}
		{
			comboBoxAge = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxAge,
					new String[]{"Edad", "Age"});
			jPanel.add(comboBoxAge, "7, 1");
		//	comboBoxAge.setText("Edad (años)");
		}
		{
			comboBoxRadialGrowth = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxRadialGrowth,
					new String[]{"Crec_rad", ""});
			jPanel.add(comboBoxRadialGrowth, "7, 3");
		//	comboBoxRadialGrowth.setText("Crecimiento radial (cm/año)");
		}
		{
			comboBoxHeightGrowth = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxHeightGrowth,
					new String[]{"Crec_alt", ""});
			jPanel.add(comboBoxHeightGrowth, "7, 5");
		//	comboBoxHeightGrowth.setText("Crecimiento en altura (cm/año)");
		}
		{
			comboBoxBark = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxBark,
					new String[]{"Esp_co", ""});
			jPanel.add(comboBoxBark, "7, 7");
		//	comboBoxBark.setText("Espesor de corteza (mm)");
		}
		{
			comboBoxShapeFactor = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxShapeFactor,
					new String[]{"Par_for", ""});
			jPanel.add(comboBoxShapeFactor, "7, 9");
		//	comboBoxShapeFactor.setText("Parámetro de forma");
		}
		{
			comboBoxSpecie = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxSpecie,
					new String[]{"Espec", ""});
			jPanel.add(comboBoxSpecie, "7, 11");
		//	comboBoxSpecie.setText("Especie");
		}
		{
			comboBoxPosition = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxPosition,
					new String[]{"Posicion", "Posi"});
			jPanel.add(comboBoxPosition, "7, 13");
		//	comboBoxPosition.setText("Posición");
		}
//		{
//			comboBoxCoords = new JComboBox(attributes);
//			wizard.selectOneOfThis(comboBoxCoords, new String[]{"", ""});
//			jPanel.add(comboBoxCoords, "7, 13");
//		//	comboBoxCoords.setText("Coordenadas");
//		}
		this.add(jPanel, BorderLayout.CENTER);

	}


	public boolean hasEnoughInformation() {

		return true;

	}

	@Override
	public boolean isFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getPlotNameFieldIndex() {

		return this.comboBoxName.getSelectedIndex();

	}

	public int getDBHFieldIndex() {

		return this.comboBoxDBH.getSelectedIndex();

	}

	public int getHeightFieldIndex() {

		return this.comboBoxHeight.getSelectedIndex();

	}

	public int getVolumeFieldIndex() {

		return this.comboBoxVolume.getSelectedIndex();

	}

	public int getNoBarkVolumeFieldIndex() {

		return this.comboBoxNoBarkVolume.getSelectedIndex();

	}

	public int getAgeFieldIndex() {

		return this.comboBoxAge.getSelectedIndex();

	}

	public int getRadialGrowthFieldIndex() {

		return this.comboBoxRadialGrowth.getSelectedIndex();

	}

	public int getHeightGrowthFieldIndex() {

		return this.comboBoxHeightGrowth.getSelectedIndex();

	}

	public int getLogHeightFieldIndexIndex() {

		return this.comboBoxLogHeight.getSelectedIndex();

	}

	public int getCrownDiameterFieldIndex() {

		return this.comboBoxCrownDiameter.getSelectedIndex();

	}

	public int getBarkFieldIndex() {

		return this.comboBoxBark.getSelectedIndex();

	}

	public int getPositionFieldIndex() {

		return this.comboBoxPosition.getSelectedIndex();

	}

	public int getSpecieFieldIndex() {

		return this.comboBoxSpecie.getSelectedIndex();

	}

	public int getShapeFactorFieldIndex() {

		return this.comboBoxShapeFactor.getSelectedIndex();

	}

}
