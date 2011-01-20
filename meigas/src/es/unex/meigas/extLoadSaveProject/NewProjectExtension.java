package es.unex.meigas.extLoadSaveProject;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import es.unex.meigas.core.DasocraticProject;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;


public class NewProjectExtension
         extends
            AbstractMeigasExtension {

   @Override
   public String getName() {

      return "Nuevo";

   }


   public String getMenuName() {

      return "Archivo";

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon("images/page_white_star.png");
      return icon;

   }


   @Override
   public void execute(final MeigasPanel meigasWindow) {

      try {

         final DasocraticProject dp = Meigas.getDasocraticProject();
         if (dp.getHasChanged()) {
            final int iResult = JOptionPane.showConfirmDialog(meigasWindow,
                     "�Quiere abrir un nuevo proyecto sin guardar los cambios del actual?", "MEIGAS", JOptionPane.YES_NO_OPTION);
            if (iResult == JOptionPane.NO_OPTION) {
               return;
            }
         }

         meigasWindow.startNewProject();

      }
      catch (final Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }


   @Override
   public void initialize() {

   }


   @Override
   public boolean showInContextMenu() {

      return false;

   }


   @Override
   public boolean showInMenuBar(final MeigasPanel panel) {

      return true;

   }


   public boolean isEnabled(final MeigasPanel window) {

      return true;

   }

}
