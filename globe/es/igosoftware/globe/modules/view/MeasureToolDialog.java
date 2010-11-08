package es.igosoftware.globe.modules.view;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.MeasureToolPanel;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class MeasureToolDialog
         extends
            JDialog
         implements
            WindowListener {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private final MeasureTool m_MeasureTool;


   public MeasureToolDialog(final JFrame parent,
                            final WorldWindow ww) {

      super(parent, "Measure tool", false);

      setAlwaysOnTop(true);

      setLocationRelativeTo(null);

      addWindowListener(this);

      m_MeasureTool = new MeasureTool(ww);
      m_MeasureTool.setController(new MeasureToolController());
      final MeasureToolPanel panel = new MeasureToolPanel(ww, m_MeasureTool);
      this.getContentPane().add(panel);
      pack();

   }


   @Override
   public void windowActivated(final WindowEvent e) {}


   @Override
   public void windowClosed(final WindowEvent e) {

      m_MeasureTool.dispose();

   }


   @Override
   public void windowClosing(final WindowEvent e) {}


   @Override
   public void windowDeactivated(final WindowEvent e) {}


   @Override
   public void windowDeiconified(final WindowEvent e) {}


   @Override
   public void windowIconified(final WindowEvent e) {}


   @Override
   public void windowOpened(final WindowEvent e) {}

}
