package es.unex.meigas.filters;

import info.clearthought.layout.TableLayout;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TreeDasometricVariableFilterDialog extends FilterDialog {

	private JLabel jLabelParameter;
	private JLabel jLabelCriteria;
	private JComboBox jComboBoxCriteria;
	private JComboBox jComboBoxParameter;
	private JTextField jTextFieldValue;
	private JLabel jLabelValue;

	public TreeDasometricVariableFilterDialog(){

		super();
		initGUI();

	}

	private void initGUI(){

		TableLayout thisLayout = new TableLayout(new double[][] {
				{TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL},
				{TableLayout.FILL, TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.FILL}});
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		getMainPane().setLayout(thisLayout);
		{
			jLabelParameter = new JLabel();
			getMainPane().add(jLabelParameter, "1, 1, 2, 1");
			jLabelParameter.setText("Parámetro");
		}
		{
			jLabelCriteria = new JLabel();
			getMainPane().add(jLabelCriteria, "1, 2, 2, 2");
			jLabelCriteria.setText("Criterio");
		}
		{
			jLabelValue = new JLabel();
			getMainPane().add(jLabelValue, "1, 3, 2, 3");
			jLabelValue.setText("Valor");
		}
		{
			jTextFieldValue = new JTextField();
			getMainPane().add(jTextFieldValue, "3, 3, 4, 3");
			jTextFieldValue.setText("0");
		}
		{
			ComboBoxModel jComboBoxParameterModel =
				new DefaultComboBoxModel(TreeDasometricVariableFilter.VARIABLES);
			jComboBoxParameter = new JComboBox();
			getMainPane().add(jComboBoxParameter, "3, 1, 4, 1");
			jComboBoxParameter.setModel(jComboBoxParameterModel);
		}
		{
			ComboBoxModel jComboBoxCriteriaModel =
				new DefaultComboBoxModel(TreeDasometricVariableFilter.CRITERIA);
			jComboBoxCriteria = new JComboBox();
			getMainPane().add(jComboBoxCriteria, "3, 2, 4, 2");
			jComboBoxCriteria.setModel(jComboBoxCriteriaModel);
		}

	}

	//@Override
	protected boolean checkValuesAndSetFilter() {

		try{
			m_Filter = new TreeDasometricVariableFilter(
					jComboBoxParameter.getSelectedIndex(),
					jComboBoxCriteria.getSelectedIndex(),
					Double.parseDouble(jTextFieldValue.getText()));
			return true;
		}catch(Exception e){
			return false;

		}

	}

}
