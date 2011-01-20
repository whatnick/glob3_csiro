package es.unex.meigas.core.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ParametersSet {

   HashMap<String, MeigasParameter> m_Parameters = new HashMap<String, MeigasParameter>();


   public void addParameter(final MeigasParameter param) {

      m_Parameters.put(param.getName(), param);

   }


   public MeigasParameter getParameter(final String sName) {

      return m_Parameters.get(sName);

   }


   public boolean checkData() {

      final Collection<MeigasParameter> values = m_Parameters.values();

      for (final Object element : values) {
         final MeigasParameter meigasParameter = (MeigasParameter) element;
         if (!meigasParameter.hasValidData()) {
            return false;
         }
      }

      return true;

   }


   public String[] getReport() {

      final ArrayList<String> errors = new ArrayList<String>();

      final Collection<MeigasParameter> values = m_Parameters.values();

      for (final Object element : values) {
         final MeigasParameter meigasParameter = (MeigasParameter) element;
         final String sError = meigasParameter.getErrorMessage();
         if (sError != null) {
            errors.add(sError);
         }
      }


      if (errors.size() != 0) {
         return errors.toArray(new String[0]);
      }
      else {
         return new String[0];
      }

   }


   public int getParameterCount() {

      return m_Parameters.size();

   }


   public Set<String> getParameterNames() {

      return m_Parameters.keySet();

   }


}
