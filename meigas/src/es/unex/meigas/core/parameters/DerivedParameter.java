package es.unex.meigas.core.parameters;

import es.unex.meigas.core.DasocraticElement;

public abstract class DerivedParameter
         extends
            MeigasNumericalValue {

   protected DasocraticElement m_Element;


   public DerivedParameter(final String sName,
                           final String sDescription,
                           final boolean bIsAccumulated,
                           final DasocraticElement element) {

      super(sName, sDescription, bIsAccumulated);

      m_Element = element;

   }


   @Override
   public abstract Object getValue();


   @Override
   public boolean setValue(final Object value) {

      return true;

   }


   @Override
   public boolean hasValidData() {

      return true;

   }


   @Override
   public String getErrorMessage() {

      return null;

   }


}
