package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;

import es.unex.meigas.core.parameters.DistributionStats;
import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.core.parameters.MeigasParameter;
import es.unex.sextante.libMath.simpleStats.SimpleStats;

public class FixedRadiusPlot
         extends
            Plot {

   public static final String RADIUS = "RADIUS";


   public FixedRadiusPlot() {

      super();

      m_Parameters.addParameter(new MeigasNumericalValue(RADIUS, "Radio", 0, 1000));

   }


   @Override
   public double getArea() {

      final double dRadius = ((Double) m_Parameters.getParameter(RADIUS).getValue()).doubleValue();

      if (dRadius != NO_DATA) {
         return (Math.PI * Math.pow(dRadius, 2.) / 10000.);
      }
      else {
         return NO_DATA;
      }

   }


   @Override
   public Rectangle2D getBoundingBox() {

      final Coordinate coord = (Coordinate) m_Parameters.getParameter(COORD).getValue();
      final double dRadius = ((Double) m_Parameters.getParameter(RADIUS).getValue()).doubleValue();
      return new Rectangle2D.Double(coord.x - dRadius, coord.y - dRadius, dRadius * 2, dRadius * 2);

   }


   @Override
   public String[] getReport() {

      return m_Parameters.getReport();

   }


   @Override
   public void calculateParameters(final Specie specie) {

      m_bCalculated = true;
      m_CurrentSpecieForParameterCalculation = specie;

      final double dArea;
      final Tree[] trees = getTrees(specie);

      if (trees.length == 0) {
         return;
      }

      m_TreeStats.clear();

      final Set<String> names = Tree.getParametersDefinition().getParameterNames();

      for (final String sName : names) {
         final MeigasParameter param = Tree.getParametersDefinition().getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            m_TreeStats.put(sName, new SimpleStats());
         }

      }

      for (final Tree tree : trees) {
         for (final String sName : names) {
            final MeigasParameter param = tree.getParameters().getParameter(sName);
            if (param instanceof MeigasNumericalValue) {
               final SimpleStats stats = m_TreeStats.get(sName);
               stats.addValue(((Number) param.getValue()).doubleValue());
            }
         }
      }

      int iMaxDBH = (int) m_TreeStats.get(Tree.DBH).getMax();

      if (iMaxDBH == NO_DATA) {
         iMaxDBH = 0;
      }

      //distributions
      for (final String sName : names) {
         final MeigasParameter param = Tree.getParametersDefinition().getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            final DistributionStats[] distributionStats = new DistributionStats[iMaxDBH + 1];
            for (int i = 0; i < distributionStats.length; i++) {
               distributionStats[i] = new DistributionStats();
            }
            m_Distributions.put(sName, distributionStats);
         }

      }

      for (final Tree tree : trees) {
         final int iDBH = ((Double) tree.getParameterValue(Tree.DBH)).intValue();
         if (iDBH > 0) {
            for (final String sName : names) {
               final MeigasParameter param = tree.getParameters().getParameter(sName);
               if (param instanceof MeigasNumericalValue) {
                  final DistributionStats[] stats = m_Distributions.get(sName);
                  final double dValue = ((Number) param.getValue()).doubleValue();
                  if (dValue != DasocraticElement.NO_DATA) {
                     stats[iDBH].addValue(dValue);
                  }
               }
            }
         }
      }


      //accumulated values
      dArea = getArea();
      for (final String sName : names) {
         final MeigasParameter param = m_Parameters.getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            if (((MeigasNumericalValue) param).isAccumulated()) {
               if (dArea == NO_DATA) {
                  m_AccumulatedTreeParameters.put(param.getName(), NO_DATA);
               }
               else {
                  m_AccumulatedTreeParameters.put(param.getName(), m_TreeStats.get(param.getName()).getSum() / dArea);
               }

            }
         }
      }


   }

}
