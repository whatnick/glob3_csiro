package es.unex.meigas.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Panel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Specie;
import es.unex.meigas.core.Tree;
import es.unex.meigas.core.parameters.DistributionStats;

public class DistributionPanel
         extends
            Panel {

   private int                     m_iInterval;
   private JPanel                  jPanelSettings;
   private JLabel                  jLabelSpecie;
   private JComboBox               jComboBoxSpecie;
   private JTextField              jTextFieldInterval;
   private JComboBox               jComboBoxParameter;
   private JLabel                  jLabelParameter;
   private JLabel                  jLabelInterval;
   private JFreeChart              chart;
   private ChartPanel              jPanelChart = null;
   private DefaultCategoryDataset  dataset;
   private final DasocraticElement m_Element;


   public DistributionPanel(final DasocraticElement element,
                            final MeigasPanel meigasPanel) {


      super();
      m_Element = element;
      setName("Distribución");
      initGUI();

   }


   protected void initGUI() {

      dataset = new DefaultCategoryDataset();
      m_iInterval = 5;

      final TableLayout thisLayout = new TableLayout(new double[][] { { TableLayout.FILL },
               { 5.0, TableLayout.FILL, 5.0, 40.0, 5.0 } });
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      this.setLayout(thisLayout);
      this.setPreferredSize(new java.awt.Dimension(472, 247));
      {
         jPanelSettings = new JPanel();
         final TableLayout jPanelSettingsLayout = new TableLayout(new double[][] {
                  { 5.0, TableLayout.FILL, TableLayout.FILL, 10.0, TableLayout.FILL, TableLayout.FILL, 5.0 },
                  { TableLayout.FILL, TableLayout.FILL } });
         jPanelSettingsLayout.setHGap(5);
         jPanelSettingsLayout.setVGap(5);
         jPanelSettings.setLayout(jPanelSettingsLayout);
         this.add(jPanelSettings, "0, 3");
         {
            jLabelSpecie = new JLabel();
            jPanelSettings.add(jLabelSpecie, "1, 1");
            jLabelSpecie.setText("Especie");
         }
         {
            jComboBoxSpecie = new JComboBox();
            jPanelSettings.add(jComboBoxSpecie, "2, 1");
            final ComboBoxModel jComboBoxSpecieModel = new DefaultComboBoxModel(m_Element.getSpeciesNames());
            jComboBoxSpecie.setModel(jComboBoxSpecieModel);
            jComboBoxSpecie.insertItemAt("Todas", 0);
            jComboBoxSpecie.setSelectedIndex(0);
            jComboBoxSpecie.addItemListener(new ItemListener() {
               public void itemStateChanged(final ItemEvent e) {
                  if (e.getStateChange() == ItemEvent.SELECTED) {
                     updateDataset();
                  }
               }
            });
         }
         {
            jLabelInterval = new JLabel();
            jPanelSettings.add(jLabelInterval, "4, 1");
            jLabelInterval.setText("Intervalo de clase");
         }
         {
            final Set<String> names = Tree.getParametersDefinition().getParameterNames();
            final ComboBoxModel jComboBoxParameterModel = new DefaultComboBoxModel(names.toArray(new String[0]));
            jComboBoxParameter = new JComboBox();
            jComboBoxParameter.setModel(jComboBoxParameterModel);
            jComboBoxParameter.setSelectedIndex(0);
            jComboBoxParameter.addItemListener(new ItemListener() {
               public void itemStateChanged(final ItemEvent e) {
                  if (e.getStateChange() == ItemEvent.SELECTED) {
                     updateDataset();
                  }
               }
            });
            jPanelSettings.add(jComboBoxParameter, "2, 0");
         }
         {
            jLabelParameter = new JLabel();
            jLabelParameter.setText("Parámetro");
            jPanelSettings.add(jLabelParameter, "1, 0");
         }
         {
            jTextFieldInterval = new JTextField();
            jPanelSettings.add(jTextFieldInterval, "5, 1");
            jTextFieldInterval.setText(Integer.toString(m_iInterval));
            jTextFieldInterval.addFocusListener(new FocusAdapter() {
               @Override
               public void focusLost(final FocusEvent e) {
                  final JTextField textField = (JTextField) e.getSource();
                  final String content = textField.getText();
                  if (checkInput(content)) {
                     updateDataset();
                  }
                  else {
                     getToolkit().beep();
                     textField.requestFocus();
                  }
               }
            });
            jTextFieldInterval.addKeyListener(new KeyAdapter() {
               @Override
               public void keyTyped(final KeyEvent event) {
                  validateKeyTyping(event);
               }
            });

         }
         this.add(getChart(), "0, 1");
         //updateDataset();
      }
   }


   public ChartPanel getChart() {

      if (jPanelChart == null) {
         chart = ChartFactory.createBarChart("Valores por clase diamétrica", "Clase diamétrica", "", dataset,
                  PlotOrientation.VERTICAL, true, false, false);
         jPanelChart = new ChartPanel(chart);
      }
      return jPanelChart;
   }


   private void validateKeyTyping(final KeyEvent event) {

      String text = ((JTextField) event.getSource()).getText();
      switch (event.getKeyChar()) {
         case KeyEvent.VK_ENTER:
            if (checkInput(text)) {
               updateDataset();
            }
            else {
               getToolkit().beep();
            }
            break;
         default:
            text += event.getKeyChar();
            break;
      }

   }


   private boolean checkInput(final String text) {

      if (text.length() != 0) {
         try {
            final int i = Integer.parseInt(text);
            if (i > 0) {
               m_iInterval = i;
               return true;
            }
            else {
               return false;
            }
         }
         catch (final NumberFormatException nfe) {
            return false;
         }
      }
      return false;

   }


   private void updateDataset() {

      int i;
      String sName;
      final Object specie = jComboBoxSpecie.getSelectedItem();
      final String sParameter = jComboBoxParameter.getSelectedItem().toString();
      String sSpecie;

      HashMap<String, DistributionStats[]> distributions;
      final DistributionStats[] distribution;
      if (specie.equals("Todas")) {
         sSpecie = "Todas las especies";
         distributions = m_Element.getDistributions(null);
      }
      else {
         sSpecie = specie.toString();
         distributions = m_Element.getDistributions((Specie) specie);
      }

      distribution = distributions.get(sParameter);


      dataset.clear();

      if (distribution != null) {
         for (i = 0; i < distribution.length; i++) {
            sName = Integer.toString((i) * m_iInterval) + "-" + Integer.toString((i + 1) * m_iInterval);
            dataset.addValue(distribution[i].mean, sSpecie, sName);
         }
      }

   }


   protected void updateContent() {

      updateDataset();

   }


}
