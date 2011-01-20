package es.unex.meigas.extOperations;

import javax.swing.JPanel;

import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import info.clearthought.layout.TableLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class MethodSelectionPanel extends BaseWizardPanel {

	private JLabel jLabelMethod;
	private JComboBox jComboBoxMethod;

	public MethodSelectionPanel(MainWizardWindow panel) {

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
			jLabelMethod = new JLabel();
			this.add(jLabelMethod, "1, 1");
			jLabelMethod.setText("Método");
		}
		{
			ComboBoxModel jComboBoxParameterModel =
				new DefaultComboBoxModel(
						new String[] { "Por lo alto", "Por lo bajo", "En todas clases diamétricas" });
			jComboBoxMethod = new JComboBox();
			this.add(jComboBoxMethod, "3, 1, 4, 1");
			jComboBoxMethod.setModel(jComboBoxParameterModel);
		}

	}

	@Override
	public boolean isFinish() {

		return false;

	}

	public int getMethod(){

		return jComboBoxMethod.getSelectedIndex();

	}

}
