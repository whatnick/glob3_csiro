package es.unex.meigas.extImportFromLayer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import info.clearthought.layout.TableLayout;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.dataObjects.AbstractInputFactory;
import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.extBase.BaseWizardPanel;

public class LayerSelectionPanel extends BaseWizardPanel{

	private ImportFromLayerWizard wizard;
	private JComboBox jComboBoxLayer;
	private JLabel jLabelLayer;

	public LayerSelectionPanel(ImportFromLayerWizard wizard) {

		super(wizard);
		this.wizard = wizard;
		initGUI();

	}

	public void initGUI(){

		TableLayout layout = new TableLayout(new double[][] {{TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, 7.0, TableLayout.MINIMUM, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}, {TableLayout.FILL, TableLayout.MINIMUM, TableLayout.PREFERRED, TableLayout.FILL}});

		jComboBoxLayer.setLayout(layout);
		jComboBoxLayer.setPreferredSize(new java.awt.Dimension(507, 175));
		{
			jLabelLayer = new JLabel();
			this.add(jLabelLayer, "2, 1");
			jLabelLayer.setText("Capa");
		}
		{
			IVectorLayer[] layers = Meigas.getInputFactory().getVectorLayers(AbstractInputFactory.SHAPE_TYPE_ANY);
			ComboBoxModel jComboBoxLayerModel = new DefaultComboBoxModel(layers);
			jComboBoxLayer = new JComboBox();
			jComboBoxLayer.setModel(jComboBoxLayerModel);
			jComboBoxLayer.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent ie) {
					 wizard.setLayer((IVectorLayer)ie.getItem());
				}
			});
			this.add(jComboBoxLayer, "4, 1");
		}

	}

	public boolean hasEnoughInformation() {

		return true;

	}

	@Override
	public boolean isFinish() {

		return false;

	}

}

