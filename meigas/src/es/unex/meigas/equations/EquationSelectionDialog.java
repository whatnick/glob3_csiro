package es.unex.meigas.equations;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.nfunk.jep.JEP;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.Specie;
import es.unex.meigas.core.SpeciesCatalog;
import es.unex.meigas.gui.MeigasPanel;

public class EquationSelectionDialog
         extends
            JDialog {

   public static final int      VOLUME              = 1;
   public static final int      VOLUME_WITHOUT_BARK = 2;
   public static final int      VOLUME_INCREMENT    = 4;
   public static final int      ALL_EQUATIONS       = VOLUME | VOLUME_WITHOUT_BARK | VOLUME_INCREMENT;

   private JPanel               jPanelButtons;
   private JLabel               jLabelOrder;
   private JComboBox            jComboBoxOrder;
   private JButton              jButtonCancel;
   private JTree                jTree;
   private JTextField           jTextFieldEquation;
   private JLabel               jLabelEquals;
   private JLabel               jLabelSpecie;
   private JLabel               jLabelSiteIndex;
   private JTextField           jTextFieldZone;
   private JLabel               jLabelZone;
   private JTextField           jTextFieldDescription;
   private JLabel               jLabelDescription;
   private JPanel               jPanelParameters;
   private JPanel               jPanelButtons2;
   private JScrollPane          jScrollPane;
   private JButton              jButtonOk;
   private JButton              jButtonNew;
   private JButton              jButtonRemove;
   private JButton              jButtonOpenCalculator;
   private JComboBox            jComboBoxSpecies;
   private JComboBox            jComboBoxSiteIndex;
   private JCheckBox[]          jCheckBoxShapeFactor;
   private JPanel               jPanelShapeFactors;
   private JLabel               jLabelShapeFactor;
   private JLabel               jLabelEquation;
   private JComboBox            jComboBoxParameter;

   private final Equation       m_Equation;
   private Equation             m_ActiveEquation;
   private final Equations      m_Equations;
   private final SpeciesCatalog m_Species;
   private TreePath             m_ActiveTreePath;
   private int                  m_iOrder;
   private final int            m_iParameter;


   public EquationSelectionDialog(final MeigasPanel panel,
                                  final int iParameter,
                                  final Equation equation) {

      super(Meigas.getMainFrame(), true);

      m_Equation = equation;
      m_Equations = Meigas.getEquations();
      m_Species = Meigas.getSpeciesCatalog();
      m_ActiveTreePath = null;
      m_ActiveEquation = null;

      m_iParameter = iParameter;

      m_iOrder = 0;

      initGUI();

      setLocationRelativeTo(null);

   }


   private void initGUI() {

      int i;

      this.setPreferredSize(new java.awt.Dimension(750, 260));
      this.setSize(new java.awt.Dimension(750, 260));
      try {
         {
            final TableLayout thisLayout = new TableLayout(new double[][] {
                     { 5.0, TableLayout.FILL, 5.0, TableLayout.FILL, TableLayout.FILL, 5.0 },
                     { 5.0, TableLayout.FILL, 20.0, 20.0, 25.0, 5.0 } });
            thisLayout.setHGap(5);
            thisLayout.setVGap(5);
            this.setLayout(thisLayout);
            this.setPreferredSize(new java.awt.Dimension(775, 335));
            {
               jPanelButtons = new JPanel();
               final TableLayout jPanelButtonsLayout = new TableLayout(new double[][] {
                        { TableLayout.FILL, 5.0, TableLayout.FILL }, { TableLayout.FILL } });
               jPanelButtonsLayout.setHGap(5);
               jPanelButtonsLayout.setVGap(5);
               jPanelButtons.setLayout(jPanelButtonsLayout);
               this.add(jPanelButtons, "4, 4");
               {
                  jButtonOk = new JButton();
                  jPanelButtons.add(jButtonOk, "0, 0");
                  jButtonOk.setText("Aceptar");
                  jButtonOk.setEnabled(false);
                  jButtonOk.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        selectEquation();
                     }
                  });
               }
               {
                  jButtonCancel = new JButton();
                  jPanelButtons.add(jButtonCancel, "2, 0");
                  jButtonCancel.setText("Cancelar");
                  jButtonCancel.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        cancel();
                     }
                  });
               }
            }
            {
               jLabelOrder = new JLabel();
               this.add(jLabelOrder, "1, 2");
               jLabelOrder.setText("Ordenar seg�n:");
            }
            {
               final ComboBoxModel jComboBoxOrderModel = new DefaultComboBoxModel(new String[] { "Zona - Especie",
                        "Especie - Zona" });
               jComboBoxOrder = new JComboBox();
               this.add(jComboBoxOrder, "1, 3");
               jComboBoxOrder.setModel(jComboBoxOrderModel);
               jComboBoxOrder.addItemListener(new ItemListener() {
                  public void itemStateChanged(final ItemEvent e) {
                     if (e.getStateChange() == ItemEvent.SELECTED) {
                        reorderTree();
                     }
                  }
               });
            }
            {
               jScrollPane = new JScrollPane();
               this.add(jScrollPane, "1, 1");
               {
                  jTree = new JTree();
                  jScrollPane.setViewportView(jTree);
                  jTree.addMouseListener(new MouseAdapter() {
                     @Override
                     public void mousePressed(final MouseEvent evt) {
                        jTreeMousePressed(evt);
                     }
                  });
               }
            }
            {
               jPanelButtons2 = new JPanel();
               final TableLayout jPanelButtonsLayout2 = new TableLayout(new double[][] {
                        { TableLayout.FILL, 5.0, TableLayout.FILL }, { TableLayout.FILL } });
               jPanelButtonsLayout2.setHGap(5);
               jPanelButtonsLayout2.setVGap(5);
               jPanelButtons2.setLayout(jPanelButtonsLayout2);
               this.add(jPanelButtons2, "1, 4");
               {
                  jButtonNew = new JButton();
                  jPanelButtons2.add(jButtonNew, "0, 0");
                  jButtonNew.setText("Nueva");
                  jButtonNew.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        newEquation();
                     }
                  });
               }
               {
                  jButtonRemove = new JButton();
                  jPanelButtons2.add(jButtonRemove, "2, 0");
                  jButtonRemove.setText("Eliminar");
                  jButtonRemove.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        removeEquation();
                     }
                  });
               }
            }
            {
               jPanelParameters = new JPanel();
               final TableLayout jPanelParametersLayout = new TableLayout(new double[][] {
                        { 5.0, TableLayout.FILL, TableLayout.FILL, 10.0, TableLayout.FILL, TableLayout.FILL, 15.0, 5.0 },
                        { TableLayout.FILL, 20.0, TableLayout.FILL, 20.0, TableLayout.FILL, 26.0, TableLayout.FILL, 20.0,
                                 TableLayout.FILL, 20.0, TableLayout.FILL } });
               jPanelParametersLayout.setHGap(5);
               jPanelParametersLayout.setVGap(5);
               jPanelParameters.setLayout(jPanelParametersLayout);
               jPanelParameters.setVisible(false);
               this.add(jPanelParameters, "3, 1, 4, 3");
               jPanelParameters.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
               {
                  jLabelDescription = new JLabel();
                  jPanelParameters.add(jLabelDescription, "1, 1");
                  jLabelDescription.setText("Descripción");
               }
               {
                  jTextFieldDescription = new JTextField();
                  jPanelParameters.add(jTextFieldDescription, "2, 1, 6, 1");
               }
               {
                  jLabelZone = new JLabel();
                  jPanelParameters.add(jLabelZone, "1, 3");
                  jLabelZone.setText("Zona de aplicación");
               }
               {
                  jTextFieldZone = new JTextField();
                  jPanelParameters.add(jTextFieldZone, "2, 3, 6, 3");
               }
               {
                  jLabelSiteIndex = new JLabel();
                  jPanelParameters.add(jLabelSiteIndex, "4, 7");
                  jLabelSiteIndex.setText("Calidad");
               }
               {
                  final ComboBoxModel jComboBoxSiteIndexModel = new DefaultComboBoxModel(new String[] { "Todas", "I", "II",
                           "III", "IV", "V" });
                  jComboBoxSiteIndex = new JComboBox();
                  jPanelParameters.add(jComboBoxSiteIndex, "5, 7, 6, 7");
                  jComboBoxSiteIndex.setModel(jComboBoxSiteIndexModel);
               }
               {
                  jLabelSpecie = new JLabel();
                  jPanelParameters.add(jLabelSpecie, "1, 7");
                  jLabelSpecie.setText("Especie");
               }
               {
                  jComboBoxSpecies = new JComboBox();
                  jPanelParameters.add(jComboBoxSpecies, "2, 7");
                  final Specie species[] = m_Species.getSpecies();
                  final ComboBoxModel jComboBoxSpeciesModel = new DefaultComboBoxModel(species);
                  jComboBoxSpecies.setModel(jComboBoxSpeciesModel);
                  /*jComboBoxSpecies.setEditable(true);
                  jComboBoxSpecies.addKeyListener(new KeyAdapter() {
                     @Override
                     public void keyTyped(final KeyEvent event) {
                        validateKeyTyping(event);
                     }
                  });*/
               }
               {
                  jLabelEquals = new JLabel();
                  jPanelParameters.add(jLabelEquals, "3, 9");
                  jLabelEquals.setText("=");
               }
               {
                  jTextFieldEquation = new JTextField();
                  jPanelParameters.add(jTextFieldEquation, "4, 9, 5, 9");
               }
               {
                  final ComboBoxModel jComboBoxParameterModel = new DefaultComboBoxModel(new String[] { "VCC", "VSC", "IAVSC" });
                  jComboBoxParameter = new JComboBox();
                  jPanelParameters.add(jComboBoxParameter, "2, 9");
                  jComboBoxParameter.setModel(jComboBoxParameterModel);
               }
               {
                  jLabelEquation = new JLabel();
                  jPanelParameters.add(jLabelEquation, "1, 9");
                  jLabelEquation.setText("Ecuaci�n");
               }
               {
                  jLabelShapeFactor = new JLabel();
                  jPanelParameters.add(jLabelShapeFactor, "1, 5");
                  jLabelShapeFactor.setText("Coefs. de forma");
               }
               {
                  jPanelShapeFactors = new JPanel();
                  jPanelParameters.add(jPanelShapeFactors, "2, 5, 6, 5");
                  jCheckBoxShapeFactor = new JCheckBox[6];
                  for (i = 0; i < 6; i++) {
                     jCheckBoxShapeFactor[i] = new JCheckBox();
                     jPanelShapeFactors.add(jCheckBoxShapeFactor[i]);
                     jCheckBoxShapeFactor[i].setText(Integer.toString(i + 1));
                  }
               }
               {
                  jButtonOpenCalculator = new JButton();
                  jPanelParameters.add(jButtonOpenCalculator, "6, 9");
                  jButtonOpenCalculator.setText("...");
                  jButtonOpenCalculator.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        final EquationCalculatorDialog dialog = new EquationCalculatorDialog(jTextFieldEquation);
                        dialog.setVisible(true);
                     }
                  });
               }
               fillTree();
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }
   }


   protected void reorderTree() {

      if (m_iOrder != jComboBoxOrder.getSelectedIndex()) {
         if (checkDataAndUpdate()) {
            fillTree();
            m_ActiveTreePath = null;
            jTree.setSelectionPath(null);
            updateSelection(jTree.getSelectionPath());
            setEnabledButtons();
            m_iOrder = jComboBoxOrder.getSelectedIndex();
         }
         else {
            jComboBoxOrder.setSelectedIndex(m_iOrder);
            JOptionPane.showMessageDialog(null, "La sintaxis de la fórmula no es correcta", "Aviso", JOptionPane.WARNING_MESSAGE);

         }
      }
   }


   private void fillTree() {

      ArrayList list;
      HashMap subMap;
      final HashMap map = new HashMap();
      Equation eq;
      final DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode("Ecuaciones");
      DefaultMutableTreeNode child, subChild, eqNode;
      String sKey;

      createMap(map);

      Set subSet;
      Iterator subIter, listIter;
      final Set set = map.keySet();
      final Iterator iter = set.iterator();

      while (iter.hasNext()) {
         sKey = (String) iter.next();
         subMap = (HashMap) map.get(sKey);
         subSet = subMap.keySet();
         subIter = subSet.iterator();
         child = new DefaultMutableTreeNode(sKey);
         while (subIter.hasNext()) {
            sKey = (String) subIter.next();
            subChild = new DefaultMutableTreeNode(sKey);
            list = (ArrayList) subMap.get(sKey);
            listIter = list.iterator();
            while (listIter.hasNext()) {
               eq = (Equation) listIter.next();
               eqNode = new DefaultMutableTreeNode(eq);
               addNodeInSortedOrder(subChild, eqNode);
            }
            addNodeInSortedOrder(child, subChild);
         }
         addNodeInSortedOrder(mainNode, child);
      }
      jTree.setModel(new DefaultTreeModel(mainNode));

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


   private void createMap(final HashMap map) {

      Equation eq;
      String primaryKey, secondaryKey;
      ArrayList list;
      HashMap submap;
      final ArrayList equations = m_Equations.getEquations();
      final Iterator iter = equations.iterator();

      while (iter.hasNext()) {
         eq = (Equation) iter.next();
         if ((eq.getParameter() | m_iParameter) != 0) {
            if (jComboBoxOrder.getSelectedIndex() == 0) { //zone - specie
               primaryKey = eq.getLocation();
               secondaryKey = eq.getSpecie().name;
            }
            else { //specie - zone
               secondaryKey = eq.getLocation();
               primaryKey = eq.getSpecie().name;
            }
            submap = (HashMap) map.get(primaryKey);
            if (submap == null) {
               submap = new HashMap();
               map.put(primaryKey, submap);
            }
            list = (ArrayList) submap.get(secondaryKey);
            if (list == null) {
               list = new ArrayList();
               submap.put(secondaryKey, list);
            }
            list.add(eq);
         }

      }

   }


   private void jTreeMousePressed(final MouseEvent e) {

      final TreePath path = jTree.getPathForLocation(e.getX(), e.getY());

      if (path == null) {
         return;
      }

      if (checkDataAndUpdate()) {
         updateSelection(path);
         setEnabledButtons();
      }
      else {
         JOptionPane.showMessageDialog(null, "La sintaxis de la f�rmula no es correcta", "Aviso", JOptionPane.WARNING_MESSAGE);
         jTree.setSelectionPath(m_ActiveTreePath);
      }


   }


   private void setEnabledButtons() {

      if (m_ActiveEquation == null) {
         jButtonRemove.setEnabled(false);
         jButtonOk.setEnabled(false);
      }
      else {
         jButtonRemove.setEnabled(true);
         jButtonOk.setEnabled(true);
      }

   }


   private boolean checkDataAndUpdate() {

      int i;

      if (m_ActiveEquation == null) {
         return true;
      }

      try {
         m_ActiveEquation.setDescription(jTextFieldDescription.getText());
         final String sFormula = jTextFieldEquation.getText().toLowerCase();
         final JEP jep = new JEP();
         jep.addStandardConstants();
         jep.addStandardFunctions();
         if (jComboBoxParameter.getSelectedIndex() == Equation.VOLUME_INCREMENT) {
            jep.addVariable("vcc", 0.0);
            //TODO should add also dn, ht, hf?
         }
         else {
            jep.addVariable("dn", 0.0);
            jep.addVariable("ht", 0.0);
            jep.addVariable("hf", 0.0);
            jep.addVariable("cz", 0.0);
         }
         jep.parseExpression(sFormula);
         if (jep.hasError()) {
            return false;
         }
         m_ActiveEquation.setEquation(jTextFieldEquation.getText());
         m_ActiveEquation.setLocation(jTextFieldZone.getText());
         m_ActiveEquation.setParameter(jComboBoxParameter.getSelectedIndex());
         m_ActiveEquation.setSiteIndex(jComboBoxSiteIndex.getSelectedIndex());
         //m_ActiveEquation.setSpecie(((JTextField) jComboBoxSpecies.getEditor().getEditorComponent()).getText());
         m_ActiveEquation.setSpecie((Specie) jComboBoxSpecies.getSelectedItem());
         final boolean[] shapeFactor = new boolean[6];
         for (i = 0; i < shapeFactor.length; i++) {
            shapeFactor[i] = jCheckBoxShapeFactor[i].isSelected();
         }
         m_ActiveEquation.setShapeFactor(shapeFactor);
      }
      catch (final Exception e) {
         return false;
      }

      return true;
   }


   private void updateSelection(final TreePath path) {

      int i;

      if ((path == null) || ((DefaultMutableTreeNode) path.getLastPathComponent()).isRoot()) {
         m_ActiveEquation = null;
         jPanelParameters.setVisible(false);
      }
      else {
         m_ActiveTreePath = path;
         final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

         if (!node.isLeaf()) {
            m_ActiveEquation = null;
            jPanelParameters.setVisible(false);
         }
         else {
            final Equation eq = (Equation) node.getUserObject();
            m_ActiveEquation = eq;
            jPanelParameters.setVisible(true);
            jTextFieldEquation.setText(eq.getEquation());
            jTextFieldDescription.setText(eq.getDescription());
            jTextFieldZone.setText(eq.getLocation());
            jComboBoxSiteIndex.setSelectedIndex(eq.getSiteIndex());
            jComboBoxParameter.setSelectedIndex(eq.getParameter());
            jComboBoxSpecies.setSelectedItem(eq.getSpecie());
            final boolean[] shapeFactor = eq.getShapeFactor();
            for (i = 0; i < shapeFactor.length; i++) {
               jCheckBoxShapeFactor[i].setSelected(shapeFactor[i]);
            }
         }
         jTree.scrollPathToVisible(m_ActiveTreePath);
      }

   }


   private void validateKeyTyping(final KeyEvent event) {

      final JTextField textField = (JTextField) event.getSource();
      String text = textField.getText();
      text += event.getKeyChar();
      ((JComboBox) textField.getParent()).setSelectedItem(text);

   }


   protected void cancel() {

      this.dispose();
      this.setVisible(false);

   }


   private void selectEquation() {

      if (m_Equation == null) {
         cancel();
         return;
      }

      if (checkDataAndUpdate()) {
         m_Equation.setDescription(m_ActiveEquation.getDescription());
         m_Equation.setLocation(m_ActiveEquation.getLocation());
         m_Equation.setEquation(m_ActiveEquation.getEquation());
         m_Equation.setParameter(m_ActiveEquation.getParameter());
         m_Equation.setShapeFactor(m_ActiveEquation.getShapeFactor());
         m_Equation.setSiteIndex(m_ActiveEquation.getSiteIndex());
         m_Equation.setSpecie(m_ActiveEquation.getSpecie());
         cancel();
      }
      else {
         JOptionPane.showMessageDialog(null, "La sintaxis de la f�rmula no es correcta", "Aviso", JOptionPane.WARNING_MESSAGE);
      }
   }


   private void removeEquation() {

      m_Equations.removeEquation(m_ActiveEquation);
      final DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_ActiveTreePath.getLastPathComponent();
      model.removeNodeFromParent(node);
      m_ActiveTreePath = null;
      jTree.setSelectionPath(null);
      updateSelection(jTree.getSelectionPath());
      setEnabledButtons();

   }


   private void newEquation() {

      if (checkDataAndUpdate()) {
         final Equation eq = new Equation();
         eq.setDescription("Nueva ecuaci�n");
         m_Equations.addEquation(eq);
         final DefaultMutableTreeNode node = new DefaultMutableTreeNode(eq);
         final DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
         final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) model.getRoot();
         model.insertNodeInto(node, parentNode, parentNode.getChildCount());
         jTree.setSelectionRow(jTree.getRowCount() - 1);
         updateSelection(jTree.getSelectionPath());
         jTree.expandRow(0);
         setEnabledButtons();
      }
      else {
         JOptionPane.showMessageDialog(null, "La sintaxis de la f�rmula no es correcta", "Aviso", JOptionPane.WARNING_MESSAGE);
         jTree.setSelectionPath(m_ActiveTreePath);
      }

   }

}
