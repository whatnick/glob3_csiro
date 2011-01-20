package es.unex.meigas.core.parameters;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.Specie;

public class MeigasSpecieValue
         extends
            MeigasParameter {

   public MeigasSpecieValue(final String sName,
                            final String sDescription) {

      super(sName, sDescription);

   }


   @Override
   public String getErrorMessage() {

      return null;

   }


   @Override
   public boolean hasValidData() {

      return true;

   }


   @Override
   public boolean setValue(final Object value) {

      if (value instanceof Specie) {
         m_Value = value;
         return true;
      }
      return false;

   }


   @Override
   protected Object getDefaultValue() {

      return Meigas.getSpeciesCatalog().getDefaultSpecie();

   }
}
