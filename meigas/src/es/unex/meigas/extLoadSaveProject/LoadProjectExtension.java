package es.unex.meigas.extLoadSaveProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import es.unex.meigas.core.DasocraticProject;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;


public class LoadProjectExtension
         extends
            AbstractMeigasExtension {

   private MeigasPanel meigasWindow;


   @Override
   public String getName() {

      return "Abrir";

   }


   public String getMenuName() {

      return "Archivo";

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon("images/folder-open-green.png");
      return icon;

   }


   protected void load(final DefaultTreeModel model,
                       final File file) throws Exception {

      if (file == null) {
         return;
      }
      final FileInputStream fis = new FileInputStream(file);
      final ObjectInputStream in = new ObjectInputStream(fis);
      try {
         model.setRoot((DefaultMutableTreeNode) in.readObject());
      }
      catch (final Exception e) {
         JOptionPane.showMessageDialog(meigasWindow, "Error: Archivo corrupto o de distinta versi�n.");
      }

   }


   private File selectLoadFile(final MeigasPanel panel) {
      final JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Abrir proyecto");

      if (JFileChooser.APPROVE_OPTION != fileChooser.showOpenDialog(panel)) {
         return null;
      }
      final File file = fileChooser.getSelectedFile();

      return file;

   }


   @Override
   public void execute(final MeigasPanel meigasWindow) {

      this.meigasWindow = meigasWindow;

      final DefaultTreeModel model = (DefaultTreeModel) meigasWindow.getTree().getModel();

      final DasocraticProject dp = Meigas.getDasocraticProject();
      if (dp.getHasChanged()) {
         final int iResult = JOptionPane.showConfirmDialog(meigasWindow,
                  "�Quiere abrir un nuevo proyecto sin guardar los cambios del actual?", "MEIGAS", JOptionPane.YES_NO_OPTION);
         if (iResult == JOptionPane.NO_OPTION) {
            return;
         }
      }

      try {
         load(model, selectLoadFile(meigasWindow));
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
