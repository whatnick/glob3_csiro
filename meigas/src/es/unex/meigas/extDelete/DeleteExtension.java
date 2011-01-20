package es.unex.meigas.extDelete;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.DasocraticProject;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class DeleteExtension
         extends
            AbstractMeigasExtension {

   @Override
   public String getName() {

      return "Eliminar";

   }


   public String getMenuName() {

      return null;

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon("images/cross.png");
      return icon;
   }


   @Override
   public void execute(final MeigasPanel panel) {

      final DefaultTreeModel model = (DefaultTreeModel) panel.getTree().getModel();
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) panel.getActiveTreePath().getLastPathComponent();

      if (!node.isRoot()) {
         final int iResult = JOptionPane.showConfirmDialog(panel, "Esta operaci�n eliminar� todos los elementos por debajo \n"
                                                                  + "de este nivel. �Est� seguro?", "Eliminar",
                  JOptionPane.YES_NO_OPTION);
         if (iResult == JOptionPane.NO_OPTION) {
            return;
         }


         DasocraticElement activeElement = panel.getActiveElement();
         // Get the previous node to set as selected
         final int selectIdx = node.getParent().getIndex(node) - 1;
         TreeNode nodeAux = null;
         TreePath treePath = null;
         if (selectIdx > -1) {
            nodeAux = node.getParent().getChildAt(selectIdx);
            treePath = new TreePath(model.getPathToRoot(nodeAux));
         }
         activeElement.getParent().removeElement(activeElement);
         activeElement = null;
         model.removeNodeFromParent(node);
         panel.setActiveTreePath(treePath);
         panel.updateSelection(treePath);
         panel.setEnabledButtons();
      }

   }


   @Override
   public void initialize() {

   }


   @Override
   public boolean showInContextMenu() {

      return true;
   }


   @Override
   public boolean showInMenuBar(final MeigasPanel panel) {

      return false;

   }


   public boolean isEnabled(final MeigasPanel window) {

      final DasocraticElement element = window.getActiveElement();

      if (element instanceof DasocraticProject) {
         return false;
      }
      else {
         return true;
      }

   }

}
