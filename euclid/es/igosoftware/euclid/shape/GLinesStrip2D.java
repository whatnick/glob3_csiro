

package es.igosoftware.euclid.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.ITransformer;


public class GLinesStrip2D
         extends
            GLinesStrip<IVector2<?>, GSegment2D, GLinesStrip2D, GAxisAlignedRectangle>
         implements
            IPolygon2D<GLinesStrip2D> {


   private static final long     serialVersionUID = 1L;

   private GAxisAlignedRectangle _bounds;


   public GLinesStrip2D(final boolean validate,
                        final IVector2<?>... points) {
      super(validate, points);
   }


   public GLinesStrip2D(final boolean validate,
                        final List<IVector2<?>> points) {
      super(validate, points);
   }


   @Override
   public boolean isSelfIntersected() {
      final List<IVector2<?>> points = getPoints();
      final int pointsCount = points.size();
      for (int i = 0; i < pointsCount; ++i) {
         if (i < pointsCount - 1) {
            for (int h = i + 1; h < pointsCount; ++h) {
               // Do two points lie on top of one another?
               if (points.get(i).equals(points.get(h))) {
                  return true;
               }
            }
         }

         final int j = (i + 1) % pointsCount;
         final IVector2<?> iToj = points.get(j).sub(points.get(i));
         final IVector2<?> iTojNormal = new GVector2D(iToj.y(), -iToj.x());
         // i is the first vertex and j is the second
         final int startK = (j + 1) % pointsCount;
         int endK = (i - 1 + pointsCount) % pointsCount;
         endK += startK < endK ? 0 : startK + 1;
         int k = startK;
         IVector2<?> iTok = points.get(k).sub(points.get(i));
         boolean onLeftSide = iTok.dot(iTojNormal) >= 0;
         IVector2<?> prevK = points.get(k);
         ++k;
         for (; k <= endK; ++k) {
            final int modK = k % pointsCount;
            iTok = points.get(modK).sub(points.get(i));
            if (onLeftSide != (iTok.dot(iTojNormal) >= 0)) {
               final IVector2<?> prevKtoK = points.get(modK).sub(prevK);
               final IVector2<?> prevKtoKNormal = new GVector2D(prevKtoK.y(), -prevKtoK.x());
               if (((points.get(i).sub(prevK).dot(prevKtoKNormal)) >= 0) != ((points.get(j).sub(prevK).dot(prevKtoKNormal)) >= 0)) {
                  return true;
               }
            }
            onLeftSide = iTok.dot(iTojNormal) > 0;
            prevK = points.get(modK);
         }
      }

      return false;

      //      return !isSimple();
   }


   @Override
   protected List<GSegment2D> initializeEdges() {
      final List<IVector2<?>> points = getPoints();
      final int pointsCount = points.size();

      final GSegment2D[] edges = new GSegment2D[pointsCount];

      for (int i = 1; i < pointsCount; i++) {
         edges[i - 1] = new GSegment2D(points.get(i - 1), points.get(i));
      }

      return Arrays.asList(edges);
   }


   @Override
   public GLinesStrip2D transformedBy(final IVectorTransformer<IVector2<?>> transformer) {
      final List<IVector2<?>> transformedPoints = GCollections.collect(getPoints(), new ITransformer<IVector2<?>, IVector2<?>>() {
         @Override
         public IVector2<?> transform(final IVector2<?> element) {
            return element.transformedBy(transformer);
         }
      });

      return new GLinesStrip2D(true, transformedPoints);
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      if (_bounds == null) {
         _bounds = GAxisAlignedRectangle.minimumBoundingRectangle(this);
      }
      return _bounds;
   }


   @Override
   public List<GTriangle2D> triangulate() {
      throw new RuntimeException("Unsupported operation");
   }


   @Override
   public IPolygon2D<?> createSimplified(final double capsRadiansTolerance) {
      final LinkedList<IVector2<?>> points = new LinkedList<IVector2<?>>(getPoints());

      List<IVector2<?>> previousPoints = new ArrayList<IVector2<?>>(points);

      boolean changed;
      do {
         changed = false;

         final int pointsCount = points.size();

         if (pointsCount < 3) {
            return GShape.createLine2(true, previousPoints);
         }

         for (int i = 0; i < pointsCount; i++) {
            final int previousI = (i + pointsCount - 1) % pointsCount;
            final int prePreviousI = (i + pointsCount - 2) % pointsCount;

            final GTriangle2D triangle = new GTriangle2D(points.get(prePreviousI), points.get(previousI), points.get(i));
            if (triangle.isCaps(capsRadiansTolerance)) {
               previousPoints = new ArrayList<IVector2<?>>(points);

               final IVector2<?> average = points.get(i).add(points.get(previousI)).div(2);
               points.set(i, average);
               points.remove(previousI);

               changed = true;
               break; // exit for
            }
         }

      }
      while (changed);

      if (points.size() < 3) {
         return GShape.createLine2(true, previousPoints);
      }

      return GShape.createLine2(true, points);
   }


   @Override
   public GRenderType getRenderType() {
      return GRenderType.POLYLINE;
   }


}