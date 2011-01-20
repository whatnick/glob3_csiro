package es.unex.meigas.gui;

import javax.swing.SwingUtilities;

import es.unex.meigas.core.Meigas;

public class MeigasGUI {

   private static DefaultMeigasWindow meigasMainFrame = null;


   public static void showMeigasWindow() {

      if (Meigas.getMainFrame() == null) {
         meigasMainFrame = new DefaultMeigasWindow();
         Meigas.setMainFrame(meigasMainFrame);
         meigasMainFrame.pack();
         meigasMainFrame.setVisible(true);
      }
      else {
         meigasMainFrame.setVisible(true);
      }

   }


   public static void main(final String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            Meigas.setDataFolder(System.getProperty("user.dir"));
            Meigas.initialize();
            final DefaultMeigasWindow inst = new DefaultMeigasWindow();
            inst.setLocationRelativeTo(null);
            inst.setVisible(true);
         }
      });
   }

}
