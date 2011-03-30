

package es.igosoftware.euclid;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.shape.GRenderType;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.ITransformer;


public class GMultiGeometry<

VectorT extends IVector<VectorT, ?, ?>,

ChildrenGeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GGeometryAbstract<VectorT, GMultiGeometry<VectorT, ChildrenGeometryT>>
         implements
            IBoundedGeometry<VectorT, GMultiGeometry<VectorT, ChildrenGeometryT>, GAxisAlignedOrthotope<VectorT, ?>>,
            Iterable<ChildrenGeometryT> {


   private static final long             serialVersionUID = 1L;

   private final List<ChildrenGeometryT> _children;


   public GMultiGeometry(final ChildrenGeometryT... children) {
      GAssert.notEmpty(children, "children");

      _children = Arrays.asList(children);
   }


   public GMultiGeometry(final List<ChildrenGeometryT> children) {
      GAssert.notEmpty(children, "children");

      _children = new ArrayList<ChildrenGeometryT>(children); // copy to avoid external modifications
   }


   @Override
   public byte dimensions() {
      return _children.get(0).dimensions();
   }


   @Override
   public boolean contains(final VectorT point) {
      for (final ChildrenGeometryT child : _children) {
         if (child.contains(point)) {
            return true;
         }
      }
      return false;
   }


   @Override
   public double squaredDistance(final VectorT point) {
      double min = Double.POSITIVE_INFINITY;

      for (final ChildrenGeometryT child : _children) {
         final double current = child.squaredDistance(point);
         if (current < min) {
            min = current;
         }
      }

      return min;
   }


   @Override
   public VectorT closestPoint(final VectorT point) {
      VectorT closest = _children.get(0).closestPoint(point);
      double closestDistance = closest.squaredDistance(point);

      for (int i = 1; i < _children.size(); i++) {
         final VectorT current = _children.get(i).closestPoint(point);
         final double currentDistance = current.squaredDistance(point);
         if (currentDistance < closestDistance) {
            closest = current;
            closestDistance = currentDistance;
         }
      }

      return closest;
   }


   @Override
   public double precision() {
      return _children.get(0).precision();
   }


   @Override
   public VectorT getCentroid() {
      final List<VectorT> centroids = GCollections.collect(_children, new ITransformer<ChildrenGeometryT, VectorT>() {
         @Override
         public VectorT transform(final ChildrenGeometryT element) {
            return element.getCentroid();
         }
      });

      return GVectorUtils.getAverage(centroids);
   }


   @Override
   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      final List<GAxisAlignedOrthotope<VectorT, ?>> bounds = GCollections.collect(_children,
               new ITransformer<ChildrenGeometryT, GAxisAlignedOrthotope<VectorT, ?>>() {
                  @Override
                  public GAxisAlignedOrthotope<VectorT, ?> transform(final ChildrenGeometryT element) {
                     return element.getBounds().asAxisAlignedOrthotope();
                  }
               });

      return GAxisAlignedOrthotope.merge(bounds);
   }


   @Override
   public GRenderType getRenderType() {
      return _children.get(0).getRenderType();
   }


   @Override
   public void save(final DataOutputStream output) throws IOException {
      output.writeInt(_children.size());
      for (final ChildrenGeometryT child : _children) {
         child.save(output);
      }
   }


   @Override
   public String toString() {
      return "GComposite " + _children;
   }


   public int getChildrenCount() {
      return _children.size();
   }


   public List<ChildrenGeometryT> getChildren() {
      return Collections.unmodifiableList(_children);
   }


   @Override
   public Iterator<ChildrenGeometryT> iterator() {
      return Collections.unmodifiableList(_children).iterator();
   }


   public static void main(final String[] args) {
      System.out.println("GComposite 0.1");
      System.out.println("--------------\n");


      final GMultiGeometry<IVector2<?>, IVector2<?>> multiPoint2D = new GMultiGeometry<IVector2<?>, IVector2<?>>(GVector2D.UNIT,
               GVector2D.X_DOWN, new GVector2D(10, 10));
      System.out.println(multiPoint2D);
      System.out.println("  bounds=" + multiPoint2D.getBounds());
      System.out.println();


      final GMultiGeometry<IVector3<?>, IVector3<?>> multiPoint3D = new GMultiGeometry<IVector3<?>, IVector3<?>>(GVector3D.UNIT,
               GVector3D.X_DOWN, new GVector3D(10, 10, 10));
      System.out.println(multiPoint3D);
      System.out.println("  bounds=" + multiPoint3D.getBounds());
      System.out.println();


      final GMultiGeometry<IVector2<?>, IPolygon2D<?>> multiLine2D = new GMultiGeometry<IVector2<?>, IPolygon2D<?>>(
               new GSegment2D(GVector2D.ZERO, GVector2D.UNIT));
      System.out.println(multiLine2D);
      System.out.println("  bounds=" + multiLine2D.getBounds());
      System.out.println();


   }


}
