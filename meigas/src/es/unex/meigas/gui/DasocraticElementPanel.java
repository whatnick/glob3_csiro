package es.unex.meigas.gui;

import java.awt.BorderLayout;
import java.awt.Panel;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Tree;

public class DasocraticElementPanel
         extends
            Panel {

   protected DasocraticElement              m_Element;
   protected MeigasPanel                    m_MeigasPanel;
   private JTabbedPane                      jTabbedPane;
   private NotesPanel                       m_NotesPanel;
   private PicturesPanel                    m_PicturesPanel;
   private DasocraticElementParametersPanel m_DasocraticElementParametersPanel;
   private DistributionPanel                m_DistributionPanel;
   private SummaryPanel                     m_SummaryPanel;


   public DasocraticElementPanel(final DasocraticElement element,
                                 final MeigasPanel panel) {

      super();


      m_Element = element;
      m_MeigasPanel = panel;

      if (m_Element != null) {
         m_Element.calculateParameters(null);
      }

      initializeContent();
      initGUI();

   }


   protected void initializeContent() {
   // TODO Auto-generated method stub

   }


   protected void updateContent() {

   }


   protected boolean checkDataAndUpdate() {

      return m_PicturesPanel.checkDataAndUpdate() && m_NotesPanel.checkDataAndUpdate()
             && m_DasocraticElementParametersPanel.checkDataAndUpdate();

   }


   protected void initGUI() {

      m_DasocraticElementParametersPanel = new DasocraticElementParametersPanel(m_Element, m_MeigasPanel);
      m_NotesPanel = new NotesPanel(m_Element, m_MeigasPanel);
      m_PicturesPanel = new PicturesPanel(m_Element, m_MeigasPanel);

      if (!(m_Element instanceof Tree)) {
         m_DistributionPanel = new DistributionPanel(m_Element, m_MeigasPanel);
         m_SummaryPanel = new SummaryPanel(m_Element, m_MeigasPanel);
      }

      try {

         final BorderLayout layout = new BorderLayout();
         this.setLayout(layout);
         this.setPreferredSize(new java.awt.Dimension(500, 300));
         jTabbedPane = new JTabbedPane();
         jTabbedPane.addTab(m_DasocraticElementParametersPanel.getName(), m_DasocraticElementParametersPanel);
         jTabbedPane.addTab(m_NotesPanel.getName(), m_NotesPanel);
         jTabbedPane.addTab(m_PicturesPanel.getName(), m_PicturesPanel);
         if (!(m_Element instanceof Tree)) {
            jTabbedPane.addTab(m_DistributionPanel.getName(), m_DistributionPanel);
            jTabbedPane.addTab(m_SummaryPanel.getName(), m_SummaryPanel);
         }

         this.add(jTabbedPane);
         jTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent evt) {
            //updatePanels();
            }
         });

      }
      catch (final Exception e) {
         e.printStackTrace();
      }


   }

   // protected void removeListeners(){

   // removeListeners(this);

   //};

   //protected void removeListeners(JPanel panel){

   // int i;
   // Component[] components = panel.getComponents();
   // for (i = 0; i < components.length; i++) {
   // if (components[i] instanceof MeigasValueTextField){
   // ((MeigasValueTextField)components[i]).removeListener();
   // }
   // else if (components[i] instanceof JPanel){
   // removeListeners((JPanel)components[i]);
   // }
   // }
   // }


}
