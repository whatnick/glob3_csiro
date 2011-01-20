package es.unex.meigas.extImportFromLayer;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import es.unex.meigas.dataObjects.IFeature;
import es.unex.meigas.dataObjects.IFeatureIterator;
import es.unex.meigas.dataObjects.IRecord;
import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.exceptions.IteratorException;
import es.unex.meigas.extBase.BaseWizardPanel;

/**
 * Panel to select if import plots
 */
//TODO Option of get and set Plots of attributes
public class SelectPlotsForTreesPanel extends BaseWizardPanel implements ActionListener{

	private JRadioButton noPlotRadio = null;
	private JRadioButton plotByFieldRadio = null;
	private JRadioButton plotByCoordinatesRadio;
	protected JComboBox plotComboBox = null;

	JLabel plotLabel = null;


	public SelectPlotsForTreesPanel(ImportFromLayerWizard wizard) {

		super(wizard);

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

		ImportFromLayerWizard wizard = (ImportFromLayerWizard)m_ParentPanel;

		double[] cols = {f,p,vs,p,vs,p,f};
		double[] rows = {f,p,hg,p,hg,p,hg,p,hg,p,hg,p,f};
		TableLayout thisLayout = new TableLayout(cols, rows);

		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(550, 220));

		ButtonGroup group = new ButtonGroup();
		noPlotRadio = new JRadioButton("Parcela única (no hay informacion de parcelas)");
		noPlotRadio.addActionListener(this);
		this.add(noPlotRadio, "1, 1, 3, 1");
		plotByFieldRadio = new JRadioButton("Asignar parcelas a partir de atributo:");
		plotByFieldRadio.addActionListener(this);
		this.add(plotByFieldRadio, "1, 3, 3, 3");
		plotLabel = new JLabel("Parcela: ");
		plotLabel.setEnabled(false);
		this.add(plotLabel, "1, 5");
		String[] attributes = wizard.getAttributes();
		plotComboBox = new JComboBox(attributes);
		plotComboBox.setEnabled(false);
		wizard.selectOneOfThis(plotComboBox,
				new String[] {"Parcela", "Parce", "Plot"});
		this.add(plotComboBox, "3, 5");
		plotByCoordinatesRadio = new JRadioButton("Asignar parcelas a partir de coordenadas");
		plotByCoordinatesRadio.addActionListener(this);
		this.add(plotByCoordinatesRadio, "1, 7, 3, 7");
		//setVisible(true);

		group.add(noPlotRadio);
		group.add(plotByFieldRadio);
		group.add(plotByCoordinatesRadio);

		noPlotRadio.setSelected(true);
		//refreshGUI();

	}

	public boolean hasEnoughInformation() {

		if (noPlotRadio == null || plotComboBox == null){
			// Check if GUI is initialized.
			// TODO Maybe it must be in other place.
			initGUI();
		}
		if (noPlotRadio.isSelected()){
			return true;
		} else {
			// First attribute is blank string
			if (plotComboBox.getSelectedIndex() != 0){
				return true;
			}
		}
		return false;

	}

	private void refreshGUI(){
		boolean useAttribute = plotByFieldRadio.isSelected();
		plotLabel.setEnabled(useAttribute);
		plotComboBox.setEnabled(useAttribute);
	}

	public void actionPerformed(ActionEvent e) {
		m_ParentPanel.updateButtons();
		refreshGUI();
	}

	public boolean isFinish() {

		return true;

	}

	protected String[] getParentTreeNodes(){

		ImportFromLayerWizard wizard = (ImportFromLayerWizard)m_ParentPanel;
		IVectorLayer layer = wizard.getLayer();

		String[] parentPlots = new String[layer.getShapesCount()];

		int plotIdx = plotComboBox.getSelectedIndex()-1;
		String plot = "default";

		int counter = 0;
		for (IFeatureIterator it = layer.iterator(); it.hasNext(); counter++){
			try {
				IFeature feat = it.next();
				IRecord record = feat.getRecord();
				if (plotIdx != -1){
					plot= (String) record.getValue(plotIdx).toString();
				}

				parentPlots[counter] = plot;

			} catch (IteratorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return parentPlots;
	}

	public boolean usePlotsAttribute(){

		return plotByFieldRadio.isSelected() &&
				plotComboBox.getSelectedIndex() != 0;

	}

	public boolean useCoordinates() {

		return plotByCoordinatesRadio.isSelected();

	}


}
