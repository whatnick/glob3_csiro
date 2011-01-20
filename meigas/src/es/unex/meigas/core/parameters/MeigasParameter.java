package es.unex.meigas.core.parameters;

public abstract class MeigasParameter {

   protected String m_sName;
   protected String m_sDescription;
   protected Object m_Value;


   public MeigasParameter(final String sName,
                          final String sDescription) {

      m_sName = sName;
      m_sDescription = sDescription;
      m_Value = getDefaultValue();

   }


   public String getName() {

      return m_sName;

   }


   public String getDescription() {

      return m_sDescription;

   }


   public Object getValue() {

      return m_Value;

   }


   public abstract boolean setValue(Object value);


   public abstract boolean hasValidData();


   public abstract String getErrorMessage();


   protected abstract Object getDefaultValue();


}
