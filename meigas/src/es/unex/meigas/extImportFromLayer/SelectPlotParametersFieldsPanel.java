package es.unex.meigas.extImportFromLayer;

import java.util.ArrayList;

import info.clearthought.layout.TableLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.extBase.BaseWizardPanel;

public class SelectPlotParametersFieldsPanel extends BaseWizardPanel{

	private IVectorLayer layer = null;
	private String[] attributes = null;

	private JLabel jLabelName;
	private JLabel jLabelDate;
	private JLabel jLabelCruiser;
	private JLabel jLabelAspect;
	private JLabel jLabelSlope;
	private JLabel jLabelElevation;
	private JLabel jLabelPlotType;
	private JLabel jLabelFixedRadio;

	private JComboBox comboBoxName;
	private JComboBox comboBoxDate;
	private JComboBox comboBoxCruiser;
	private JComboBox comboBoxAspect;
	private JComboBox comboBoxSlope;
	private JComboBox comboBoxElevation;
	private JComboBox comboBoxPlotType;
	private JComboBox comboBoxFixedRadio;
	private JComboBox[] jComboRadius;
	private JComboBox[] jComboMinDiameter;

	public SelectPlotParametersFieldsPanel(ImportFromLayerWizard wizard) {

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
		double[] rows = {f, p, hg, p, hg, p, hg, p, hg, p, p, p, p, p, p, b};
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
			jLabelDate = new JLabel();
			jPanel.add(jLabelDate, "1, 3");
			jLabelDate.setText("Fecha");
		}
		{
			jLabelCruiser = new JLabel();
			jPanel.add(jLabelCruiser, "1, 5");
			jLabelCruiser.setText("Operario");
		}
		{
			jLabelPlotType = new JLabel();
			jPanel.add(jLabelPlotType, "1, 7");
			jLabelPlotType.setText("Tipo");
		}
		{
			jLabelElevation = new JLabel();
			jPanel.add(jLabelElevation, "5, 1");
			jLabelElevation.setText("Altitud");
		}
		{
			jLabelSlope = new JLabel();
			jPanel.add(jLabelSlope, "5, 3");
			jLabelSlope.setText("Pendiente");
		}
		{
			jLabelAspect = new JLabel();
			jPanel.add(jLabelAspect, "5, 5");
			jLabelAspect.setText("Orientacion");
		}
		{
			jLabelFixedRadio = new JLabel();
			jPanel.add(jLabelFixedRadio, "5, 7");
			jLabelFixedRadio.setText("Radio fijo");
		}
		attributes = wizard.getAttributes();
		// JComboBoxes
		{
			comboBoxName = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxName,
					new String[]{"Nombre", "Name", "Parcel", "Plot"});
			jPanel.add(comboBoxName, "3, 1");

		}
		{
			comboBoxDate = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxDate,
					new String[]{"Fecha", "Date"});
			jPanel.add(comboBoxDate, "3, 3");

		}
		{
			comboBoxCruiser = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxCruiser,
					new String[]{"Oper", "Cruiser"});
			jPanel.add(comboBoxCruiser, "3, 5");
		}
		{
			comboBoxPlotType = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxPlotType,
					new String[]{"Tipo", "Type"});
			jPanel.add(comboBoxPlotType, "3, 7");
		}

		{
			comboBoxElevation = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxElevation,
					new String[]{"Altitud", "Altur", "Elevat"});
			jPanel.add(comboBoxElevation, "7, 1");
		}
		{
			comboBoxSlope = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxSlope,
					new String[]{"Pendi", "Slope"});
			jPanel.add(comboBoxSlope, "7, 3");
		}
		{
			comboBoxAspect = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxAspect,
					new String[]{"Orientac", "Rumbo", "Aspect"});
			jPanel.add(comboBoxAspect, "7, 5");
		}
		{
			comboBoxFixedRadio = new JComboBox(attributes);
			wizard.selectOneOfThis(comboBoxFixedRadio,
					new String[]{"RadioFi", "Radio_fi", "FixedRad", "Fixed_rad", "Radio_1", "Radius_1"});
			jPanel.add(comboBoxFixedRadio, "7, 7");
		}


		{
			//Radius and diameters
			{
				JLabel jLabelRadius = new JLabel();
				jPanel.add(jLabelRadius, "5, 9");
				jLabelRadius.setText("Radio");
			}
			{
				JLabel jLabelMinDiameter = new JLabel();
				jPanel.add(jLabelMinDiameter, "7, 9");
				jLabelMinDiameter.setText("Diámetro mínimo");
			}
			{
				jComboRadius = new JComboBox[5];

				jComboRadius[0] = new JComboBox(attributes);
				wizard.selectOneOfThis(jComboRadius[0],
						new String[]{"Radio1", "Radio_1", "Radius1", "Radius_1"});
				jPanel.add(jComboRadius[0], "5, 10" );

				jComboRadius[1] = new JComboBox(attributes);
				wizard.selectOneOfThis(jComboRadius[1],
						new String[]{"Radio2", "Radio_2", "Radius2", "Radius_2"});
				jPanel.add(jComboRadius[1], "5, 11");


				jComboRadius[2] = new JComboBox(attributes);
				wizard.selectOneOfThis(jComboRadius[2],
						new String[]{"Radio3", "Radio_3", "Radius3", "Radius_3"});
				jPanel.add(jComboRadius[2], "5, 12" );


				jComboRadius[3] = new JComboBox(attributes);
				wizard.selectOneOfThis(jComboRadius[3],
						new String[]{"Radio4", "Radio_4", "Radius4", "Radius_4"});
				jPanel.add(jComboRadius[3], "5, 13");

				jComboRadius[4] = new JComboBox(attributes);
				wizard.selectOneOfThis(jComboRadius[4],
						new String[]{"Radio5", "Radio_5", "Radius5", "Radius_5"});
				jPanel.add(jComboRadius[4], "5, 14" );

			}
			{
				jComboMinDiameter = new JComboBox[5];
				//Double[] dMinDiameter = plot.getMinAcceptableDiameters();

				jComboMinDiameter[0] = new JComboBox(attributes);

				wizard.selectOneOfThis(jComboMinDiameter[0],
						new String[]{"MinDia_1", "MinDiam1", "Min_diam_1", "MinDia1"});
				jPanel.add(jComboMinDiameter[0], "7, 10");


				jComboMinDiameter[1] = new JComboBox(attributes);

				wizard.selectOneOfThis(jComboMinDiameter[1],
						new String[]{"MinDia_2", "MinDiam2", "Min_diam_2", "MinDia2"});
				jPanel.add(jComboMinDiameter[1], "7, 11");


				jComboMinDiameter[2] = new JComboBox(attributes);

				wizard.selectOneOfThis(jComboMinDiameter[2],
						new String[]{"MinDia_3", "MinDiam3", "Min_diam_3", "MinDia3"});
				jPanel.add(jComboMinDiameter[2], "7, 12");

				jComboMinDiameter[3] = new JComboBox(attributes);

				wizard.selectOneOfThis(jComboMinDiameter[3],
						new String[]{"MinDia_4", "MinDiam4", "Min_diam_4", "MinDia4"});
				jPanel.add(jComboMinDiameter[3], "7, 13");

				jComboMinDiameter[4] = new JComboBox(attributes);

				wizard.selectOneOfThis(jComboMinDiameter[4],
						new String[]{"MinDia_5", "MinDiam5", "Min_diam_5", "MinDia5"});
				jPanel.add(jComboMinDiameter[4], "7, 14");
			}
		}
		//////////
		this.add(jPanel);

	}

	public boolean hasEnoughInformation() {

		return true;

	}

	@Override
	public boolean isFinish() {

		return true;

	}

	public int getNameFieldIndex() {

		return this.comboBoxName.getSelectedIndex();
	}

	public int getDateFieldIndex() {

		return this.comboBoxDate.getSelectedIndex();

	}

	public int getCruiserFieldIndex() {

		return this.comboBoxCruiser.getSelectedIndex();

	}

	public int getElevationFieldIndex() {

		return this.comboBoxElevation.getSelectedIndex();

	}

	public int getSlopeFieldIndex() {

		return this.comboBoxAspect.getSelectedIndex();

	}

	public int getAspectFieldIndex() {

		return this.comboBoxAspect.getSelectedIndex();

	}

	public int getPlotTypeFieldIndex() {

		return this.comboBoxPlotType.getSelectedIndex();

	}

	public int getRadiusIndex() {

		return this.comboBoxFixedRadio.getSelectedIndex();

	}

	public Integer[] getConcentricPlotRadiusFieldIndices() {

		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int j = 0; j < jComboRadius.length; j++) {
			int iIndex = jComboRadius[j].getSelectedIndex();
			if (iIndex != -1){
				list.add(new Integer(iIndex));
			}
		}

		return (Integer[]) list.toArray(new Integer[0]);

	}

	public Integer[] getMinimumDiameterFieldIndices() {

		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int j = 0; j < jComboMinDiameter.length; j++) {
			int iIndex = jComboMinDiameter[j].getSelectedIndex();
			if (iIndex != -1){
				list.add(new Integer(iIndex));
			}
		}

		return (Integer[]) list.toArray(new Integer[0]);

	}

}
