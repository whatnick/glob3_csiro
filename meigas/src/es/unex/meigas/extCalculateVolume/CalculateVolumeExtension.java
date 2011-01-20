package es.unex.meigas.extCalculateVolume;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class CalculateVolumeExtension
         extends
            AbstractMeigasExtension {

   @Override
   public String getName() {

      return "Calcular vol�menes";

   }


   public String getMenuName() {

      return "Herramientas";

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon(getClass().getClassLoader().getResource("images/calendar_view_month.png"));
      return icon;
   }


   @Override
   public void execute(final MeigasPanel meigasPanel) {

      final CalculateVolumeDialog dialog = new CalculateVolumeDialog(meigasPanel);
      dialog.setVisible(true);

   }


   @Override
   public boolean showInContextMenu() {

      return true;
   }


   @Override
   public void initialize() {

   }


   @Override
   public boolean showInMenuBar(final MeigasPanel panel) {

      return false;

   }


   public boolean isEnabled(final MeigasPanel window) {

      if (window.getActiveElement().hasTrees()) {
         return true;
      }
      else {
         return false;
      }

   }

}
