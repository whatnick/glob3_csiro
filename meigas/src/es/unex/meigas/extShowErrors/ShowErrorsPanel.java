package es.unex.meigas.extShowErrors;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.DasocraticProject;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.gui.MeigasPanel;

public class ShowErrorsPanel
         extends
            JDialog {

   private JTable            jTable;
   private JScrollPane       jScrollPane;
   private final MeigasPanel m_MeigasPanel;
   private ArrayList         m_Elements;


   public ShowErrorsPanel(final MeigasPanel panel) {

      super(Meigas.getMainFrame(), true);

      m_MeigasPanel = panel;
      initGUI();
      setLocationRelativeTo(null);

   }


   private void initGUI() {
      try {
         {
            this.setSize(new java.awt.Dimension(421, 151));
            final BorderLayout thisLayout = new BorderLayout();
            thisLayout.setHgap(5);
            thisLayout.setVgap(5);
            this.setLayout(thisLayout);
            this.setPreferredSize(new java.awt.Dimension(412, 245));
            {
               jScrollPane = new JScrollPane();
               this.add(jScrollPane, BorderLayout.CENTER);
               jScrollPane.setPreferredSize(new java.awt.Dimension(83, 66));
               {
                  final TableModel jTableModel = new DefaultTableModel(getErrors(), new String[] { "Elemento", "Error" }) {
                     @Override
                     public boolean isCellEditable(int row,
                                                   int column) {
                        return false;
                     }
                  };
                  jTable = new JTable();
                  jScrollPane.setViewportView(jTable);
                  jTable.setModel(jTableModel);
                  jTable.addMouseListener(new MouseAdapter() {
                     @Override
                     public void mouseClicked(final MouseEvent e) {
                        if (e.getClickCount() == 2) {
                           final Point p = e.getPoint();
                           final int iRow = jTable.rowAtPoint(p);
                           if (iRow != -1) {
                              m_MeigasPanel.selectNodeFromObject(m_Elements.get(iRow));
                              //cancel();
                           }
                        }
                     }
                  });
               }
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }
   }


   private String[][] getErrors() {

      int i, j;
      final DasocraticProject dp = Meigas.getDasocraticProject();
      final ArrayList elements = new ArrayList();
      String[] errs;
      final ArrayList errors = new ArrayList();
      //ArrayList elementNames = new ArrayList();
      DasocraticElement de;
      String[][] ret;

      m_Elements = new ArrayList();

      dp.getElementsRecursive(elements);

      for (i = 0; i < elements.size(); i++) {
         de = (DasocraticElement) elements.get(i);
         errs = de.getErrorsReport();
         if (errs != null) {
            for (j = 0; j < errs.length; j++) {
               errors.add(errs[j]);
               m_Elements.add(de);
            }
         }
      }

      if (errors.size() > 0) {
         ret = new String[errors.size()][2];

         for (i = 0; i < errors.size(); i++) {
            ret[i] = new String[] { ((DasocraticElement) m_Elements.get(i)).getName(), (String) errors.get(i) };
         }

         return ret;
      }
      else {
         return new String[][] { new String[] { " ", " " } };
      }

   }


   protected void cancel() {

      this.dispose();
      this.setVisible(false);

   }

}
