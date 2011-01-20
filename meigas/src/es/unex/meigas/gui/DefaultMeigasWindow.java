package es.unex.meigas.gui;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.extLoadSaveProject.SaveProjectExtension;

public class DefaultMeigasWindow
         extends
            JFrame
         implements
            WindowListener {

   private final MeigasPanel m_MeigasPanel;


   protected DefaultMeigasWindow() {

      super("Meigas");
      m_MeigasPanel = new MeigasPanel();
      this.setSize(new Dimension(800, 600));
      this.getContentPane().add(m_MeigasPanel);

   }


   public void windowClosed(final WindowEvent arg0) {

      Meigas.setMainFrame(null);

   }


   public void windowClosing(final WindowEvent arg0) {

      if (Meigas.getDasocraticProject().getHasChanged()) {
         final int iResult = JOptionPane.showConfirmDialog(this, "�Quiere guardar los cambios?", "MEIGAS",
                  JOptionPane.YES_NO_OPTION);
         if (iResult == JOptionPane.YES_OPTION) {
            //this.dispose();
            (new SaveProjectExtension()).execute(m_MeigasPanel);

         }
      }
      this.setVisible(false);

   }


   public void windowDeactivated(final WindowEvent arg0) {}


   public void windowDeiconified(final WindowEvent arg0) {}


   public void windowIconified(final WindowEvent arg0) {}


   public void windowOpened(final WindowEvent arg0) {}


   public void windowActivated(final WindowEvent arg0) {}


}
