package es.unex.meigas.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import es.unex.meigas.core.DasocraticElement;

public class NotesPanel
         extends
            DasocraticInfoPanel {

   JPanel      jPanel;
   JTextArea   jTextArea;
   JScrollPane jScrollPane;


   public NotesPanel(final DasocraticElement element,
                     final MeigasPanel meigasPanel) {

      super(element, meigasPanel);

      setName("Notas");

   }


   @Override
   protected void initGUI() {

      try {
         final BorderLayout thisLayout = new BorderLayout();
         this.setLayout(thisLayout);
         this.setPreferredSize(new Dimension(300, 300));
         {
            jPanel = new JPanel();
            jPanel.setBorder(BorderFactory.createTitledBorder("Notas"));
            final BorderLayout layout = new BorderLayout();
            jPanel.setLayout(layout);
            this.add(jPanel, BorderLayout.CENTER);
            jTextArea = new JTextArea(m_Element.getNotes());
            jTextArea.setLineWrap(true);
            jTextArea.setWrapStyleWord(true);
            jScrollPane = new JScrollPane(jTextArea);
            jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jPanel.add(jScrollPane, BorderLayout.CENTER);
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @Override
   public boolean checkDataAndUpdate() {

      m_Element.setNotes(jTextArea.getText());

      return true;

   }


   protected void initializeContent() {}


   @Override
   protected void updateContent() {}

}
