package es.unex.meigas.extIFN2;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Stand;
import es.unex.meigas.extBase.BaseWizardPanel;

public class ExtentDefinitionPanel
         extends
            BaseWizardPanel {

   public static final int USE_RECT     = 0;
   public static final int NO_LIMITS    = 1;
   public static final int USE_GEOMETRY = 2;

   private JLabel          jLabelTitle;
   private JRadioButton    jRadioButtonNoLimits;
   private JLabel          jLabelWarning;
   private JTextField      jTextFieldXMin;
   private JTextField      jTextFieldXMax;
   private JTextField      jTextFieldYMin;
   private JTextField      jTextFieldYMax;
   private JRadioButton    jRadioButtonUseRect;
   private JRadioButton    jRadioButtonUseGeometry;
   private JPanel          jPanelCoords;


   public ExtentDefinitionPanel(final IFN2Panel panel) {

      super(panel);

      initGUI();

   }


   @Override
   public boolean hasEnoughInformation() {

      if (jRadioButtonNoLimits.isSelected() || jRadioButtonUseGeometry.isSelected()) {
         return true;
      }
      else {
         try {
            final double dXMin = Double.parseDouble(jTextFieldXMin.getText());
            final double dXMax = Double.parseDouble(jTextFieldXMax.getText());
            final double dYMin = Double.parseDouble(jTextFieldYMin.getText());
            final double dYMax = Double.parseDouble(jTextFieldYMax.getText());

            if ((dXMin >= dXMax) || (dYMin >= dYMax)) {
               return false;
            }

            return true;
         }
         catch (final Exception e) {
            return false;
         }
      }

   }


   @Override
   public void initGUI() {

      try {
         {
            final TableLayout thisLayout = new TableLayout(new double[][] {
                     { 29.0, 20.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 161.0, 25.0 },
                     { 10.0, 20.0, 10.0, 20.0, 20.0, 20.0, 10.0, TableLayout.FILL, 5.0 } });
            thisLayout.setHGap(5);
            thisLayout.setVGap(5);
            this.setLayout(thisLayout);
            this.setPreferredSize(new java.awt.Dimension(572, 273));
            {
               jLabelTitle = new JLabel();
               this.add(jLabelTitle, "1, 1, 5, 1");
               jLabelTitle.setText("Seleccione la extension geogr�fica a la que desea limitar los datos extra�dos");
               jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 11));
            }
            {
               jPanelCoords = new JPanel();
               final TableLayout jPanelCoordsLayout = new TableLayout(new double[][] {
                        { TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL },
                        { 5.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 5.0 } });
               jPanelCoordsLayout.setHGap(5);
               jPanelCoordsLayout.setVGap(5);
               jPanelCoords.setLayout(jPanelCoordsLayout);
               this.add(jPanelCoords, "2, 7, 5, 7");
               jPanelCoords.setBorder(BorderFactory.createTitledBorder("Coordenadas del marco"));
               {
                  jTextFieldYMax = new JTextField();
                  jPanelCoords.add(jTextFieldYMax, "2, 1");
                  jTextFieldYMax.setText("Y m�xima");
                  jTextFieldYMax.addKeyListener(new KeyAdapter() {
                     @Override
                     public void keyTyped(final KeyEvent event) {
                        updateKeyTyped(event);
                     }
                  });
               }
               {
                  jTextFieldYMin = new JTextField();
                  jPanelCoords.add(jTextFieldYMin, "2, 3");
                  jTextFieldYMin.setText("Y m�nima");
                  jTextFieldYMin.addKeyListener(new KeyAdapter() {
                     @Override
                     public void keyTyped(final KeyEvent event) {
                        updateKeyTyped(event);
                     }
                  });
               }
               {
                  jTextFieldXMax = new JTextField();
                  jPanelCoords.add(jTextFieldXMax, "3, 2");
                  jTextFieldXMax.setText("X m�xima");
                  jTextFieldXMax.addKeyListener(new KeyAdapter() {
                     @Override
                     public void keyTyped(final KeyEvent event) {
                        updateKeyTyped(event);
                     }
                  });
               }
               {
                  jTextFieldXMin = new JTextField();
                  jPanelCoords.add(jTextFieldXMin, "1, 2");
                  jTextFieldXMin.setText("X m�nima");
                  jTextFieldXMin.addKeyListener(new KeyAdapter() {
                     @Override
                     public void keyTyped(final KeyEvent event) {
                        updateKeyTyped(event);
                     }
                  });
               }
               jPanelCoords.setEnabled(false);
            }
            {
               jRadioButtonUseGeometry = new JRadioButton();
               this.add(jRadioButtonUseGeometry, "2, 4, 4, 4");
               jRadioButtonUseGeometry.setText("Restringir a los l�mites de cada canton");
               jRadioButtonUseGeometry.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     enableCoordsFields(false);
                     m_ParentPanel.updateButtons();
                  }
               });
            }
            {
               jRadioButtonNoLimits = new JRadioButton();
               this.add(jRadioButtonNoLimits, "2, 3, 5, 3");
               jRadioButtonNoLimits.setText("No aplicar restricciones geogr�ficas");
               jRadioButtonNoLimits.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     enableCoordsFields(false);
                     m_ParentPanel.updateButtons();
                  }
               });
               jRadioButtonNoLimits.setSelected(true);
            }
            {
               jRadioButtonUseRect = new JRadioButton();
               this.add(jRadioButtonUseRect, "2, 5, 4, 5");
               jRadioButtonUseRect.setText("Establecer un marco manualmente");
               jRadioButtonUseRect.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     enableCoordsFields(true);
                     m_ParentPanel.updateButtons();
                  }
               });
            }
            {
               final ButtonGroup group = new ButtonGroup();
               group.add(jRadioButtonUseRect);
               group.add(jRadioButtonNoLimits);
               group.add(jRadioButtonUseGeometry);

            }
            {
               jLabelWarning = new JLabel();
               this.add(jLabelWarning, "5, 4");
               jLabelWarning.setText("Aviso: Hay cantones sin límites.");
               jLabelWarning.setForeground(new java.awt.Color(255, 0, 0));
            }
            final DasocraticElement element = m_ParentPanel.getMeigasPanel().getActiveElement();
            final ArrayList<Stand> stands = new ArrayList<Stand>();
            element.getElementsOfClassRecursive(stands, Stand.class);
            final int iStandsCount = stands.size();
            final int iStandsWithLimitsCount = element.getStandsWithLimits().size();
            if (iStandsWithLimitsCount == 0) {
               jRadioButtonUseGeometry.setEnabled(false);
               jLabelWarning.setVisible(false);
            }
            else if (iStandsWithLimitsCount < iStandsCount) {
               jRadioButtonUseGeometry.setEnabled(true);
               jLabelWarning.setVisible(true);
            }
            else {
               jRadioButtonUseGeometry.setEnabled(true);
               jLabelWarning.setVisible(false);
            }
            enableCoordsFields(false);

         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }
   }


   protected void updateKeyTyped(final KeyEvent event) {

      String text = ((JTextField) event.getSource()).getText();
      text += event.getKeyChar();
      m_ParentPanel.updateButtons();

   }


   protected void enableCoordsFields(final boolean b) {

      jTextFieldXMin.setEnabled(b);
      jTextFieldXMax.setEnabled(b);
      jTextFieldYMin.setEnabled(b);
      jTextFieldYMax.setEnabled(b);

   }


   public Rectangle2D getRect() {

      final double dXMin = Double.parseDouble(jTextFieldXMin.getText());
      final double dXMax = Double.parseDouble(jTextFieldXMax.getText());
      final double dYMin = Double.parseDouble(jTextFieldYMin.getText());
      final double dYMax = Double.parseDouble(jTextFieldYMax.getText());

      final Rectangle2D rect = new Rectangle2D.Double(dXMin, dYMin, dXMax - dXMin, dYMax - dYMin);

      return rect;

   }


   public int getSelectedType() {

      if (jRadioButtonUseGeometry.isSelected()) {
         return USE_GEOMETRY;
      }
      if (jRadioButtonUseGeometry.isSelected()) {
         return USE_RECT;
      }
      else {
         return NO_LIMITS;
      }
   }


   @Override
   public boolean isFinish() {
      if (hasEnoughInformation()) {
         return true;
      }
      return false;
   }

}
