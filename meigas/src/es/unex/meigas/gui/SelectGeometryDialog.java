package es.unex.meigas.gui;

import info.clearthought.layout.TableLayout;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.NamedGeometry;
import es.unex.meigas.dataObjects.IFeature;
import es.unex.meigas.dataObjects.IFeatureIterator;
import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.exceptions.IteratorException;

public class SelectGeometryDialog
         extends
            JDialog {

   private JComboBox            jComboBoxLayer;
   private JComboBox            jComboBoxPolygon;
   private JComboBox            jComboBoxField;
   private JLabel               jLabelField;
   private JButton              jButtonCancel;
   private JButton              jButtonOK;
   private JPanel               jPanelButtons;
   private JLabel               jLabelPolygon;
   private JLabel               jLabelLayer;
   private final IVectorLayer[] m_Layers;
   private NamedGeometry        m_Geometry;
   private final int            m_iShapeType;


   public SelectGeometryDialog(final int shapeType) {

      super(Meigas.getMainFrame(), true);

      m_iShapeType = shapeType;
      m_Layers = Meigas.getGIS().getVectorLayers(shapeType);

      initGUI();

   }


   private void initGUI() {

      this.setSize(new java.awt.Dimension(395, 178));

      try {
         {
            final TableLayout thisLayout = new TableLayout(new double[][] { { 5.0, 126.0, TableLayout.FILL, 5.0 },
                     { 5.0, 25.0, TableLayout.FILL, 25.0, TableLayout.FILL, 25.0, 10.0, 25.0, 5.0 } });
            thisLayout.setHGap(5);
            thisLayout.setVGap(5);
            this.setLayout(thisLayout);
            {
               jLabelLayer = new JLabel();
               this.add(jLabelLayer, "1, 1");
               jLabelLayer.setText("Capa");
            }
            {
               jLabelPolygon = new JLabel();
               this.add(jLabelPolygon, "1, 5");
               jLabelPolygon.setText("Pol�gono");
            }
            {
               final DefaultComboBoxModel jComboBoxLayerModel = new DefaultComboBoxModel(getLayerNames());
               jComboBoxLayer = new JComboBox();
               this.add(jComboBoxLayer, "2, 1");
               jComboBoxLayer.setModel(jComboBoxLayerModel);
               jComboBoxLayer.addItemListener(new java.awt.event.ItemListener() {
                  public void itemStateChanged(final java.awt.event.ItemEvent e) {
                     String sNames[] = null;
                     DefaultComboBoxModel jComboBoxFieldModel;
                     sNames = getFieldNames(jComboBoxLayer.getSelectedIndex());
                     if (sNames != null) {
                        jComboBoxFieldModel = new DefaultComboBoxModel(sNames);
                        jComboBoxField.setModel(jComboBoxFieldModel);
                     }
                     sNames = getFeatureNames(jComboBoxLayer.getSelectedIndex(), jComboBoxField.getSelectedIndex());
                     DefaultComboBoxModel jComboBoxPolygonModel;
                     if (sNames != null) {
                        jComboBoxPolygonModel = new DefaultComboBoxModel(sNames);
                        jComboBoxPolygon.setModel(jComboBoxPolygonModel);
                     }
                  }
               });
            }
            {
               final ComboBoxModel jComboBoxPolygonModel = new DefaultComboBoxModel(getFeatureNames(0, 0));
               jComboBoxPolygon = new JComboBox();
               this.add(jComboBoxPolygon, "2, 5");
               jComboBoxPolygon.setModel(jComboBoxPolygonModel);
            }
            {
               jPanelButtons = new JPanel();
               final FlowLayout jPanelButtonsLayout = new FlowLayout();
               jPanelButtonsLayout.setAlignment(FlowLayout.RIGHT);
               jPanelButtonsLayout.setHgap(0);
               jPanelButtonsLayout.setVgap(0);
               jPanelButtons.setLayout(jPanelButtonsLayout);
               this.add(jPanelButtons, "2, 7");
               {
                  jButtonOK = new JButton();
                  jPanelButtons.add(jButtonOK);
                  jButtonOK.setText("Aceptar");
                  jButtonOK.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        selectPolygon();
                     }
                  });
               }
               {
                  jButtonCancel = new JButton();
                  jPanelButtons.add(jButtonCancel);
                  jButtonCancel.setText("Cancelar");
                  jButtonCancel.addActionListener(new ActionListener() {
                     public void actionPerformed(final ActionEvent evt) {
                        m_Geometry = null;
                        cancel();
                     }
                  });
               }
            }
            {
               jLabelField = new JLabel();
               this.add(jLabelField, "1, 3");
               jLabelField.setText("Campo");
            }
            {
               final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(getFieldNames(0));
               jComboBoxField = new JComboBox();
               this.add(jComboBoxField, "2, 3");
               jComboBoxField.setModel(jComboBoxFieldModel);
               jComboBoxField.addItemListener(new java.awt.event.ItemListener() {
                  public void itemStateChanged(final java.awt.event.ItemEvent e) {
                     String sNames[] = null;
                     sNames = getFeatureNames(jComboBoxLayer.getSelectedIndex(), jComboBoxField.getSelectedIndex());
                     DefaultComboBoxModel jComboBoxPolygonModel;
                     if (sNames != null) {
                        jComboBoxPolygonModel = new DefaultComboBoxModel(sNames);
                        jComboBoxPolygon.setModel(jComboBoxPolygonModel);
                     }
                  }
               });
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }
   }


   private String[] getFeatureNames(final int iLayer,
                                    final int iField) {

      int i = 0;

      int iShapes = 0;
      String[] names = null;

      iShapes = m_Layers[iLayer].getShapesCount();
      names = new String[iShapes];

      final IFeatureIterator iter = m_Layers[iLayer].iterator();
      while (iter.hasNext()) {
         IFeature feature;
         try {
            feature = iter.next();
            names[i] = feature.getRecord().getValue(iField).toString();
            i++;
         }
         catch (final IteratorException e) {
            //TODO
         }

      }
      iter.close();

      return names;

   }


   private String[] getLayerNames() {

      int i;
      final String[] names = new String[m_Layers.length];

      for (i = 0; i < names.length; i++) {
         names[i] = m_Layers[i].getName();
      }

      return names;

   }


   private String[] getFieldNames(final int iLayer) {

      return m_Layers[iLayer].getFieldNames();

   }


   private void cancel() {

      this.dispose();
      this.setVisible(false);

   }


   private void selectPolygon() {

      final int iLayer = jComboBoxLayer.getSelectedIndex();
      final int iShape = jComboBoxPolygon.getSelectedIndex();
      final String sName = jComboBoxPolygon.getSelectedItem().toString();

      final IFeatureIterator iter = m_Layers[iLayer].iterator();
      int i = 0;
      while (iter.hasNext()) {
         IFeature feature;
         try {
            feature = iter.next();
            if (i == iShape) {
               m_Geometry = new NamedGeometry(sName, feature.getGeometry());
               break;
            }
            i++;
         }
         catch (final IteratorException e) {
            //TODO
         }

      }

      cancel();

   }


   public NamedGeometry getGeometry() {

      return m_Geometry;

   }

}
