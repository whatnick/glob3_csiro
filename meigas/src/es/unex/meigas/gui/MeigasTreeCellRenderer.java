package es.unex.meigas.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import es.unex.meigas.core.AdministrativeUnit;
import es.unex.meigas.core.Cruise;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Stand;
import es.unex.meigas.core.Tree;

public class MeigasTreeCellRenderer
         extends
            DefaultTreeCellRenderer {

   Icon m_AdministrativeUnitIcon;
   Icon m_StandIcon;
   Icon m_CruiseIcon;
   Icon m_PlotIcon;
   Icon m_TreeIcon;
   Icon m_AdministrativeUnitIconW;
   Icon m_StandIconW;
   Icon m_CruiseIconW;
   Icon m_PlotIconW;
   Icon m_TreeIconW;


   public MeigasTreeCellRenderer() {

      m_AdministrativeUnitIconW = new ImageIcon("images/AdministrativeUnitW.gif");
      m_StandIconW = new ImageIcon("images/StandW.gif");
      m_CruiseIconW = new ImageIcon("images/CruiseW.gif");
      m_PlotIconW = new ImageIcon("images/PlotW.gif");
      m_TreeIconW = new ImageIcon("images/TreeW.gif");
      m_AdministrativeUnitIcon = new ImageIcon("images/AdministrativeUnit.gif");
      m_StandIcon = new ImageIcon("images/Stand.gif");
      m_CruiseIcon = new ImageIcon("images/Cruise.gif");
      m_PlotIcon = new ImageIcon("images/Plot.gif");
      m_TreeIcon = new ImageIcon("images/Tree.gif");

   }


   @Override
   public Component getTreeCellRendererComponent(final JTree tree,
                                                 final Object value,
                                                 final boolean sel,
                                                 final boolean expanded,
                                                 final boolean leaf,
                                                 final int row,
                                                 final boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      final Icon icon = getCustomIcon(value);
      setOpenIcon(null);
      setClosedIcon(null);
      setIcon(icon);

      return this;
   }


   protected Icon getCustomIcon(final Object value) {

      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

      try {

         final Object element = node.getUserObject();

         if (element instanceof AdministrativeUnit) {
            if (((DasocraticElement) element).hasValidData()) {
               return m_AdministrativeUnitIcon;
            }
            else {
               return m_AdministrativeUnitIconW;
            }
         }
         if (element instanceof Stand) {
            if (((DasocraticElement) element).hasValidData()) {
               return m_StandIcon;
            }
            else {
               return m_StandIconW;
            }
         }
         if (element instanceof Cruise) {
            if (((DasocraticElement) element).hasValidData()) {
               return m_CruiseIcon;
            }
            else {
               return m_CruiseIconW;
            }
         }
         if (element instanceof Plot) {
            if (((DasocraticElement) element).hasValidData()) {
               return m_PlotIcon;
            }
            else {
               return m_PlotIconW;
            }
         }
         if (element instanceof Tree) {
            if (((DasocraticElement) element).hasValidData()) {
               return m_TreeIcon;
            }
            else {
               return m_TreeIconW;
            }
         }

      }
      catch (final Exception e) {}

      return null;
   }
}
