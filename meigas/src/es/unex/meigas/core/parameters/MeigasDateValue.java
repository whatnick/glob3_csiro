package es.unex.meigas.core.parameters;

import java.util.Date;

public class MeigasDateValue
         extends
            MeigasParameter {

   private Date m_Date;


   public MeigasDateValue(final String sName,
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

      if (value instanceof Date) {
         m_Date = (Date) value;
         return true;
      }
      else {
         return false;
      }

   }


   @Override
   protected Object getDefaultValue() {

      return new Date();

   }

}
