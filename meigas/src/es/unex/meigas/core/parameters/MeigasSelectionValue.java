package es.unex.meigas.core.parameters;

public class MeigasSelectionValue
         extends
            MeigasParameter {

   private final Object[] m_Options;


   public MeigasSelectionValue(final String sName,
                               final String sDescription,
                               final Object[] options) {

      super(sName, sDescription);
      m_Options = options;
      m_Value = options[0];

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

      for (final Object element : m_Options) {
         if (value.equals(element)) {
            m_Value = value;
            return true;
         }
      }
      return false;

   }


   public Object[] getOptions() {

      return m_Options;

   }


   @Override
   protected Object getDefaultValue() {

      return null;

   }
}
