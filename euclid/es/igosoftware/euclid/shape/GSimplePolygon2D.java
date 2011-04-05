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
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.utils.GTriangulate;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;


public final class GSimplePolygon2D
         extends
            GSimplePolytope<IVector2, GSegment2D, GAxisAlignedRectangle>
         implements
            ISimplePolygon2D {


   private static final long     serialVersionUID = 1L;


   private GAxisAlignedRectangle _bounds;


   public GSimplePolygon2D(final boolean validate,
                           final IVector2... points) {
      super(validate, points);
   }


   public GSimplePolygon2D(final boolean validate,
                           final List<IVector2> points) {
      super(validate, points);
   }


   //   public List<GSegment2D> getEdges() {
   //      if (_edges == null) {
   //         _edges = Collections.unmodifiableList(initializeEdges());
   //      }
   //      return _edges;
   //   }


   @Override
   protected List<GSegment2D> initializeEdges() {
      final List<IVector2> points = getPoints();
      final int pointsCount = points.size();

      final GSegment2D[] edges = new GSegment2D[pointsCount];

      int j = pointsCount - 1;
      for (int i = 0; i < pointsCount; j = i++) {
         edges[j] = new GSegment2D(points.get(j), points.get(i));
      }

      return Arrays.asList(edges);
   }


   //   /**
   //    * a polygon is defined to be simple if the edges do not intersect each other. O(n^2)
   //    */
   //   public boolean isSimple() {
   //      final List<GSegment2D> edges = getEdges();
   //      int pointsCount = getPointsCount();
   //
   //      for (int j = 2; j < pointsCount - 1; j++) {
   //         if (edges.get(0).intersects(edges.get(j))) {
   //            return false;
   //         }
   //      }
   //
   //      for (int i = 1; i < pointsCount - 2; i++) {
   //         for (int j = i + 2; j < pointsCount; j++) {
   //            if (edges.get(i).intersects(edges.get(j))) {
   //               return false;
   //            }
   //         }
   //      }
   //
   //      return true;
   //   }

   @Override
   public boolean contains(final IVector2 point) {
      if (!getBounds().contains(point)) {
         return false;
      }

      final List<IVector2> points = getPoints();

      final double x = point.x();
      final double y = point.y();

      int hits = 0;

      final IVector2 last = points.get(points.size() - 1);

      double lastX = last.x();
      double lastY = last.y();
      double curX;
      double curY;

      // Walk the edges of the polygon
      for (int i = 0; i < points.size(); lastX = curX, lastY = curY, i++) {
         final IVector2 cur = points.get(i);
         curX = cur.x();
         curY = cur.y();

         if (curY == lastY) {
            continue;
         }

         final double leftx;
         if (curX < lastX) {
            if (x >= lastX) {
               continue;
            }
            leftx = curX;
         }
         else {
            if (x >= curX) {
               continue;
            }
            leftx = lastX;
         }

         final double test1;
         final double test2;
         if (curY < lastY) {
            if ((y < curY) || (y >= lastY)) {
               continue;
            }
            if (x < leftx) {
               hits++;
               continue;
            }
            test1 = x - curX;
            test2 = y - curY;
         }
         else {
            if ((y < lastY) || (y >= curY)) {
               continue;
            }
            if (x < leftx) {
               hits++;
               continue;
            }
            test1 = x - lastX;
            test2 = y - lastY;
         }

         if (test1 < (test2 / (lastY - curY) * (lastX - curX))) {
            hits++;
         }
      }

      return ((hits & 1) != 0);


      //      boolean inside = false;
      //
      //      final double xt = point.x();
      //      final double yt = point.y();
      //
      //      final int npoints = _points.size();
      //      if (npoints < 3) {
      //         return false;
      //      }
      //
      //      double xold = _points.get(npoints - 1).x();
      //      double yold = _points.get(npoints - 1).y();
      //      for (final IVector2 each : _points) {
      //         final double xnew = each.x();
      //         final double ynew = each.y();
      //
      //         final double x1;
      //         final double x2;
      //         final double y1;
      //         final double y2;
      //         if (xnew > xold) {
      //            x1 = xold;
      //            x2 = xnew;
      //            y1 = yold;
      //            y2 = ynew;
      //         }
      //         else {
      //            x1 = xnew;
      //            x2 = xold;
      //            y1 = ynew;
      //            y2 = yold;
      //         }
      //
      //         if (((xnew < xt) == (xt <= xold)) /* edge "open" at one end */
      //             && ((yt - y1) * (x2 - x1) < (y2 - y1) * (xt - x1))) {
      //            inside = !inside;
      //         }
      //
      //         xold = xnew;
      //         yold = ynew;
      //      }
      //
      //      return inside;
   }


   @Override
   public boolean isSelfIntersected() {
      final List<IVector2> points = getPoints();
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
         final IVector2 iToj = points.get(j).sub(points.get(i));
         final IVector2 iTojNormal = new GVector2D(iToj.y(), -iToj.x());
         // i is the first vertex and j is the second
         final int startK = (j + 1) % pointsCount;
         int endK = (i - 1 + pointsCount) % pointsCount;
         endK += startK < endK ? 0 : startK + 1;
         int k = startK;
         IVector2 iTok = points.get(k).sub(points.get(i));
         boolean onLeftSide = iTok.dot(iTojNormal) >= 0;
         IVector2 prevK = points.get(k);
         ++k;
         for (; k <= endK; ++k) {
            final int modK = k % pointsCount;
            iTok = points.get(modK).sub(points.get(i));
            if (onLeftSide != (iTok.dot(iTojNormal) >= 0)) {
               final IVector2 prevKtoK = points.get(modK).sub(prevK);
               final IVector2 prevKtoKNormal = new GVector2D(prevKtoK.y(), -prevKtoK.x());
               if (((points.get(i).sub(prevK).dot(prevKtoKNormal)) >= 0) != ((points.get(j).sub(prevK).dot(prevKtoKNormal)) >= 0)) {
                  return true;
               }
            }
            onLeftSide = iTok.dot(iTojNormal) > 0;
            prevK = points.get(modK);
         }
      }

      return false;
   }


   @Override
   protected String getStringName() {
      return "SimplePolygon";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      if (_bounds == null) {
         _bounds = GAxisAlignedRectangle.minimumBoundingRectangle(this);
      }
      return _bounds;
   }


   @Override
   public double squaredDistance(final IVector2 point) {
      if (contains(point)) {
         return 0;
      }

      double min = Double.POSITIVE_INFINITY;

      for (final GSegment2D edge : getEdges()) {
         final double current = edge.squaredDistance(point);
         if (current < min) {
            min = current;
         }
      }

      return min;
   }


   @Override
   public List<GTriangle2D> triangulate() {
      final GTriangulate.IndexedTriangle[] iTriangles = GTriangulate.triangulate(_points);

      final List<GTriangle2D> result = new ArrayList<GTriangle2D>(iTriangles.length);
      for (final GTriangulate.IndexedTriangle iTriangle : iTriangles) {
         result.add(new GTriangle2D(_points.get(iTriangle._v0), _points.get(iTriangle._v1), _points.get(iTriangle._v2)));
      }
      return result;
   }


   @Override
   public boolean isConvex() {
      throw new RuntimeException("not yet implemented");
   }


}
