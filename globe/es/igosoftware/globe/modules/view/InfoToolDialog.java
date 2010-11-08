package es.igosoftware.globe.modules.view;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;

import es.igosoftware.util.GPair;

public class InfoToolDialog
         extends
            JDialog
         implements
            WindowListener {
   private static final long     serialVersionUID = 1L;

   private static InfoToolDialog _dialog;

   private JScrollPane           jScrollPaneList;
   private JScrollPane           jScrollPaneTable;
   private JTable                jTable;
   private JList                 jList;


   public InfoToolDialog(final JFrame parent,
                         final PointInfo[] info) {

      super(parent, "Info", false);

      initGUI(info);
      this.setLocationRelativeTo(null);

      _dialog = this;

   }


   private void initGUI(final PointInfo[] info) {

      final TableLayout thisLayout = new TableLayout(new double[][] {
               { 3.0, TableLayoutConstants.FILL, TableLayoutConstants.FILL, 7.0, TableLayoutConstants.FILL,
                        TableLayoutConstants.FILL, TableLayoutConstants.FILL, 3.0 }, { 3.0, TableLayoutConstants.FILL, 3.0 } });
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      getContentPane().setLayout(thisLayout);
      setAlwaysOnTop(true);
      {
         jScrollPaneList = new JScrollPane();
         getContentPane().add(jScrollPaneList, "1, 1, 2, 1");
         {
            final ListModel jListModel = new DefaultComboBoxModel(info);
            jList = new JList();
            jScrollPaneList.setViewportView(jList);
            jList.setModel(jListModel);
            jList.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseClicked(final MouseEvent e) {
                  if (e.getClickCount() == 1) {
                     final int iIndex = jList.locationToIndex(e.getPoint());
                     final ListModel dlm = jList.getModel();
                     final Object item = dlm.getElementAt(iIndex);
                     updateTable((PointInfo) item);
                     jList.ensureIndexIsVisible(iIndex);
                  }
               }

            });
         }
      }
      {
         jScrollPaneTable = new JScrollPane();
         getContentPane().add(jScrollPaneTable, "4, 1, 6, 1");
         {
            final DefaultTableModel jTableModel = new DefaultTableModel();
            jTableModel.setColumnIdentifiers(new String[] { "Parameter", "Value" });
            jTable = new JTable();
            jScrollPaneTable.setViewportView(jTable);
            jTable.setModel(jTableModel);
         }
         updateTable(info[0]);
      }
      this.setSize(613, 392);

      addWindowListener(this);

   }


   public void updateInfo(final PointInfo[] info) {

      if (info.length != 0) {
         final ListModel jListModel = new DefaultComboBoxModel(info);
         jList.setModel(jListModel);
         updateTable(info[0]);
      }

   }


   protected void updateTable(final PointInfo info) {

      final DefaultTableModel model = new DefaultTableModel();
      model.setColumnIdentifiers(new String[] { "Parameter", "Value" });
      for (final GPair<String, Object> element : info._info) {
         model.addRow(new Object[] { element._first, element._second });
      }
      jTable.setModel(model);

   }


   public static InfoToolDialog getCurrentInfoDialog() {

      return _dialog;

   }


   @Override
   public void windowActivated(final WindowEvent e) {
      // TODO Auto-generated method stub

   }


   @Override
   public void windowClosed(final WindowEvent e) {

      _dialog = null;

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
