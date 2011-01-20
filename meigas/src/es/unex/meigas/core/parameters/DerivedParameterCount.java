package es.unex.meigas.core.parameters;


public class DerivedParameterCount
         extends
            DerivedParameter {

   public static final String COUNT = "COUNT";


   public DerivedParameterCount() {

      super(COUNT, "Conteo", true, null);

   }


   @Override
   public Object getValue() {

      return Double.valueOf(1);

   }

}
