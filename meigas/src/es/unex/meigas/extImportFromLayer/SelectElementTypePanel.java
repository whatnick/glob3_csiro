package es.unex.meigas.extImportFromLayer;

import info.clearthought.layout.TableLayout;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import es.unex.meigas.extBase.BaseWizardPanel;

public class SelectElementTypePanel extends BaseWizardPanel {

	public static final int TREE = 0;
	public static final int PLOT = 1;
	private JRadioButton treeRadio;
	private JRadioButton plotsRadio;

	public SelectElementTypePanel(ImportFromLayerWizard wizardPanel) {

		super(wizardPanel);
		initGUI();

	}

	public void initGUI(){

		removeAll();
		// border
		double b = 10;
		// vertical space between label and element
		double vs = 5;
		// vertical gap
		double vg = 10;
		// horizontal gap
		double hg = 10;
		// prerrefered
		double p = TableLayout.PREFERRED;
		// fill
		double f = TableLayout.FILL;

		double[] cols = {f,p,vs,p,vs,p,f};
		double[] rows = {f,p,hg,p,hg,p,hg,p,f};
		TableLayout thisLayout = new TableLayout(cols, rows);

		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(550, 220));

		ButtonGroup group = new ButtonGroup();
		treeRadio = new JRadioButton("Árboles");
		this.add(treeRadio, "1, 1, 3, 1");
		plotsRadio = new JRadioButton("Parcelas");
		this.add(plotsRadio, "1, 3, 3, 1");
		group.add(treeRadio);
		group.add(plotsRadio);

		treeRadio.setSelected(true);

	}

	@Override
	public boolean hasEnoughInformation() {

		return true;

	}

	@Override
	public boolean isFinish() {

		return false;

	}

	public int getElementType() {

		if (treeRadio.isSelected()){
			return TREE;
		}
		else{
			return PLOT;

		}
	}

}
