package es.unex.meigas.extOperations;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import info.clearthought.layout.TableLayout;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;

public class ParameterSelectionPanel extends BaseWizardPanel {

	public static final int DENSITY = 0;
	public static final int BASIMETRIC_AREA = 1;
	public static final int VOLUME = 2;

	private JLabel jLabelParameter;
	private JComboBox jComboBoxParameter;

	public ParameterSelectionPanel(MainWizardWindow panel) {

		super(panel);
		initGUI();

	}

	@Override
	public boolean hasEnoughInformation() {

		return true;

	}

	@Override
	public void initGUI() {

		TableLayout thisLayout = new TableLayout(new double[][] {
				{TableLayout.FILL, TableLayout.FILL, 6.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL},
				{TableLayout.FILL, TableLayout.MINIMUM, TableLayout.FILL}});
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(400, 161));
		{
			jLabelParameter = new JLabel();
			this.add(jLabelParameter, "1, 1");
			jLabelParameter.setText("Variable");
		}
		{
			final String[] sParameters = new String[] { "Espesura", "Área basimétrica", "Volumen" };
			ComboBoxModel jComboBoxParameterModel =
				new DefaultComboBoxModel(sParameters);
			((PerformOperationWizard)m_ParentPanel).setParameter(0);
			jComboBoxParameter = new JComboBox();
			this.add(jComboBoxParameter, "3, 1, 4, 1");
			jComboBoxParameter.setModel(jComboBoxParameterModel);
			jComboBoxParameter.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent e) {
					String sItem = (String) e.getItem();
					for (int i = 0; i < sParameters.length; i++) {
						if (sParameters[i].equals(sItem)){
							((PerformOperationWizard)m_ParentPanel).setParameter(i);
						}
					}


				}
			});
		}

	}

	@Override
	public boolean isFinish() {

		return false;

	}

	public int getParameter(){

		return jComboBoxParameter.getSelectedIndex();

	}

}
