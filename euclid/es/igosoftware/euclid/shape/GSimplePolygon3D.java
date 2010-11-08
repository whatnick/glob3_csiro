/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.euclid.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.utils.GTriangulate;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.ITransformer;


public final class GSimplePolygon3D
         extends
            GSimplePolytope<IVector3<?>, GSegment3D, GSimplePolygon3D, GAxisAlignedBox>
         implements
            IPolygon3D<GSimplePolygon3D> {


   private static final long serialVersionUID = 1L;


   private GAxisAlignedBox   _bounds;

   private final GPlane      _plane;
   private GSimplePolygon2D  _polygon2d;


   public GSimplePolygon3D(final IVector3<?>... points) {
      super(points);

      _plane = initializePlane();
   }


   public GSimplePolygon3D(final List<IVector3<?>> points) {
      super(points);

      _plane = initializePlane();
   }


   private GSimplePolygon2D getPolygon2D() {

      if (_polygon2d == null) {
         _polygon2d = initializePolygon2D();
      }
      return _polygon2d;
   }


   private GPlane initializePlane() {
      try {
         final GPlane plane = GPlane.getBestFitPlane(_points);

         for (final IVector3<?> point : _points) {
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
   public List<GTriangle3D> triangulate() {
      final GTriangulate.IndexedTriangle[] iTriangles = GTriangulate.triangulate(getPolygon2D()._points);

      final List<GTriangle3D> result = new ArrayList<GTriangle3D>(iTriangles.length);
      for (final GTriangulate.IndexedTriangle iTriangle : iTriangles) {
         result.add(new GTriangle3D(_points.get(iTriangle._v0), _points.get(iTriangle._v1), _points.get(iTriangle._v2)));
      }
      return result;
   }


   @Override
   public boolean contains(final IVector3<?> point) {
      if (!getBounds().contains(point)) {
         return false;
      }

      if (!GMath.closeToZero(_plane.distance(point))) {
         return false;
      }

      for (final GTriangle3D triangle : triangulate()) {
         if (!triangle.contains(point)) {
            return false;
         }
      }

      return true;
   }


   @Override
   public boolean isSelfIntersected() {
      return getPolygon2D().isSelfIntersected();
   }


   private GSimplePolygon2D initializePolygon2D() {
      final List<IVector2<?>> points2d;

      if (_plane.isCloseToPlaneXY()) {
         points2d = GCollections.collect(_points, new ITransformer<IVector3<?>, IVector2<?>>() {
            @Override
            public IVector2<?> transform(final IVector3<?> element) {
               return new GVector2D(element.x(), element.y());
            }
         });
      }
      else if (_plane.isCloseToPlaneXZ()) {
         points2d = GCollections.collect(_points, new ITransformer<IVector3<?>, IVector2<?>>() {
            @Override
            public IVector2<?> transform(final IVector3<?> element) {
               return new GVector2D(element.x(), element.z());
            }
         });
      }
      else /*if (_plane.isCloseToPlaneYZ())*/{
         points2d = GCollections.collect(_points, new ITransformer<IVector3<?>, IVector2<?>>() {
            @Override
            public IVector2<?> transform(final IVector3<?> element) {
               return new GVector2D(element.y(), element.z());
            }
         });
      }

      return new GSimplePolygon2D(points2d);
   }


   @Override
   public IPolygon3D<?> createSimplified(final double capsRadiansTolerance) {
      final LinkedList<IVector3<?>> points = new LinkedList<IVector3<?>>(getPoints());

      List<IVector3<?>> previousPoints = new ArrayList<IVector3<?>>(points);

      boolean changed;
      do {
         changed = false;

         final int pointsCount = points.size();

         if (pointsCount < 3) {
            return GShape.createPolygon3(previousPoints);
         }

         for (int i = 0; i < pointsCount; i++) {
            final int previousI = (i + pointsCount - 1) % pointsCount;
            final int prePreviousI = (i + pointsCount - 2) % pointsCount;

            final GTriangle3D triangle = new GTriangle3D(points.get(prePreviousI), points.get(previousI), points.get(i));
            if (triangle.isCaps(capsRadiansTolerance)) {
               previousPoints = new ArrayList<IVector3<?>>(points);

               final IVector3<?> average = points.get(i).add(points.get(previousI)).div(2);
               points.set(i, average);
               points.remove(previousI);

               changed = true;
               break; // exit for
            }
         }

      }
      while (changed);

      if (points.size() < 3) {
         return GShape.createPolygon3(previousPoints);
      }

      return GShape.createPolygon3(points);
   }


   @Override
   protected String getStringName() {
      return "SimplePolygon";
   }


   @Override
   public GAxisAlignedBox getBounds() {
      if (_bounds == null) {
         _bounds = GAxisAlignedBox.minimumBoundingBox(this);
      }
      return _bounds;
   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return getBounds().getAxisAlignedBoundingBox();
   //   }


   @Override
   public double squaredDistance(final IVector3<?> point) {
      if (contains(point)) {
         return 0;
      }

      double min = Double.POSITIVE_INFINITY;

      for (final GSegment3D edge : getEdges()) {
         final double current = edge.squaredDistance(point);
         if (current < min) {
            min = current;
         }
      }

      return min;
   }


   @Override
   public GSimplePolygon3D getHull() {
      return this;
   }


   @Override
   public GSimplePolygon3D transformedBy(final IVectorTransformer<IVector3<?>> transformer) {
      return new GSimplePolygon3D(GCollections.collect(_points, transformer));
   }


}
