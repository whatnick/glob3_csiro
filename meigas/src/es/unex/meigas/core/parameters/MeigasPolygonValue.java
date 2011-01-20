package es.unex.meigas.core.parameters;

import es.unex.meigas.core.NamedGeometry;

public class MeigasPolygonValue
         extends
            MeigasParameter {

   private NamedGeometry m_Polygon;


   public MeigasPolygonValue(final String name,
                             final String description) {
      super(name, description);

   }


   @Override
   public boolean hasValidData() {

      return true;

   }


   @Override
   public boolean setValue(final Object value) {

      if (value instanceof NamedGeometry) {
         m_Value = value;
         return true;
      }
      else {
         return false;
      }

   }


   @Override
   public String getErrorMessage() {

      return null;

   }


   @Override
   protected Object getDefaultValue() {

      return null;

   }

}
