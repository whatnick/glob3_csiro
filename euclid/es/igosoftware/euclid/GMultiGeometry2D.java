

package es.igosoftware.euclid;

import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.ILineal2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;


public class GMultiGeometry2D<

ChildrenGeometryT extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>

>
         extends
            GMultiGeometry<IVector2, ChildrenGeometryT, GAxisAlignedRectangle> {


   public GMultiGeometry2D(final ChildrenGeometryT... children) {
      super(children);
   }


   public GMultiGeometry2D(final List<ChildrenGeometryT> children) {
      super(children);
   }


   private static final long serialVersionUID = 1L;


   public static void main(final String[] args) {
      System.out.println("GMultiGeometry2D 0.1");
      System.out.println("--------------------\n");


      final GMultiGeometry2D<IVector2> multiPoint2D = new GMultiGeometry2D<IVector2>(GVector2D.UNIT, GVector2D.X_DOWN,
               new GVector2D(10, 10));
      System.out.println(multiPoint2D);
      System.out.println("  bounds=" + multiPoint2D.getBounds());
      System.out.println();


      final GMultiGeometry2D<ILineal2D> multiLine2D = new GMultiGeometry2D<ILineal2D>(new GSegment2D(GVector2D.ZERO,
               GVector2D.UNIT));
      System.out.println(multiLine2D);
      System.out.println("  bounds=" + multiLine2D.getBounds());
      System.out.println();
   }


}
