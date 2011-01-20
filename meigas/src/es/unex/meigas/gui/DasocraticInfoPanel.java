package es.unex.meigas.gui;

import javax.swing.JPanel;

import es.unex.meigas.core.DasocraticElement;

public abstract class DasocraticInfoPanel
         extends
            JPanel {


   protected DasocraticElement m_Element;
   protected MeigasPanel       m_MeigasPanel;


   public DasocraticInfoPanel(final DasocraticElement element,
                              final MeigasPanel panel) {

      super();

      m_Element = element;
      m_MeigasPanel = panel;

      initGUI();

   }


   protected void updateContent() {}


   protected abstract boolean checkDataAndUpdate();


   protected abstract void initGUI();

}
