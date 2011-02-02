package es.unex.meigas.gui;

import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Specie;
import es.unex.meigas.core.parameters.DistributionStats;

public class DasocraticElementChartFactory {

   public static JFreeChart getChart(final DasocraticElement element,
                                     final Specie specie,
                                     final String sParameter,
                                     final int iInterval) {

      int iCount = 0;
      double dSum = 0;
      int i;
      String sName;
      String sSpecie;
      final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

      HashMap<String, DistributionStats[]> distributions;
      final DistributionStats[] distribution;
      if (specie.equals(Specie.ALL_SPECIES)) {
         distributions = element.getDistributions(null);
      }
      else {

         distributions = element.getDistributions(specie);
      }

      sSpecie = specie.toString();

      distribution = distributions.get(sParameter);

      dataset.clear();

      if (distribution != null) {
         for (i = 0; i < distribution.length; i++) {
            dSum += distribution[i].sum;
            iCount += distribution[i].count;
            if ((i + 1) % iInterval == 0) {
               sName = Integer.toString((i) * iInterval) + "-" + Integer.toString((i + 1) * iInterval);
               if (iCount == 0) {
                  dataset.addValue(0, sSpecie, sName);
               }
               else {
                  dataset.addValue(dSum / iCount, sSpecie, sName);
               }
               dSum = 0;
               iCount = 0;
            }
         }
      }

      final JFreeChart chart = ChartFactory.createBarChart("Valores por clase diamétrica", "Clase diamétrica", "", dataset,
               PlotOrientation.VERTICAL, true, false, false);

      return chart;


   }

}
