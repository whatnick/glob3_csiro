package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class DasocraticProject
         extends
            DasocraticElement
         implements
            Serializable {

   public DasocraticProject() {

      super();

      setName("Proyecto");

   }


   @Override
   protected DasocraticElement _addElement(final DasocraticElement element) {

      if (element instanceof AdministrativeUnit) {
         m_Elements.add(element);
         element.setParent(this);
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
   public String[] getErrorsReport() {

      return null;

   }


   @Override
   public Class[] getParentElementClass() {

      return null;

   }


   @Override
   public void setHasChanged(final boolean bHasChanged) {

      m_bHasChanged = bHasChanged;

   }

}
