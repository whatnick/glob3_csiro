package es.unex.meigas.extCalculateVolume;

import info.clearthought.layout.TableLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import es.unex.meigas.extBase.BaseWizardPanel;

public class OptionsPanel extends BaseWizardPanel{

	public static final int SHAPE_FACTOR = 1;
	public static final int SITE_INDEX = 2;

	private JPanel jPanelRadioButtons;
	private JCheckBox jCheckBoxSiteIndex;
	private JCheckBox jCheckBoxShapeFactor;
	private JLabel jLabelText;

	public OptionsPanel(CalculateVolumeDialog panel) {

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
				jPanelRadioButtons.setBorder(BorderFactory.createTitledBorder("Pies a completar"));
				{
					jCheckBoxShapeFactor = new JCheckBox();
					jPanelRadioButtons.add(jCheckBoxShapeFactor, "0, 0");
					jCheckBoxShapeFactor
						.setText("Aplicar sólo si coincide el factor de forma");
				}
				{
					jCheckBoxSiteIndex = new JCheckBox();
					jPanelRadioButtons.add(jCheckBoxSiteIndex, "0, 1");
					jCheckBoxSiteIndex
						.setText("Aplicar sólo si coincide la calidad de estación");
				}
			}
			{
				jLabelText = new JLabel();
				this.add(jLabelText, "1, 1, 3, 1");
				jLabelText.setText("¿A que pies desea aplicar las ecuaciones seleccionadas?");
				jLabelText.setFont(new java.awt.Font("Tahoma",1,11));
			}

		}

	}

	public boolean hasEnoughInformation() {

		return true;

	}

	public int getSelection(){

		int ret = 0;

		if (jCheckBoxShapeFactor.isSelected()){
			ret = ret | SHAPE_FACTOR;
		}

		if (jCheckBoxSiteIndex.isSelected()){
			ret = ret | SITE_INDEX;
		}

		return ret;


	}

	public boolean isFinish() {

		return true;

	}



}