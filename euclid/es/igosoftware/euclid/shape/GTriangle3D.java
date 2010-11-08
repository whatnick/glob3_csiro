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
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorTransformer;


public final class GTriangle3D
         extends
            GTriangle<IVector3<?>, GSegment3D, GTriangle3D, GAxisAlignedBox>
         implements
            IPolygon3D<GTriangle3D> {

   private static final long serialVersionUID = 1L;


   public GTriangle3D(final IVector3<?> pV1,
                      final IVector3<?> pV2,
                      final IVector3<?> pV3) {
      super(pV1, pV2, pV3);
   }


   public boolean isCaps(final double radiansTolerance) {
      // final double[] angles = getInternalAngles();
      // final double maxAngle = Math.max(angles[0], Math.max(angles[1], angles[2]));
      //      
      // return ((Math.PI - maxAngle) < radiansTolerance);

      final List<GSegment3D> edges = getEdges();

      final double l1 = edges.get(0).getLength();
      final double l2 = edges.get(1).getLength();
      final double l3 = edges.get(2).getLength();

      final double angle1 = Math.acos((l2 * l2 + l3 * l3 - l1 * l1) / (2 * l2 * l3));
      if ((Math.PI - angle1) <= radiansTolerance) {
         return true;
      }

      final double angle2 = Math.acos((l1 * l1 + l3 * l3 - l2 * l2) / (2 * l1 * l3));
      if ((Math.PI - angle2) <= radiansTolerance) {
         return true;
      }

      final double angle3 = Math.PI - angle1 - angle2;
      return (Math.PI - angle3) <= radiansTolerance;

   }


   @Override
   public GAxisAlignedBox getBounds() {
      final IVector3<?> lower = _v0.min(_v1).min(_v2);
      final IVector3<?> upper = _v0.max(_v1).max(_v2);
      return new GAxisAlignedBox(lower, upper);
   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return getBounds().getAxisAlignedBoundingBox();
   //   }


   @Override
   public boolean contains(final IVector3<?> point) {
      if (!getBounds().contains(point)) {
         return false;
      }

      final List<IVector3<?>> points = getPoints();

      final double x = point.x();
      final double y = point.y();

      int hits = 0;

      final IVector3<?> last = points.get(points.size() - 1);

      double lastX = last.x();
      double lastY = last.y();
      double curX;
      double curY;

      // Walk the edges of the polygon
      for (int i = 0; i < points.size(); lastX = curX, lastY = curY, i++) {
         final IVector3<?> cur = points.get(i);
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
   public GTriangle3D createSimplified(final double capsRadiansTolerance) {
      return this;
   }


   @Override
   public GTriangle3D getHull() {
      return this;
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


   //   private GSegment3D getE1() {
   //      return new GSegment3D(_v3, _v2);
   //   }
   //   private GSegment3D getE2() {
   //      return new GSegment3D(_v1, _v3);
   //   }
   //   private GSegment3D getE3() {
   //      return new GSegment3D(_v2, _v1);
   //   }


   @Override
   protected List<GSegment3D> initializeEdges() {
      final List<GSegment3D> result = new ArrayList<GSegment3D>(3);
      result.add(new GSegment3D(_v2, _v1));
      result.add(new GSegment3D(_v0, _v2));
      result.add(new GSegment3D(_v1, _v0));
      return result;
   }


   @Override
   public GTriangle3D transformedBy(final IVectorTransformer<IVector3<?>> transformer) {
      return new GTriangle3D(_v0.transformedBy(transformer), _v1.transformedBy(transformer), _v2.transformedBy(transformer));
   }


   @Override
   public List<GTriangle3D> triangulate() {
      final ArrayList<GTriangle3D> result = new ArrayList<GTriangle3D>(1);
      result.add(this);
      return result;
   }


}
