package es.unex.meigas.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Panel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Specie;
import es.unex.meigas.core.Tree;

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
   //private JFreeChart              chart;
   private ChartPanel              jPanelChart = null;
   //private DefaultCategoryDataset  dataset;
   private final DasocraticElement m_Element;


   public DistributionPanel(final DasocraticElement element,
                            final MeigasPanel meigasPanel) {


      super();
      m_Element = element;
      setName("Distribución");
      initGUI();

   }


   protected void initGUI() {

      //dataset = new DefaultCategoryDataset();
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
            final ComboBoxModel jComboBoxSpecieModel = new DefaultComboBoxModel(m_Element.getSpecies());
            jComboBoxSpecie.setModel(jComboBoxSpecieModel);
            jComboBoxSpecie.insertItemAt(Specie.ALL_SPECIES, 0);
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
            final String[] names = Tree.getParametersDefinition().getParameterDescriptions();
            final ComboBoxModel jComboBoxParameterModel = new DefaultComboBoxModel(names);
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
         final Specie specie = (Specie) jComboBoxSpecie.getSelectedItem();
         final String sParameterDescription = jComboBoxParameter.getSelectedItem().toString();
         final String sParameterName = m_Element.getParameters().getParameterNameFromDescription(sParameterDescription);
         final JFreeChart chart = DasocraticElementChartFactory.getChart(m_Element, specie, sParameterName, m_iInterval);
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

      final Specie specie = (Specie) jComboBoxSpecie.getSelectedItem();
      final String sParameterDescription = jComboBoxParameter.getSelectedItem().toString();
      final String sParameterName = m_Element.getParameters().getParameterNameFromDescription(sParameterDescription);
      final JFreeChart chart = DasocraticElementChartFactory.getChart(m_Element, specie, sParameterName, m_iInterval);

      getChart().setChart(chart);

   }


   protected void updateContent() {

      updateDataset();

   }


}
