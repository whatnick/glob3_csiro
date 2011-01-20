package es.unex.meigas.extGIS;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class ZoomExtension extends AbstractMeigasExtension{

	@Override
	public void execute(MeigasPanel panel) {
		IGISConnection conn = Meigas.getGISConnection();
		DasocraticElement element = panel.getActiveElement();
		conn.setZoom(true);
		conn.sync(element);
		conn.setZoom(false);
	}

	@Override
	public String getName() {

		return "Zoom";
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
	}

	public Icon getIcon() {

		Icon icon = new ImageIcon(getClass().getClassLoader()
				.getResource("images/zoom.png"));
		return icon;

	}

	@Override
	public boolean showInContextMenu() {
		// TODO When GIS is configured and if is Plot or Tree
		return true;
	}

	@Override
	public boolean showInMenuBar(MeigasPanel panel) {
		// TODO Auto-generated method stub
		return true;
	}


	public boolean isEnabled(MeigasPanel window) {

		IGISConnection conn = Meigas.getGISConnection();
		LayerAndIDField[] plotLayers = conn.getPlotLayersAndIDFields();
		LayerAndIDField[] treeLayers = conn.getTreeLayersAndIDFields();

		return conn.isSync() && plotLayers.length != 0 && treeLayers.length != 0;

	}

	public String getMenuName() {

		// TODO Auto-generated method stub
		return null;

	}

}
