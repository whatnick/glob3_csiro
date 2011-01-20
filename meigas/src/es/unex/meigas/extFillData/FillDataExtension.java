package es.unex.meigas.extFillData;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class FillDataExtension extends AbstractMeigasExtension {

		public String getName(){

			return "Rellenar Datos";

		}

		public String getMenuName() {

			return "Herramientas";
			
		}
		
		public Icon getIcon() {

			Icon icon = new ImageIcon(getClass().getClassLoader()
					.getResource("images/tab_edit.png"));
			return icon;
		}


		public void execute(MeigasPanel meigasPanel) {

			FillDataExtensionPanel dialog = new FillDataExtensionPanel(meigasPanel);
			dialog.setVisible(true);

		}

		public void initialize() {

		}

		public boolean showInContextMenu() {

			return true;

		}

		public boolean showInMenuBar(MeigasPanel panel) {

			return false;

		}

		public boolean isEnabled(MeigasPanel window) {

			if (window.getActiveElement().hasTrees()){
				return true;
			}
			else{
				return false;
			}

		}

}
