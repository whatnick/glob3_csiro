package es.unex.meigas.extOperations;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.DasocraticProject;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Tree;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class PerformOperationExtension extends AbstractMeigasExtension {

	public String getName(){

		return "Actuación selvícola";

	}

	public String getMenuName() {

		return null;

	}

	public Icon getIcon() {

		Icon icon = new ImageIcon(getClass().getClassLoader()
				.getResource("images/axe.gif"));
		return icon;
	}


	public void execute(MeigasPanel meigasPanel) {

		PerformOperationWizard dialog = new PerformOperationWizard(meigasPanel);
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

		if (element instanceof Tree || element instanceof Plot || element instanceof DasocraticProject){
			return false;
		}
		else{
			return true;
		}

	}

}
