package es.unex.meigas.extRandomUtils;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;


public class RandomStandExtension
         extends
            AbstractMeigasExtension {

   /**
    * Minimum number of each level
    */
   int[]  minElements   = { 1, 1, 1, 1, 1, 5 };
   /**
    * Maximum number of each level
    */
   int[]  maxElements   = { 2, 3, 1, 15, 15, 26 };

   double height_mean   = 15.0;
   double height_stdDev = 10.0;

   double dbh_mean      = 40.0;
   double dbh_stdDev    = 30.0;

   double radius        = 25.0;


   @Override
   public String getName() {

      return "Crear muestreo de datos aleatorio";

   }


   public String getMenuName() {

      return "Herramientas";

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon("images/chart_organisation.png");
      return icon;
   }


   @Override
   public void execute(final MeigasPanel panel) {

      //Just for testing!
      final DefaultTreeModel model = (DefaultTreeModel) panel.getTree().getModel();

      RandomUtils.createNewDasocraticElements(model, minElements, maxElements, dbh_mean, dbh_stdDev, height_mean, height_stdDev,
               radius, true);
      final DefaultMutableTreeNode leaf = ((DefaultMutableTreeNode) model.getRoot()).getLastLeaf();
      panel.getTree().setSelectionPath(new TreePath(model.getPathToRoot(leaf)));

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
