package es.unex.meigas.extCalculateVolume;

import es.unex.meigas.extBase.BaseWizardPanel;
import info.clearthought.layout.TableLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SingleOrMultipleEquationsPanel extends BaseWizardPanel{

	public static final int SINGLE = 1;
	public static final int MULTIPLE = 2;

	private JPanel jPanelRadioButtons;
	private JRadioButton jRadioButtonMutiple;
	private JRadioButton jRadioButtonSingle;
	private JLabel jLabelText;

	public SingleOrMultipleEquationsPanel(CalculateVolumeDialog panel) {

		super(panel);

		initGUI();

	}

	public void initGUI() {

		{
			TableLayout thisLayout = new TableLayout(new double[][] {
					{ 50.0, TableLayout.FILL, TableLayout.FILL,
							TableLayout.FILL, 50.0 },
					{ TableLayout.FILL, TableLayout.FILL, TableLayout.FILL,
							TableLayout.FILL, TableLayout.FILL,
							TableLayout.FILL, TableLayout.FILL } });
			thisLayout.setHGap(5);
			thisLayout.setVGap(5);
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(488, 220));
			{
				jPanelRadioButtons = new JPanel();
				TableLayout jPanelRadioButtonsLayout = new TableLayout(
					new double[][] { { TableLayout.FILL },
							{ TableLayout.FILL, TableLayout.FILL } });
				jPanelRadioButtonsLayout.setHGap(5);
				jPanelRadioButtonsLayout.setVGap(5);
				jPanelRadioButtons.setLayout(jPanelRadioButtonsLayout);
				this.add(jPanelRadioButtons, "1, 3, 3, 5");
				jPanelRadioButtons.setBorder(BorderFactory.createTitledBorder("Ecuaciones a utilizar"));
				{
					jRadioButtonSingle = new JRadioButton();
					jPanelRadioButtons.add(jRadioButtonSingle, "0, 0");
					jRadioButtonSingle
						.setText("Utilizar una única ecuación para todas las especies");
				}
				{
					jRadioButtonMutiple = new JRadioButton();
					jPanelRadioButtons.add(jRadioButtonMutiple, "0, 1");
					jRadioButtonMutiple
						.setText("Utilizar una ecuación distinta para cada especie");
				}
			}
			{
				jLabelText = new JLabel();
				this.add(jLabelText, "1, 1, 3, 1");
				jLabelText.setText("¿Desea una ecuación única o una para cada especie?");
				jLabelText.setFont(new java.awt.Font("Tahoma",1,11));
			}
			{
			    ButtonGroup group = new ButtonGroup();
			    group.add(jRadioButtonMutiple);
			    group.add(jRadioButtonSingle);
			    jRadioButtonSingle.setSelected(true);
			}
		}

	}

	public boolean hasEnoughInformation() {

		return true;

	}

	public int getSelection(){

		if (jRadioButtonSingle.isSelected()){
			return SINGLE;
		}
		else{
			return MULTIPLE;
		}
	}

	public boolean isFinish() {
		return false;
	}

}
