package es.unex.meigas.core.parameters;

public class MeigasStringValue
         extends
            MeigasParameter {

   private final boolean m_bIsLongString;


   public MeigasStringValue(final String sName,
                            final String sDescription) {

      this(sName, sDescription, false);

   }


   public MeigasStringValue(final String sName,
                            final String sDescription,
                            final boolean bIsLongString) {

      super(sName, sDescription);
      m_bIsLongString = bIsLongString;

   }


   @Override
   public boolean hasValidData() {

      return true;

   }


   @Override
   public boolean setValue(final Object value) {

      if (value instanceof String) {
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


   public boolean isLongString() {

      return m_bIsLongString;

   }


   @Override
   protected Object getDefaultValue() {

      return "";

   }

}
