package es.unex.meigas.filters;

import info.clearthought.layout.TableLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import es.unex.meigas.core.Meigas;

public class SpecieFilterDialog extends FilterDialog {

	private JLabel jLabelSpecie;
	private JComboBox jComboBoxSpecie;
	private JPanel jPanelRadioButtons;
	private JRadioButton jRadioReject;
	private JRadioButton jRadioAccept;

	public SpecieFilterDialog(){

		super();
		initGUI();

	}

	private void initGUI() {
		try {
			TableLayout thisLayout = new TableLayout(new double[][] {
					{6.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 6.0},
					{TableLayout.FILL, TableLayout.MINIMUM, 7.0, TableLayout.MINIMUM, TableLayout.FILL}});
			thisLayout.setHGap(5);
			thisLayout.setVGap(5);
			getMainPane().setLayout(thisLayout);
			{
				jLabelSpecie = new JLabel();
				getMainPane().add(jLabelSpecie, "1, 1");
				jLabelSpecie.setText("Especie");
			}
			{
				ComboBoxModel jComboBoxSpecieModel =
					new DefaultComboBoxModel(Meigas.getSpeciesCatalog().getSpecies());
				jComboBoxSpecie = new JComboBox();
				getMainPane().add(jComboBoxSpecie, "2, 1, 3, 1");
				jComboBoxSpecie.setModel(jComboBoxSpecieModel);
			}
			{
				jPanelRadioButtons = new JPanel();
				getMainPane().add(jPanelRadioButtons, "1, 3, 3, 3");
				jPanelRadioButtons.setBorder(BorderFactory.createTitledBorder(null, "Criterio", TitledBorder.LEADING, TitledBorder.TOP));
				{
					jRadioAccept = new JRadioButton();
					jPanelRadioButtons.add(jRadioAccept);
					jRadioAccept.setText("Aceptar");
					jRadioAccept.setSelected(true);
				}
				{
					jRadioReject = new JRadioButton();
					jPanelRadioButtons.add(jRadioReject);
					jRadioReject.setText("Rechazar");
				}
				{
				    ButtonGroup group = new ButtonGroup();
				    group.add(jRadioReject);
				    group.add(jRadioAccept);
				}
				pack();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean checkValuesAndSetFilter() {

		if (jRadioAccept.isSelected()){
			m_Filter = new SpecieFilter((String)jComboBoxSpecie.getSelectedItem(), SpecieFilter.CRITERIA_ACCEPT);
		}
		else{
			m_Filter = new SpecieFilter((String)jComboBoxSpecie.getSelectedItem(), SpecieFilter.CRITERIA_REJECT);
		}

		return true;

	}

}
