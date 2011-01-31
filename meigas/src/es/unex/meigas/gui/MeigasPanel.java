

package es.unex.meigas.gui;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.vividsolutions.jts.geom.Coordinate;

import es.unex.meigas.core.AdministrativeUnit;
import es.unex.meigas.core.ConcentricPlot;
import es.unex.meigas.core.Cruise;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.DasocraticProject;
import es.unex.meigas.core.FixedRadiusPlot;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.MeigasExtension;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Stand;
import es.unex.meigas.core.Tree;
import es.unex.meigas.extBase.IMeigasExtension;


public class MeigasPanel
         extends
            JPanel {

   private static final int       BUTTON_WIDTH = 36;

   //private DasocraticProject m_DP;
   private DasocraticElement      m_ActiveElement;
   private TreePath               m_ActiveTreePath;

   private JScrollPane            jScrollPaneTree;
   private JPanel                 jPanelMenu;
   private JButton                jButtonAddUnit;
   private JButton                jButtonAddStand;
   private JButton                jButtonAddCruise;
   private JButton                jButtonAddPlot;
   private JButton                jButtonAddTree;
   private JToolBar               jPanelButtons;
   private JPanel                 jPanelTreeAndButtons;
   private JSplitPane             jSplitPane;
   private DasocraticElementPanel m_DEPanel    = null;
   private JTree                  jTree;
   private JPopupMenu             popupMenu;
   private JMenuBar               m_MenuBar;

   private JMenu                  m_FilesMenu;
   private JMenu                  m_ToolsMenu;


   public MeigasPanel() {

      initialize();

   }


   public DasocraticElement getActiveElement() {

      return m_ActiveElement;

   }


   public void startNewProject() {

      m_ActiveElement = Meigas.startNewDasocraticProject();
      m_DEPanel = new EmptyPanel();
      jSplitPane.setRightComponent(m_DEPanel);

      fillTree();
      setEnabledButtons();

   }


   public JTree getTree() {

      return jTree;

   }


   public TreePath getActiveTreePath() {

      return m_ActiveTreePath;

   }


   public void setActiveTreePath(final TreePath path) {

      m_ActiveTreePath = path;
      jTree.setSelectionPath(path);

   }


   protected void initialize() {

      this.setPreferredSize(new java.awt.Dimension(700, 450));
      this.setSize(new java.awt.Dimension(700, 450));
      {
         final TableLayout thisLayout = new TableLayout(new double[][] { { 6.0, TableLayout.FILL, 6.0 },
                  { 7.0, 15.0, TableLayout.FILL, 6.0 } });
         thisLayout.setHGap(5);
         thisLayout.setVGap(5);
         this.setLayout(thisLayout);
         {
            jScrollPaneTree = new JScrollPane();
            jTree = new JTree();
            jScrollPaneTree.setViewportView(jTree);
            final MouseListener ml = new CustomMouseListener();
            jTree.addMouseListener(ml);

            //Update selection when using cursor keys on the JTree
            //            jTree.addTreeSelectionListener(new TreeSelectionListener() {
            //               public void valueChanged(final TreeSelectionEvent e) {
            //                  final TreePath path = jTree.getSelectionPath();
            //                  if (path == null) {
            //                     return;
            //                  }
            //                  updateSelection(path);
            //               }
            //            });

            jPanelTreeAndButtons = new JPanel();
            final TableLayout jPanelTreeAndButtonsLayout = new TableLayout(new double[][] { { TableLayout.FILL },
                     { TableLayout.FILL, 35.0 } });
            jPanelTreeAndButtonsLayout.setHGap(5);
            jPanelTreeAndButtonsLayout.setVGap(1);
            jPanelTreeAndButtons.setLayout(jPanelTreeAndButtonsLayout);
            jPanelTreeAndButtons.add(jScrollPaneTree, "0,  0");
            jPanelButtons = new JToolBar();
            jPanelButtons.setFloatable(false);
            final TableLayout jPanelButtonsLayout = new TableLayout(new double[][] {
                     { BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_WIDTH }, { TableLayout.FILL } });
            jPanelButtonsLayout.setHGap(5);
            jPanelButtonsLayout.setVGap(5);
            jPanelButtons.setLayout(jPanelButtonsLayout);
            jPanelTreeAndButtons.add(jPanelButtons, "0, 1");
            {
               jButtonAddUnit = new JButton();
               jButtonAddUnit.setIcon(new ImageIcon("images/AdministrativeUnit.gif"));
               jPanelButtons.add(jButtonAddUnit, "0,  0");
               jButtonAddUnit.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     addElement(AdministrativeUnit.class);
                  }
               });
               jButtonAddStand = new JButton();
               jButtonAddStand.setIcon(new ImageIcon("images/Stand.gif"));
               jPanelButtons.add(jButtonAddStand, "1,  0");
               jButtonAddStand.setPreferredSize(new java.awt.Dimension(199, 14));
               jButtonAddStand.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     addElement(Stand.class);
                  }
               });
               jButtonAddCruise = new JButton();
               jButtonAddCruise.setIcon(new ImageIcon("images/Cruise.gif"));
               jPanelButtons.add(jButtonAddCruise, "2,  0");
               jButtonAddCruise.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     addElement(Cruise.class);
                  }
               });
               jButtonAddPlot = new JButton();
               jButtonAddPlot.setIcon(new ImageIcon("images/Plot.gif"));
               jPanelButtons.add(jButtonAddPlot, "3,  0");
               jButtonAddPlot.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     showPlotPopupMenu();
                  }
               });
               jButtonAddTree = new JButton();
               jButtonAddTree.setIcon(new ImageIcon("images/Tree.gif"));
               jPanelButtons.add(jButtonAddTree, "4,  0");
               jButtonAddTree.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     addElement(Tree.class);
                  }
               });
            }

            m_MenuBar = new JMenuBar();

            m_FilesMenu = new JMenu("Archivo");
            final IMeigasExtension[] extensions = Meigas.getExtensions();
            for (final IMeigasExtension extension : extensions) {
               final MeigasPanel panel = this;
               if (extension.showInMenuBar(this) && (extension.getMenuName() == m_FilesMenu.getText())) {
                  final JMenuItem menuItem = new JMenuItem(extension.getName());
                  menuItem.setIcon(extension.getIcon());
                  menuItem.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        extension.execute(panel);
                     }
                  });
                  m_FilesMenu.add(menuItem);
               }
            }
            m_MenuBar.add(m_FilesMenu);

            m_ToolsMenu = new JMenu("Herramientas");
            for (final IMeigasExtension extension : extensions) {
               final MeigasPanel panel = this;
               final String name = m_ToolsMenu.getText();
               if (extension.showInMenuBar(this) && (extension.getMenuName() == m_ToolsMenu.getText())) {
                  final JMenuItem menuItem = new JMenuItem(extension.getName());
                  menuItem.setIcon(extension.getIcon());
                  menuItem.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        extension.execute(panel);
                     }
                  });
                  m_ToolsMenu.add(menuItem);
               }
            }
            m_MenuBar.add(m_ToolsMenu);
            //this.setJMenuBar(m_MenuBar);

            m_DEPanel = new EmptyPanel();
            jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jPanelTreeAndButtons, m_DEPanel);
            this.add(jSplitPane, "1, 2");
         }
         {
            jPanelMenu = new JPanel();
            final BorderLayout jPanelMenuLayout = new BorderLayout();
            jPanelMenu.setLayout(jPanelMenuLayout);
            jPanelMenu.add(m_MenuBar);
            m_MenuBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            this.add(jPanelMenu, "1, 1");
         }

         startNewProject();

      }

   }


   protected void showPopupMenu(final MouseEvent e) {

      int i;
      JMenuItem menuItem;
      popupMenu = new JPopupMenu("Menu");

      final IMeigasExtension[] extensions = Meigas.getExtensions();
      final IMeigasExtension[] extensionsOrdered = new IMeigasExtension[extensions.length];
      System.arraycopy(extensions, 0, extensionsOrdered, 0, extensions.length);
      Arrays.sort(extensionsOrdered);
      for (i = 0; i < extensionsOrdered.length; i++) {
         final IMeigasExtension extension = extensionsOrdered[i];
         final MeigasPanel panel = this;
         if (extension.showInContextMenu()) {
            menuItem = new JMenuItem(extension.getName());
            menuItem.setEnabled(extension.isEnabled(this));
            menuItem.setIcon(extension.getIcon());
            menuItem.addActionListener(new ActionListener() {
               public void actionPerformed(final ActionEvent evt) {
                  extension.execute(panel);
               }
            });
            popupMenu.add(menuItem);
         }
      }

      if (popupMenu.getComponentCount() != 0) {
         popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }


   }


   protected void showPlotPopupMenu() {

      JMenuItem menuItem;
      final JPopupMenu popup = new JPopupMenu("Menu");

      menuItem = new JMenuItem("Parcela circular de radio fijo");
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(final ActionEvent evt) {
            addElement(FixedRadiusPlot.class);
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Parcela circular de radio variable");
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(final ActionEvent evt) {
            addElement(ConcentricPlot.class);
         }
      });
      popup.add(menuItem);

      popup.show(jButtonAddPlot, jButtonAddPlot.getWidth() / 2, jButtonAddPlot.getHeight() / 2);

   }


   public void fillTree() {

      int i;
      final DasocraticProject dp = Meigas.getDasocraticProject();
      DefaultMutableTreeNode node;
      final DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(dp);

      for (i = 0; i < dp.getElementsCount(); i++) {
         node = createNode(dp.getElement(i));
         addNodeInSortedOrder(mainNode, node);
      }

      jTree.setModel(new DefaultTreeModel(mainNode));
      jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      jTree.setCellRenderer(new MeigasTreeCellRenderer());
      jTree.setSelectionPath(jTree.getPathForRow(0));

   }


   private DefaultMutableTreeNode createNode(final DasocraticElement element) {

      int i;
      DefaultMutableTreeNode node;
      final DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(element);

      for (i = 0; i < element.getElementsCount(); i++) {
         node = createNode(element.getElement(i));
         addNodeInSortedOrder(mainNode, node);
      }

      return mainNode;

   }


   private void addNodeInSortedOrder(final DefaultMutableTreeNode parent,
                                     final DefaultMutableTreeNode child) {

      final int n = parent.getChildCount();
      if (n == 0) {
         parent.add(child);
         return;
      }
      DefaultMutableTreeNode node = null;
      for (int i = 0; i < n; i++) {
         node = (DefaultMutableTreeNode) parent.getChildAt(i);
         try {
            if (node.toString().compareTo(child.toString()) > 0) {
               parent.insert(child, i);
               return;
            }
         }
         catch (final Exception e) {
            e.printStackTrace();
         }
      }
      parent.add(child);
      return;
   }


   public void setEnabledButtons() {

      jButtonAddUnit.setEnabled(true);
      jButtonAddStand.setEnabled(false);
      jButtonAddCruise.setEnabled(false);
      jButtonAddPlot.setEnabled(false);
      jButtonAddTree.setEnabled(false);

      if (m_ActiveElement instanceof AdministrativeUnit) {
         jButtonAddStand.setEnabled(true);
      }
      if (m_ActiveElement instanceof Stand) {
         jButtonAddStand.setEnabled(true);
         jButtonAddCruise.setEnabled(true);
      }
      if (m_ActiveElement instanceof Cruise) {
         jButtonAddStand.setEnabled(true);
         jButtonAddCruise.setEnabled(true);
         jButtonAddPlot.setEnabled(true);
      }
      if ((m_ActiveElement instanceof Plot) || (m_ActiveElement instanceof Tree)) {
         jButtonAddStand.setEnabled(true);
         jButtonAddCruise.setEnabled(true);
         jButtonAddPlot.setEnabled(true);
         jButtonAddTree.setEnabled(true);
      }

   }


   public void selectNodeFromObject(final Object ob) {

      final DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
      final DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
      DefaultMutableTreeNode node = null;

      if (root != null) {
         for (final Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements();) {
            final DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
            if (ob.equals(current.getUserObject())) {
               node = current;
               break;
            }
         }
      }

      if (node != null) {
         selectPath(new TreePath(model.getPathToRoot(node)));
      }

   }


   public void selectPath(final TreePath path) {

      if (path.equals(m_ActiveTreePath)) {
         return;
      }

      if (m_DEPanel.checkDataAndUpdate()) {
         jTree.setSelectionPath(path);
         jTree.scrollPathToVisible(path);
         updateSelection(path);
      }

   }


   public void updateSelection(final TreePath path) {

      /*if (m_DEPanel != null) {
         //m_DEPanel.removeListeners();
      }*/

      if (path == null) {
         m_ActiveElement = null;
         m_DEPanel = new EmptyPanel();
      }
      else {
         m_ActiveTreePath = path;
         final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

         if (node.isRoot()) {
            m_ActiveElement = Meigas.getDasocraticProject();
            m_DEPanel = new EmptyPanel();
         }
         else {
            final DasocraticElement element = ((DasocraticElement) node.getUserObject());
            m_ActiveElement = element;
            m_DEPanel = new DasocraticElementPanel(element, this);

            /*final IGISConnection gisConn = Meigas.getGISConnection();
            gisConn.sync(m_ActiveElement);*/

         }
      }

      jSplitPane.setRightComponent(m_DEPanel);
      setEnabledButtons();

   }


   public void updateGeoPosition(final TreePath path) {
      if (path != null) {
         final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
         final DasocraticElement element = ((DasocraticElement) node.getUserObject());
         if (element instanceof Tree) {
            final Coordinate coord = ((Coordinate) element.getParameterValue(Tree.COORD));
            MeigasExtension.getGisConnection().zoomToPosition(coord.x, coord.y, 1000);
         }
         if (element instanceof Plot) {
            final Coordinate coord = ((Coordinate) element.getParameterValue(Plot.COORD));
            MeigasExtension.getGisConnection().zoomToPosition(coord.x, coord.y, 1000);
         }
      }
   }


   private void addElement(final Class clazz) {

      DasocraticElement element = null;
      DasocraticElement parentElement = null;
      TreePath parentPath = null;
      DefaultMutableTreeNode parentNode = null;
      DefaultMutableTreeNode node = null;
      final DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();

      try {
         element = (DasocraticElement) clazz.newInstance();
      }
      catch (final InstantiationException e) {
         e.printStackTrace();
         return;
      }
      catch (final IllegalAccessException e) {
         e.printStackTrace();
         return;
      }

      if (!(m_ActiveElement instanceof DasocraticProject)) {
         final Class[] classes = element.getParentElementClass();
         for (final Class classe : classes) {
            parentElement = m_ActiveElement.getParentOfType(classe);
            parentPath = getParentPathOfType(classe);
            if ((parentElement != null) && (parentPath != null)) {
               break;
            }
         }

         if ((parentElement == null) || (parentPath == null)) {
            return;
         }
         else {
            parentElement = parentElement.addElement(element);
            if (parentElement != null) {
               parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
               DasocraticElement elementFromTree = (DasocraticElement) parentNode.getUserObject();
               while (elementFromTree != parentElement) {
                  parentNode = (DefaultMutableTreeNode) parentNode.getParent();
                  elementFromTree = (DasocraticElement) parentNode.getUserObject();
               }
               node = new DefaultMutableTreeNode(element);
            }
            else {
               return;
            }
         }
      }
      else {
         if (clazz.equals(AdministrativeUnit.class)) {
            Meigas.getDasocraticProject().addElement(element);
            node = new DefaultMutableTreeNode(element);
            parentNode = (DefaultMutableTreeNode) model.getRoot();
         }
         else {
            return;
         }
      }

      model.insertNodeInto(node, parentNode, parentNode.getChildCount());
      selectPath(new TreePath(model.getPathToRoot(node)));
      jTree.expandPath(m_ActiveTreePath);
      jTree.expandRow(0);

   }


   private TreePath getParentPathOfType(final Class clazz) {

      DasocraticElement parentElement;
      DefaultMutableTreeNode parentNode;
      TreePath parent = m_ActiveTreePath;

      try {
         parentNode = (DefaultMutableTreeNode) parent.getLastPathComponent();
         parentElement = (DasocraticElement) parentNode.getUserObject();
         while (!clazz.isInstance(parentElement) && (parentElement != null)) {
            parent = parent.getParentPath();
            parentNode = (DefaultMutableTreeNode) parent.getLastPathComponent();
            parentElement = (DasocraticElement) parentNode.getUserObject();
         }
      }
      catch (final Exception e) {
         return null;
      }

      return parent;

   }


   public class CustomMouseListener
            extends
               MouseAdapter {

      @Override
      public void mousePressed(final MouseEvent e) {

         final TreePath path = jTree.getPathForLocation(e.getX(), e.getY());
         if (path == null) {
            return;
         }
         if (e.getButton() == MouseEvent.BUTTON1) {
            if (path.equals(m_ActiveTreePath)) {
               return;
            }
            if (m_DEPanel.checkDataAndUpdate()) {
               updateSelection(path);
               updateGeoPosition(path);
            }
            else {
               JOptionPane.showMessageDialog(null, "Parámetros inválidos", "Aviso", JOptionPane.WARNING_MESSAGE);
               jTree.setSelectionPath(m_ActiveTreePath);
            }
         }
         else {
            if (path.equals(m_ActiveTreePath)) {
               if (!m_DEPanel.checkDataAndUpdate()) {
                  JOptionPane.showMessageDialog(null, "Parámetros inválidos", "Aviso", JOptionPane.WARNING_MESSAGE);
                  return;
               }
            }
            else {
               if (m_DEPanel.checkDataAndUpdate()) {
                  jTree.setSelectionPath(path);
                  updateSelection(path);
               }
               else {
                  JOptionPane.showMessageDialog(null, "Parámetros inválidos", "Aviso", JOptionPane.WARNING_MESSAGE);
                  return;
               }
            }
            showPopupMenu(e);
         }
      }
   }

}
