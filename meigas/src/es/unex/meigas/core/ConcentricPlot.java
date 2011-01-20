package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;

import es.unex.meigas.core.parameters.MeigasNumericalValue;

public class ConcentricPlot
         extends
            Plot {

   public static final int    PLOTS_COUNT               = 5;
   public static final String RADIUS[]                  = { "RADIUS1", "RADIUS2", "RADIUS3", "RADIUS4", "RADIUS5" };
   public static final String MIN_ACCEPTABLE_DIAMETER[] = { "MINIMUM_ACCEPTABLE_DIAMETER1", "MINIMUM_ACCEPTABLE_DIAMETER2",
            "MINIMUM_ACCEPTABLE_DIAMETER3", "MINIMUM_ACCEPTABLE_DIAMETER4", "MINIMUM_ACCEPTABLE_DIAMETER5" };

   private final Double[]     m_dRadius;
   private final Double[]     m_dMinDiameter;


   public ConcentricPlot() {

      super();

      int i;

      m_dRadius = new Double[PLOTS_COUNT];
      m_dMinDiameter = new Double[PLOTS_COUNT];

      for (i = 0; i < PLOTS_COUNT; i++) {
         m_dRadius[i] = new Double(NO_DATA);
         m_dMinDiameter[i] = new Double(NO_DATA);
      }

      for (i = 0; i < PLOTS_COUNT; i++) {
         m_Parameters.addParameter(new MeigasNumericalValue(RADIUS[i], "Radio " + Integer.toString(i + 1), 0, 1000));
         m_Parameters.addParameter(new MeigasNumericalValue(MIN_ACCEPTABLE_DIAMETER[i], "Diámetro mínimo "
                                                                                        + Integer.toString(i + 1), 0, 200));
      }


   }


   @Override
   public double getArea() {

      final double dRadius = getMaxRadius();

      if (dRadius != NO_DATA) {
         return (Math.PI * Math.pow(dRadius, 2.) / 10000.);
      }
      else {
         return NO_DATA;
      }

   }


   public double getMaxRadius() {

      int i;
      int iIndex = -1;

      for (i = 0; i < PLOTS_COUNT; i++) {
         final double dRadius = ((Double) m_Parameters.getParameter(RADIUS[i]).getValue()).doubleValue();
         if (m_dRadius[i].doubleValue() == NO_DATA) {
            iIndex = i;
            break;
         }
      }

      if (iIndex >= 0) {
         return m_dRadius[iIndex].doubleValue();
      }
      else {
         return NO_DATA;
      }

   }


   @Override
   public Rectangle2D getBoundingBox() {

      final double dRadius = getMaxRadius();
      final Coordinate coord = (Coordinate) m_Parameters.getParameter(COORD).getValue();

      if (dRadius != NO_DATA) {
         return new Rectangle2D.Double(coord.x - dRadius, coord.y - dRadius, dRadius * 2, dRadius * 2);
      }
      else {
         return null;
      }

   }


   public double[] getDistribution(final String sSpecie,
                                   final int iInterval,
                                   final int iParameter) {

      /*int i, j;
      int iCount;
      int iClass, iClass2;
      int iClasses;
      int iMaxDiameter;
      int iTreesCount = 0;
      double dDiameter;
      double dValue;
      double area[];
      String s;
      final ArrayList trees = getTrees(getFilters());
      Tree tree;

      iMaxDiameter = (int) Math.ceil(getMaximumDiameter(trees));
      iClasses = iMaxDiameter;

      if (iClasses == 0) {
         return null;
      }

      area = new double[iClasses];

      iCount = getConcentricPlotsCount();
      if (iCount == 0) {
         return null;
      }

      for (i = 0; i < iClasses; i++) {
         for (j = 0; j < iCount; j++) {
            if (i < m_dMinDiameter[j].doubleValue()) {
               break;
            }
         }
         if (j == 0) {
            area[i] = (Math.PI * Math.pow(m_dRadius[0].doubleValue(), 2.) / 10000.);
         }
         else {
            area[i] = (Math.PI * Math.pow(m_dRadius[j - 1].doubleValue(), 2.) / 10000.);
         }
      }

      final double distribution[] = new double[iClasses];
      final int treesCount[] = new int[iClasses];

      for (i = 0; i < iClasses; i++) {
         distribution[i] = 0;
         treesCount[i] = 0;
      }

      for (i = 0; i < trees.size(); i++) {
         tree = (Tree) trees.get(i);
         s = tree.getSpecie();
         if (s.equals(sSpecie) || sSpecie.equals("Todas")) {
            dDiameter = tree.getDBH().getValue();
            if (dDiameter != NO_DATA) {
               switch (iParameter) {
                  case FREQUENCY:
                     dValue = 1;
                     break;
                  case HEIGHT:
                     dValue = tree.getHeight().getValue();
                     break;
                  case LOG_HEIGHT:
                     dValue = tree.getLogHeight().getValue();
                     break;
                  case VOLUME:
                     dValue = tree.getVolumeWithBark().getValue();
                     break;
                  case VOLUME_WITHOUT_BARK:
                     dValue = tree.getVolumeWithoutBark().getValue();
                     break;
                  case AGE:
                     dValue = tree.getAge().getValue();
                     break;
                  case BASIMETRIC_AREA:
                     dValue = tree.getBasimetricArea();
                     break;
                  default:
                     return null;
               }
               if (dValue != NO_DATA) {
                  iClass = (int) Math.floor(dDiameter);
                  iClass = Math.min(iClass, iClasses - 1);
                  distribution[iClass] += dValue;
                  treesCount[iClass]++;
               }
            }
         }
      }

      if (iParameter == FREQUENCY) {
         for (i = 0; i < iClasses; i++) {
            distribution[i] /= area[i];
         }
      }

      iClasses = (int) Math.ceil((double) iMaxDiameter / (double) iInterval);
      final double ret[] = new double[iClasses];

      for (i = 0; i < iClasses; i++) {
         ret[i] = 0;
      }

      for (iClass = 0, iClass2 = 0; iClass < iClasses; iClass++, iClass2 += iInterval) {
         iTreesCount = 0;
         for (i = 0; i < iInterval; i++) {
            if (iClass2 + i < iMaxDiameter) {
               ret[iClass] += distribution[iClass2 + i];
               iTreesCount += treesCount[iClass2 + i];
            }
         }
         if (iParameter != FREQUENCY) {
            if (iTreesCount != 0) {
               ret[iClass] /= (iTreesCount);
            }
         }
      }

      return ret;*/

      return new double[1];


   }


   private int getConcentricPlotsCount() {

      int i;

      for (i = 0; i < PLOTS_COUNT; i++) {
         final double dRadius = ((Double) m_Parameters.getParameter(RADIUS[i]).getValue()).doubleValue();
         final double dDiameter = ((Double) m_Parameters.getParameter(MIN_ACCEPTABLE_DIAMETER[i]).getValue()).doubleValue();
         if ((dRadius == NO_DATA) || (dDiameter == NO_DATA)) {
            break;
         }
      }

      return Math.min(PLOTS_COUNT - 1, i);

   }


   @Override
   public String[] getReport() {
      // TODO Auto-generated method stub
      return null;
   }


   @Override
   public boolean hasValidData() {

      if (super.hasValidData()) {
         double dLastRadius = 0;
         double dLastMinDiameter = 0;
         boolean bIsNoData = false;

         for (int i = 0; i < PLOTS_COUNT; i++) {
            final double dRadius = ((Double) m_Parameters.getParameter(RADIUS[i]).getValue()).doubleValue();
            final double dDiameter = ((Double) m_Parameters.getParameter(MIN_ACCEPTABLE_DIAMETER[i]).getValue()).doubleValue();
            if ((dRadius == NO_DATA) || (dDiameter == NO_DATA)) {
               if (dRadius != dDiameter) {
                  return false;
               }
               bIsNoData = true;
            }
            else {
               if ((dRadius <= dLastRadius) || (dDiameter <= dLastMinDiameter)) {
                  return false;
               }
               if (bIsNoData) {
                  return false;
               }
               dLastMinDiameter = dDiameter;
               dLastRadius = dRadius;
            }
         }
      }
      else {
         return false;
      }

      return true;

   }

}
