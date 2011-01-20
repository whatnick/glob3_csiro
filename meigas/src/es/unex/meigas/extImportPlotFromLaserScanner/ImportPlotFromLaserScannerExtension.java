package es.unex.meigas.extImportPlotFromLaserScanner;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.core.Cruise;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class ImportPlotFromLaserScannerExtension extends AbstractMeigasExtension {

	public String getName(){

		return "Importar parcela de Laser-Scanner";

	}

	public String getMenuName() {

		return "Herramientas";

	}

	public Icon getIcon() {

		Icon icon = new ImageIcon(getClass().getClassLoader()
				.getResource("images/laser_ray.png"));
		return icon;
	}


	public void execute(MeigasPanel meigasPanel) {

		ImportPlotFromLaserScannerWizard dialog = new ImportPlotFromLaserScannerWizard(meigasPanel);
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
			return true;
		}
		else{
			return false;
		}

	}

}
