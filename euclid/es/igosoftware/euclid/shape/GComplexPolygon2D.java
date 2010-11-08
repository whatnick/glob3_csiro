package es.igosoftware.euclid.shape;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.ITransformer;

public final class GComplexPolygon2D
         extends
            GComplexPolytope<IVector2<?>, GSegment2D, GComplexPolygon2D, GAxisAlignedRectangle, IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle>>
         implements
            IPolygon2D<GComplexPolygon2D> {

   private static final long serialVersionUID = 1L;


   public GComplexPolygon2D(final IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> hull,
                            final List<? extends IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle>> holes) {
      super(hull, holes);
   }


   @Override
   public boolean contains(final IVector2<?> point) {
      if (!_hull.contains(point)) {
         return false;
      }

      for (final IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> hole : _holes) {
         if (hole.contains(point)) {
            return false;
         }
      }

      return true;
   }


   @Override
   public boolean isSelfIntersected() {
      if (_hull.isSelfIntersected()) {
         return true;
      }

      for (final IPolygon<?, ?, ?, ?> hole : _holes) {
         if (hole.isSelfIntersected()) {
            return true;
         }
      }

      return false;
   }


   @Override
   public GComplexPolygon2D createSimplified(final double capsRadiansTolerance) {
      final IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> simplifiedShell = _hull.createSimplified(capsRadiansTolerance);

      final List<IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle>> simplifiedHoles = new ArrayList<IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle>>(
               _holes.size());

      for (final IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> hole : _holes) {
         simplifiedHoles.add(hole.createSimplified(capsRadiansTolerance));
      }

      return new GComplexPolygon2D(simplifiedShell, simplifiedHoles);
   }


   @Override
   protected String getStringName() {
      return "ComplexPolygon";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _hull.getBounds();
   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return _hull.getAxisAlignedBoundingBox();
   //   }


   @Override
   public double squaredDistance(final IVector2<?> point) {
      double min = _hull.squaredDistance(point);

      for (final IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> hole : _holes) {
         final double current = hole.squaredDistance(point);
         if (current < min) {
            min = current;
         }
      }

      return min;
   }


   @Override
   protected List<GSegment2D> initializeEdges() {
      final List<GSegment2D> result = new ArrayList<GSegment2D>();
      result.addAll(_hull.getEdges());
      for (final IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> hole : _holes) {
         result.addAll(hole.getEdges());
      }
      return result;
   }


   @Override
   public GComplexPolygon2D transformedBy(final IVectorTransformer<IVector2<?>> transformer) {
      final List<IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle>> transformedHoles = GCollections.collect(
               _holes,
               new ITransformer<IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle>, IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle>>() {
                  @Override
                  public IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> transform(final IPolygon<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> element) {
                     return element.transformedBy(transformer);
                  }
               });

      return new GComplexPolygon2D(_hull.transformedBy(transformer), transformedHoles);
   }


   @Override
   public List<GTriangle2D> triangulate() {
      throw new IllegalArgumentException("Not yet implemented");
   }

}
