package es.unex.meigas.core;

import java.util.ArrayList;

import es.unex.meigas.core.parameters.MeigasCoordinateValue;
import es.unex.meigas.core.parameters.MeigasDateValue;
import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.core.parameters.MeigasStringValue;
import es.unex.meigas.filters.IFilter;

public abstract class Plot
         extends
            DasocraticElement {

   protected final static int TREES_HA               = 0;
   protected final static int BASIMETRIC_AREA_HA     = 1;
   protected final static int VOLUME_HA              = 2;
   protected final static int VOLUME_WITHOUT_BARK_HA = 3;

   public final static String DATE                   = "DATE";
   public final static String SLOPE                  = "SLOPE";
   public final static String ASPECT                 = "ASPECT";
   public final static String ELEVATION              = "ELEVATION";
   public final static String CRUISER                = "CRUISER";
   public final static String COORD                  = "COORD";
   public final static String REGENERATION_COUNT     = "REGENERATION COUNT";
   public final static String REGENERATION_NOTES     = "REGENERATION_STRING";


   public Plot() {

      super();

      m_Parameters.addParameter(new MeigasDateValue(DATE, "Fecha"));
      m_Parameters.addParameter(new MeigasCoordinateValue(COORD, "Coordenadas"));
      m_Parameters.addParameter(new MeigasNumericalValue(SLOPE, "Pendiente", 0, 100));
      m_Parameters.addParameter(new MeigasNumericalValue(ASPECT, "Orientación", 0, 360));
      m_Parameters.addParameter(new MeigasNumericalValue(ELEVATION, "Altitud", 0, 5000));
      m_Parameters.addParameter(new MeigasNumericalValue(REGENERATION_COUNT, "Conteo regeneración", 0, 500, true, true));
      m_Parameters.addParameter(new MeigasStringValue(REGENERATION_NOTES, "Notas regeneración", true));
      m_Parameters.addParameter(new MeigasStringValue(CRUISER, "Operario", false));

      setName("Nueva parcela");

   }


   public ArrayList getPlots(final IFilter[] filters) {

      final ArrayList plots = new ArrayList();

      for (int i = 0; i < filters.length; i++) {
         if (!filters[i].accept(this)) {
            return plots;
         }
      }

      plots.add(this);

      return plots;

   }


   @Override
   protected DasocraticElement _addElement(final DasocraticElement element) {

      if (element instanceof Tree) {
         element.setParent(this);
         m_Elements.add(element);
         return this;
      }
      else {
         return null;
      }

   }


   @Override
   public Class[] getParentElementClass() {

      return new Class[] { SubCruise.class, Cruise.class };

   }


   @Override
   public boolean hasTrees() {

      return m_Elements.size() != 0;

   }


}
