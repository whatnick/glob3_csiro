package es.unex.meigas.extCalculateVolume;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import info.clearthought.layout.TableLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import es.unex.meigas.equations.Equation;
import es.unex.meigas.equations.EquationSelectionDialog;
import es.unex.meigas.extBase.BaseWizardPanel;

public class EquationSelectionForSpeciesPanel extends BaseWizardPanel{

	private Equation[] m_Equations;
	private String[] m_Species;
	private JButton[] m_Buttons;
	private JTextField[] m_TextFields;

	public EquationSelectionForSpeciesPanel(CalculateVolumeDialog panel,
											int iMultipleOrSingle) {

		super(panel);

		initGUI(iMultipleOrSingle);

	}

	private void initGUI(int iMultipleOrSingle) {

		int i;
		int iRow;
		int iSpecies;
		int iRows;
		JLabel jLabel;
		JPanel panel = new JPanel();;

		if (iMultipleOrSingle== SingleOrMultipleEquationsPanel.SINGLE){
			m_Species = new String[]{"Todas las especies"};
		}
		else{
			m_Species =  m_ParentPanel.getMeigasPanel().
								getActiveElement().getSpeciesNames();
		}
		iSpecies = m_Species.length;
		m_Equations = new Equation[iSpecies];
		m_Buttons = new JButton[iSpecies];
		m_TextFields = new JTextField[iSpecies];
		iRows = iSpecies * 3 + 2;
		double [][] layoutSetting = new double[2][iRows];
		layoutSetting[0] = new double[]{10, -1, 5, -1, 5, 25, 10};
		layoutSetting[1] = new double[iRows];
		layoutSetting[1][0] = -1;
		for (i = 0; i < m_Species.length; i++){
			layoutSetting[1][i * 3 + 1] = 20;
			layoutSetting[1][i * 3 + 2] = 20;
			layoutSetting[1][i * 3 + 3] = 20;
		}
		layoutSetting[1][iRows - 1] = -1;

		TableLayout tableLayout = new TableLayout(layoutSetting);
		tableLayout.setHGap(5);
		tableLayout.setVGap(5);
		panel.setLayout(tableLayout);

		for (i = 0; i < m_Species.length; i++){
			iRow = i * 3 + 1;
			panel.add(new JLabel("Especie"), "1 ," + Integer.toString(iRow));
			jLabel = new JLabel();
			jLabel.setFont(new java.awt.Font("Tahoma",1,11));
			jLabel.setText(m_Species[i]);
			panel.add(jLabel, "3, " + Integer.toString(iRow));
			iRow = i * 3 + 2;
			panel.add(new JLabel("Ecuación"), "1 ," + Integer.toString(iRow));
			m_TextFields[i] = new JTextField();
			m_TextFields[i].setEditable(false);
			panel.add(m_TextFields[i], "3, " + Integer.toString(iRow));
			m_Buttons[i] = new JButton("...");
			panel.add(m_Buttons[i], "5, " + Integer.toString(iRow));
			final int iIndex = i;
			m_Buttons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					openEquationSelector(iIndex);
				}
			});
		}

		JScrollPane scrollPane = new JScrollPane();
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		this.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(panel);

	}

	protected void openEquationSelector(int iIndex) {

		Equation eq = new Equation();

		EquationSelectionDialog dialog = new EquationSelectionDialog(
											m_ParentPanel.getMeigasPanel(),
											EquationSelectionDialog.VOLUME |
											EquationSelectionDialog.VOLUME_WITHOUT_BARK,
											eq);
		dialog.setVisible(true);

		if (eq.getEquation() != null){
			m_Equations[iIndex] = eq;
			m_TextFields[iIndex].setText(eq.getDescription());
			m_ParentPanel.updateButtons();
		}

	}

	public boolean hasEnoughInformation() {

		int i;

		for (i = 0; i < m_Equations.length; i++){
			if (m_Equations[i] == null){
				return false;
			}
		}

		return true;

	}

	public Equation[] getEquations(){

		return m_Equations;

	}

	public boolean isFinish() {
		return false;
	}

	@Override
	public void initGUI() {
		// TODO Auto-generated method stub

	}

}
