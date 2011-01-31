package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;

public class SubCruise
         extends
            DasocraticElement {

   @Override
   protected DasocraticElement _addElement(final DasocraticElement element) {

      if (element instanceof Plot) {
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

      int i;
      DasocraticElement element;
      Rectangle2D rect;
      double dMinX = Double.MAX_VALUE;
      double dMaxX = Double.NEGATIVE_INFINITY;
      double dMinY = Double.MAX_VALUE;
      double dMaxY = Double.NEGATIVE_INFINITY;

      for (i = 0; i < m_Elements.size(); i++) {
         element = m_Elements.get(i);
         rect = element.getBoundingBox();
         dMinX = Math.min(rect.getMinX(), dMinX);
         dMinY = Math.min(rect.getMinY(), dMinY);
         dMaxX = Math.max(rect.getMaxX(), dMaxX);
         dMaxY = Math.max(rect.getMaxY(), dMaxY);
      }

      return new Rectangle2D.Double(dMinX, dMinY, dMaxX - dMinX, dMaxY - dMinY);

   }


   @Override
   public double getArea() {

      int i;
      double dValue;
      double dArea = 0;

      for (i = 0; i < m_Elements.size(); i++) {
         dValue = (m_Elements.get(i)).getArea();
         if (dValue != NO_DATA) {
            dArea += dValue;
         }
      }

      if (dArea == 0) {
         dArea = NO_DATA;
      }

      return dArea;

   }


   @Override
   public String[] getErrorsReport() {

      return null;

   }


   @Override
   public Class[] getParentElementClass() {

      return new Class[] { Cruise.class };

   }

}
