package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import es.unex.meigas.core.parameters.DerivedParameterBasimetricArea;
import es.unex.meigas.core.parameters.DerivedParameterCount;
import es.unex.meigas.core.parameters.MeigasCoordinateValue;
import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.core.parameters.MeigasParameter;
import es.unex.meigas.core.parameters.MeigasSelectionValue;
import es.unex.meigas.core.parameters.MeigasSpecieValue;
import es.unex.meigas.core.parameters.ParametersSet;

public class Tree
         extends
            DasocraticElement {

   public static final String HEIGHT         = "HEIGHT";
   public static final String DBH            = "DBH";
   public static final String VOLUME         = "VOLUME";
   public static final String NO_BARK_VOLUME = "NO_BARK_VOLUME";
   public static final String SPECIE         = "SPECIE";
   public static final String AGE            = "AGE";
   public static final String RADIAL_GROWTH  = "RADIAL_GROWTH";
   public static final String HEIGHT_GROWTH  = "HEIGHT_GROWTH";
   public static final String CROWN_DIAMETER = "CROWN_DIAMETER";
   public static final String LOG_HEIGHT     = "LOG_HEIGHT";
   public static final String BARK           = "BARK";
   public static final String SHAPE_FACTOR   = "SHAPE_FACTOR";
   public static final String POSITION       = "POSITION";
   public static final String COORD          = "COORD";

   private int[]              m_InjuredParts;
   private int[]              m_InjuryCauses;
   private final int          m_iSeverity    = 0;


   public Tree() {

      super();

      m_Parameters = getParametersDefinition();
      final DerivedParameterBasimetricArea basimetricArea = (DerivedParameterBasimetricArea) m_Parameters.getParameter(DerivedParameterBasimetricArea.BASIMETRIC_AREA);
      basimetricArea.setElement(this);


      setName("Nuevo árbol");

   }


   public static ParametersSet getParametersDefinition() {


      final ParametersSet params = new ParametersSet();
      params.addParameter(new MeigasNumericalValue(DBH, "Diámetro normal", 0, 150));
      params.addParameter(new MeigasNumericalValue(HEIGHT, "Altura", 0, 50));
      params.addParameter(new MeigasNumericalValue(VOLUME, "Volumen c/c", 0, 10, false, true));
      params.addParameter(new MeigasNumericalValue(NO_BARK_VOLUME, "Volumen s/c", 0, 10, false, true));
      params.addParameter(new MeigasNumericalValue(AGE, "Edad", 0, 200, true, false));
      params.addParameter(new MeigasNumericalValue(RADIAL_GROWTH, "Incremento diametral anual", 0, 2));
      params.addParameter(new MeigasNumericalValue(HEIGHT_GROWTH, "Incremento de altura anual", 0, 5));
      params.addParameter(new MeigasNumericalValue(LOG_HEIGHT, "Altura de fuste", 0, 50));
      params.addParameter(new MeigasNumericalValue(CROWN_DIAMETER, "Diámetro de copa", 0, 20));
      params.addParameter(new MeigasNumericalValue(BARK, "Espesor de corteza", 0, 50));
      params.addParameter(new MeigasSelectionValue(SHAPE_FACTOR, "Factor de forma", new String[] { "1", "2", "3" }));
      params.addParameter(new MeigasCoordinateValue(COORD, "Coordenadas"));
      params.addParameter(new MeigasSpecieValue(SPECIE, "Especie"));
      params.addParameter(new DerivedParameterBasimetricArea(null));
      params.addParameter(new DerivedParameterCount());

      return params;

   }


   @Override
   protected DasocraticElement _addElement(final DasocraticElement element) {

      return null;

   }


   @Override
   public String[] getSpeciesNames() {

      final String[] species = new String[1];

      final String specie = ((Specie) getParameterValue(SPECIE)).name;

      species[0] = specie;

      return species;

   }


   /*public ArrayList getTrees(final IFilter[] filters) {

      final ArrayList trees = new ArrayList();

      for (int i = 0; i < filters.length; i++) {
         if (!filters[i].accept(this)) {
            return trees;
         }
      }
      trees.add(this);

      return trees;

   }*/


   @Override
   public boolean hasTrees() {

      return true;

   }


   public double getBasimetricArea() { // in m2

      final double dDBH = ((Double) m_Parameters.getParameter(DBH).getValue()).doubleValue();

      if (dDBH != MeigasNumericalValue.NO_DATA) {
         return (Math.PI * Math.pow(dDBH / 2., 2.) / 10000.);
      }
      else {
         return MeigasNumericalValue.NO_DATA;
      }

   }


   @Override
   public double getArea() {

      return 1;

   }


   @Override
   public Rectangle2D getBoundingBox() {

      final Coordinate coord = ((Coordinate) getParameterValue(COORD));
      return new Rectangle2D.Double(coord.x, coord.y, 0, 0);

   }


   @Override
   public boolean hasValidData() {

      if (super.hasValidData()) {
         final double dHeight = ((Double) m_Parameters.getParameter(HEIGHT).getValue()).doubleValue();
         final double dDBH = ((Double) m_Parameters.getParameter(DBH).getValue()).doubleValue();
         final double dRatio = dHeight / dDBH * 100;
         if (dRatio > 150) {
            return false;
         }

         //Check if tree is on its parent plot
         return insidePlot();

      }
      else {
         return false;
      }

   }


   @Override
   public String[] getReport() {

      final ArrayList<String> list = new ArrayList<String>();
      final String[] errors = m_Parameters.getReport();

      for (final String element : errors) {
         list.add(element);
      }

      final double dHeight = ((Double) m_Parameters.getParameter(HEIGHT).getValue()).doubleValue();
      final double dDBH = ((Double) m_Parameters.getParameter(DBH).getValue()).doubleValue();
      final double dRatio = dHeight / dDBH * 100;
      if (dRatio > 150) {
         list.add("El coeficiente de esbeltez no parece lógico. Revise los valores de altura y diámetro normal");
      }
      final double dLogHeight = ((Double) m_Parameters.getParameter(LOG_HEIGHT).getValue()).doubleValue();
      if (dLogHeight > dHeight) {
         list.add("La altura de fuste es mayor que la altura total");
      }
      final double dVolume = ((Double) m_Parameters.getParameter(VOLUME).getValue()).doubleValue();
      final double dNoBarkVolume = ((Double) m_Parameters.getParameter(NO_BARK_VOLUME).getValue()).doubleValue();
      if (dNoBarkVolume > dVolume) {
         list.add("El volumen con corteza es menos que el volumen sin corteza");
      }

      if (!insidePlot()) {
         list.add("Las coordenadas del árbol no están dentro de la parcela");
      }


      if (list.size() > 0) {
         return list.toArray(new String[0]);
      }
      else {
         return null;
      }

   }


   @Override
   public void getElementsRecursive(final ArrayList elements) {

      elements.add(this);

   }


   private boolean insidePlot() {

      try {
         return insidePlot((Plot) getParent());
      }
      catch (final Exception e) {
         return false;
      }

   }


   public boolean insidePlot(final Plot plot) {

      final Coordinate coord = ((Coordinate) getParameterValue(COORD));
      final GeometryFactory gf = new GeometryFactory();
      final Point point = gf.createPoint(coord);
      double radius = 0;
      if (plot instanceof FixedRadiusPlot) {
         radius = ((Double) ((FixedRadiusPlot) plot).getParameterValue(FixedRadiusPlot.RADIUS)).doubleValue();
      }
      if (plot instanceof ConcentricPlot) {
         radius = ((ConcentricPlot) plot).getMaxRadius();
      }
      final Geometry area = point.buffer(radius);
      return area.contains(point);

   }


   @Override
   public void calculateParameters(final Specie specie) {

      m_bCalculated = true;
      m_CurrentSpecieForParameterCalculation = specie;

      final Set<String> names = Tree.getParametersDefinition().getParameterNames();
      for (final String sName : names) {
         final MeigasParameter param = Tree.getParametersDefinition().getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            if (((MeigasNumericalValue) param).isAccumulated()) {
               final double dValue = ((Number) param.getValue()).doubleValue();
               m_AccumulatedTreeParameters.put(param.getName(), dValue);
            }
         }
      }

   }


   @Override
   public Class[] getParentElementClass() {

      return new Class[] { Plot.class };

   }


}
