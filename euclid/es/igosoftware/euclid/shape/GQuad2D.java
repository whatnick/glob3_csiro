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
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector2;


public final class GQuad2D
         extends
            GQuad<IVector2, GSegment2D, GAxisAlignedRectangle>
         implements
            ISimplePolygon2D {

   private static final long serialVersionUID = 1L;


   public GQuad2D(final IVector2 pV0,
                  final IVector2 pV1,
                  final IVector2 pV2,
                  final IVector2 pV3) {
      super(pV0, pV1, pV2, pV3);
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      final IVector2 lower = GVectorUtils.min(_v0, _v1, _v2, _v3);
      final IVector2 upper = GVectorUtils.max(_v0, _v1, _v2, _v3);
      return new GAxisAlignedRectangle(lower, upper);
   }


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
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


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


   @Override
   public boolean isConvex() {
      return GShape.isConvexQuad(new GVector3D(_v0, 0), new GVector3D(_v1, 0), new GVector3D(_v2, 0), new GVector3D(_v3, 0));
   }


   @Override
   public List<GTriangle2D> triangulate() {
      //      final List<IVector2> points = getPoints();
      final GTriangulate.IndexedTriangle[] iTriangles = GTriangulate.triangulate(_v0, _v1, _v2, _v3);

      final List<GTriangle2D> result = new ArrayList<GTriangle2D>(iTriangles.length);
      for (final GTriangulate.IndexedTriangle iTriangle : iTriangles) {
         result.add(new GTriangle2D(getPoint(iTriangle._v0), getPoint(iTriangle._v1), getPoint(iTriangle._v2)));
      }
      return result;
   }


}
