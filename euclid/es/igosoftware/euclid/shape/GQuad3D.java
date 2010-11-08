package es.igosoftware.euclid.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.utils.GTriangulate;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.ITransformer;


public final class GQuad3D
         extends
            GQuad<IVector3<?>, GSegment3D, GQuad3D, GAxisAlignedBox>
         implements
            IPolygon3D<GQuad3D> {

   private static final long serialVersionUID = 1L;

   private final GPlane      _plane;
   private GQuad2D           _quad2d;


   public GQuad3D(final IVector3<?> pV0,
                  final IVector3<?> pV1,
                  final IVector3<?> pV2,
                  final IVector3<?> pV3) {
      super(pV0, pV1, pV2, pV3);

      _plane = initializePlane();
   }


   private GPlane initializePlane() {
      final List<IVector3<?>> points = new ArrayList<IVector3<?>>(getPoints());
      try {
         final GPlane plane = GPlane.getBestFitPlane(points);

         for (final IVector3<?> point : points) {
            if (!plane.contains(point)) {
               throw new IllegalArgumentException("Points are not coplanar");
            }
         }

         return plane;
      }
      catch (final GColinearException e) {
         throw new IllegalArgumentException(e);
      }
      catch (final GInsufficientPointsException e) {
         throw new IllegalArgumentException(e);
      }
   }


   @Override
   public GAxisAlignedBox getBounds() {
      final IVector3<?> lower = GVectorUtils.min(_v0, _v1, _v2, _v3);
      final IVector3<?> upper = GVectorUtils.max(_v0, _v1, _v2, _v3);
      return new GAxisAlignedBox(lower, upper);
   }


   private GQuad2D getPolygon2D() {
      if (_quad2d == null) {
         _quad2d = initializeQuad2D();
      }
      return _quad2d;
   }


   private GQuad2D initializeQuad2D() {
      final List<IVector2<?>> points2d;

      final List<IVector3<?>> points = getPoints();
      if (_plane.isCloseToPlaneXY()) {
         points2d = GCollections.collect(points, new ITransformer<IVector3<?>, IVector2<?>>() {
            @Override
            public IVector2<?> transform(final IVector3<?> element) {
               return new GVector2D(element.x(), element.y());
            }
         });
      }
      else if (_plane.isCloseToPlaneXZ()) {
         points2d = GCollections.collect(points, new ITransformer<IVector3<?>, IVector2<?>>() {
            @Override
            public IVector2<?> transform(final IVector3<?> element) {
               return new GVector2D(element.x(), element.z());
            }
         });
      }
      else /*if (_plane.isCloseToPlaneYZ())*/{
         points2d = GCollections.collect(points, new ITransformer<IVector3<?>, IVector2<?>>() {
            @Override
            public IVector2<?> transform(final IVector3<?> element) {
               return new GVector2D(element.y(), element.z());
            }
         });
      }

      return new GQuad2D(points2d.get(0), points2d.get(1), points2d.get(2), points2d.get(3));
   }


   @Override
   public List<GTriangle3D> triangulate() {
      final GQuad2D pol2d = getPolygon2D();
      final GTriangulate.IndexedTriangle[] iTriangles = GTriangulate.triangulate(pol2d._v0, pol2d._v1, pol2d._v2, pol2d._v3);

      final List<GTriangle3D> result = new ArrayList<GTriangle3D>(iTriangles.length);
      for (final GTriangulate.IndexedTriangle iTriangle : iTriangles) {
         result.add(new GTriangle3D(getPoint(iTriangle._v0), getPoint(iTriangle._v1), getPoint(iTriangle._v2)));
      }
      return result;
   }


   @Override
   public boolean contains(final IVector3<?> point) {
      if (!getBounds().contains(point)) {
         return false;
      }

      for (final GTriangle3D triangle : triangulate()) {
         if (triangle.contains(point)) {
            return true;
         }
      }

      return false;
   }


   @Override
   public GQuad3D createSimplified(final double capsRadiansTolerance) {
      return this;
   }


   @Override
   public GQuad3D getHull() {
      return this;
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


   //   @Override
   //   protected List<GSegment2D> initializeEdges() {
   //      final List<GSegment2D> result = new ArrayList<GSegment2D>(3);
   //      result.add(new GSegment2D(_v2, _v1));
   //      result.add(new GSegment2D(_v0, _v2));
   //      result.add(new GSegment2D(_v1, _v0));
   //      return result;
   //   }

   @Override
   protected List<GSegment3D> initializeEdges() {
      final List<IVector3<?>> points = getPoints();
      final int pointsCount = points.size();

      final GSegment3D[] edges = new GSegment3D[pointsCount];

      int j = pointsCount - 1;
      for (int i = 0; i < pointsCount; j = i++) {
         edges[j] = new GSegment3D(points.get(j), points.get(i));
      }

      return Arrays.asList(edges);
   }


   @Override
   public GQuad3D transformedBy(final IVectorTransformer<IVector3<?>> transformer) {
      final IVector3<?> tv0 = _v0.transformedBy(transformer);
      final IVector3<?> tv1 = _v1.transformedBy(transformer);
      final IVector3<?> tv2 = _v2.transformedBy(transformer);
      final IVector3<?> tv3 = _v3.transformedBy(transformer);

      return new GQuad3D(tv0, tv1, tv2, tv3);

      //      return new GQuad2D(_v0.transformedBy(transformer), _v1.transformedBy(transformer), _v2.transformedBy(transformer),
      //               _v3.transformedBy(transformer));
   }


   @Override
   public boolean isConvex() {
      return GShape.isConvexQuad(_v0, _v1, _v2, _v3);
   }

}
