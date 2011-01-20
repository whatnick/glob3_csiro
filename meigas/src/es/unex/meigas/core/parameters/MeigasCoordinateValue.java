package es.unex.meigas.core.parameters;

import com.vividsolutions.jts.geom.Coordinate;

public class MeigasCoordinateValue
         extends
            MeigasParameter {

   public MeigasCoordinateValue(final String sName,
                                final String sDescription) {

      super(sName, sDescription);

   }


   @Override
   public String getErrorMessage() {

      if (hasValidData()) {
         return null;
      }

      return "La coordenada no es válida.";

   }


   @Override
   public boolean hasValidData() {

      return m_Value != null;

   }


   @Override
   public boolean setValue(final Object value) {

      if (value instanceof Coordinate) {
         m_Value = value;
         return true;
      }
      else {
         return false;
      }

   }


   @Override
   protected Object getDefaultValue() {

      return new Coordinate(0, 0);

   }

}
