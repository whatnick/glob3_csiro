package es.unex.meigas.extGIS;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.extBase.IMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class GisConfigurationExtension extends AbstractMeigasExtension{

	@Override
	public void execute(MeigasPanel panel) {
		GisPanel gispanel = new GisPanel();
		gispanel.initGUI();
		gispanel.pack();
		gispanel.setVisible(true);
	}

	@Override
	public String getName() {

		return "SIG Config";
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	public Icon getIcon() {

		Icon icon = new ImageIcon(getClass().getClassLoader()
				.getResource("images/gvsig_icon.png"));
		return icon;

	}

	public String getMenuName() {
		return "Herramientas";
	}


	@Override
	public boolean showInContextMenu() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean showInMenuBar(MeigasPanel panel) {
		// TODO Auto-generated method stub
		return true;
	}


	public boolean isEnabled(MeigasPanel window) {
		// TODO Auto-generated method stub
		return true;
	}

}
