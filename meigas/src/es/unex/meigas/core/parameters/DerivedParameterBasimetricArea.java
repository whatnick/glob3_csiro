package es.unex.meigas.core.parameters;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Tree;

public class DerivedParameterBasimetricArea
         extends
            DerivedParameter {

   public static final String BASIMETRIC_AREA = "BASIMETRIC_AREA";


   public DerivedParameterBasimetricArea(final DasocraticElement element) {

      super(BASIMETRIC_AREA, "Área basimétrica", true, element);

   }


   @Override
   public Object getValue() {

      if (m_Element instanceof Tree) {
         return ((Tree) m_Element).getBasimetricArea();
      }

      return 0;
   }


   public void setElement(final Tree tree) {

      m_Element = tree;

   }

}
