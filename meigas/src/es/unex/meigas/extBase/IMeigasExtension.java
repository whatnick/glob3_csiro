package es.unex.meigas.extBase;

import javax.swing.Icon;

import es.unex.meigas.gui.MeigasPanel;

public interface IMeigasExtension extends Comparable{

	public void initialize();

	public abstract void execute(MeigasPanel panel);

	public boolean showInMenuBar(MeigasPanel panel);

	public boolean showInContextMenu();

	public boolean isEnabled(MeigasPanel window);

	public String getName();

	public Icon getIcon();
	
	public String getMenuName();

}
