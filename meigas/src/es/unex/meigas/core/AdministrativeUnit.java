package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;

import es.unex.meigas.core.parameters.MeigasStringValue;

public class AdministrativeUnit
         extends
            DasocraticElement {

   public static final String AREA = "AREA";


   public AdministrativeUnit() {

      super();

      setName("Nueva división");

      m_Parameters.addParameter(new MeigasStringValue(AREA, "Area"));

   }


   @Override
   protected DasocraticElement _addElement(final DasocraticElement element) {

      int i;

      if ((element instanceof Stand) || (element instanceof AdministrativeUnit)) {
         if (element instanceof AdministrativeUnit) {
            for (i = 0; i < m_Elements.size(); i++) {
               if (m_Elements.get(i) instanceof Stand) {
                  if (m_Parent != null) {
                     return m_Parent.addElement(element);
                  }
                  else {
                     return null;
                  }
               }
            }
         }
         if (element instanceof Stand) {
            for (i = 0; i < m_Elements.size(); i++) {
               if (m_Elements.get(i) instanceof AdministrativeUnit) {
                  return null;
               }
            }
         }
         element.setParent(this);
         m_Elements.add(element);
         return this;
      }
      else {
         return null;
      }

   }


   @Override
   public Rectangle2D getBoundingBox() {

      Rectangle2D bb = new Rectangle2D.Double();
      for (int i = 0; i < m_Elements.size(); i++) {
         final Rectangle2D rect = m_Elements.get(i).getBoundingBox();
         bb = bb.createUnion(rect);
      }
      return bb;

   }


   @Override
   public double getArea() {

      int i;
      double dArea = 0;
      double dUnitArea;

      for (i = 0; i < m_Elements.size(); i++) {
         dUnitArea = (m_Elements.get(i)).getArea();
         if (dUnitArea != NO_DATA) {
            dArea += dUnitArea;
         }
      }

      if (dArea == 0) {
         dArea = NO_DATA;
      }

      return dArea;

   }


   @Override
   public String[] getReport() {

      return null;

   }


   @Override
   public Class[] getParentElementClass() {

      return new Class[] { AdministrativeUnit.class, DasocraticProject.class };

   }

}
