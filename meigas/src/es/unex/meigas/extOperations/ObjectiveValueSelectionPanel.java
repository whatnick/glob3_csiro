package es.unex.meigas.extOperations;

import info.clearthought.layout.TableLayout;

import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;

public class ObjectiveValueSelectionPanel extends BaseWizardPanel {

	private double m_dCurrentValue;
	private JLabel jLabelCurrentValue;
	private JTextField jTextFieldObjectiveValue;
	private JTextField jTextFieldCurrentValue;
	private JLabel jLabelObjectiveValue;

	public ObjectiveValueSelectionPanel(MainWizardWindow panel) {

		super(panel);
		initGUI();

	}

	@Override
	public boolean hasEnoughInformation() {

		try{
			double dObjectiveValue = Double.parseDouble(jTextFieldObjectiveValue.getText());
			return m_dCurrentValue != 0 && m_dCurrentValue > dObjectiveValue;
		}
		catch(Exception e){
			return false;
		}

	}

	@Override
	public void initGUI() {

		{
			TableLayout thisLayout = new TableLayout(new double[][] {
					{TableLayout.FILL, TableLayout.FILL, 3.0, TableLayout.FILL, TableLayout.FILL},
					{TableLayout.FILL, TableLayout.MINIMUM, TableLayout.FILL, TableLayout.MINIMUM, TableLayout.FILL}});
			thisLayout.setHGap(5);
			thisLayout.setVGap(5);
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(485, 278));
			{
				jLabelCurrentValue = new JLabel();
				this.add(jLabelCurrentValue, "1, 1");
				jLabelCurrentValue.setText("Valor actual");
			}
			{
				jLabelObjectiveValue = new JLabel();
				this.add(jLabelObjectiveValue, "1, 3");
				jLabelObjectiveValue.setText("Valor objetivo");
			}
			{
				DecimalFormat df = new DecimalFormat("##.##");
				jTextFieldCurrentValue = new JTextField();
				this.add(jTextFieldCurrentValue, "3, 1");
				jTextFieldCurrentValue.setEditable(false);
				jTextFieldCurrentValue.setEnabled(false);
				setCurrentValue();
				if (m_dCurrentValue == DasocraticElement.NO_DATA){
					jTextFieldCurrentValue.setText("No disponible");
				}
				else{
					jTextFieldCurrentValue.setText(df.format(m_dCurrentValue));
				}
			}
			{
				jTextFieldObjectiveValue = new JTextField();
				jTextFieldObjectiveValue.getDocument().addDocumentListener(new DocumentListener(){
					public void changedUpdate(DocumentEvent e) {}
					public void insertUpdate(DocumentEvent e) {
						m_ParentPanel.updateButtons();
					}
					public void removeUpdate(DocumentEvent e) {
						m_ParentPanel.updateButtons();
					}
				});

				this.add(jTextFieldObjectiveValue, "3, 3");
			}
		}

		//this.getParentPanel().updateButtons();

	}

	private void setCurrentValue() {

		int iParameter = ((PerformOperationWizard)m_ParentPanel).getParameter();

		DasocraticElement element = m_ParentPanel.getMeigasPanel().getActiveElement();
		switch (iParameter){
		case ParameterSelectionPanel.DENSITY:
			m_dCurrentValue = 0;
			break;
		case ParameterSelectionPanel.BASIMETRIC_AREA:
			m_dCurrentValue = element.getTotalBasimetricAreaByHa();
			break;
		case ParameterSelectionPanel.VOLUME:
		default:
			m_dCurrentValue = element.getTotalVolumeWithBarkByHa();
			break;
		}

	}

	@Override
	public boolean isFinish() {

		return false;

	}

	public double getObjectiveValue(){

		return Double.parseDouble(jTextFieldObjectiveValue.getText());

	}

}
