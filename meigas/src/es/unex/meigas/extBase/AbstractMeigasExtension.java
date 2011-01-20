package es.unex.meigas.extBase;

import es.unex.meigas.gui.MeigasPanel;

public abstract class AbstractMeigasExtension implements IMeigasExtension {

	public abstract void execute(MeigasPanel panel);

	public abstract String getName();

	public abstract void initialize();

	public abstract boolean showInContextMenu();

	public abstract boolean showInMenuBar(MeigasPanel panel);

	public int compareTo(Object obj){

		if (obj instanceof IMeigasExtension){
			return this.getName().compareTo(
					((IMeigasExtension)obj).getName());
		}
		else{
			return 0;
		}
	}

}
