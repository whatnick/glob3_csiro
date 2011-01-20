/*******************************************************************************
FittingDataPanel.java
Copyright (C) Victor Olaya

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *******************************************************************************/
package es.unex.meigas.extFillData;

import info.clearthought.layout.TableLayout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.Tree;
import es.unex.sextante.libMath.regression.LeastSquaresFit;

public class FittingDataPanel
         extends
            JDialog {

   private static final int         HEIGHT_MARGIN   = 65;
   private static final int         WIDTH_MARGIN    = 40;
   private static final int         WIDTH           = 500;
   private static final int         HEIGHT          = 350;
   private JFreeChart               chart;
   private ChartPanel               jPanelChart     = null;
   private final LeastSquaresFit    m_Lsf;
   private int                      width           = WIDTH, height = HEIGHT;
   private JLabel                   jLabel;
   private JSpinner                 jSpinnerOrder;
   private JLabel                   jLabelOrder;
   private JPanel                   jPanelOrder;
   private JButton                  jButtonCancel;
   private JButton                  jButtonOk;
   private JPanel                   jPanelButtons;
   private final Tree[]             m_Trees;
   private final XYVariables        m_XY;

   private final XYSeriesCollection m_Data1         = new XYSeriesCollection();
   private final XYSeriesCollection m_Data2         = new XYSeriesCollection();

   private final String             m_sParameters[] = { "Diametro normal", "Altura total", "Altura de fuste",
            "Volumen sin corteza", "Volumen con corteza", "Espesor de corteza", "Diametro de copa" };


   public FittingDataPanel(final LeastSquaresFit lsf,
                           final Tree[] trees,
                           final XYVariables xy) {

      super(Meigas.getMainFrame(), "Ajuste de datos", true);

      m_Lsf = lsf;
      m_Trees = trees;
      m_XY = xy;

      createChart();

      initialize();

      final Plot plot = this.jPanelChart.getChart().getPlot();
      plot.setOutlineStroke(new BasicStroke(1));
      plot.setOutlinePaint(Color.blue);

   }


   private void initialize() {

      final TableLayout flowLayout = new TableLayout(new double[][] {
               { TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL },
               { 25.0, TableLayout.FILL, TableLayout.FILL, 40.0 } });
      flowLayout.setHGap(5);
      flowLayout.setVGap(5);
      this.setLayout(flowLayout);
      this.setSize(new java.awt.Dimension(width, height));
      this.setPreferredSize(new java.awt.Dimension(width, height));
      final String s = m_Lsf.getExpression();
      this.add(getJLabel(), "0, 0, 3, 0");
      getJLabel().setText("Ecuacion de ajuste: y = " + s);
      this.add(getChart(), "0, 1, 3, 2");
      this.add(getJPanelButtons(), "2, 3, 3, 3");
      this.add(getJPanelOrder(), "0, 3, 1, 3");

   }


   public ChartPanel getChart() {

      if (jPanelChart == null) {
         jPanelChart = new ChartPanel(chart);
         jPanelChart.setSize(new java.awt.Dimension(width - WIDTH_MARGIN, height - HEIGHT_MARGIN));
         jPanelChart.setPreferredSize(new java.awt.Dimension(width - WIDTH_MARGIN, height - HEIGHT_MARGIN));
         jPanelChart.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray, 1));
      }
      return jPanelChart;
   }


   private void createChart() {

      updateDataset();

      chart = ChartFactory.createScatterPlot(null, m_sParameters[m_XY.x], m_sParameters[m_XY.y], m_Data1,
               PlotOrientation.VERTICAL, false, true, true);

      final XYPlot plot = chart.getXYPlot();
      plot.setRenderer(new XYDotRenderer());
      plot.setDomainCrosshairVisible(true);
      plot.setRangeCrosshairVisible(true);
      plot.getRenderer().setSeriesPaint(0, Color.blue);


      final XYItemRenderer renderer = new StandardXYItemRenderer();
      renderer.setSeriesPaint(0, Color.red);

      plot.setDataset(1, m_Data2);
      plot.setRenderer(1, renderer);

   }


   private void updateDataset() {

      setOriginalDataset();
      setDatasetFromFitting();

   }


   public void setComponentSize(final int w,
                                final int h) {

      width = w;
      height = h;
      this.setSize(new java.awt.Dimension(w, h));
      this.setPreferredSize(new java.awt.Dimension(w, h));
      jPanelChart.setSize(new java.awt.Dimension(w - WIDTH_MARGIN, h - HEIGHT_MARGIN));
      jPanelChart.setPreferredSize(new java.awt.Dimension(w - WIDTH_MARGIN, h - HEIGHT_MARGIN));

   }


   private void setOriginalDataset() {

      int i;
      final XYSeries series = new XYSeries("");

      final double x[] = new double[m_Lsf.getNumPoints()];
      final double y[] = new double[m_Lsf.getNumPoints()];
      m_Lsf.getPoints(x, y);
      for (i = 0; i < x.length; i++) {
         series.add(x[i], y[i]);
      }

      m_Data1.removeAllSeries();
      m_Data1.addSeries(series);

   }


   private void setDatasetFromFitting() {

      int i;
      final int STEPS = 200;
      double x, y;
      final XYSeries series = new XYSeries("");

      final double dStep = (m_Lsf.getXMax() - m_Lsf.getXMin()) / STEPS;

      for (i = 0; i < STEPS; i++) {
         x = m_Lsf.getXMin() + dStep * i;
         y = m_Lsf.getY(x);
         series.add(x, y);
      }

      m_Data2.removeAllSeries();
      m_Data2.addSeries(series);

   }


   private JPanel getJPanelButtons() {

      if (jPanelButtons == null) {
         jPanelButtons = new JPanel();
         jPanelButtons.add(getJButtonOk());
         jPanelButtons.add(getJButtonCancel());
      }
      return jPanelButtons;

   }


   private JButton getJButtonOk() {

      if (jButtonOk == null) {
         jButtonOk = new JButton();
         jButtonOk.setText("Aceptar");
         jButtonOk.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
               fillData();
               cancel();
            }
         });
      }
      return jButtonOk;

   }


   private JButton getJButtonCancel() {

      if (jButtonCancel == null) {
         jButtonCancel = new JButton();
         jButtonCancel.setText("Cancelar");
         jButtonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
               cancel();
            }
         });
      }
      return jButtonCancel;

   }


   private void fillData() {

      int iTree;
      double dX, dY;

      for (iTree = 0; iTree < m_Trees.length; iTree++) {
         dY = getValue(m_XY.y, m_Trees[iTree]);
         if (dY == DasocraticElement.NO_DATA) {
            dX = getValue(m_XY.x, m_Trees[iTree]);
            if (dX != DasocraticElement.NO_DATA) {
               setValue(m_XY.y, m_Lsf.getY(dX), m_Trees[iTree]);
            }
         }
      }

   }


   private double getValue(final int iParameter,
                           final Tree tree) {

      String sParameter;

      switch (iParameter) {
         case FillDataPanel.DBH:
            sParameter = Tree.DBH;
            break;
         case FillDataPanel.HEIGHT:
            sParameter = Tree.HEIGHT;
            break;
         case FillDataPanel.LOG_HEIGHT:
            sParameter = Tree.LOG_HEIGHT;
            break;
         case FillDataPanel.VOLUME_WITH_BARK:
            sParameter = Tree.VOLUME;
            break;
         case FillDataPanel.VOLUME_WITHOUT_BARK:
            sParameter = Tree.NO_BARK_VOLUME;
            break;
         case FillDataPanel.BARK:
            sParameter = Tree.BARK;
            break;
         case FillDataPanel.CROWN_DIAMETER:
            sParameter = Tree.CROWN_DIAMETER;
            break;
         default:
            return DasocraticElement.NO_DATA;
      }


      return ((Double) tree.getParameterValue(sParameter)).doubleValue();

   }


   private void setValue(final int iParameter,
                         final double dValue,
                         final Tree tree) {

      String sParameter = null;

      switch (iParameter) {
         case FillDataPanel.DBH:
            sParameter = Tree.DBH;
            break;
         case FillDataPanel.HEIGHT:
            sParameter = Tree.HEIGHT;
            break;
         case FillDataPanel.LOG_HEIGHT:
            sParameter = Tree.LOG_HEIGHT;
            break;
         case FillDataPanel.VOLUME_WITH_BARK:
            sParameter = Tree.VOLUME;
            break;
         case FillDataPanel.VOLUME_WITHOUT_BARK:
            sParameter = Tree.NO_BARK_VOLUME;
            break;
         case FillDataPanel.BARK:
            sParameter = Tree.BARK;
            break;
         case FillDataPanel.CROWN_DIAMETER:
            sParameter = Tree.CROWN_DIAMETER;
            break;
      }

      if (sParameter != null) {
         tree.getParameter(sParameter).setValue(new Double(dValue));
      }

   }


   protected void cancel() {

      this.dispose();
      this.setVisible(false);

   }


   private JPanel getJPanelOrder() {

      if (jPanelOrder == null) {
         jPanelOrder = new JPanel();
         jPanelOrder.add(getJLabelOrder());
         jPanelOrder.add(getJSpinnerOrder());
      }

      return jPanelOrder;

   }


   private JLabel getJLabelOrder() {

      if (jLabelOrder == null) {
         jLabelOrder = new JLabel();
         jLabelOrder.setText("Orden del polinomio de ajuste:");
      }
      return jLabelOrder;

   }


   private JSpinner getJSpinnerOrder() {

      if (jSpinnerOrder == null) {
         final SpinnerListModel jSpinnerOrderModel = new SpinnerListModel(new String[] { "1", "2", "3", "4", "5", "6", "7" });
         jSpinnerOrder = new JSpinner();
         jSpinnerOrder.setModel(jSpinnerOrderModel);
         jSpinnerOrder.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent evt) {
               String s = (String) ((SpinnerListModel) jSpinnerOrder.getModel()).getValue();
               m_Lsf.calculate(Integer.parseInt(s));
               updateDataset();
               s = m_Lsf.getExpression();
               getJLabel().setText("Ecuacion de ajuste: y = " + s);
            }
         });
      }

      return jSpinnerOrder;

   }


   private JLabel getJLabel() {

      if (jLabel == null) {
         jLabel = new JLabel();
         jLabel.setAlignmentX(0.5f);
      }
      return jLabel;

   }

}
