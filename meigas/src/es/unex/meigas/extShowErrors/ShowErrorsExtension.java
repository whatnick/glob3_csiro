package es.unex.meigas.extShowErrors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class ShowErrorsExtension
         extends
            AbstractMeigasExtension {

   @Override
   public void initialize() {}


   public String getMenuName() {

      return "Archivo";

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon("images/exclamation.png");
      return icon;
   }


   @Override
   public void execute(final MeigasPanel panel) {

      final ShowErrorsPanel dialog = new ShowErrorsPanel(panel);
      dialog.setVisible(true);

   }


   @Override
   public boolean showInMenuBar(final MeigasPanel panel) {

      return true;

   }


   @Override
   public String getName() {

      return "Mostrar errores";

   }


   @Override
   public boolean showInContextMenu() {

      return false;

   }


   public boolean isEnabled(final MeigasPanel window) {

      return true;

   }


}
