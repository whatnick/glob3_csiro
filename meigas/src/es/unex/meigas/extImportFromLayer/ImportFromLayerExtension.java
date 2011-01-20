package es.unex.meigas.extImportFromLayer;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.core.Cruise;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.dataObjects.AbstractInputFactory;
import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class ImportFromLayerExtension extends AbstractMeigasExtension{

	public String getName(){

		return "Importar datos de una capa vectorial";

	}

	public String getMenuName() {

		return "Herramientas";

	}

	public Icon getIcon() {

		Icon icon = new ImageIcon(getClass().getClassLoader()
				.getResource("images/folder_in.png"));
		return icon;
	}


	public void execute(MeigasPanel meigasPanel) {

		ImportFromLayerWizard dialog = new ImportFromLayerWizard(meigasPanel);
		dialog.setSize(new java.awt.Dimension(700, 450));
		dialog.setVisible(true);

	}

	public void initialize() {}

	public boolean showInContextMenu() {

		return true;

	}

	public boolean showInMenuBar(MeigasPanel panel) {

		return false;

	}

	public boolean isEnabled(MeigasPanel window) {

		DasocraticElement element = window.getActiveElement();

		if (element instanceof Cruise){
			IVectorLayer[] layers = Meigas.getInputFactory().getVectorLayers(AbstractInputFactory.SHAPE_TYPE_ANY);
			return layers.length != 0;
		}
		else{
			return false;
		}

	}

}
