package es.unex.meigas.extFillData;


import info.clearthought.layout.TableLayout;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import es.unex.meigas.extBase.BaseWizardPanel;


public class FillDataPanel
         extends
            BaseWizardPanel {

   public static final int               DBH                 = 0;
   public static final int               HEIGHT              = 1;
   public static final int               LOG_HEIGHT          = 2;
   public static final int               VOLUME_WITH_BARK    = 3;
   public static final int               VOLUME_WITHOUT_BARK = 4;
   public static final int               BARK                = 5;
   public static final int               CROWN_DIAMETER      = 6;

   String                                m_sParameters[]     = { "Diametro normal", "Altura total", "Altura de fuste",
            "Volumen sin corteza", "Volumen con corteza", "Espesor de corteza", "Diametro de copa" };

   private ParameterForRegressionPanel[] m_ParameterPanel;


   public FillDataPanel(final FillDataExtensionPanel panel) {

      super(panel);

      initGUI();

   }


   @Override
   public void initGUI() {

      int iRow;
      int iParameters;
      int iRows;
      JLabel jLabel;
      final JPanel panel = new JPanel();

      this.setPreferredSize(new java.awt.Dimension(513, 268));
      final TableLayout thisLayout = new TableLayout(new double[][] { { 5.0, TableLayout.FILL, 5.0 },
               { 5.0, TableLayout.MINIMUM, TableLayout.FILL, 5.0 } });
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      this.setLayout(thisLayout);
      this.setSize(new java.awt.Dimension(380, 490));
      {
         jLabel = new JLabel();
         this.add(jLabel, "1, 1");
         jLabel.setText("Seleccione los par�metros que desea rellenar.");
         jLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
      }
      int i;

      iParameters = m_sParameters.length;
      m_ParameterPanel = new ParameterForRegressionPanel[iParameters];
      iRows = iParameters * 2 + 1;
      final double[][] layoutSetting = new double[2][iRows];
      layoutSetting[0] = new double[] { 10, -1, 10 };
      layoutSetting[1] = new double[iRows];
      layoutSetting[1][0] = -1;
      for (i = 0; i < iParameters; i++) {
         layoutSetting[1][i * 2 + 1] = 20;
         layoutSetting[1][i * 2 + 2] = -1;
      }
      layoutSetting[1][iRows - 1] = -1;

      final TableLayout tableLayout = new TableLayout(layoutSetting);
      tableLayout.setHGap(5);
      tableLayout.setVGap(5);
      panel.setLayout(tableLayout);

      for (i = 0; i < iParameters; i++) {
         iRow = i * 2 + 1;
         m_ParameterPanel[i] = new ParameterForRegressionPanel(m_sParameters[i], m_sParameters);
         panel.add(m_ParameterPanel[i], "1 ," + Integer.toString(iRow));
      }

      final JScrollPane scrollPane = new JScrollPane();
      this.add(scrollPane, "1,2");
      scrollPane.setViewportView(panel);

   }


   @Override
   public boolean hasEnoughInformation() {

      int i;

      for (i = 0; i < m_ParameterPanel.length; i++) {
         if (m_ParameterPanel[i].isSelected()) {
            return true;
         }
      }

      return false;
   }


   public ArrayList getVariables() {

      int i;
      final ArrayList variables = new ArrayList();

      for (i = 0; i < m_ParameterPanel.length; i++) {
         if (m_ParameterPanel[i].isSelected()) {
            variables.add(new XYVariables(m_ParameterPanel[i].getXParameter(), i));
         }
      }

      return variables;

   }


   public void updateParentButtons() {

      m_ParentPanel.updateButtons();

   }


   @Override
   public boolean isFinish() {
      return true;
   }

}
