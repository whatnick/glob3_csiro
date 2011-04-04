

package es.igosoftware.euclid.multigeometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.ITransformer;


public abstract class GMultiGeometry<

VectorT extends IVector<VectorT, ?>,

ChildrenGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>,

BoundsT extends GAxisAlignedOrthotope<VectorT, BoundsT>

>
         extends
            GGeometryAbstract<VectorT>
         implements
            IBoundedGeometry<VectorT, BoundsT>,
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


   public ChildrenGeometryT getExemplar() {
      return _children.get(0);
   }


   @Override
   public byte dimensions() {
      return getExemplar().dimensions();
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
      VectorT closest = getExemplar().closestPoint(point);
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
      return getExemplar().precision();
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


   @SuppressWarnings("unchecked")
   @Override
   public BoundsT getBounds() {
      final List<GAxisAlignedOrthotope<VectorT, ?>> bounds = GCollections.collect(_children,
               new ITransformer<ChildrenGeometryT, GAxisAlignedOrthotope<VectorT, ?>>() {
                  @Override
                  public GAxisAlignedOrthotope<VectorT, ?> transform(final ChildrenGeometryT element) {
                     return element.getBounds().asAxisAlignedOrthotope();
                  }
               });

      return (BoundsT) GAxisAlignedOrthotope.merge(bounds);
   }


   @Override
   public String toString() {
      return "GMultiGeometry" + dimensions() + " " + _children;
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


   public ChildrenGeometryT getChild(final int index) {
      return _children.get(index);
   }


   @Override
   public VectorT closestPointOnBoundary(final VectorT point) {
      GAssert.notNull(point, "point");

      double minDistance = Double.POSITIVE_INFINITY;
      VectorT closestPoint = null;

      for (final ChildrenGeometryT child : _children) {
         final VectorT currentPoint = child.closestPointOnBoundary(point);
         final double currentDistance = currentPoint.squaredDistance(point);

         if (currentDistance <= minDistance) {
            minDistance = currentDistance;
            closestPoint = currentPoint;
         }
      }

      return closestPoint;
   }


   @Override
   public double squaredDistanceToBoundary(final VectorT point) {
      return closestPointOnBoundary(point).squaredDistance(point);
   }


   @Override
   public double distanceToBoundary(final VectorT point) {
      return Math.sqrt(squaredDistance(point));
   }


   @Override
   public final boolean containsOnBoundary(final VectorT point) {
      return GMath.closeToZero(distanceToBoundary(point));
   }


}
