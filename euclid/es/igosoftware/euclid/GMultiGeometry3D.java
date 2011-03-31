

package es.igosoftware.euclid;

import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;


public class GMultiGeometry3D<

ChildrenGeometryT extends IBoundedGeometry<IVector3, ? extends IFiniteBounds<IVector3, ?>>

>
         extends
            GMultiGeometry<IVector3, ChildrenGeometryT, GAxisAlignedBox> {


   private static final long serialVersionUID = 1L;


   public GMultiGeometry3D(final ChildrenGeometryT... children) {
      super(children);
   }


   public GMultiGeometry3D(final List<ChildrenGeometryT> children) {
      super(children);
   }


   public static void main(final String[] args) {
      System.out.println("GMultiGeometry3D 0.1");
      System.out.println("--------------------\n");


      final GMultiGeometry3D<IVector3> multiPoint2D = new GMultiGeometry3D<IVector3>(GVector3D.UNIT, GVector3D.X_DOWN,
               new GVector3D(10, 10, 5));
      System.out.println(multiPoint2D);
      System.out.println("  bounds=" + multiPoint2D.getBounds());
      System.out.println();
   }


}
