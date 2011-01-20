package es.unex.meigas.extLoadSaveProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import es.unex.meigas.core.DasocraticProject;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;


public class SaveProjectExtension
         extends
            AbstractMeigasExtension {

   MeigasPanel meigasWindow = null;


   @Override
   public String getName() {

      return "Guardar";

   }


   public String getMenuName() {

      return "Archivo";

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon("images/disk.png");
      return icon;
   }


   protected void save(final DefaultMutableTreeNode root,
                       final File file) throws Exception {


      if (file == null) {
         return;
      }

      final FileOutputStream fos = new FileOutputStream(file);
      final ObjectOutputStream bos = new ObjectOutputStream(fos);
      bos.writeObject(root);
      bos.close();

      final DasocraticProject dp = Meigas.getDasocraticProject();
      dp.setHasChanged(false);

   }


   private File selectSaveFile() {

      final JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Guardar proyecto");
      if (JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(meigasWindow)) {
         return null;
      }
      final File file = fileChooser.getSelectedFile();
      if (file.exists()) {
         final int iResult = JOptionPane.showConfirmDialog(meigasWindow, "El fichero ya existe. \n �Desea sobreescribirlo?",
                  "Meigas", JOptionPane.YES_NO_OPTION);

         if (iResult == JOptionPane.NO_OPTION) {
            return null;
         }
      }
      return file;

   }


   @Override
   public void execute(final MeigasPanel meigasWindow) {

      this.meigasWindow = meigasWindow;

      final DefaultMutableTreeNode root = (DefaultMutableTreeNode) meigasWindow.getTree().getModel().getRoot();
      try {
         save(root, selectSaveFile());
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
