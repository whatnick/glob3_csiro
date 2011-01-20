package es.unex.meigas.extOperations;

import info.clearthought.layout.TableLayout;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;

public class CreateNewDataPanel extends BaseWizardPanel {

	private JRadioButton jRadioButtonModifyData;
	private JRadioButton jRadioButtonCreateNewData;

	public CreateNewDataPanel(MainWizardWindow panel) {

		super(panel);
		initGUI();

	}

	@Override
	public boolean hasEnoughInformation() {

		return true;

	}

	@Override
	public void initGUI() {
		{
			TableLayout thisLayout = new TableLayout(new double[][] {{TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}, {TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}});
			thisLayout.setHGap(5);
			thisLayout.setVGap(5);
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(367, 277));
			ButtonGroup group = new ButtonGroup();
			{
				jRadioButtonCreateNewData = new JRadioButton();
				this.add(jRadioButtonCreateNewData, "1, 1, 2, 1");
				group.add(jRadioButtonCreateNewData);
				jRadioButtonCreateNewData.setText("Crear nueva copia");
				jRadioButtonCreateNewData.setSelected(true);
			}
			{
				jRadioButtonModifyData = new JRadioButton();
				this.add(jRadioButtonModifyData, "1, 3, 2, 3");
				group.add(jRadioButtonModifyData);
				jRadioButtonModifyData.setText("Modificar datos existentes");
			}
		}

	}

	@Override
	public boolean isFinish() {

		return false;

	}

	public boolean getCreateNewData() {

		return jRadioButtonCreateNewData.isSelected();

	}

}
