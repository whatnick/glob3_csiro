package es.unex.meigas.core.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class ParametersSet {

   TreeMap<String, MeigasParameter> m_Parameters = new TreeMap<String, MeigasParameter>();


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


   public String[] getParameterDescriptions() {

      final String[] descriptions = new String[m_Parameters.size()];
      final Set<String> set = m_Parameters.keySet();
      final Iterator<String> iter = set.iterator();
      int i = 0;
      while (iter.hasNext()) {
         final String key = iter.next();
         final MeigasParameter param = m_Parameters.get(key);
         descriptions[i] = param.getDescription();
         i++;
      }
      return descriptions;

   }


   public String getParameterNameFromDescription(final String sDescription) {

      final Set<String> set = m_Parameters.keySet();
      final Iterator<String> iter = set.iterator();
      int i = 0;
      while (iter.hasNext()) {
         final String key = iter.next();
         final MeigasParameter param = m_Parameters.get(key);
         if (param.getDescription().equalsIgnoreCase(sDescription)) {
            return param.getName();
         }
         i++;
      }

      return null;

   }

}
