package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Envelope;

import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.core.parameters.MeigasStringValue;

public class Stand
         extends
            DasocraticElement {

   public static final String AREA           = "AREA";
   public static final String MIN_AGE        = "MIN_AGE";
   public static final String MAX_AGE        = "MAX_AGE";
   public static final String MIN_ELEVATION  = "MIN_ELEVATION";
   public static final String MAX_ELEVATION  = "MAX_ELEVATION";
   public static final String SITE_INDEX     = "SITE_INDEX";
   public static final String POLYGON        = "POLYGON";
   public static final String SOUTHERN_LIMIT = "SOUTHERN_LIMIT";
   public static final String NORTHERN_LIMIT = "NORTHERN_LIMIT";
   public static final String EASTERN_LIMIT  = "EASTERN_LIMIT";
   public static final String WESTERN_LIMIT  = "WESTERN_LIMIT";


   public Stand() {

      super();

      setName("Nuevo cantón");

      m_Parameters.addParameter(new MeigasNumericalValue(AREA, "�rea", 0, Double.MAX_VALUE));
      m_Parameters.addParameter(new MeigasNumericalValue(MIN_AGE, "Edad mínima", 0, 200));
      m_Parameters.addParameter(new MeigasNumericalValue(MAX_AGE, "Edad míxima", 0, 200));
      m_Parameters.addParameter(new MeigasNumericalValue(MIN_ELEVATION, "Cota mínima", 0, 5000));
      m_Parameters.addParameter(new MeigasNumericalValue(MAX_ELEVATION, "Cota máxima", 0, 5000));
      m_Parameters.addParameter(new MeigasStringValue(SITE_INDEX, "�ndice de estaci�n"));
      m_Parameters.addParameter(new MeigasStringValue(SOUTHERN_LIMIT, "Sur"));
      m_Parameters.addParameter(new MeigasStringValue(NORTHERN_LIMIT, "Norte"));
      m_Parameters.addParameter(new MeigasStringValue(EASTERN_LIMIT, "Este"));
      m_Parameters.addParameter(new MeigasStringValue(WESTERN_LIMIT, "Oeste"));
   }


   @Override
   protected DasocraticElement _addElement(final DasocraticElement element) {

      if (element instanceof Cruise) {
         element.setParent(this);
         m_Elements.add(element);
         return this;
      }
      else {
         return null;
      }

   }


   @Override
   public double getArea() {

      return ((Double) m_Parameters.getParameter(AREA).getValue()).doubleValue();

   }


   @Override
   public Rectangle2D getBoundingBox() {

      final NamedGeometry polyg = (NamedGeometry) m_Parameters.getParameter(POLYGON).getValue();
      if (polyg != null) {
         final Envelope env = polyg.geom.getEnvelopeInternal();
         final Rectangle2D rect = new Rectangle2D.Double(env.getMinX(), env.getMinY(), env.getWidth(), env.getHeight());
         return rect;
      }
      else {
         final ArrayList plots = new ArrayList();
         getElementsOfClassRecursive(plots, Plot.class);
         Rectangle2D bbox = null;
         for (int i = 0; i < plots.size(); i++) {
            final Plot plot = (Plot) plots.get(i);
            final Rectangle2D rect = plot.getBoundingBox();
            if (rect != null) {
               if (bbox == null) {
                  bbox = rect;
               }
               else {
                  rect.add(bbox);
               }
            }
         }
         return bbox;
      }

   }


   @Override
   public boolean hasValidData() {

      if (!super.hasValidData()) {
         return false;
      }

      double dMin = ((Double) m_Parameters.getParameter(MIN_AGE).getValue()).doubleValue();
      double dMax = ((Double) m_Parameters.getParameter(MAX_AGE).getValue()).doubleValue();
      if (dMin > dMax) {
         return false;
      }
      dMin = ((Double) m_Parameters.getParameter(MIN_ELEVATION).getValue()).doubleValue();
      dMax = ((Double) m_Parameters.getParameter(MAX_ELEVATION).getValue()).doubleValue();
      if (dMin > dMax) {
         return false;
      }

      return true;

   }


   @Override
   public String[] getReport() {

      final ArrayList<String> list = new ArrayList<String>();
      final String[] errors = m_Parameters.getReport();

      for (final String element : errors) {
         list.add(element);
      }

      if (((MeigasNumericalValue) m_Parameters.getParameter(AREA)).isNoData()) {
         list.add("Se necesita un valor de area para el cantón");
      }
      double dMin = ((Double) m_Parameters.getParameter(MIN_AGE).getValue()).doubleValue();
      double dMax = ((Double) m_Parameters.getParameter(MAX_AGE).getValue()).doubleValue();
      if (dMin > dMax) {
         list.add("La edad máxima es menor que la edad mínima");
      }
      dMin = ((Double) m_Parameters.getParameter(MIN_ELEVATION).getValue()).doubleValue();
      dMax = ((Double) m_Parameters.getParameter(MAX_ELEVATION).getValue()).doubleValue();
      if (dMin > dMax) {
         list.add("La edad máxima es menor que la edad mínima");
      }

      if (list.size() > 0) {
         return list.toArray(new String[0]);
      }
      else {
         return null;
      }

   }


   @Override
   public Class[] getParentElementClass() {

      return new Class[] { AdministrativeUnit.class };

   }

}
